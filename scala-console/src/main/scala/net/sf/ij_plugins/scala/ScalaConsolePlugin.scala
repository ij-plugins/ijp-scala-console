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

package net.sf.ij_plugins.scala

import console.ScalaConsoleFrame
import ij.plugin.PlugIn
import java.lang.String
import ij.IJ
import javax.swing.WindowConstants


private object ScalaConsolePlugin {

    ScalaUtils.addPluginsJarsToClassPath()

    var console: Option[ScalaConsoleFrame] = None
}


/**
 * ImageJ plugin for starting Scala Console
 */
class ScalaConsolePlugin extends PlugIn {

    def run(arg: String) {
        ScalaConsolePlugin.console match {
            case None => {
                IJ.showStatus("Starting Scala Console...")
                val frame = new ScalaConsoleFrame()
                frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
                frame.pack()
                frame.setVisible(true)
                ScalaConsolePlugin.console = Some(frame)
                IJ.showStatus("")
            }
            case Some(frame) => frame.setVisible(true)
        }
    }

}