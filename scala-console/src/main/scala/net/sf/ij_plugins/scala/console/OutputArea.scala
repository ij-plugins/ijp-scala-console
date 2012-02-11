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

import java.awt.{BorderLayout, Color}
import javax.swing.{JPanel, JTextArea}
import java.util.concurrent.locks.ReentrantLock
import swing.Swing
import java.io.{Writer, OutputStream}


/**
 * Area where output of the executed code and possible error messages are printed to.
 *
 * @author Jarek Sacha
 * @since 2/10/12
 */
class OutputArea extends JPanel {

    /**
     * Redirects standard console output stream the output area. Usage:
     */
    lazy val consoleOut: OutputStream = new LogOutputStream()

    /**
     * Redirects standard console error stream the output area. Usage:
     }*/
    lazy val consoleErr: OutputStream = new LogOutputStream()

    /**
     * Used for reporting internal interpreter messages. Usage:
     */
    lazy val interpreterOut: Writer = new LogWriter()

    private val outputLock = new ReentrantLock(true)

    private val outputArea = new JTextArea(10, 80) {
        setText("")
        setEditable(false)
        setBackground(new Color(255, 255, 218))
    }

    setLayout(new BorderLayout())
    add(outputArea, BorderLayout.CENTER)


    /**
     * Append text to the output area.
     */
    def appendText(text: String) {
        Swing.onEDT({
            //            try {
            //                outputLock.lock()
            outputArea.setText(outputArea.getText + text)
            //            } finally {
            //                outputLock.unlock()
            //            }
        })
    }

    private class LogOutputStream extends OutputStream {
        override def write(b: Array[Byte], off: Int, len: Int) {
            appendText(new String(b, off, len))
        }

        def write(b: Int) {
            write(Array(b.toByte), 0, 1)
        }
    }

    private class LogWriter extends Writer {
        def close() {}

        def flush() {}

        def write(buf: Array[Char], off: Int, len: Int) {
            appendText(new String(buf, off, len))
        }
    }

}
