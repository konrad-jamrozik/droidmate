// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.data_aggregators

import groovy.util.logging.Slf4j
import org.droidmate.storage.IStorage2

@Slf4j
class ExplorationOutput2 extends ArrayList<IApkExplorationOutput2>
{
  private static final long serialVersionUID = 1

  public static ExplorationOutput2 from(IStorage2 storage)
  {
    return storage.getSerializedRuns2().collect {
      def apkout2 = storage.deserialize(it) as IApkExplorationOutput2
      log.info("Deserialized exploration output of $apkout2.packageName from $it")
      apkout2.verify()
      return apkout2

    } as ExplorationOutput2
  }
}

