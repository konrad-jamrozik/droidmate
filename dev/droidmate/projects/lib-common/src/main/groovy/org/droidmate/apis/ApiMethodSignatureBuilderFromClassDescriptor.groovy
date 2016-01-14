// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.apis

import static org.droidmate.common.ClassFileFormat.convertJNItypeNotationToSourceCode
import static org.droidmate.common.ClassFileFormat.matchClassFieldDescriptors

class ApiMethodSignatureBuilderFromClassDescriptor implements IApiMethodSignatureBuilder
{

  /**
   * Example:
   * <pre>Landroid/content/ContentProviderClient;->update(Landroid/net/Uri;ZLandroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/Object; static</pre>
   */
  private final String descriptor

  ApiMethodSignatureBuilderFromClassDescriptor(String descriptor)
  {
    this.descriptor = descriptor
  }

  @Override
  ApiMethodSignature build()
  {
    def (objectClass, _) = descriptor.split("->")
    objectClass = convertJNItypeNotationToSourceCode(objectClass) // e.g. android.content.ContentProviderClient

    def (String methodHeader, String staticness) = descriptor.tokenize(" ")
    assert staticness in ["static", "instance", null]
    boolean isStatic = staticness == "static"

    /* This line ensures that if there are no params, the match below will match a one-space string to methodParams,
       instead of skipping it and matching returnClass to methodParams.
       The ApiLogcatMessage.matchClassFieldDescriptors then will properly handle the " " methodParams.
    */
    methodHeader = methodHeader.replace("()", "( )")
    // methodName: e.g. update
    // returnClass: e.g. Ljava/lang/Object;
    def (String methodName, String methodParams, String returnClass) = methodHeader.tokenize("()")

    methodName = methodName.split("->")[1]

    methodParams = methodParams != " " ? methodParams : ""

    List<String> paramsList = matchClassFieldDescriptors(methodParams)
    paramsList = paramsList.collect {convertJNItypeNotationToSourceCode(it, /* replaceDollarsWithDots */ true)}
    returnClass = convertJNItypeNotationToSourceCode(returnClass)


    def out = new ApiMethodSignature(objectClass, returnClass, methodName, paramsList, isStatic)
    out.assertValid()
    return out
  }

}
