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

  final String methodName
  final String currentlyDeployedPackageName
  final int callIndex

  ExceptionSpec(String methodName, String currentlyDeployedPackageName, int callIndex = 1)
  {
    this.methodName = methodName
    this.currentlyDeployedPackageName = currentlyDeployedPackageName
    this.callIndex = callIndex
  }
}
