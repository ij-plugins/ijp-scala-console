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

import javax.swing.JFrame
import swing.SimpleSwingApplication


/**
 * Runs Scala Console as a stand alone application.
 * When the main frame is closed it shuts down the framework and quits the application.
 *
 * @author Jarek Sacha
 * @since 2/11/12
 */
object ScalaConsoleApp extends SimpleSwingApplication {
    private val view = new ScalaConsole().view
    view.peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    def top = view
}
