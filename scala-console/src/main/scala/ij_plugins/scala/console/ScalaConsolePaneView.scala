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

import ij_plugins.scala.console.editor.Action
import org.scalafx.extras._
import org.scalafx.extras.mvcfx.ControllerFX
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.scene.control._
import scalafx.scene.image.Image
import scalafx.scene.layout.BorderPane
import scalafx.scene.{Cursor, Parent}
import scalafxml.core.macros.sfxml

/**
 * View for the Scala Console main pane.
 */
@sfxml
class ScalaConsolePaneView(
  private val fileMenu: Menu,
  private val toolBar: ToolBar,
  private val runMenuItem: MenuItem,
  private val editorPane: BorderPane,
  private val outputPane: BorderPane,
  private val statusLabel: Label,
  private val model: ScalaConsolePaneModel
) extends ControllerFX {

  private val runAction = Action(
    name = "Run",
    icon = new Image("/ij_plugins/scala/console/resources/icons/script_go.png"),
    eventHandler = () => model.onRun()
  )

  // Create tool bar buttons
  private val runButton = new Button {
    graphic = runAction.icon
    onAction = runAction.eventHandler
    tooltip = Tooltip(runAction.name)
  }
  private val enabledWhenReady = Seq(runButton)

  model.editor.fileActions.filter(_.icon != null).foreach { a =>
    val b = new Button {
      graphic = a.icon
      onAction = a.eventHandler
      tooltip = Tooltip(a.name)
    }
    toolBar.content += b
  }

  toolBar.content += new Separator()

  toolBar.content += runButton

  // Try to request the focus after this component is added to the scene
  Platform.runLater {
    runButton.requestFocus()
  }

  // Create editor menu items
  private val fileMenuItems = model.editor.fileActions.map { a =>
    new MenuItem {
      text = a.name
      graphic = a.icon
      onAction = a.eventHandler
    }.delegate
  }

  fileMenu.items ++= fileMenuItems
  fileMenu.items += new SeparatorMenuItem()
  fileMenu.items += new MenuItem {
    text = "Exit"
    onAction = () => {
      model.onExit()
    }
  }.delegate

  runMenuItem.disable <== !model.isReady
  enabledWhenReady.foreach(c => c.disable <== !model.isReady)

  runMenuItem.onAction = () => model.onRun()
  runButton.onAction = (ae: ActionEvent) => {
    actDisabled { () => runAction.eventHandler.handle(ae) }
  }

  statusLabel.text <== model.statusText

  editorPane.center = model.editor.view
  outputPane.center = model.outputArea.view

  private def actDisabled[R](op: () => R): Unit = {
    onFX {
      parent.disable = true
      parent.cursor = Cursor.Wait
    }

    // Define your task
    val task = new javafx.concurrent.Task[R] {
      override def call(): R = {
        op()
      }
      override def succeeded(): Unit = {
        parent.cursor = Cursor.Default
        parent.disable = false
      }
      override def failed(): Unit = {
        // TODO: show error message
        val ex = this.getException
        ex.printStackTrace()
        parent.cursor = Cursor.Default
        parent.disable = false
      }
    }

    // Run your task
    val t = new Thread(task, "UI Task")
    t.setDaemon(true)
    t.start()
  }

  private def parent: Parent = {
    runButton.parent().scene().root()
  }

}
