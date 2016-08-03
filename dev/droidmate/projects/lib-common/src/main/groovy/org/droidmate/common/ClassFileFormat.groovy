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

/** See:  http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.2 */
class ClassFileFormat
{

  static final baseTypeToSourceMap = [
    B: "byte",
    C: "char",
    D: "double",
    F: "float",
    I: "int",
    J: "long",
    S: "short",
    Z: "boolean",
    V: "void"
  ]

  /*
  The javaJavaIdentifier stuff is based on:
  http://stackoverflow.com/questions/5205339/regular-expression-matching-fully-qualified-java-classes
  http://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#isJavaIdentifierPart-char-
  */
  public static final  String genericTypeEscape        = "_"
  private static final String addedSymbols             = /[<>\?\[\]$genericTypeEscape]/
  private static final String idStart                  = /(?:\p{javaJavaIdentifierStart}|$addedSymbols)/
  private static final String idParts                  = /(?:\p{javaJavaIdentifierPart}|$addedSymbols)*/
  private static final String javaClassIdPattern       = /(?:$idStart$idParts\.)+$idStart$idParts/
  private static final String javaPrimitiveTypePattern = baseTypeToSourceMap.values().join("|")
  public static final  String javaTypePattern          = /(?:$javaPrimitiveTypePattern|$javaClassIdPattern)(?:\[\])*/

  static List<String> matchClassFieldDescriptors(String classFieldDescriptors)
  {
    // Notation reference: http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.2
    // (?: is a non-capturing group. See http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#special
    String base = baseTypeToSourceMap.keySet().join("|")
    String arrays = /\[*/
    String object = /L(?:\w+\/)*(?:(?:\w|\$)*\w)+;/

    Matcher matcher = classFieldDescriptors =~ /$arrays(?:$base|$object)/

    List<String> out = []
    while (matcher.find())
      out << matcher.group()

    assert out.join("") == classFieldDescriptors
    return out
  }

  static String convertJNItypeNotationToSourceCode(String type, boolean replaceDollarsWithDots = false)
  {
    StringBuilder out = new StringBuilder()
    int arraysCount = type.count("[")
    def typePrim = type.replace("[", "")

    if (typePrim.startsWith("L"))
    {
      assert typePrim.endsWith(";")
      typePrim = typePrim[1..-2].replace("/", ".")
      if (replaceDollarsWithDots)
        typePrim = typePrim.replace("\$", ".")

      out.append(typePrim)
    } else
    {
      assert typePrim.size() == 1
      String baseType = typePrim.find {it in baseTypeToSourceMap.keySet()}
      assert baseType != null
      out.append(baseTypeToSourceMap[baseType])
    }

    arraysCount.times {out.append "[]"}
    return out.toString()
  }

}
