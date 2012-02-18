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

import editor.Editor
import editor.Editor.SourceFileEvent
import ScalaInterpreter._
import java.util.ArrayList
import swing._
import swing.BorderPanel._
import swing.Component._
import java.awt.Cursor
import tools.nsc.interpreter.Results
import net.sf.ij_plugins.scala.swing.ToolBar
import net.sf.ij_plugins.scala.console


/**
 * Main view for the Scala Console.
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
        val icons = new ArrayList[Image]
        for (name <- names) icons.add(console.loadImage(this.getClass, "resources/" + name))
        icons
    })


    val outputArea = new OutputArea()

    val statusLine = new Label("Welcome to Scala Console")

    private val runAction = new Action("Run") {
        icon = console.loadIcon(this.getClass, "resources/icons/script_go.png")

        def apply() {
            controller.run()
        }
    }

    // Menu bar
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

    // Tool bar
    val toolBar = new ToolBar
    // Add editor actions that have icons
    val fileToolActions = editor.fileActions.filter(a => a.icon != null && a.icon != Swing.EmptyIcon)
    fileToolActions.foreach(toolBar += _)
    if (!fileToolActions.isEmpty)
        toolBar.addSeparator()
    // Add script run
    toolBar += runAction

    contents = new BorderPanel {
        add(toolBar, Position.North)
        add(new SplitPane(Orientation.Horizontal, editor.view, wrap(outputArea)), Position.Center)
        add(statusLine, Position.South)
    }

    private val enablable = Array(runAction)

    listenTo(model, editor)
    reactions += {
        case StateEvent(state) => {
            val isReady = state == State.Ready
            enablable.foreach(_.enabled = isReady)
            cursor = if (isReady) Cursor.getDefaultCursor else Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
            statusLine.text = state.toString
        }

        case ErrStreamEvent(data) => outputArea.appendErrStream(data)
        case OutStreamEvent(data) => outputArea.appendOutStream(data)
        case InterpreterLogEvent(data) => outputArea.appendInterpreterOut(data)
        case ResultEvent(result) => {
            result match {
                case Results.Error => outputArea.appendErrStream(result.toString)
                case Results.Success => outputArea.appendOutStream(result.toString)
                case Results.Incomplete => outputArea.appendErrStream(result.toString)
            }
        }
        case SourceFileEvent(fileOption) => fileOption match {
            case Some(file) => title = defaultTitle + " - " + file.getCanonicalPath
            case None => title = defaultTitle
        }
    }


}
