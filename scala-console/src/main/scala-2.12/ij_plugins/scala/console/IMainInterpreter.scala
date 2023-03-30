package ij_plugins.scala.console

import ij_plugins.scala.console.scripting.{Interpreter, Results}

import java.io.Writer
import scala.tools.nsc

class IMainInterpreter(writer: Writer) extends Interpreter {

  private val interpreterSettings: nsc.Settings = new nsc.Settings() {
    usejavacp.value = true
    //        classpath.value +=
  }

  private val iMain = new nsc.interpreter.IMain(interpreterSettings, new nsc.NewLinePrintWriter(writer, true))

  override def interpret(line: String): Results.Result = {
    iMain.interpret(line) match {
      case scala.tools.nsc.interpreter.Results.Success => Results.Success
      case scala.tools.nsc.interpreter.Results.Error => Results.Error
      case scala.tools.nsc.interpreter.Results.Incomplete => Results.Incomplete
    }
  }
}