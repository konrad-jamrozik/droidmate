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

import groovy.transform.Immutable

@Immutable
class ApiMethodSignature
{
  String objectClass
  String returnClass
  String methodName
  List<String> paramClasses
  boolean isStatic

  /**
   * <p>
   * Parsing done according to:
   *
   * </p><p>
   * <code>
   * http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3<br/>
   * </code>
   * </p><p>
   * Additional reference:
   * </p><p>
   * <code>
   * http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html#getName%28%29<br/>
   * http://stackoverflow.com/questions/5085889/l-array-notation-where-does-it-come-from<br/>
   * http://stackoverflow.com/questions/3442090/java-what-is-this-ljava-lang-object<br/>
   * </code>
   * </p>
   */
  static ApiMethodSignature fromDescriptor(String descriptor)
  {
    def builder = new ApiMethodSignatureBuilderFromClassDescriptor(descriptor)
    return builder.build()
  }

  public void assertValid()
  {
    assert objectClass?.size() > 0
    assert returnClass?.size() > 0
    assert methodName?.size() > 0
    assert methodName.startsWith("<").implies(methodName.endsWith(">"))
    assert paramClasses != null
  }

  List getDistinctSignature()
  {
    return [objectClass, methodName, paramClasses]
  }

  String getShortSignature()
  {
    String paramString = paramClasses.size() > 0 ? paramClasses.join(", ") : ""
    return "$objectClass.$methodName($paramString)"
  }

  boolean isConstructor() { return methodName == "<init>" }

  String getObjectClassJni() {
    return convertToJniNotation(objectClass)
  }

  private static String convertToJniNotation(String input)
  {
    if (sourceToBaseTypeMap.containsKey(input))
      return sourceToBaseTypeMap[input]
    else
      return "L" + input.replace(".", "/") + ";"
  }

  String getParamsJni()
  {
    paramClasses.collect { convertToJniNotation(it)}.join("")
  }

  static private final sourceToBaseTypeMap = [
    "byte"    : "B",
    "char"    : "C",
    "double"  : "D",
    "float"   : "F",
    "int"     : "I",
    "long"    : "J",
    "short"   : "S",
    "boolean" : "Z",
    "void"    : "V",
  ]

  @SuppressWarnings("GroovyUnusedDeclaration")
  private static debugPrintln(String methodSignature, String objectClass, String returnClass, String methodName, List<String> paramsList, Boolean isStatic)
  {
    println "signature   $methodSignature"
    println "objectClass $objectClass"
    println "returnClass $returnClass"
    println "methodName  $methodName"
    println "paramsList  $paramsList"
    println "isStatic    $isStatic"
    println ""
  }

}
