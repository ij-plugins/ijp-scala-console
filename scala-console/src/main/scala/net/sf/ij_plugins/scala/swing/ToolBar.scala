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

package net.sf.ij_plugins.scala.swing

import javax.swing.JToolBar

import scala.swing._

/**
 * A tool bar, wrapper for [[javax.swing.JToolBar]]
 *
 * @author Jarek Sacha
 * @since 2/11/12
 * @see javax.swing.JToolBar
 * @param removeActionText if `true` buttons created from actions that have icons will use only icons.
 */
class ToolBar(removeActionText: Boolean = true,
              insets: Insets = new Insets(2, 4, 2, 4)) extends Component with SequentialContainer.Wrapper {

    override lazy val peer: JToolBar = new JToolBar

    //    def buttons: Seq[Button] = contents.filter(_.isInstanceOf[Button]).map(_.asInstanceOf[Button])

    def +=(a: Action) = contents += new Button(a) {
        if (removeActionText && icon != null) {
            tooltip = text
            text = ""
        }
        margin = insets
    }

    def +=(b: Button) = contents += b

    def addSeparator() {
        peer.addSeparator()
    }
}