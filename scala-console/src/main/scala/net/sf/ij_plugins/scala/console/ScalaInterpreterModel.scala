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

import net.sf.ij_plugins.scala.console.event.Changed

import java.io.{OutputStream, Writer}
import scala.swing.Publisher
import scala.tools.nsc.interpreter.Results.Result
import scala.tools.nsc.interpreter._
import scala.tools.nsc.{NewLinePrintWriter, Settings}


/**
 *
 *
 */
class ScalaInterpreterModel(val outStream: OutputStream,
                            val errStream: OutputStream,
                            val interpreterOut: Writer) extends Publisher {

    val interpreterSettings = new Settings() {
        usejavacp.value = true
        //            classpath.value +=
    }

    // Create interpreter
    val interpreter = new IMain(interpreterSettings, new NewLinePrintWriter(interpreterOut, true))

    private var _statusText = "Welcome to Scala Console"
    private var _isReady = false


    def statusText: String = _statusText


    def statusText_=(text: String) {
        _statusText = text
        publish(new Changed(this, "status"))
    }


    def isReady = _isReady


    def isReady_=(ready: Boolean) {
        _isReady = ready
        publish(new Changed(this, "ready"))
    }


    def run(code: String) {

        isReady = false
        statusText = "Running..."
        println("Running:\n" + code)

        // TODO: Can scala.swing.SwingWorker be used here?

        // Setup
        val worker = new javax.swing.SwingWorker[Result, Void] {
            override def doInBackground(): Result = {
                Console.setOut(outStream)
                Console.setErr(errStream)

                interpreter.interpret(code)
            }


            override def done() {
                //                val r = get
                //                statusText = r.toString
                statusText = "Done"
                isReady = true
            }
        }

        // Execute
        worker.execute()
    }
}