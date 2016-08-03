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

package org.droidmate.common

import java.util.regex.Matcher


class Assert
{

  public static void checkClosureFirstParameterSignature(Closure closure, Class clazz)
  {
    assert closure != null
    assert clazz != null

    def assertFailMsgPrefix = "The supplied closure is expected to operate on ${clazz.simpleName}, " +
      "which is to be given to it as a first parameter."
    assert closure.parameterTypes.size() >= 1: "$assertFailMsgPrefix However, the closure doesn't accept any parameters."
    assert closure.parameterTypes[0].isAssignableFrom(clazz): "$assertFailMsgPrefix However, the type of the first parameter accepted by " +
      "the closure is instead ${closure.parameterTypes[0].simpleName}"

  }

  public static void checkClosureParameterSignatures(Closure closure, Class... classes)
  {
    assert closure != null
    assert classes?.size() > 0

    assert closure.parameterTypes.size() == classes.size(): "The supplied closure is expected to operate on ${classes.size()} " +
      "parameters. However, instead, the closure operates on ${closure.parameterTypes.size()} parameters"

    classes.eachWithIndex {Class clazz, int i ->

      assert closure.parameterTypes[i] == clazz: "The expected class of the parameter of closure having index $i is expected " +
        "to be ${clazz.simpleName}. However, instead it is: ${closure.parameterTypes[i].simpleName}"

    }
  }

  public static void assertRegexMatches(String string, Matcher matcher)
  {
    assert matcher.matches(): "Regex failed to match.\nString:$string\nPattern:${matcher.pattern()}"
  }
}
