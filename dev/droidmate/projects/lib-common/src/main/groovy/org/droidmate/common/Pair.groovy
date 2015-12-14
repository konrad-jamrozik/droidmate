// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common

class Pair<T1 extends Serializable, T2 extends Serializable> implements Serializable
{
  private static final long serialVersionUID = 1

  T1 first
  T2 second

  Pair(T1 first, T2 second)
  {
    this.first = first
    this.second = second
  }
}