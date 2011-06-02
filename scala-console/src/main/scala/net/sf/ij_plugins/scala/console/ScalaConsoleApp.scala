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

import swing._
import swing.BorderPanel.Position._
import swing.event.ButtonClicked
import java.awt.Cursor
import javax.swing.SwingUtilities


/**
 * This is a test of Scala.Swing, buggy, use {@link ScalaConsoleFrame} instead.
 * There are problems with layout manager and event handling, will try again with Scala 2.9.
 */
object ScalaConsoleApp extends SimpleSwingApplication {

  val model = new ScalaInterpreterModel

  val outputArea = new TextArea {
    columns = 80
    rows = 10
    text = "scala>\n"
    background = new Color(255, 255, 218)
  }

  val runButton = new Button {
    text = "Run"
  }

  val statusLine = new Label {
    text = "Welcome to Scala Console"
    //      border = Swing.EtchedBorder
  }


  def top = new MainFrame {
    title = "Scala Console"

    val editor = new TextArea {
      columns = 80
      rows = 25
      text = "// Simple test\n" +
              "val a = \"Hello\"\n" +
              "println(a)"
    }

    //    contents = new BoxPanel(Orientation.Vertical) {
    contents = new BorderPanel {

      add(runButton, North)
      add(new SplitPane(Orientation.Horizontal,
        new ScrollPane {
          contents = editor
        },
        new ScrollPane {
          contents = outputArea
        }), Center)

      add(statusLine, South)

      border = Swing.EmptyBorder(5, 5, 5, 5)

      listenTo(runButton)
      reactions += {
        case ButtonClicked(b) => actionRun()
      }
    }


    def actionRun() {
      statusLine.text = "Running..."
      cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
      runButton.enabled = false

      model.run(editor.text)

      //      top.cursor = Cursor.getDefaultCursor
      //      top.runButton.enabled = true

    }


  }


  //  listenTo(model)
  //  reactions += {
  //    case Changed(model) => onEDT({
  //      println("Reactions")
  //      runButton.enabled = model.ready
  //      top.cursor = if (model.ready) Cursor.getDefaultCursor else Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
  //      outputArea.text = model.output
  //      statusLine.text = model.status
  //    })
  //  }


  def onEDT(op: => Unit) {
    if (SwingUtilities.isEventDispatchThread) {
      op
    } else {
      Swing.onEDTWait(op)
    }
  }
}