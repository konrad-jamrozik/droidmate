// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import static org.droidmate.exploration.actions.ExplorationAction.newTerminateExplorationAction

@Deprecated
class ApkExplorationOutputTestHelper
{

  static void assertNonemptyAndValid(IApkExplorationOutput explorationOutput, boolean includeTerminateAction = true, boolean expectApiLogs = true, boolean expectGuiSnapshots = true, boolean expectUiaTestCase = false)
  {
    assert explorationOutput != null

    assert explorationOutput.appPackageName?.length() > 0

    if (expectGuiSnapshots)
    {
      def gss = explorationOutput.guiSnapshots
      assert gss?.size() > 0
      assert gss.every {it.guiState.widgets.size() > 0}
    }

    def eas = explorationOutput.actions
    assert eas?.size() > 0

    if (expectApiLogs)
    {
      assert explorationOutput.monitorInitTime != null
      assert explorationOutput.apiLogs.flatten().size() > 0
    }
    else
    {
      assert explorationOutput.monitorInitTime == null
      assert explorationOutput.apiLogs.flatten().size() == 0
    }

    if (includeTerminateAction)
    {
      assert eas.take(eas.size() - 1).every {it != newTerminateExplorationAction()}
      explorationOutput.verifyCompletedDataIntegrity()

    } else
      assert eas.every {it != newTerminateExplorationAction()}

    if (expectUiaTestCase)
    {
      assert explorationOutput.isUiaTestCase
    }
  }
}
