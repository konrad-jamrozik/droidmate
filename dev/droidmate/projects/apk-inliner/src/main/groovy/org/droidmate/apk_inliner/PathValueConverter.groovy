// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

package org.droidmate.apk_inliner

import joptsimple.ValueConverter

import java.nio.file.FileSystem
import java.nio.file.Path

class PathValueConverter implements ValueConverter<Path>
{

  private final FileSystem fs

  PathValueConverter(FileSystem fs)
  {
    this.fs = fs
  }

  static ValueConverter<Path> pathIn(FileSystem fs)
  {
    return new PathValueConverter(fs)
  }

  /**
   * Converts the given string value into a Java type.
   *
   * @param value the string to convert
   * @return the converted value
   * @throws ValueConversionException if a problem occurs while converting the value
   */
  @Override
  Path convert(String value)
  {
    fs.getPath(value)
  }

  /**
   * Gives the class of the type of values this converter converts to.
   *
   * @return the target class for conversion
   */
  @Override
  Class<? extends Path> valueType()
  {
    return Path
  }

  /**
   * Gives a string that describes the pattern of the values this converter expects, if any.  For example, a date
   * converter can respond with a {@link java.text.SimpleDateFormat date format string}.
   *
   * @return a value pattern, or {@code null} if there's nothing interesting here
   */
  @Override
  String valuePattern()
  {
    return null
  }
}
