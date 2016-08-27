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

package net.sf.ij_plugins.scala.console

import java.io.{OutputStream, Writer}

import org.junit.Assert._
import org.junit._

import scala.tools.nsc.interpreter.{IMain, Results}
import scala.tools.nsc.{NewLinePrintWriter, Settings}

/**
 * @author Jarek Sacha
 * @since 2/10/12
 */
class InterpreterTest {

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

    @Test
    def printHelloTest(): Unit = {

        val interpreterSettings = new Settings() {
            usejavacp.value = true
            //            classpath.value +=
        }

        // Capture output streams
        val defaultOut = Console.out
        val outStream = new BufferOutputStream
        Console.setOut(outStream)
        val defaultErr = Console.err
        val errStream = new BufferOutputStream
        Console.setErr(errStream)

        // Create interpreter
        val interpreter = new IMain(interpreterSettings, new NewLinePrintWriter(bufferWriter, true))

        // Interpret simple code
        val outString = "Hello"
        val code = "print(\"" + outString + "\")"
        val result = interpreter.interpret(code)

        // Restore default streams
        Console.setOut(defaultOut)
        Console.setErr(defaultErr)

        // Verify
        assertEquals(Results.Success, result)
        assertEquals(outString, outStream.buffer)
        assertTrue(errStream.buffer.isEmpty)
        assertTrue(bufferWriter.buffer.isEmpty)
    }

}
