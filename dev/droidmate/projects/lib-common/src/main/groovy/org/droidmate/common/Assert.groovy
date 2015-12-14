// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

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
