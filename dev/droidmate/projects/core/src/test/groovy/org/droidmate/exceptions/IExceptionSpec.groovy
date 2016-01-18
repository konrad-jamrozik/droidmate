// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

interface IExceptionSpec extends Serializable
{
  boolean matches(String methodName, String packageName, int callIndex)

  boolean getThrowsEx()

  String getPackageName()

  String getMethodName()

  int getCallIndex()

  void throwEx() throws TestDeviceException

  Boolean getExceptionalReturnBool()
}