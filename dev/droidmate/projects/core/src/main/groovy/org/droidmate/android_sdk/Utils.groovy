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

package org.droidmate.android_sdk

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.lang3.text.WordUtils

import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Matcher

public class Utils
{

  public static String enumNameToArgName(String enumName)
  {
    // example: DISPLAY_HELP --> DisplayHelp
    String output = WordUtils.capitalizeFully(enumName, ['_'] as char[]).replaceAll("_", "");
    // example: DisplayHelp -> displayHelp
    // "displayHelp" is the returned value.
    return StringUtils.uncapitalize(output);
  }


  // WISH DRY-violation with Utils in lib-common
  public static String quoteIfIsPathToExecutable(String path)
  {
    if (SystemUtils.IS_OS_WINDOWS)
    {
      if (Files.isExecutable(Paths.get(path)))
        return '"' + path + '"';
      else
        return path;
    } else
    {
      return path;
    }
  }

  public static String osPath(File file)
  {
    return file.path.replace("/", File.separator)
  }

  public static String getAndValidateFirstMatch(Matcher matcher)
  {
    String firstMatch = matcher[0][1]
    assert firstMatch?.length() > 0
    return firstMatch

  }

}
