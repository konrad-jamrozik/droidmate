// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import groovy.transform.TypeChecked
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.deprecated_still_used.IExplorationOutputCollector
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.exploration.device.IDeviceMessagesReader
import org.droidmate.exploration.strategy.IExplorationStrategy
import org.droidmate.exploration.strategy.IWidgetStrategy

@Deprecated
@TypeChecked
public interface IExplorationComponentsFactory
{
  IExplorationStrategy createStrategy(String appPackageName)

  IWidgetStrategy createWidgetStrategy(String appPackageName)

  IDeviceExplorationDriver createDriver(IExplorableAndroidDevice device,
                                        String appPackageName,
                                        String appLaunchableActivityComponentName,
                                        IApkExplorationOutput explorationOutput)

  IExplorationActionToVerifiableDeviceActionsTranslator createActionsTranslator(
    String appPackageName,
    String appLaunchableActivityComponentName)

  IVerifiableDeviceActionsExecutor createVerifiableDeviceActionsExecutor(IExplorableAndroidDevice device, IApkExplorationOutput explorationOutput)

  IExplorationOutputCollector createExplorationOutputCollector(String appPackageName)

  IDeviceMessagesReader createDeviceMessagesReader(IExplorableAndroidDevice device)
}