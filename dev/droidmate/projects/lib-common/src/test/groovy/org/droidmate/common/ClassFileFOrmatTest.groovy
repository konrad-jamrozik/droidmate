// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.util.regex.Matcher

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class ClassFileFormatTest
{

  @Test
  public void "Parses java parameter descriptors"()
  {
    // Act 1
    ClassFileFormat.matchClassFieldDescriptors("")

    List<String> expected = [
      "Z",
      "C",
      "[[D",
      "Landroid/net/Uri;",
      "[Lclass1;",
      "[Lclas/s2s/x;",
      "J",
      "Landroid/content/ContentValues;",
      "[Z",
      "F",
      "S",
      "[V",
      "[[Ljava/lang/String;",
      "Landroid/location/GpsStatus\$Listener\$SubListener;"]

    // Act 2
    List<String> descriptors = ClassFileFormat.matchClassFieldDescriptors(expected.join(""))

    expected.eachWithIndex {String expectedItem, int i ->
      assert expectedItem == descriptors[i]
    }
    assert expected == descriptors
  }

  @Test
  void "Matches java type patterns"()
  {
    Matcher m

    ["boolean",
     "int",
     "some.class.name",
     "java.lang.String[][]",
     "java.util.List<java.util.String[]>[]",
     "org.apache.http.client.ResponseHandler<?_extends_T>",
     "java.util.List<?_extends_Integer[][]>[]"

    ].each {
      // Act
      m = it =~ ClassFileFormat.javaTypePattern
      assert m.matches(): it
    }
    ["intx", "[]<>?"].each {
      // Act
      m = it =~ ClassFileFormat.javaTypePattern
      assert !m.matches(): it
    }
  }


}
