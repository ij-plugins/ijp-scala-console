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
import swing._
import javax.swing._
import java.awt.event.ActionEvent
import java.awt.BorderLayout._
import java.awt.{Cursor, Color}


/*
* Implementation of a Scala Console as 'regular' Swing JFrame, Scala.swing is still too buggy.
*
*/
object ScalaConsoleFrame {

  def main(args: Array[String]) {
    Swing.onEDT {
      val frame = new ScalaConsoleFrame
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      frame.pack()
      frame.setVisible(true)
    }
  }
}


/**
 * Implements the view part of the Scala Console.
 */
class ScalaConsoleFrame extends JFrame with Reactor {

  val model = new ScalaConsoleModel

  val editor = new JTextArea(25, 80) {
    setText("import ij._\n" +
            "val img = WindowManager.getCurrentImage()")
  }

  val outputArea = new JTextArea(10, 80) {
    setText("")
    setEditable(false)
    setBackground(new Color(255, 255, 218))
  }

  val statusLine = new JLabel("Welcome to Scala Console")

  val runAction = new AbstractAction("Run") {
    def actionPerformed(e: ActionEvent) {
      run()
    }
  }

  val runButton = new JButton(runAction)

  val enablable = Array(this, runButton)


  setTitle("Scala Console")
  add(runButton, NORTH)
  add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(editor), new JScrollPane(outputArea)), CENTER)
  add(statusLine, SOUTH)


  def run() {
    statusLine.setText("Running...")
    model.run(editor.getText)
  }


  listenTo(model)
  reactions += {
    case Changed(model) => onEDT({
      println("Reactions")
      enablable.foreach(_.setEnabled(model.ready))
      setCursor(if (model.ready) Cursor.getDefaultCursor else Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))
      outputArea.setText(model.output)
      statusLine.setText(model.status)
    })
  }


  def onEDT(op: => Unit) {
    if (SwingUtilities.isEventDispatchThread) {
      op
    } else {
      Swing.onEDTWait(op)
    }
  }
}