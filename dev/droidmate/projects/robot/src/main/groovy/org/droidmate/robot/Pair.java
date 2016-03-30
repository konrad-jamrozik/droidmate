// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.robot;

public class Pair<X, Y>
{
  private X x;
  private Y y;

  public Pair(X x, Y y)
  {
    this.x = x;
    this.y = y;
  }

  public X getX()
  {
    return x;
  }

  public Y getY()
  {
    return y;
  }

  public void setX(X x)
  {
    this.x = x;
  }

  public void setY(Y y)
  {
    this.y = y;
  }
}
