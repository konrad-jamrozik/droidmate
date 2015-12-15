// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common

import groovy.transform.Canonical

@Canonical
class LabeledEdge<TSource extends Serializable, TLabel extends Serializable, TTarget extends Serializable> implements Serializable
{

  private static final long serialVersionUID = 1

  TSource source
  TLabel  label
  TTarget target

  LabeledEdge(TSource source, TLabel label, TTarget target)
  {
    this.source = source
    this.label = label
    this.target = target
  }


  @Override
  public String toString()
  {
    return "LabeledEdge{\n" +
      "source = " + source + "\n" +
      "label  = " + label + "\n" +
      "target = " + target + "\n" +
      "} " + super.toString()
  }
}
