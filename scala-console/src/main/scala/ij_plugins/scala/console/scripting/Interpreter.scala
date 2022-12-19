package ij_plugins.scala.console.scripting

import ij_plugins.scala.console.scripting.Results.Result

trait Interpreter {

  def interpret(line: String): Result

}
