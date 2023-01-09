package dotty.tools.repl

import dotty.tools.dotc.core.StdNames.str

import java.io.PrintStream
import java.lang.reflect.Method
import scala.util.control.NonFatal

/** Interprets Scala code, based on `dotty.tools.repl.ScriptEngine` */
class SCIMain(out: PrintStream, loader: ClassLoader) {

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

  private var state     = driver.initialState
  private val rendering = new Rendering(Some(getClass.getClassLoader))

  def bind(tup: (String, Any)): Unit =
    state = driver.bind(tup._1, tup._2)(using state)

  def interpret(line: String): SCResults = {
    val methodOpt: Option[Method] =
      try {
        evalToMethod(line)
      } catch {
        case NonFatal(ex) =>
//          ex.printStackTrace()
//          ex.printStackTrace(out)
          return SCResults.Error
      }

    val valueOpt: Option[Any] = methodOpt.map(_.invoke(null))

    val value      = valueOpt.getOrElse(())
    val methodName = methodOpt.fold("")(_.getName)
    if (methodOpt.isDefined && valueOpt.isDefined && !valueOpt.contains(())) {
      out.println(s"$methodName: $value")
    }
    SCResults.Success
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

}
