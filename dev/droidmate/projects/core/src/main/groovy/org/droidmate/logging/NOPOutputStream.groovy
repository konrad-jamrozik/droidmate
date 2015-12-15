// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.logging

public class NOPOutputStream extends OutputStream {

  @Override
  public void write(int b) throws IOException {
    // do nothing
  }

}