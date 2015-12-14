// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import java.nio.file.Path

/**
 * See {@link Storage}
 */
@Deprecated
public interface IStorage
{
  Writer getWriter(String targetName)

  void delete(String deletionTargetNameSuffix)

  // It is actually used by org.droidmate.deprecated_still_used.Storage.serializeToFile(org.droidmate.deprecated_still_used.IApkExplorationOutput, java.lang.String)
  @SuppressWarnings("GroovyUnusedDeclaration")
  void serialize(IApkExplorationOutput apkExplorationOutput)
  void serialize(IApkExplorationOutput apkExplorationOutput, String nameSuffix)

  ExplorationOutput deserializeAll()

  Collection<Path> getSerializedRuns()

  IApkExplorationOutput deserializeApkExplorationOutput(Path serPath)

  void deleteEmpty()
}