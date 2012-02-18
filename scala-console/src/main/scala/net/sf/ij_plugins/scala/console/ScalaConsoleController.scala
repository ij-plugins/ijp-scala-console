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
import swing.Frame

/**
 * Controller for the Scala Console.
 *
 * @author Jarek Sacha
 * @since 2/15/12 9:51 PM
 */
private class ScalaConsoleController {

    private[console] val editor = new Editor

    private val _model = new ScalaInterpreter()

    private val _view = new ScalaConsoleFrameS(editor, this, _model)

    def view: Frame = _view


    private[console] def run() {
        _view.outputArea.clear()
        _view.statusLine.text = "Running..."
        val selection = editor.selection
        val code = if (selection != null && !selection.isEmpty) selection else editor.text
        _model.run(code)
    }
}
