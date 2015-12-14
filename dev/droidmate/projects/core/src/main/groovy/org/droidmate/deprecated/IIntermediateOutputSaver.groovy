// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.deprecated

import org.droidmate.deprecated_still_used.IApkExplorationOutput

@Deprecated
interface IIntermediateOutputSaver
{

  boolean save(IApkExplorationOutput output)

  void init()
}