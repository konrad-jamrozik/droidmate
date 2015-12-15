// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

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
