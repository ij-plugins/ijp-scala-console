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

package ij_plugins.scala.console.editor

import ij_plugins.scala.console.YesNoAlert
import scalafx.Includes._
import scalafx.scene.image.Image
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.{FileChooser, Window}

import java.io.File
import java.util.prefs.Preferences

/**
 * Translate user actions into commands for the editor model. Controller in the MVC pattern.
 *
 * @author Jarek Sacha
 * @since 2/17/12
 */
private class EditorController(private val ownerWindow: Window, private val model: EditorModel) {

  private val defaultExtension = "scala"

  private lazy val fileChooser = new FileChooser {
    initialDirectory = currentDirectory
    extensionFilters += new ExtensionFilter("*." + defaultExtension, "*." + defaultExtension)
  }

  private def loadIcon(url: String): Image = {
    new Image(url)
  }

  private val fileNewAction = Action(
    name = "New...",
    icon = loadIcon("/ij_plugins/scala/console/resources/icons/page.png"),
    eventHandler = () => {
      if (ifModifiedAskAndSave()) {
        model.reset()
      }
    }
  )

  private val fileOpenAction = Action(
    name = "Open...",
    icon = loadIcon("/ij_plugins/scala/console/resources/icons/folder_page.png"),
    eventHandler = () => {
      if (ifModifiedAskAndSave()) {
        val file = fileChooser.showOpenDialog(ownerWindow)
        if (file != null) {
          currentDirectory = file.getParentFile
          read(file)
        }
      }
    }
  )

  private val fileSaveAction = Action(
    name = "Save",
    icon = loadIcon("/ij_plugins/scala/console/resources/icons/disk.png"),
    eventHandler = () => save()
  )

  private val fileSaveAsAction = Action(
    name = "Save As...",
    icon = null,
    eventHandler = () => saveAs()
  )

  def fileActions = Seq(fileNewAction, fileOpenAction, fileSaveAction, fileSaveAsAction)

  def read(file: File): Unit = {
    model.read(file)
  }

  private def save(): Unit = {
    model.sourceFile.value match {
      case Some(file) => model.save(file)
      case None       => saveAs()
    }
  }

  private def saveAs(): Boolean = {
    val file = fileChooser.showSaveDialog(ownerWindow)
    if (file == null) {
      return false
    }

    currentDirectory = file.getParentFile
    model.save(ensureExtension(file, defaultExtension))

    true
  }

  private def ensureExtension(file: File, extension: String): File = {
    if (file.getName.toLowerCase.endsWith("." + extension))
      file
    else
      new File(file.getPath + "." + extension)
  }

  /**
   * Return current directory saved in preferences, if cannot be retrieved return `null`.
   * FileChooser constructor is using `null` to indicate that starting directory is as user's default directory.
   */
  private def currentDirectory: File = {
    try {
      val prefNode         = Preferences.userRoot.node(this.getClass.getName)
      val currentDirectory = prefNode.get("fileChooser.currentDirectory", null)
      if (currentDirectory == null)
        null
      else
        new File(currentDirectory)
    } catch {
      case _: Throwable => null
    }
  }

  private def currentDirectory_=(dir: File): Unit = {
    try {
      val prefNode = Preferences.userRoot.node(this.getClass.getName)
      prefNode.put("fileChooser.currentDirectory", dir.getCanonicalPath)
    } catch {
      case _: Throwable =>
    }
  }

  /**
   * Perform operations needed to safely close the editor, save files, etc.
   *
   * Return 'true' if can be closed.
   */
  def prepareToClose(): Boolean = ifModifiedAskAndSave()

  /**
   * If editor text was modified, ask user to save current editor content.
   * Return `false` if user cancelled the operation.
   *
   * @return 'true' if content is saved or used did not want to save content.
   *         `false` if user canceled the dialog.
   */
  private def ifModifiedAskAndSave(): Boolean = {
    if (model.needsSaving.value) {
      val alert = YesNoAlert(
        parent = null,
        title = "New...",
        header = "Editor Content Modified.",
        content = "Do you want to save current script?"
      )

      val result = alert.showAndWait()

      result match {
        case Some(YesNoAlert.ButtonTypeYes) =>
          save()
          true
        case Some(YesNoAlert.ButtonTypeNo) =>
          true
        case _ =>
          // user chose CANCEL or closed the dialog
          false
      }
    } else {
      true
    }
  }
}
