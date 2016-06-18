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

import java.io.File

import net.sf.ij_plugins.scala.console.editor.Editor

import scala.swing.Frame

/**
 * Controller for the Scala Console.
 *
 * @author Jarek Sacha
 * @since 2/15/12 9:51 PM
 */
private class ScalaConsoleController {

    private[console] val editor = new Editor

    private val _model = new ScalaInterpreter()

    private val _view = new ScalaConsoleFrame(editor, this, _model)

    def view: Frame = _view

    def read(file:File) {
        editor.read(file)

    }

    /**
     * Interpret current editor selection. If selection is empty the whole editor text.
     */
    private[console] def run() {
        _view.outputArea.clear()
        _view.statusLine.text = "Running..."

        // Use selection if not empty
        val selection = editor.selection
        val code = if (selection != null && !selection.isEmpty) selection else editor.text

        // Show which code will be run
        _view.outputArea.list(code)

        // Run the code
        _model.run(code)
    }

    /**
     * Hide console frame, and possibly exit application.
     */
    private[console] def exit() {
        _view.visible = false
    }


}
