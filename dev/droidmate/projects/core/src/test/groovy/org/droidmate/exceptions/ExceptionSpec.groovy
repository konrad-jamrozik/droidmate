// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

import groovy.transform.Canonical

@Canonical
class ExceptionSpec implements IExceptionSpec
{

  private static final long serialVersionUID = 1

  final String  methodName
  final String  packageName
  final int     callIndex
  final boolean throwsEx
  final Boolean exceptionalReturnBool
  final boolean throwsAssertionError

  ExceptionSpec(String methodName, String packageName = null, int callIndex = 1, boolean throwsEx = true, Boolean exceptionalReturnBool = null, boolean throwsAssertionError = false)
  {
    this.methodName = methodName
    this.packageName = packageName
    this.callIndex = callIndex
    this.throwsEx = throwsEx
    this.exceptionalReturnBool = exceptionalReturnBool
    this.throwsAssertionError = throwsAssertionError

    assert this.throwsEx == (this.exceptionalReturnBool == null)
    assert this.throwsAssertionError.implies(this.throwsEx)
  }

  boolean matches(String methodName, String packageName, int callIndex)
  {
    if (this.methodName == methodName && (this.packageName in [null, packageName]) && this.callIndex == callIndex)
      return true
    return false
  }

  void throwEx() throws TestDeviceException
  {
    assert this.exceptionalReturnBool == null
    if (this.throwsAssertionError)
      throw new AssertionError()
    else
      throw new TestDeviceException(this)
  }
}
