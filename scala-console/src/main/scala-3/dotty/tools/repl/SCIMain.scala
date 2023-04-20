package dotty.tools.repl

import dotty.tools.dotc.core.StdNames.str

import java.io.PrintStream
import java.lang.reflect.{InvocationTargetException, Method}
import scala.annotation.tailrec
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

/** Interprets Scala code, based on `dotty.tools.repl.ScriptEngine` */
class SCIMain(out: PrintStream, loader: ClassLoader) {
  import SCIMain.*

  private val driver =
    new ReplDriver(
      Array(
//        "-classpath", "", // Avoid the default "."
        "-usejavacp",
        "-color:never",
        "-Xrepl-disable-display",
        "-explain"
      ),
      out,
      Some(loader)
    )
  private val rendering = new Rendering(Some(getClass.getClassLoader))
  private var state     = driver.initialState

  def bind(tup: (String, Any)): Unit =
    state = driver.bind(tup._1, tup._2)(using state)

  def interpret(line: String): SCResults = {
    // Parse script
    val methodTry: Try[Option[Method]] =
      Try {
        evalToMethod(line)
      }

    // Execute parsed script
    val result: Try[Unit] =
      for methodOpt <- methodTry yield {
        for method <- methodOpt do
          val value = method.invoke(null)
          if value != () then
            out.println(s"${method.getName}: $value")
      }

    // Interpret script execution result
    result match
      case Success(_) =>
        // Check if the states indicate that there errors during parsing
        if state.context.reporter.hasErrors then
          SCResults.Error
        else
          SCResults.Success
      case Failure(ex) =>
        ex match {
          case ex1 if wasCausedByImageJMacroAbort(ex1) =>
            out.println(s"WARNING: Detected ImageJ's \"$IMAGEJ_MACRO_CANCELED\" request. Stopping script execution.")
            SCResults.Success
          // Attempt to unwrap the exception that may be thrown in the interpreted scripts, skip the wrapping
          case e1: InvocationTargetException =>
            e1.getCause match {
              case ex2: ExceptionInInitializerError =>
                Option(ex2.getCause).foreach(_.printStackTrace(out))
              case _ =>
              // ???
            }
            SCResults.Error
          case _ =>
            // ???
            SCResults.Error
        }
  }

  private def evalToMethod(script: String): Option[Method] = {
    val vid = state.valIndex
    state = driver.run(script)(using state)
    val oid = state.objectIndex
    Class
      .forName(s"${Rendering.REPL_WRAPPER_NAME_PREFIX}$oid", true, rendering.classLoader()(using state.context))
      .getDeclaredMethods
      .find(_.getName == s"${str.REPL_RES_PREFIX}$vid")
  }

  /**
   * Check if exception has a signature of an exception thrown by ImageJ to indicate that macro cancellation.
   * @param t exception to test
   * @return `true` is the exception matches the ImageJ' macro abort exception.
   */
  @tailrec
  private final def wasCausedByImageJMacroAbort(t: Throwable): Boolean = {
    if t == null then
      false
    else if t.isInstanceOf[RuntimeException] & t.getMessage == IMAGEJ_MACRO_CANCELED then
      true
    else
      wasCausedByImageJMacroAbort(t.getCause)
  }

}

object SCIMain:
  private val IMAGEJ_MACRO_CANCELED = "Macro canceled"
end SCIMain
