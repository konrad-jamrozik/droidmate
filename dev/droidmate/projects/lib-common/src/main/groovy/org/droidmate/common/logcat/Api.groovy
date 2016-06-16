// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

// WISH move to org.droidmate.apis. Now it is not done as it would break deserialization. See also: org.droidmate.deprecated_still_used.DeprecatedClassesDeserializer
package org.droidmate.common.logcat

import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import org.droidmate.apis.IApi

import java.util.regex.Matcher

import static org.droidmate.common.Assert.assertRegexMatches

@Slf4j
/**
 * See {@link IApi}
 */
// WISH Api should be a decorator to ApiMethodSignature. WATCH OUT FOR SERIALIZATION ISSUES!
// WISH Api should be renamed to ApiCall. WATCH OUT FOR SERIALIZATION ISSUES!
@Canonical
class Api implements IApi, Serializable
{

  public static final String monitorRedirectionPrefix = "org.droidmate.monitor.Monitor.redir"

  // !!! DUPLICATION WARNING !!! org.droidmate.lib_android.MonitorJavaTemplate.stack_trace_frame_delimiter
  public static final String stack_trace_frame_delimiter = "->";

  private static final long serialVersionUID = 1

  String       objectClass = "fixture.Dummy"
  String       methodName  = "fixture.Dummy.method"
  String       returnClass = "fixture.Dummy"
  List<String> paramTypes  = []
  List<String> paramValues = []
  String       threadId    = "?"
  String       stackTrace  = "N/A"


  private List<String> stackTraceFrames = null

  Api(String objectClass, String methodName, String returnClass, List<String> paramTypes, List<String> paramValues, String threadId, String stackTrace)
  {
    this.objectClass = objectClass
    this.methodName = methodName
    this.returnClass = returnClass
    this.paramTypes = paramTypes
    this.paramValues = paramValues
    this.threadId = threadId
    this.stackTrace = stackTrace
  }

  Api(Map map)
  {
    map.each { k, v ->
      this."$k" = map[k]
    }
  }

  @Override
  List<String> getStackTraceFrames()
  {
    if (stackTraceFrames == null)
      stackTraceFrames = stackTrace.split(stack_trace_frame_delimiter)
    return stackTraceFrames
  }

  /**
   * <p>
   * Examples of a ContentResolver URIs:
   * <pre><code>
   * content://ks.cm.antivirus.config.security
   * content://com.android.launcher2.settings/favorites?notify=false
   * content://com.android.launcher2.settings/favorites?notify=true
   * content://media/external/images/thumbnails/31
   * content://media/external/images/thumbnails/33
   * </code></pre>
   * </p>
   */
  @Override
  String getUniqueString()
  {
    String uriSuffix = ""
    String intentSuffix = ""

    if (objectClass.contains("ContentResolver") && paramTypes.findIndexOf {it == "android.net.Uri"} != -1)
      uriSuffix = " uri: " + parseUri()

    if (paramTypes.findIndexOf {it == "android.content.Intent"} != -1)
      intentSuffix = " intent: " + parseIntent()[0]

    if (methodName == "<init>")
      return "$objectClass: $returnClass $methodName"
    else
      return "$objectClass: $returnClass $methodName(${paramTypes.join(",")})$uriSuffix$intentSuffix"
  }

  private String parseUri()
  {
    assert paramTypes.findIndexValues {it == "android.net.Uri"}.size() == 1

    int uriIndex = paramTypes.findIndexOf {it == "android.net.Uri"}
    String uri = paramValues[uriIndex]

    assert uri.startsWith("content://") || uri.startsWith("android.resource://") || uri.startsWith("file://")

    String[] uriParts = uri.split(/\?/)
    assert uriParts.size() <= 2
    uri = uriParts[0]

    uriParts = uri.split("/")
    if (uriParts.last().isNumber())
      uri = uriParts.take(uriParts.length - 1).join("/") + "/<number>"

    return uri
  }

  /**
   * Parses the sole {@code android.content.Intent} parameter of the API call. The Intent is expected to be in a format as
   * returned by {@code android.content.Intent #toUri(1)}.
   *
   * @return A two-element list: [1. unique string of the intent, 2. the package name of targeted intent's recipient or null]
   */
  private List<String> parseIntent()
  {
    assert paramTypes.findIndexValues {it == "android.content.Intent"}.size() == 1

    int intentIndex = paramTypes.findIndexOf {it == "android.content.Intent"}
    String intent = paramValues[intentIndex]

    if (intent == "null")
      return ["null", null]

    assert intent.contains("#Intent;")
    intent -= "intent:"

    Matcher m = intent =~ /(.*)#Intent;(.*)end/
    assertRegexMatches(intent, m)
    List<String> matchedParts = (m[0] as List)
    assert matchedParts.size() == 3
    matchedParts.remove(0) // Drops the field with the entire matched string.

    String data = "data=" + mergeFileNames(matchedParts[0])

    List<String> attributes = matchedParts[1].split(";")
    attributes = stripExtras(attributes)

    String intentTargetPackageName = extractIntentTargetPackageName(attributes)

    String uniqueString = ([data] + attributes).toString()

    return [uniqueString, intentTargetPackageName]
  }

  private static String mergeFileNames(String intentData)
  {
    Matcher m = intentData =~ $////storage/(.*)/(.+)\.(\w{2,4})/$
    if (m.matches())
    {
      List<String> matchedParts = (m[0] as List)
      assert matchedParts.size() == 4
      String body = matchedParts[1]
      String filename = matchedParts[2]
      String ext = matchedParts[3]
      return "///storage/${body}/<filename>.$ext"
    } else return intentData
  }

  private static String extractIntentTargetPackageName(List<String> intentAttributes)
  {
    List<String> componentAttr = intentAttributes.findAll {it.startsWith("component=")}
    List<String> packageAttr = intentAttributes.findAll {it.startsWith("package=")}
    assert componentAttr.size() <= 1
    assert packageAttr.size() <= 1

    String componentPackage = extractComponentPackage(componentAttr)
    String packagePackage = extractPackagePackage(packageAttr)

    if (componentPackage != null && packagePackage != null)
      assert componentPackage == packagePackage

    return [componentPackage, packagePackage].find()
  }

  private static String extractPackagePackage(ArrayList<String> packageAttr)
  {
    String packagePackage = null
    if (!packageAttr.isEmpty())
    {
      Matcher m = packageAttr[0] =~ /package=(.+)/
      assertRegexMatches(packageAttr[0], m)
      List<String> matchedParts = (m[0] as List)
      assert matchedParts.size() == 2
      packagePackage = matchedParts[1]
    }
    return packagePackage
  }

  private static String extractComponentPackage(ArrayList<String> componentAttr)
  {
    String componentPackage = null
    if (!componentAttr.isEmpty())
    {
      Matcher m = componentAttr[0] =~ /component=(.+)\/(?:.+)/
      assertRegexMatches(componentAttr[0], m)
      List<String> matchedParts = (m[0] as List)
      assert matchedParts.size() == 2
      componentPackage = matchedParts[1]
    }
    return componentPackage
  }

  // WISH would be nice to have this in one of the output files, but first its caller, org.droidmate.exploration.output.DataExtractor.filterApiLogs,
  // has to be refactored out so it is called only once, avoiding duplicate logging.
  @Override
  boolean isCallToStartInternalActivity(String appPackageName)
  {
    if ((paramTypes.findIndexOf {it == "android.content.Intent"} != -1) &&
      (methodName.startsWith("startActivit") || methodName == "startIntentSender"))
    {
      def (String _, String targetPackage) = parseIntent()
      if (targetPackage == appPackageName)
        return true
    }
    return false

  }

  // Implementation based on android.content.Intent#toUriInner
  static List<String> stripExtras(List<String> attributes)
  {
    return attributes.findAll {
      Matcher m = it =~ /(?:S|B|b|c|d|f|i|l|s)\.(.*)=(.*)/
      return !m.matches()
    }
  }
}
