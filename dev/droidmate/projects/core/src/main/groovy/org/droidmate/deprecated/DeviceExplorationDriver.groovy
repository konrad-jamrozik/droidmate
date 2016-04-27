// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import com.google.common.annotations.VisibleForTesting
import groovy.util.logging.Slf4j
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.deprecated_still_used.IApkExplorationOutput
import org.droidmate.device.IExplorableAndroidDevice
import org.droidmate.device.datatypes.GuiState
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.TcpServerUnreachableException
import org.droidmate.exploration.actions.ExplorationAction
import org.droidmate.exploration.actions.ResetAppExplorationAction
import org.droidmate.exploration.actions.TerminateExplorationAction
import org.droidmate.exploration.device.IDeviceMessagesReader
import org.droidmate.logcat.ITimeFormattedLogcatMessage
import org.droidmate.uiautomator_daemon.Constants
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.droidmate.common.logging.Markers.exceptions

@Deprecated
@Slf4j
class DeviceExplorationDriver implements IDeviceExplorationDriver
{

  private final IExplorableAndroidDevice                             device
  private final IExplorationActionToVerifiableDeviceActionsTranslator actionsTranslator
  private final IVerifiableDeviceActionsExecutor                      verifiableDeviceActionsExecutor
  private final IDeviceMessagesReader                                 deviceMsgsReader

  private final boolean deployRawApks

  @VisibleForTesting
  final IApkExplorationOutput explorationOutput

  private boolean firstActionWasExecuted = false


  DeviceExplorationDriver(
    IExplorationComponentsFactory componentsFactory,
    boolean deployRawApks,
    IExplorableAndroidDevice device,
    String appPackageName,
    String launchableActivityComponentName,
    IApkExplorationOutput explorationOutput)
  {
    this.device = device
    this.actionsTranslator = componentsFactory.createActionsTranslator(appPackageName, launchableActivityComponentName)
    this.verifiableDeviceActionsExecutor = componentsFactory.createVerifiableDeviceActionsExecutor(device, explorationOutput)
    this.deviceMsgsReader = componentsFactory.createDeviceMessagesReader(device)

    this.deployRawApks = deployRawApks

    this.explorationOutput = explorationOutput

  }

  @Override
  GuiState execute(ExplorationAction explAction)
  {
    assert (!deployRawApks && !firstActionWasExecuted).implies(explAction?.class == ResetAppExplorationAction)

    // This will be a GUI state obtained from the first GUI snapshot after executing parameter exploration action that is valid.
    GuiState currGuiState
    DeviceException executeActionException = null
    try
    {
      //noinspection GroovyUnusedAssignment
      currGuiState = executeAction(explAction)
    }
    catch (DeviceException e)
    {
      executeActionException = e
      throw e
    }
    catch (Exception e)
    {
      log.error("Unexpected exception! e: $e")
      log.error(exceptions, "Unexpected exception!", e)
      throw e
    }
    finally
    {
      try
      {
        boolean readApiLogs = !(explAction instanceof TerminateExplorationAction)
        readLogsFromDevice(readApiLogs)

      } catch (DeviceException e2)
      {
        if (executeActionException != null)
        {
          log.error("Exception was thrown that is about to be suppressed by exception from readLogsFromDevice(). " +
            "The original exception was thrown while executing action $explAction. " +
            "Its message is ${executeActionException}. $LogbackConstants.err_log_msg")

          log.error(exceptions, "An exception was thrown and suppressed by exception from readLogsFromDevice(). " +
            "The exception happened while executing action $explAction. The full exception: \n", executeActionException)
        }

        throw e2
      }
      finally
      {
        if (explorationOutput?.actions?.size() == explorationOutput.apiLogs?.size() + 1)
          explorationOutput.apiLogs << []

        assert explorationOutput?.actions?.size() == explorationOutput?.apiLogs?.size()
      }
    }

    assert currGuiState != null
    return currGuiState
  }

  private GuiState executeAction(ExplorationAction explAction) throws DeviceException
  {
    VerifiableDeviceActions verifiableDeviceActions

    verifiableDeviceActions = actionsTranslator.translate(explAction)

    log.trace("Executing and verifying action.")
    def stateOfLastExecutedAndVerifiedGuiSnapshot = verifiableDeviceActionsExecutor.executeAndVerify(verifiableDeviceActions)
    return stateOfLastExecutedAndVerifiedGuiSnapshot
  }

  private Logger uiadLogger = LoggerFactory.getLogger(LogbackConstants.logger_name_uiad)

  private void readLogsFromDevice(boolean readApiLogs) throws TcpServerUnreachableException, DeviceException
  {
    log.trace("Reading logs from device...")

    if (!deployRawApks && !firstActionWasExecuted)
    {
      explorationOutput.monitorInitTime = deviceMsgsReader.readMonitorMessages()
      explorationOutput.instrumentationMsgs.addAll(deviceMsgsReader.readInstrumentationMessages())

      firstActionWasExecuted = true
    }

    if (readApiLogs)
      explorationOutput.apiLogs << deviceMsgsReader.getAndClearCurrentApiLogsFromMonitorTcpServer()
    else
      explorationOutput.apiLogs << []

    List<ITimeFormattedLogcatMessage> uiaDaemonLogs = device.readLogcatMessages(Constants.uiaDaemon_logcatTag)

    device.clearLogcat()

    uiaDaemonLogs.each {
      if (it.level == "W")
        uiadLogger.warn("${it.messagePayload}")
      else
        uiadLogger.trace("${it.messagePayload}")
    }

    log.trace("DONE reading logs from device.")
  }


}