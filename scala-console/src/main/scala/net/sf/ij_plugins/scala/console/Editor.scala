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

import javax.swing.JComponent
import org.fife.ui.rsyntaxtextarea.{SyntaxConstants, RSyntaxTextArea}
import org.fife.ui.rtextarea.RTextScrollPane
import java.io.{FileWriter, File}

class Editor {

    private val viewTextArea = new RSyntaxTextArea(25, 80) {
        setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA)
    }

    private val viewScrollPane = new RTextScrollPane(viewTextArea)

    private var sourceFileOption: Option[File] = None

    // Initialize editor
    reset()


    /**
     * Return a component displaying content of this editor
     */
    def view: JComponent = {
        viewScrollPane
    }


    /**
     * Return a text content of this editor
     */
    def text: String = {
        viewTextArea.getText
    }

    def selection: String = {
        viewTextArea.getSelectedText
    }


    def needsSave: Boolean = {
        // TODO: detect changes and return 'true' only of document was modified
        true
    }


    /**
     * Associated file from which the file was loaded or saved last time.
     */
    def sourceFile = sourceFileOption


    def reset() {
        viewTextArea.setText("import ij._\n" +
                "val img = WindowManager.getCurrentImage()")
        sourceFileOption = None
    }

    def read(file: File) {
        val source = scala.io.Source.fromFile(file)
        val lines = source.mkString
        source.close()

        viewTextArea.setText(lines)
        sourceFileOption = Some(file)
    }


    def save(file: File) {
        sourceFileOption = Some(file)
        val writer = new FileWriter(file)
        try {
            writer.write(text)
        } finally {
            writer.close()
        }
    }

}