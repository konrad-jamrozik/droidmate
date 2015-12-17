// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.misc

import groovy.transform.Canonical

@Canonical
class Failable<TResult extends Serializable, TException extends Exception> implements Serializable
{

  private static final long serialVersionUID = 1

  TResult    result
  TException exception

  Failable(TResult result, TException exception)
  {
    this.result = result
    this.exception = exception
    assert (this.result == null).implies(this.exception != null)
  }
}