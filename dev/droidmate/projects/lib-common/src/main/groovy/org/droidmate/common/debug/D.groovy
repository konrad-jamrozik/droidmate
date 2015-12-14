// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common.debug

class D
{
  // Debug counter
  public static int C = 0

  public static File debugFile = new File("./temp_debug.txt")
  static {
    debugFile.delete()
  }

  public static void e(int dc, Closure c)
  {
    if (dc == C)
      c()
  }

  public static void Dprintln(String debugContent)
  {
    debugFile.append(debugContent + "\n")
    // println debugContent
  }
}
