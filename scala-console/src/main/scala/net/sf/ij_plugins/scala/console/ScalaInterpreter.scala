/*
 * Image/J Plugins
 * Copyright (C) 2002-2012 Jarek Sacha
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

package net.sf.ij_plugins.scala.console


import java.io.{PrintStream, OutputStream, Writer}
import scala.Enumeration
import scala.swing.Publisher
import scala.swing.event.Event
import scala.tools.nsc.interpreter.Results.Result
import scala.tools.nsc.interpreter._
import scala.tools.nsc.{NewLinePrintWriter, Settings}
import scala.collection.mutable.ArrayBuffer

object ScalaInterpreter {

    /**
     * Interpreter execution state.
     */
    object State extends Enumeration {
        val Running = Value("Running...")
        val Ready = Value("Ready")
    }

    /**
     * Interpreter state changed.
     */
    case class StateEvent(state: State.Value) extends Event

    /**
     * Posted after interpreter finished with results returned by the interpreter
     */
    case class ResultEvent(result: Result) extends Event

    /**
     * New value `data` in the standard out stream.
     */
    case class OutStreamEvent(data: String) extends Event

    /**
     * New value `data` in the standard err stream.
     */
    case class ErrStreamEvent(data: String) extends Event

    /**
     * New value `data` in the interpreter log.
     */
    case class InterpreterLogEvent(data: String) extends Event

}


/**
 * Wrapper for scala interpreter. Publishes events when output is printed to standard output, standard error,
 * and to interpreter log.
 *
 * Publishes events:
 * [[net.sf.ij_plugins.scala.console.ScalaInterpreter.StateEvent]]
 * [[net.sf.ij_plugins.scala.console.ScalaInterpreter.OutStreamEvent]]
 * [[net.sf.ij_plugins.scala.console.ScalaInterpreter.ErrStreamEvent]]
 * [[net.sf.ij_plugins.scala.console.ScalaInterpreter.InterpreterLogEvent]]
 * [[net.sf.ij_plugins.scala.console.ScalaInterpreter.ResultEvent]]
 */
class ScalaInterpreter() extends Publisher {

    import ScalaInterpreter._

    val interpreterOutBuffer = new ArrayBuffer[String]

    private class LogOutputStream extends OutputStream {
        def write(b: Int) {
            write(Array(b.toByte), 0, 1)
        }
    }

    private object outStream extends LogOutputStream {
        override def write(b: Array[Byte], off: Int, len: Int) {
            publish(OutStreamEvent(new String(b, off, len)))
        }
    }

    private object errStream extends LogOutputStream {
        override def write(b: Array[Byte], off: Int, len: Int) {
            publish(ErrStreamEvent(new String(b, off, len)))
        }
    }


    private object interpreterOut extends Writer {
        def close() {}

        def flush() {}

        def write(buf: Array[Char], off: Int, len: Int) {
            val s = new String(buf, off, len)
            interpreterOutBuffer.append(s)
        }
    }


    val interpreterSettings = new Settings() {
        usejavacp.value = true
        // classpath.value +=
    }

    // Create interpreter
    val interpreter = new IMain(interpreterSettings, new NewLinePrintWriter(interpreterOut, true))

    private var _state = State.Ready

    /**
     * Current state.
     */
    def state: State.Value = _state


    private def state_=(newState: State.Value) {
        _state = newState
        publish(StateEvent(_state))
    }


    /**
     * Interpret `code`
     * @param code actual text of the code to be interpreted.
     */
    def run(code: String) {

        interpreterOutBuffer.clear()
        state = State.Running
        println("Running:\n" + code)

        // TODO: Can scala.swing.SwingWorker be used here?

        // Setup
        val worker = new javax.swing.SwingWorker[Result, Result] {
            override def doInBackground(): Result = {
                Console.setOut(outStream)
                java.lang.System.setOut(new PrintStream(outStream))
                Console.setErr(errStream)
                java.lang.System.setErr(new PrintStream(outStream))

                interpreter.interpret(code)
            }


            override def done() {
                get match {
                    case Results.Error => {
                        ScalaInterpreter.this.publish(ErrStreamEvent(interpreterOutBuffer.mkString))
                    }
                    case _ => {
                        ScalaInterpreter.this.publish(InterpreterLogEvent("\n" + interpreterOutBuffer.mkString))
                    }
                }
                ScalaInterpreter.this.state = State.Ready
            }
        }

        // Execute
        worker.execute()
    }
}