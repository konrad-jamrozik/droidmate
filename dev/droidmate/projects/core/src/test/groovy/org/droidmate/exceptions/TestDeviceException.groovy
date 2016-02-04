// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exceptions

public class TestDeviceException extends DeviceException implements ITestException
{

  private static final long serialVersionUID = 1

  final IExceptionSpec exceptionSpec

  public TestDeviceException(IExceptionSpec exceptionSpec)
  {
    super("Test-enforced device exception. Package name: $exceptionSpec.packageName Method name: $exceptionSpec.methodName Call index: $exceptionSpec.callIndex")
    this.exceptionSpec = exceptionSpec
  }
}