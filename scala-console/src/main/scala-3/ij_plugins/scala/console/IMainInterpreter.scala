package ij_plugins.scala.console

import ij_plugins.scala.console.scripting.{Interpreter, Results}

import java.io.{PrintWriter, Writer}
//import scala.tools.nsc

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
//
  override def interpret(line: String): Results.Result = {
//    iMain.interpret(line) match {
//      case scala.tools.nsc.interpreter.Results.Success    => Results.Success
//      case scala.tools.nsc.interpreter.Results.Error      => Results.Error
//      case scala.tools.nsc.interpreter.Results.Incomplete => Results.Incomplete
//    }
    ???
  }
}
