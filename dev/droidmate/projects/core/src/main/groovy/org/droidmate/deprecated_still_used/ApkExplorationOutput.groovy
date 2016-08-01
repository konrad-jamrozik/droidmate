// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import groovy.transform.Canonical
import groovy.util.logging.Slf4j
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.logcat.ITimeFormattedLogcatMessage

import java.time.LocalDateTime

@Slf4j
@Canonical
@Deprecated
class ApkExplorationOutput implements IApkExplorationOutput
{

  private static final long serialVersionUID = 1

  /**
   * Denotes the exploration from which this output was collected completed successfully, i.e. without throwing exception
   * that would terminate the exploration before correctly completing this output.
   */
  boolean completed = false

  String appPackageName

  List<ITimeFormattedLogcatMessage> instrumentationMsgs = []

  List<TimestampedExplorationAction> actions = []

  List<IDeviceGuiSnapshot> guiSnapshots = []

  List<List<IApiLogcatMessage>> apiLogs = []

  Exception caughtException = null

  LocalDateTime monitorInitTime
  LocalDateTime explorationEndTime

  static ApkExplorationOutput create(Map params)
  {
    params.remove("uiaTestCaseName")
    return new ApkExplorationOutput(params)
  }

  @Override
  public String toString()
  {
    return """\
ApkExplorationOutput{
    completed=$completed,
    appPackageName='$appPackageName',
    # of instrumentationMsgs=${instrumentationMsgs.size()},
    # of actions=${actions.size()},
    # of guiSnapshots=${guiSnapshots.size()},
    # of apiLogs=${apiLogs.size()},
    caughtException=$caughtException,
    monitorInitTime=$monitorInitTime,
    explorationEndTime=$explorationEndTime
}"""
  }
}
