package ij_plugins.scala.console

import dotty.tools.repl.{SCIMain, SCResults}
import ij_plugins.scala.console.IMainInterpreter.WriterOutputStream
import ij_plugins.scala.console.scripting.{Interpreter, Results}

import java.io.{OutputStream, PrintStream, PrintWriter, Writer}

object IMainInterpreter {
  private final class WriterOutputStream(writer: Writer) extends OutputStream {
    override def write(b: Array[Byte], off: Int, len: Int): Unit = {
      val str = new String(b, off, len)
      writer.write(str, 0, str.length)
    }

    def write(b: Int): Unit = write(Array(b.toByte), 0, 1)
  }
}

class IMainInterpreter(writer: Writer) extends Interpreter {

//  private val interpreterSettings: nsc.Settings = new nsc.Settings() {
//    usejavacp.value = true
//    //        classpath.value +=
//  }
//
//  private val shellConfig  = nsc.interpreter.shell.ShellConfig(interpreterSettings)
//  private val replReporter = new nsc.interpreter.shell.ReplReporterImpl(shellConfig, writer = new PrintWriter(writer))
//
//  private val iMain = new nsc.interpreter.IMain(interpreterSettings, replReporter)

  private val out: PrintStream = new PrintStream(new WriterOutputStream(writer))
  private val loader  = getClass.getClassLoader

  private val iMain: SCIMain = new SCIMain(out, loader)

  override def interpret(line: String): Results.Result = {
    iMain.interpret(line) match {
      case SCResults.Success    => Results.Success
      case SCResults.Error      => Results.Error
      case SCResults.Incomplete => Results.Incomplete
    }
  }
}
