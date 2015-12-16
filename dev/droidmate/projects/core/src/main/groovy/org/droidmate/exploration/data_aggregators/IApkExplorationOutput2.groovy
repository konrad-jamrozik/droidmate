// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.data_aggregators

import org.droidmate.android_sdk.IApk
import org.droidmate.device.datatypes.IDeviceGuiSnapshot
import org.droidmate.exceptions.DeviceException
import org.droidmate.exploration.actions.IExplorationActionRunResult
import org.droidmate.exploration.actions.IRunnableExplorationAction
import org.droidmate.exploration.actions.RunnableExplorationActionWithResult
import org.droidmate.logcat.IApiLogcatMessage
import org.droidmate.storage.IStorage2

import java.time.LocalDateTime

interface IApkExplorationOutput2 extends Serializable
{

  void add(IRunnableExplorationAction action, IExplorationActionRunResult result)

  LocalDateTime getExplorationStartTime()

  LocalDateTime getExplorationEndTime()

  void setExplorationEndTime(LocalDateTime time)

  List<RunnableExplorationActionWithResult> getActRess()

  void verify()

  boolean getNoException()

  DeviceException getException()

  IApk getApk()

  String getPackageName()

  List<List<IApiLogcatMessage>> getApiLogs()

  List<IDeviceGuiSnapshot> getGuiSnapshots()

  Integer getExplorationTimeInMs()

  boolean getContainsExplorationStartTime()

  void serialize(IStorage2 storage2)
}