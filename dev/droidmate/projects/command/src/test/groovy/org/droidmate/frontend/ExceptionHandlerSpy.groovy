// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.frontend

import org.droidmate.exceptions.ThrowablesCollection

class ExceptionHandlerSpy implements IExceptionHandler
{

  @Delegate
  ExceptionHandler exceptionHandler = new ExceptionHandler()

  Throwable handledThrowable

  @Override
  int handle(Throwable e)
  {
    this.handledThrowable = e
    exceptionHandler.handle(e)
  }

  List<Throwable> getThrowables()
  {
    return (this.handledThrowable as ThrowablesCollection).throwables
  }
}
