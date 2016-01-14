// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.storage

import java.nio.file.Path

/**
 * See {@link org.droidmate.storage.Storage2}
 */
public interface IStorage2
{
  void delete(String deletionTargetNameSuffix)

  void serializeToFile(def obj, Path file)

  Collection<Path> getSerializedRuns2()

  Object deserialize(Path serPath)

  void serialize(Object obj, String namePart)
}