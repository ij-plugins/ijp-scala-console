/*
 * Image/J Plugins
 * Copyright (C) 2002-2011 Jarek Sacha
 * Author's email: jsacha at users dot sourceforge dot net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Latest release available at http://sourceforge.net/projects/ij-plugins/
 */

package net.sf.ij_plugins.scala

import java.io.PrintWriter
import tools.nsc.{GenericRunnerSettings, Interpreter}


class ScalaInterpreter {

  /**
   * Interpreter uses it to print <i>regular</i> messages.
   */
  private val outputStream = new BufferedPrintStream()
  private val outputPrinter = new PrintWriter(outputStream)

  /**
   * Interpreter uses it to print errors messages?
   */
  private val errorPrinter = new BufferedPrintStream()


  private def errorPrinterFn() = (x: String) => errorPrinter.println(x)


  protected val interpreter = {
    val settings = new GenericRunnerSettings(errorPrinterFn())
    settings.usejavacp.value = true
    //    settings.classpath.value = ...
    new Interpreter(settings, outputPrinter)
  }

  init()


  def init() {
    interpreter.interpret("" +
            "import ij._")
  }


  def interpret(code: String): (String, String, String) = {

    val result = interpreter.interpret(code)
    //    IJ.log("Interpreter: \n" + text)
    //    interpreter.interpret(text) match {
    //      case InterpreterResults.Error => IJ.log(">> Error interpreting")
    //      case InterpreterResults.Success => IJ.log(">> Ok. <" + interpreter.mostRecentVar + ">")
    //      case InterpreterResults.Incomplete => IJ.log(">> Incomplete code")
    //      case _ => IJ.log("???")
    //    }


    outputPrinter.flush()
    errorPrinter.flush()

    val output = outputStream.toString
    val error = errorPrinter.toString

    outputStream.reset()
    errorPrinter.reset()

    (output, error, result.toString)
  }


  def reset() {
    interpreter.reset()
    init()
  }


  def dispose() {
    interpreter.close()
  }
}