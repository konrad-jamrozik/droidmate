// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exceptions

public class TestAssertionError extends AssertionError implements ITestException
{
  private static final long serialVersionUID = 1

  final IExceptionSpec exceptionSpec

  public TestAssertionError(IExceptionSpec exceptionSpec)
  {
    super("Test-enforced assertion error. Package name: $exceptionSpec.packageName Method name: $exceptionSpec.methodName Call index: $exceptionSpec.callIndex" as Object)
    this.exceptionSpec = exceptionSpec
  }

}
