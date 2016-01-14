// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.apis

@Deprecated
class ApiMapping
{

  @SuppressWarnings("GrFinalVariableAccess")
  final List<ApiMethodSignature> apis

  ApiMapping(
    List<String> jellybeanPublishedApiMapping,
    List<String> jellybeanStaticMethods,
    List<String> appguardLegacyApisInput)
  {
    assert !jellybeanPublishedApiMapping.empty
    assert !jellybeanStaticMethods.empty
    assert !appguardLegacyApisInput.empty

    List<Map<String, String>> staticMethodsInfo = jellybeanStaticMethods.findAll {
      it.size() > 0 && !(it.startsWith("#"))
    }.collect {
      def (objectClass, methodName) = it.tokenize("->")
      return [objectClass: objectClass, methodName: methodName] as Map<String, String>
    }

    List<ApiMethodSignature> pscoutApis = parsePscoutApis(jellybeanPublishedApiMapping, staticMethodsInfo)

    List<ApiMethodSignature> appguardLegacyApis = parseAppguardLegacyApis(appguardLegacyApisInput)

    this.apis = pscoutApis + appguardLegacyApis

    this.apis.each {it.assertValid()}

    pscoutApis.each {def psApi ->
      appguardLegacyApis.each {def appgApi ->
        if (psApi.distinctSignature == appgApi.distinctSignature)
          assert psApi.returnClass == appgApi.returnClass
      }
    }

    this.apis.unique {it.distinctSignature}
    this.apis
      .sort {it.paramClasses.size()}
      .sort {it.methodName}
      .sort {it.objectClass}

    assert !this.apis.empty
  }

  private static List<ApiMethodSignature> parsePscoutApis(List<String> apiMapping, List<Map<String, String>> staticMethodsInfo)
  {
    // methodSignature: e.g. <android.nfc.NfcAdapter: void enableForegroundDispatch(android.app.Activity,android.app.PendingIntent,android.content.IntentFilter[],java.lang.String[][])>
    return apiMapping.findAll {it.startsWith("<")}.collect {String methodSignature ->

      // objectClass: e.g. android.nfc.NfcAdapter
      def (objectClass, methodHeader) = methodSignature[1..-2].tokenize(":")

      methodHeader = (methodHeader as String)[1..-1] // e.g. void enableForegroundDispatch(android.app.Activity,android.app.PendingIntent,android.content.IntentFilter[],java.lang.String[][])

      def (returnClass, methodBody) = methodHeader.tokenize(" ")
      def (methodName, methodParams) = methodBody.tokenize("(")

      // e.g. void
      // The replacement of _ with space is done for generics, e.g. generic "<T> T" is encoded as "<T>_T" to simplify tokenizing, by allowing tokenizing by space without ripping apart returnClass.
      returnClass = returnClass.replace("\$", ".").replace("_", " ")

      methodName = methodName.replace("\$", "_") // e.g. enableForegroundDispatch

      methodParams = methodParams.replace(")", "") // e.g. android.app.Activity,android.app.PendingIntent,android.content.IntentFilter[],java.lang.String[][]
      List<String> paramsList = methodParams.tokenize(",")
      paramsList = paramsList.collectNested {
        it.replace("\$", ".").replace("_", " ")
      } // e.g. [android.app.Activity, android.app.PendingIntent, android.content.IntentFilter[], java.lang.String[][]]

      boolean isStatic = false;
      if (staticMethodsInfo.any {it.objectClass == objectClass && it.methodName == methodName})
        isStatic = true;

      return new ApiMethodSignature(objectClass, returnClass, methodName, paramsList, isStatic)
    }
  }

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
  public static List<ApiMethodSignature> parseAppguardLegacyApis(List<String> legacyApiMapping)
  {
    List<String> processedApiMapping
    processedApiMapping = legacyApiMapping.findAll {it.size() > 0 && !it.startsWith("#")}

    def out = processedApiMapping.collect { ApiMethodSignature.fromDescriptor(it) }
    return out
  }

}
