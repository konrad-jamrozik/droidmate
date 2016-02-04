// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor_generator

import org.droidmate.apis.ApiMethodSignature
import org.droidmate.common.logcat.Api
import org.droidmate.common.logcat.ApiLogcatMessage
import org.droidmate.lib_android.MonitorJavaTemplate

class RedirectionsGenerator implements IRedirectionsGenerator
{

  private static final nl   = System.lineSeparator()
  private static final ind6 = "      "
  private static final ind4 = "    "

  private static String redirMethodNamePrefix = "redir_";
  private static String redirMethodDefPrefix = "Lorg/droidmate/monitor_generator/generated/Monitor;->$redirMethodNamePrefix";

  private static Map<Integer, String> ctorRedirNames = [:]

  @Override
  List<String> generateCtorCallsAndTargets(List<ApiMethodSignature> signatures)
  {
    StringBuilder calls = new StringBuilder()
    StringBuilder targets = new StringBuilder()
    signatures.findAll {it.isConstructor()}.eachWithIndex {ApiMethodSignature ams, int id ->

      ams.with {

        // --- The generation of Instrumentation.redirectMethod call ---

        String fromId = $/"${ams.objectClassJni}-><init>(${ams.paramsJni})V"/$


        String objectClassAsMethodName = getObjectClassAsMethodName(objectClass)
        ctorRedirNames[id] = "${id}_${objectClassAsMethodName}_ctor${paramClasses.size()}"
        /* We use Object here instead of the proper name because sometimes the class is hidden from public Android API
        and so the generated file couldn't be compiled. The instrumentation still works with Object, though.
        */
        String objectClassJni = "Ljava/lang/Object;" // ams.objectClassJni
        String toId = $/"$redirMethodDefPrefix${ctorRedirNames[id]}($objectClassJni${ams.paramsJni})V"/$

        calls << ind6 + "ctorHandles.add(Instrumentation.redirectMethod(" + nl
        calls << ind6 + ind4 + "Signature.fromIdentifier($fromId, classLoaders)," + nl
        calls << ind6 + ind4 + "Signature.fromIdentifier($toId, classLoaders)));" + nl
        calls << ind6 + nl

        // --- The generation of redirected method (target of the .redirectMethod call) ---

        // Items for method signature.

        String objectClassWithDots = getObjectClassWithDots(objectClass)
        List<String> paramVarNames = buildParamVarNames(it)
        String formalParams = buildFormalParams(it, paramVarNames)

        // Items for logcat message payload.

        String stackTraceVarName = "stackTrace"
        String threadIdVarName = "threadId"
        String apiLogcatMessagePayload = buildApiLogcatMessagePayload(it, paramVarNames, threadIdVarName, stackTraceVarName)

        // Items for call to Instrumentation method returning value.

        String commaSeparatedParamVars = buildCommaSeparatedParamVarNames(ams, paramVarNames)

        targets << ind4 + "public static void $redirMethodNamePrefix${ctorRedirNames[id]}($objectClassWithDots _this$formalParams)" + nl
        targets << ind4 + "{" + nl
        targets << ind4 + ind4 + "String $stackTraceVarName = getStackTrace();" + nl
        targets << ind4 + ind4 + "long $threadIdVarName = getThreadId();" + nl
        targets << ind4 + ind4 + "Log.${MonitorJavaTemplate.loglevel}(\"${MonitorJavaTemplate.tag_api}\", \"$apiLogcatMessagePayload\"); " + nl
        targets << ind4 + ind4 + "addCurrentLogs(\"$apiLogcatMessagePayload\");" + nl
        targets << ind4 + ind4 + "Instrumentation.callVoidMethod(ctorHandles.get($id), _this$commaSeparatedParamVars);" + nl
        targets << ind4 + "}" + nl
        targets << ind4 + nl
      }

    }
    return [calls.toString(), targets.toString()]

  }

  private static String getObjectClassWithDots(String objectClass)
  {
    /* We use Object here instead of the proper name because sometimes the class is hidden from public Android API
       and so the generated file couldn't be compiled. The instrumentation still works with Object, though.
      */
    return "Object" //  objectClass.replace("\$", ".")
  }


  @Override
  String generateMethodTargets(List<ApiMethodSignature> signatures)
  {
    return signatures
      .findAll {!it.isConstructor()} // Skip ctors here. They are handled in #generateCtorCallsAndTargets()
      .findAll {!(it.objectClass.startsWith("android.test."))} // For justification, see [1] in dev doc at the end of this method.
      .collect {ApiMethodSignature ams ->

      StringBuilder out = new StringBuilder()

      ams.with {

        // Items for method signature.

        String objectClassAsMethodName = getObjectClassAsMethodName(objectClass)
        String redirMethodName = "$redirMethodNamePrefix${objectClassAsMethodName}_$methodName${paramClasses.size()}"
        String objectClassWithDots = getObjectClassWithDots(objectClass)
        String thisParam = isStatic ? "" : "$objectClassWithDots _this"
        List<String> paramVarNames = buildParamVarNames(it)
        String formalParams = buildFormalParams(it, paramVarNames)

        // Items for logcat message payload.

        String stackTraceVarName = "stackTrace"
        String threadIdVarName = "threadId"
        String apiLogcatMessagePayload = buildApiLogcatMessagePayload(it, paramVarNames, threadIdVarName, stackTraceVarName)

        // Items for call to Instrumentation method returning value.

        String returnStatement = returnClass != "void" ? "return (${degenerify(returnClass)}) " : ""
        String instrCallStatic = isStatic ? "Static" : ""
        String instrCallType = returnClass in instrCallMethodTypeMap.keySet() ? instrCallMethodTypeMap[returnClass] : "Object"
        String thisVarOrClass = isStatic ? "${objectClassWithDots}.class" : "_this"
        String commaSeparatedParamVars = buildCommaSeparatedParamVarNames(ams, paramVarNames)

        out << ind4 + "@Redirect(\"$objectClass->$methodName\") " + nl
        out << ind4 + "public static $returnClass $redirMethodName($thisParam$formalParams)" + nl
        out << ind4 + "{" + nl
        out << ind4 + ind4 + "String $stackTraceVarName = getStackTrace();" + nl
        out << ind4 + ind4 + "long $threadIdVarName = getThreadId();" + nl
        out << ind4 + ind4 + "Log.${MonitorJavaTemplate.loglevel}(\"${MonitorJavaTemplate.tag_api}\", \"$apiLogcatMessagePayload\"); " + nl
        out << ind4 + ind4 + "addCurrentLogs(\"$apiLogcatMessagePayload\");" + nl
        out << ind4 + ind4 + "class \$ {} " + nl
        out << ind4 + ind4 + "${returnStatement}Instrumentation.call${instrCallStatic}${instrCallType}Method(\$.class, ${thisVarOrClass}${commaSeparatedParamVars});" + nl
        out << ind4 + "}" + nl
        out << ind4 + nl
      }

      return out.toString()
    }.join("")
    /*
    [1]:
    Redirection fails on classes from android.test.*
    Snippet of observed exception stack trace:

    (...)
    java.lang.ClassNotFoundException: Didn't find class "android.test.SyncBaseInstrumentation" on path:
    DexPathList[[zip file "/data/local/tmp/monitor.apk"]
    (...)

    */
  }

  private static String getObjectClassAsMethodName(String objectClass)
  {
    return objectClass.replace("\$", "_").replace(".", "_")
  }

  private static List<String> buildParamVarNames(ApiMethodSignature ams)
  {
    return ams.paramClasses.isEmpty() ? [] : (0..ams.paramClasses.size() - 1).collect {"p$it"}
  }

  private static String buildFormalParams(ApiMethodSignature ams, List<String> paramVarNames)
  {
    return ams.paramClasses.isEmpty() ? "" : (ams.isStatic ? "" : ", ") + (0..ams.paramClasses.size() - 1).collect {
      ams.paramClasses[it] + " " + paramVarNames[it]
    }.join(", ")
  }

  private
  static String buildApiLogcatMessagePayload(ApiMethodSignature ams, List<String> paramVarNames, String threadIdVarName, String stackTraceVarName)
  {
    List<String> paramValues = paramVarNames.collect {"convert(${it})"}

    return ApiLogcatMessage.toLogcatMessagePayload(
      new Api(ams.objectClass, ams.methodName, ams.returnClass, ams.paramClasses, paramValues, threadIdVarName, stackTraceVarName),
      /* useVarNames */ true)
  }

  private static String buildCommaSeparatedParamVarNames(ApiMethodSignature ams, List<String> paramVarNames)
  {
    return ams.paramClasses.isEmpty() ? (ams.isStatic ? ", 0" : "") :
      ", " + (0..ams.paramClasses.size() - 1).collect {paramVarNames[it]}.join(", ")
  }

  private static String degenerify(String returnClass)
  {
    // Generic types contain space in their name, e.g. "<T> T".
    if (returnClass.contains(" "))
      return returnClass.dropWhile {it != " "}.drop(1) // Will return only "T" in the above-given example.
    else
      return returnClass // No generics, return type as-is.
  }
  /*
    The generated source will be compiled with java 1.5 which requires this mapping.
    It is compiled with java 1.5 because it is build with the old ant-based android SDK build and java 1.5
    is what the ant build file definition in Android SDK defines.
   */
  static private final instrCallMethodTypeMap = [
    "void"   : "Void",
    "boolean": "Boolean",
    "byte"   : "Byte",
    "char"   : "Character",
    "float"  : "Float",
    "int"    : "Int",
    "long"   : "Long",
    "short"  : "Short",
    "double" : "Double"
  ]


}
