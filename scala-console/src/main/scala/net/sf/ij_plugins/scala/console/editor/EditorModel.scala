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

import java.io.{FileWriter, File}
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import swing.Publisher
import swing.event.Event


private object EditorModel {

    case class SourceFileEvent(file: Option[File]) extends Event

}

/**
 * Model of the code editor, in the MVC sense. Publishes `SourceFileEvent` when new file is read or saved to.
 * @author Jarek Sacha
 * @since 2/17/12 5:57 PM
 */
private class EditorModel(private val textArea: RSyntaxTextArea) extends Publisher {

    private var _sourceFile: Option[File] = None

    /**
     * Associated file from which the file was loaded or saved last time.
     */
    def sourceFile: Option[File] = _sourceFile

    private def sourceFile_=(file: Option[File]) {
        _sourceFile = file
        publish(EditorModel.SourceFileEvent(_sourceFile))
    }


    /**
     * Return a text content of this editor
     */
    def text: String = {
        textArea.getText
    }

    def selection: String = {
        textArea.getSelectedText
    }


    def needsSave: Boolean = {
        // TODO: detect changes and return 'true' only of document was modified
        true
    }


    def reset() {
        textArea.setText("import ij._\n" +
                "val img = WindowManager.getCurrentImage()\n" +
                "IJ.log(\"Hello\")\n" +
                "println(\"I am here\")\n")
        sourceFile = None
    }

    def read(file: File) {
        val source = scala.io.Source.fromFile(file)
        val lines = source.mkString
        source.close()

        textArea.setText(lines)
        sourceFile = Some(file)
    }


    def save(file: File) {
        val writer = new FileWriter(file)
        try {
            writer.write(text)
        } finally {
            writer.close()
        }
        sourceFile = Some(file)
    }


}
