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

package org.droidmate.debug

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
