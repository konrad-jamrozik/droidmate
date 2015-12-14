// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

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
