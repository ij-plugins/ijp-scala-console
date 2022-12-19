ijp-scala-console
=================

IJP Scala Console is simple user interface for executing Scala scripts.

[![Actions Status](https://github.com/ij-plugins/ijp-scala-console/workflows/Scala%20CI/badge.svg)](https://github.com/ij-plugins/ijp-scala-console/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.sf.ij-plugins/ijp-scala-console_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.sf.ij-plugins/ijp-scala-console_2.12)
[![Scaladoc](https://javadoc.io/badge2/net.sf.ij-plugins/ijp-scala-console_2.12/scaladoc.svg)](https://javadoc.io/doc/net.sf.ij-plugins/ijp-scala-console_2.12)

The Scala Console can be run stand-alone, embedded in a desktop application, or [ImageJ] plugin. UI is build with
ScalaFX.

![Screenshot](docs/images/Scala-Console-2_screenshot.png)

ImageJ Script Examples
----------------------

Example of an ImageJ script that get a handle to currently selected image and runs the "Median" filter plugin. If there
is no image an error dialog is shown:

```scala
import ij.IJ._

Option(getImage) match {
  case Some(imp) => run(imp, "Median...", "radius=4")
  case None => noImage()
}
```

Additional example scripts can be found the [examples] directory.

ImageJ Plugin Download
----------------------

Binaries can be downloaded from the [releases] page. Extract the binaries to the ImageJ plugins directory. The plugin
install `Plugins` > `Scripting` > `Scala Console`.

Related Projects
----------------

* [ScalaInterpreterPane] - a Swing component for editing code in the Scala programming language and executing it in an
  interpreter.
* [Scala Scripting](https://github.com/scijava/scripting-scala/) - a library providing a JSR-223-compliant scripting
  plugin for the Scala language, part of [SciJava Script Editor] project. The project support multiple scripting
  languages. Detailed info can be found at [Using the Script Editor](https://imagej.net/scripting/script-editor) wiki.

License
-------

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA


[ImageJ]: http://rsb.info.nih.gov/ij/

[examples]: https://github.com/ij-plugins/ijp-scala-console/tree/main/scala-console/examples

[releases]: https://github.com/ij-plugins/ijp-scala-console/releases

[ScalaInterpreterPane]: https://github.com/Sciss/ScalaInterpreterPane

[SciJava Script Editor]: https://github.com/scijava/script-editor