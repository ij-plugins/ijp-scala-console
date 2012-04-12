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

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.io.File
import swing.event.Event
import swing.{Publisher, Action, Component}

object Editor {

    /**
     * Notifies that file name associated with the editor changed.
     * Informs what file was saved or opened in the editor.
     */
    case class SourceFileEvent(file: Option[File]) extends Event

}

/**
 * Code input area of the console.  Creates MVC components and gives access to the view, and externally relevant
 * parts of the model (selection and text) and controller (actions).
 *
 * Publishes event [[net.sf.ij_plugins.scala.console.editor.Editor.SourceFileEvent]]
 */
class Editor extends Publisher {

    private val textArea = new RSyntaxTextArea(10, 80)

    private lazy val _view       = new EditorView(textArea)
    private lazy val _model      = new EditorModel(textArea)
    private lazy val _controller = new EditorController(Component.wrap(textArea), _model)

    listenTo(_model)
    reactions += {
        case EditorModel.SourceFileEvent(file) => publish(Editor.SourceFileEvent(file))
    }

    // Initialize editor
    _model.reset()


    /**
     * Component displaying content of this editor
     */
    def view: Component = _view

    /**
     * Currently selected text in the editor. May be an empty string.
     */
    def selection: String = _model.selection

    /**
     * Full editor content.
     */
    def text = _model.text

    /**
     * Actions for file menu.
     */
    def fileActions: Seq[Action] = _controller.fileActions

    /**
     * Perform operations needed to safely close the editor, save files, etc.
     */
    def prepareToClose() {
        _controller.prepareToClose()
    }

}