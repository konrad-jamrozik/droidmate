// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import groovy.transform.Canonical

@Deprecated
@Canonical
class UiaTestCaseAnnotations implements IApkExplorationOutput.IUiaTestCaseAnnotations
{
  private static final long serialVersionUID = 1

  String testCaseName

  List<String> comments = []
}
