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

package net.sf.ij_plugins.scala.console.editor

import net.sf.ij_plugins.scala.console._
import java.io.File
import java.util.prefs.Preferences
import javax.swing.filechooser.FileNameExtensionFilter
import swing.{FileChooser, Action, Dialog, Component}

/**
 * Translate user actions into commands for the editor model. Controller in the MVC pattern.
 * @author Jarek Sacha
 * @since 2/17/12
 */
private class EditorController(private val parentView: Component,
                               private val model: EditorModel) {

    private val defaultExtension = "scala"

    private lazy val fileChooser = new FileChooser(currentDirectory) {
        fileFilter = new FileNameExtensionFilter("*." + defaultExtension, defaultExtension)
        multiSelectionEnabled = false
    }


    private val fileNewAction = new Action("New...") {

        icon = loadIcon(this.getClass, "../resources/icons/page.png")

        def apply() {

            askAndSave()

            // Reset editor
            model.reset()
        }
    }

    private val fileOpenAction = new Action("Open...") {
        icon = loadIcon(this.getClass, "../resources/icons/folder_page.png")

        def apply() {

            val status = fileChooser.showOpenDialog(parentView)
            if (status != FileChooser.Result.Approve) {
                return
            }

            val file = fileChooser.selectedFile
            if (file == null) {
                return
            }

            currentDirectory = file.getParentFile
            read(file)
        }
    }

    private val fileSaveAction = new Action("Save") {
        icon = loadIcon(this.getClass, "../resources/icons/disk.png")

        def apply() {
            save()
        }
    }

    private val fileSaveAsAction = Action("Save As...") {
        saveAs()
    }

    def fileActions = Array(fileNewAction, fileOpenAction, fileSaveAction, fileSaveAsAction)

    def read(file: File) {
        model.read(file)
    }

    private def save() {
        model.sourceFile match {
            case Some(file) => {
                model.save(file)
            }
            case None => saveAs()
        }

    }

    private def saveAs(): Boolean = {
        val status = fileChooser.showSaveDialog(parentView)
        if (status != FileChooser.Result.Approve) {
            return false
        }

        val file = fileChooser.selectedFile
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
     * JFileChooser constructor is using `null` to indicate that starting directory is as user's default directory.
     */
    private def currentDirectory: File = {
        try {
            val prefNode = Preferences.userRoot.node(this.getClass.getName)
            val currentDirectory = prefNode.get("fileChooser.currentDirectory", null)
            if (currentDirectory == null)
                null
            else
                new File(currentDirectory)
        } catch {
            case _: Throwable => null
        }
    }

    private def currentDirectory_=(dir: File) {
        try {
            val prefNode = Preferences.userRoot.node(this.getClass.getName)
            prefNode.put("fileChooser.currentDirectory", dir.getCanonicalPath)
        } catch {
            case _: Throwable => {}
        }
    }

    /**
     * Perform operations needed to safely close the editor, save files, etc.
     */
    def prepareToClose() {
        askAndSave()
    }

    private def askAndSave() {
        if (model.needsSave) {
            // Check if current document needs to be saved
            val status = Dialog.showConfirmation(
                parentView,
                "Do you want to save current script?",
                "Editor Content Modified",
                Dialog.Options.YesNo)
            // If not cancelled, save
            status match {
                case Dialog.Result.Yes => save()
                case Dialog.Result.No => {}
                case _ => return
            }
        }
    }
}
