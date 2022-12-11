/*
 * ImageJ Plugins
 * Copyright (C) 2002-2022 Jarek Sacha
 * Author's email: jpsacha at gmail dot com
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
 * Latest release available at https://github.com/ij-plugins
 *
 */

package net.sf.ij_plugins.scala.console.outputarea

import enumeratum.{Enum, EnumEntry}
import org.fxmisc.richtext.InlineCssTextArea
import org.scalafx.extras.mvcfx.ModelFX
import org.scalafx.extras.{onFX, onFXAndWait}

import scala.collection.convert.ImplicitConversions._
import scala.collection.immutable

object OutputAreaModel {
  sealed abstract class Style(override val entryName: String) extends EnumEntry

  /**
   * Document style identifiers.
   */
  object Style extends Enum[Style] {

    val values: immutable.IndexedSeq[Style] = findValues

    case object Regular extends Style("regular")
    case object Error   extends Style("error")
    case object Log     extends Style("log")
    case object Code    extends Style("code")
  }
}

/**
 */
class OutputAreaModel extends ModelFX {

  import OutputAreaModel._

  val CodeColor  = "#404080"
  val LogColor   = "grey"
  val ErrorColor = "red"

  //  val outputText = new StringProperty()
  val codeArea = new InlineCssTextArea()
  codeArea.setStyle("-fx-font-family: monospace; -fx-background-color: #FFFFDA")

  def clear(): Unit = onFXAndWait { codeArea.replaceText(0, codeArea.getLength, "") }

  def list(code: String): Unit = {
    code.lines.toList.foreach {
      line =>
        appendText("scala> ", Style.Log)
        appendText(line + "\n", Style.Code)
    }
    appendText("\n", Style.Code)
  }

  def appendOutStream(text: String): Unit = {
    appendText(text, Style.Regular)
  }

  def appendErrStream(text: String): Unit = {
    appendText(text, Style.Error)
  }

  def appendInterpreterOut(text: String): Unit = {
    appendText(text, Style.Log)
  }

  private def appendText(text: String, style: Style): Unit = {
    val cssStyle: String = "-fx-font-family: monospace;" + {
      style match {
        case Style.Regular => ""
        case Style.Error   => s"-fx-fill: $ErrorColor;"
        case Style.Log     => s"-fx-fill: $LogColor;"
        case Style.Code    => s"-fx-fill: $CodeColor;"
        case _             => ""
      }
    }

    onFX {
      val position = codeArea.getLength
      codeArea.replaceText(position, position, text)
      codeArea.setStyle(position, codeArea.getLength, cssStyle)
    }
  }
}
