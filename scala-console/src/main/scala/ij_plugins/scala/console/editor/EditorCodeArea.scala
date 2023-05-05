/*
 *  ImageJ Plugins
 *  Copyright (C) 2002-2022 Jarek Sacha
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

package ij_plugins.scala.console.editor

import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.model.{RichTextChange, StyleSpans, StyleSpansBuilder}
import org.fxmisc.richtext.{CodeArea, LineNumberFactory}
import scalafx.Includes.*
import scalafx.beans.value.ObservableValue
import scalafx.scene.Parent
import scalafx.scene.control.ScrollPane.ScrollBarPolicy

import java.util
import java.util.Collections
import java.util.function.Consumer
import java.util.regex.Pattern
import scala.compat.java8.FunctionConverters.*

/**
 * Text area that provides Scala syntax highlighting.
 *
 * Example use:
 * {{{
 *   object ScalaKeywordsDemo extends JFXApp {
 *     val editorCodeArea = new EditorCodeArea()
 *     editorCodeArea.text = ...
 *     stage = new JFXApp.PrimaryStage {
 *       title = "Scala Keywords Demo"
 *       scene = new Scene(640, 480) {
 *         root = editorCodeArea.view
 *       }
 *     }
 *   }
 * }}}
 */
class EditorCodeArea {
  private val Keywords: Array[String] = Array[String](
    "abstract",
    "as",
    "case",
    "catch",
    "class",
    "def",
    "derives",
    "do",
    "else",
    "end",
    "enum",
    "export",
    "extends",
    "extension",
    "false",
    "final",
    "finally",
    "for",
    "forSome",
    "if",
    "infix",
    "inline",
    "implicit",
    "import",
    "lazy",
    "match",
    "new",
    "null",
    "object",
    "opaque",
    "open",
    "override",
    "package",
    "private",
    "protected",
    "return",
    "sealed",
    "super",
    "then",
    "this",
    "throw",
    "trait",
    "transparent",
    "try",
    "true",
    "type",
    "using",
    "val",
    "var",
    "while",
    "with",
    "yield",
    "-",
    ":",
    "=",
    "=>",
    "<-",
    "<:",
    "<%",
    ">:",
    "#",
    "@",
    "=>>",
//    "?=>",
//    "|",
//    "*",
//    "+"
  )
  private val KeywordPattern: String   = """\b(""" + Keywords.mkString("|") + """)\b"""
  private val ParenPattern: String     = """\(|\)"""
  private val BracePattern: String     = """\{|\}"""
  private val BracketPattern: String   = """\[|\]"""
  private val SemicolonPattern: String = """\;"""
  private val StringPattern: String    = """"([^"\\]|\\.)*""""
  private val CommentPattern: String   = "//[^\n]*" + "|" + """/\*(.|\R)*?\*/"""
  private val ScalaPattern: Pattern = Pattern.compile(
    "(?<KEYWORD>" + KeywordPattern + ")" + "|(?<PAREN>" + ParenPattern + ")" + "|(?<BRACE>" + BracePattern + ")" +
      "|(?<BRACKET>" + BracketPattern + ")" + "|(?<SEMICOLON>" + SemicolonPattern + ")" + "|(?<STRING>" +
      StringPattern + ")" + "|(?<COMMENT>" + CommentPattern + ")"
  )

  private val codeArea = new CodeArea()
  codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea))
  codeArea.stylesheets += this.getClass.getResource("scala-keywords.css").toExternalForm

  private val filterOp: RichTextChange[util.Collection[String], String, util.Collection[String]] => Boolean =
    ch => !ch.getInserted.equals(ch.getRemoved)

  codeArea.richChanges.filter(asJavaPredicate(filterOp)).subscribe(
    (t: RichTextChange[util.Collection[String], String, util.Collection[String]]) => {
      codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()))
    }
  )

  private val _view = new VirtualizedScrollPane(codeArea, ScrollBarPolicy.AsNeeded, ScrollBarPolicy.Always)

  def view: Parent = _view

  val text: ObservableValue[String, String] = codeArea.textProperty

  def replaceText(text: String): Unit = codeArea.replaceText(text)

  def selectedText: String = codeArea.getSelectedText

  private def computeHighlighting(text: String): StyleSpans[util.Collection[String]] = {
    val matcher        = ScalaPattern.matcher(text)
    var lastKwEnd: Int = 0
    val spansBuilder   = new StyleSpansBuilder[util.Collection[String]]
    while (matcher.find) {
      val styleClass: String = if (matcher.group("KEYWORD") != null) "keyword"
      else if (matcher.group("PAREN") != null) "paren"
      else if (matcher.group("BRACE") != null) "brace"
      else if (matcher.group("BRACKET") != null) "bracket"
      else if (matcher.group("SEMICOLON") != null) "semicolon"
      else if (matcher.group("STRING") != null) "string"
      else if (matcher.group("COMMENT") != null) "comment"
      else null /* never happens */
      assert(styleClass != null)
      spansBuilder.add(Collections.emptyList(), matcher.start - lastKwEnd)
      spansBuilder.add(Collections.singleton(styleClass), matcher.end - matcher.start)
      lastKwEnd = matcher.end
    }
    spansBuilder.add(Collections.emptyList(), text.length - lastKwEnd)
    spansBuilder.create
  }
}
