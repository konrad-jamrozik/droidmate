// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.configuration

import org.droidmate.exceptions.ConfigurationException

class Assert
{
  // WISH DRY-violation very similar method: org.droidmate.common.FileUtils.validateDirectory
  public static void validateDirectory(File dir)
  {
    if (!dir.isDirectory())
      throw new ConfigurationException("Directory $dir doesn't exist or is not a directory. \n" +
        "Expected path: ${dir.absolutePath}");
  }

}
