// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used
// KJA to remove next
@Deprecated
public interface IStorage
{
  void delete(String deletionTargetNameSuffix)

  void serialize(IApkExplorationOutput apkExplorationOutput, String nameSuffix)

}