/*
 *  ImageJ Plugins
 *  Copyright (C) 2002-2022 Jarek Sacha
 *  Author's email: jpsacha at gmail dot com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   Latest release available at https://github.com/ij-plugins
 */

package ij_plugins.scala.console

import enumeratum.{EnumEntry, *}
import ij_plugins.scala.console.ScalaInterpreter.InterpreterEvent
import ij_plugins.scala.console.editor.extra.Publisher
import ij_plugins.scala.console.scripting.Results.Result
import ij_plugins.scala.console.scripting.{Interpreter, Results}

import java.io.{OutputStream, PrintStream, Writer}
import scala.collection.immutable
import scala.collection.mutable.ArrayBuffer

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
 * [[ij_plugins.scala.console.ScalaInterpreter.StateEvent StateEvent]],
 * [[ij_plugins.scala.console.ScalaInterpreter.OutStreamEvent OutStreamEvent]],
 * [[ij_plugins.scala.console.ScalaInterpreter.ErrStreamEvent ErrStreamEvent]],
 * [[ij_plugins.scala.console.ScalaInterpreter.InterpreterLogEvent InterpreterLogEvent]],
 * [[ij_plugins.scala.console.ScalaInterpreter.ResultEvent ResultEvent]].
 */
class ScalaInterpreter extends Publisher[InterpreterEvent] {

  import ScalaInterpreter._

  private val interpreterOutBuffer = new ArrayBuffer[String]

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

  // Create interpreter
  private val interpreter: Interpreter = IMainFactory.create(interpreterOut)

  private var _state: State = State.Ready

  /**
   * Current state.
   */
  private def state: State = _state

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

    // Console.setOut(outStream)
    // Console.setErr(errStream)
    java.lang.System.setOut(new PrintStream(outStream))
    java.lang.System.setErr(new PrintStream(outStream))
    val r =
      Console.withOut(outStream) {
        Console.withErr(errStream) {
          interpreter.interpret(code)
        }
      }

    r match {
      case Results.Error =>
        publish(ErrStreamEvent(interpreterOutBuffer.mkString))
      case _ =>
        publish(InterpreterLogEvent("\n" + interpreterOutBuffer.mkString))
    }
    state = State.Ready
  }
}
