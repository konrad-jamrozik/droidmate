// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.test_tools.exceptions

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

  void throwEx() throws ITestException
  {
    assert this.exceptionalReturnBool == null
    //noinspection GroovyIfStatementWithIdenticalBranches
    if (this.throwsAssertionError)
      throw new TestAssertionError(this)
    else
      throw new TestDeviceException(this)
  }
}
