// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
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

  public static void wait8seconds()
  {
    println "waiting 8 seconds"
    sleep(1000)
    println "7"
    sleep(1000)
    println "6"
    sleep(1000)
    println "5"
    sleep(1000)
    println "4"
    sleep(1000)
    println "3"
    sleep(1000)
    println "2"
    sleep(1000)
    println "1"
    sleep(1000)
    println "continue"
  }
}
