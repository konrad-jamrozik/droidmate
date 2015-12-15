// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

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

  static pathIn(FileSystem fs)
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
