/*
 * ImageJ Plugins
 * Copyright (C) 2002-2016 Jarek Sacha
 * Author's email: jpsacha at gmail dot com
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
 * Latest release available at https://github.com/ij-plugins
 *
 */

package net.sf.ij_plugins.scala.console

import enumeratum.{EnumEntry, _}
import net.sf.ij_plugins.scala.console.ScalaInterpreter.InterpreterEvent
import net.sf.ij_plugins.scala.console.editor.extra.Publisher

import java.io.{OutputStream, PrintStream, Writer}
import scala.collection.immutable
import scala.collection.mutable.ArrayBuffer
import scala.tools.nsc.interpreter.Results.Result
import scala.tools.nsc.interpreter.{IMain, Results}
import scala.tools.nsc.{NewLinePrintWriter, Settings}

object ScalaInterpreter {

  /**
   * Event marker trait.
   */
  trait InterpreterEvent

  /**
   * Interpreter execution state.
   */
  sealed abstract class State(override val entryName: String) extends EnumEntry

  object State extends Enum[State] {

    val values: immutable.IndexedSeq[State] = findValues

    case object Running extends State("Running...")

    case object Ready extends State("Ready")

  }

  /**
   * Interpreter state changed.
   */
  case class StateEvent(state: State) extends InterpreterEvent

  /**
   * Posted after interpreter finished with results returned by the interpreter
   */
  case class ResultEvent(result: Result) extends InterpreterEvent

  /**
   * New value `data` in the standard out stream.
   */
  case class OutStreamEvent(data: String) extends InterpreterEvent

  /**
   * New value `data` in the standard err stream.
   */
  case class ErrStreamEvent(data: String) extends InterpreterEvent

  /**
   * New value `data` in the interpreter log.
   */
  case class InterpreterLogEvent(data: String) extends InterpreterEvent

}

/**
 * Wrapper for scala interpreter. Publishes events when output is printed to standard output, standard error,
 * and to interpreter log.
 *
 * Publishes events:
 * [[console.ScalaInterpreter.StateEvent StateEvent]],
 * [[console.ScalaInterpreter.OutStreamEvent OutStreamEvent]],
 * [[console.ScalaInterpreter.ErrStreamEvent ErrStreamEvent]],
 * [[console.ScalaInterpreter.InterpreterLogEvent InterpreterLogEvent]],
 * [[console.ScalaInterpreter.ResultEvent ResultEvent]].
 */
class ScalaInterpreter() extends Publisher[InterpreterEvent] {

  import ScalaInterpreter._

  val interpreterOutBuffer = new ArrayBuffer[String]

  private class LogOutputStream extends OutputStream {
    def write(b: Int): Unit = {
      write(Array(b.toByte), 0, 1)
    }
  }

  private object outStream extends LogOutputStream {
    override def write(b: Array[Byte], off: Int, len: Int): Unit = {
      publish(OutStreamEvent(new String(b, off, len)))
    }
  }

  private object errStream extends LogOutputStream {
    override def write(b: Array[Byte], off: Int, len: Int): Unit = {
      publish(ErrStreamEvent(new String(b, off, len)))
    }
  }

  private object interpreterOut extends Writer {
    def close(): Unit = {}

    def flush(): Unit = {}

    def write(buf: Array[Char], off: Int, len: Int): Unit = {
      val s = new String(buf, off, len)
      interpreterOutBuffer.append(s)
    }
  }

  val interpreterSettings: Settings = new Settings() {
    usejavacp.value = true
    //        classpath.value +=
  }

  // Create interpreter
  val interpreter = new IMain(interpreterSettings, new NewLinePrintWriter(interpreterOut, true))

  private var _state: State = State.Ready

  /**
   * Current state.
   */
  def state: State = _state

  private def state_=(newState: State): Unit = {
    _state = newState
    publish(StateEvent(_state))
  }

  /**
   * Interpret `code`
   *
   * @param code actual text of the code to be interpreted.
   */
  def run(code: String): Unit = {

    interpreterOutBuffer.clear()
    state = State.Running

    Console.setOut(outStream)
    java.lang.System.setOut(new PrintStream(outStream))
    Console.setErr(errStream)
    java.lang.System.setErr(new PrintStream(outStream))

    val r = interpreter.interpret(code)

    r match {
      case Results.Error =>
        publish(ErrStreamEvent(interpreterOutBuffer.mkString))
      case _ =>
        publish(InterpreterLogEvent("\n" + interpreterOutBuffer.mkString))
    }
    state = State.Ready
  }
}
