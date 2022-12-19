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

package ij_plugins.scala.console

import ij_plugins.scala.console.scripting.Results
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.io.{OutputStream, Writer}

/**
 * @author Jarek Sacha
 */
class InterpreterSpec extends AnyFlatSpec with Matchers {

  private trait Buffer {
    protected lazy val _buffer = new StringBuffer

    def buffer: String = _buffer.toString
  }

  private object bufferWriter extends Writer with Buffer {
    def close(): Unit = {}

    def flush(): Unit = {}

    def write(buf: Array[Char], off: Int, len: Int): Unit = {
      _buffer.append(new String(buf, off, len))
    }
  }

  private class BufferOutputStream extends OutputStream with Buffer {
    override def write(b: Array[Byte], off: Int, len: Int): Unit = {
      _buffer.append(new String(b, off, len))
    }

    def write(b: Int): Unit = {
      write(Array(b.toByte), 0, 1)
    }
  }

  "Interpreter" should "capture output streams" in {

    // Capture output streams
    val outStream = new BufferOutputStream
    val errStream = new BufferOutputStream

    val outString = "Hello"

    val result =
      Console.withOut(outStream) {
        Console.withErr(errStream) {

          // Create interpreter
          val interpreter = IMainFactory.create(bufferWriter)

          // Interpret simple code

          val code = "print(\"" + outString + "\")"
          interpreter.interpret(code)
        }
      }

    // Verify
    result should equal(Results.Success)
    outStream.buffer should equal(outString)
    result should equal(Results.Success)
    errStream.buffer.isEmpty should equal(true)
    bufferWriter.buffer.isEmpty should equal(true)
  }
}
