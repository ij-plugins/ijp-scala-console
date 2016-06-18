/*
 *  ImageJ Plugins
 *  Copyright (C) 2002-2016 Jarek Sacha
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

package net.sf.ij_plugins.scala.console

import java.awt.Cursor

import net.sf.ij_plugins.scala.console
import net.sf.ij_plugins.scala.console.ScalaInterpreter._
import net.sf.ij_plugins.scala.console.editor.Editor
import net.sf.ij_plugins.scala.console.editor.Editor.SourceFileEvent
import net.sf.ij_plugins.scala.swing.ToolBar

import scala.swing.BorderPanel._
import scala.swing.Component._
import scala.swing._
import scala.swing.event.WindowClosing
import scala.tools.nsc.interpreter.Results


/**
 * Main view for the Scala Console.
  *
  * @author Jarek Sacha
 * @since 2/11/12
 */
private class ScalaConsoleFrame(val editor: Editor,
                                val controller: ScalaConsoleController,
                                val model: ScalaInterpreter) extends Frame {

    private val defaultTitle = "Scala Console"
    title = defaultTitle

    // Set frame icons, at different scales.
    peer.setIconImages({
        val names = Array("scala16.png", "scala32.png", "scala48.png", "scala64.png")
        val icons = for (name <- names) yield console.loadImage(this.getClass, "resources/" + name)
      import scala.collection.JavaConversions._
      icons.toList
    })


    // Create local actions
    private val runAction = new Action("Run") {
        icon = console.loadIcon(this.getClass, "resources/icons/script_go.png")

        def apply() {
            controller.run()
        }
    }

    // Setup menu bar
    menuBar = new MenuBar {
        contents += new Menu("File") {
            contents ++= editor.fileActions.map(new MenuItem(_))

            contents += new Separator()

            contents += new MenuItem(Action("Exit") {
                controller.exit()
            })
        }
        contents += new Menu("Script") {
            contents += new MenuItem(runAction)
        }

    }

    // Setup tool bar
    val toolBar         = new ToolBar
    // Add editor actions that have icons
    val fileToolActions = editor.fileActions.filter(a => a.icon != null && a.icon != Swing.EmptyIcon)
    fileToolActions.foreach(toolBar += _)
    if (!fileToolActions.isEmpty)
        toolBar.addSeparator()
    // Add script run
    toolBar += runAction


    // Position components
    val outputArea = new OutputArea()
    val statusLine = new Label("Welcome to Scala Console")
    contents = new BorderPanel {
        add(toolBar, Position.North)
        add(new SplitPane(Orientation.Horizontal, editor.view, wrap(outputArea)), Position.Center)
        add(statusLine, Position.South)
    }


    private val enablable = Array(runAction)

    // Listen to models
    listenTo(model, editor)

    // React to models and internal changes
    reactions += {
      case StateEvent(state) =>
        val isReady = state == State.Ready
        enablable.foreach(_.enabled = isReady)
        cursor = if (isReady) Cursor.getDefaultCursor else Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
        statusLine.text = state.toString

      case ErrStreamEvent(data) => outputArea.appendErrStream(data)
      case OutStreamEvent(data) => outputArea.appendOutStream(data)
        case InterpreterLogEvent(data) => outputArea.appendInterpreterOut(data)
      case ResultEvent(result) =>
        result match {
          case Results.Error => outputArea.appendErrStream(result.toString)
          case Results.Success => outputArea.appendOutStream(result.toString)
          case Results.Incomplete => outputArea.appendErrStream(result.toString)
        }

      case SourceFileEvent(fileOption) => fileOption match {
        case Some(file) => title = defaultTitle + " - " + file.getCanonicalPath
            case None => title = defaultTitle
        }

        case WindowClosing(_) => editor.prepareToClose()
    }
}
