// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.apis

/**
 * <p>
 * Monitored Android API method signature.
 * </p>
 */

interface IApi
{

  String getObjectClass()

  String getMethodName()

  String getReturnClass()

  List<String> getParamTypes()

  List<String> getParamValues()

  String getThreadId()

  String getStackTrace()

  List<String> getStackTraceFrames()

  String getUniqueString()

  boolean isCallToStartInternalActivity(String appPackageName)
}
