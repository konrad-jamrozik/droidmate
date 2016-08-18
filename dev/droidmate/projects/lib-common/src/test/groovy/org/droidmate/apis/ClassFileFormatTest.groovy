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

package org.droidmate.apis

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
