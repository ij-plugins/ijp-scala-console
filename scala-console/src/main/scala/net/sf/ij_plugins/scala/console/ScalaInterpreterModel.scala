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

package net.sf.ij_plugins.scala.console

import event.Changed
import swing.Publisher
import java.io.PrintWriter
import net.sf.ij_plugins.scala.{BufferedPrintStream, ScalaInterpreter}


/**
 *
 *
 */
class ScalaInterpreterModel extends Publisher {

  private val interpreter = new ScalaInterpreter();
  //  private val out = ScalaUtils.redirectSystemOut()
  private val out = new BufferedPrintStream()
  private val flusher = new PrintWriter(out)

  private var statusText = "Welcome to Scala Console"
  private var outputText = ""
  private var readyFlag = false


  def status: String = statusText


  def status_(text: String) {
    statusText = text
    publish(new Changed(this))
  }


  def output = outputText


  def output_(text: String) {
    outputText = text
    publish(new Changed(this))
  }


  def ready = readyFlag


  def ready_(state: Boolean) {
    readyFlag = state
    publish(new Changed(this))
  }


  def run(code: String) {

    ready_(false)
    status_("Running...")
    println("Running:\n" + code)

    // TODO: Can scala.swing.SwingWorker be used here?

    // Setup
    val worker = new javax.swing.SwingWorker[((String, String, String), String), Void] {
      override def doInBackground(): ((String, String, String), String) = {
        flusher.flush()
        out.reset()

        val r = interpreter.interpret(code)

        flusher.flush()
        val stdOut = out.toString
        out.reset()

        return (r, stdOut)
      }


      override def done() {
        val ((outputNew, error, result), stdOut) = get
        output_("\n" + stdOut + "\n---\n" + outputNew + "\n+++\n" + error)
        status_(result)
        ready_(true)
      }
    }

    // Execute
    worker.execute()
  }
}