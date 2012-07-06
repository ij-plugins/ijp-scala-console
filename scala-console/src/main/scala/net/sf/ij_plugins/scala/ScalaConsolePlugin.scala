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

package net.sf.ij_plugins.scala

import console.ScalaConsole
import ij.plugin.PlugIn
import java.lang.String
import ij.IJ
import java.io.File


private object ScalaConsolePlugin {

    lazy val scalaConsole: ScalaConsole = {
        IJ.showStatus("Starting Scala Console...")
        console.addPluginsJarsToClassPath()
        val r = new ScalaConsole()
        IJ.showStatus("")
        r
    }
}


/**
 * ImageJ plugin for starting Scala Console
 */
class ScalaConsolePlugin extends PlugIn {

    def run(arg: String) {
        val scripFile = if (arg != null && !arg.isEmpty) {
            val file = new File(arg.trim)
            if (file.exists) Some(file) else None
        } else {
            None
        }

        ScalaConsolePlugin.scalaConsole.view.visible = true
        if (scripFile.isDefined) {
            ScalaConsolePlugin.scalaConsole.loadScriptFile(scripFile.get)
        }
    }

}