/*
 * Image/J Plugins
 * Copyright (C) 2002-2014 Jarek Sacha
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

import java.awt.{Dimension, BorderLayout, Color}
import javax.swing._
import swing.Swing
import text.{StyleConstants, StyleContext, StyledDocument}


/**
 * Area where output of the executed code and possible error messages are printed to.
 *
 * @author Jarek Sacha
 * @since 2/10/12
 */
private class OutputArea extends JPanel {


    /**
     * Document style identifiers.
     */
    object Style extends Enumeration {
        val Regular = Value("regular")
        val Error   = Value("error")
        val Log     = Value("log")
        val Code    = Value("code")
    }

    private val outputArea = new JTextPane() {
        setText("")
        setEditable(false)
        setBackground(new Color(255, 255, 218))
        setPreferredSize(new Dimension(100, 100))
    }

    private val scrollPane = new JScrollPane(outputArea)


    setLayout(new BorderLayout())
    add(scrollPane, BorderLayout.CENTER)

    def clear() {
        outputArea.setText("")
    }

    def list(code: String) {
        code.lines.foreach {
            line =>
                appendText("scala> ", Style.Log)
                appendText(line + "\n", Style.Code)
        }
        appendText("\n", Style.Code)
    }

    def appendOutStream(text: String) {
        appendText(text, Style.Regular)
    }

    def appendErrStream(text: String) {
        appendText(text, Style.Error)
    }

    def appendInterpreterOut(text: String) {
        appendText(text, Style.Log)
    }

    private def appendText(text: String, style: Style.Value) {
        Swing.onEDT({
            val doc = outputArea.getStyledDocument
            addStylesToDocument(doc)
            doc.insertString(doc.getLength, text, doc.getStyle(style.toString))
        })
    }

    /**
     * Initialize document styles.
     */
    private def addStylesToDocument(doc: StyledDocument) {
        val default = StyleContext.getDefaultStyleContext.getStyle(StyleContext.DEFAULT_STYLE)

        val regular = doc.addStyle(Style.Regular.toString, default)
        StyleConstants.setFontFamily(default, "Consolas")

        val error = doc.addStyle(Style.Error.toString, regular)
        StyleConstants.setForeground(error, Color.RED)

        val log = doc.addStyle(Style.Log.toString, regular)
        StyleConstants.setForeground(log, Color.GRAY)

        val code = doc.addStyle(Style.Code.toString, regular)
        StyleConstants.setForeground(code, new Color(64, 64, 128))
    }
}
