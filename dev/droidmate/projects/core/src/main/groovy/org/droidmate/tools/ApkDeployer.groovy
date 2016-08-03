// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org

package org.droidmate.tools

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.ApkExplorationException
import org.droidmate.android_sdk.IApk
import org.droidmate.common.Assert
import org.droidmate.configuration.Configuration
import org.droidmate.device.IDeployableAndroidDevice
import org.droidmate.exceptions.DeviceException

/**
 * @see IApkDeployer#withDeployedApk(org.droidmate.device.IDeployableAndroidDevice, org.droidmate.android_sdk.IApk, groovy.lang.Closure)
 */
@Slf4j
@TypeChecked
public class ApkDeployer implements IApkDeployer
{

  private final Configuration cfg

  ApkDeployer(Configuration cfg)
  {
    this.cfg = cfg
  }

  /**
   * <p>
   * Deploys the {@code apk} on a {@code device} A(V)D, executes the {@code closure} and undeploys the apk from
   * the {@code device}. Adds any exceptions to the returned collection of exceptions.
   *
   * </p>
   */
  @Override
  public List<ApkExplorationException> withDeployedApk(IDeployableAndroidDevice device, IApk apk, Closure computation)
  {
    log.debug("withDeployedApk(device, $apk.fileName, computation)")

    assert device != null
    Assert.checkClosureFirstParameterSignature(computation, IApk)

    List<ApkExplorationException> apkExplorationExceptions = []
    ApkExplorationException deployApkException = deployApk(device, apk)
    if (deployApkException != null)
    {
      apkExplorationExceptions << deployApkException
      return apkExplorationExceptions
    }

    assert apkExplorationExceptions.empty
    try
    {
      computation(apk)
    }
    catch (Throwable computationThrowable)
    {
      log.warn("! Caught ${computationThrowable.class.simpleName} in withDeployedApk($device, $apk.fileName)->computation(). " +
        "Adding as a cause to an ${ApkExplorationException.class.simpleName}. Then adding to the collected exceptions list.\n" +
        "The ${computationThrowable.class.simpleName}: $computationThrowable")

      apkExplorationExceptions << new ApkExplorationException(apk, computationThrowable)
    }
    finally
    {
      log.debug("Finalizing: withDeployedApk($device, ${apk.fileName}).finally{} for computation($apk.fileName)")
      try
      {
        tryUndeployApk(device, apk)
      }
      catch (Throwable undeployApkThrowable)
      {
        log.warn("! Caught ${undeployApkThrowable.class.simpleName} in withDeployedApk($device, $apk.fileName)->tryUndeployApk(). " +
          "Adding as a cause to an ${ApkExplorationException.class.simpleName}. Then adding to the collected exceptions list.\n" +
          "The ${undeployApkThrowable.class.simpleName}: $undeployApkThrowable")

        apkExplorationExceptions << new ApkExplorationException(apk, undeployApkThrowable, true)
      }
      log.debug("Finalizing DONE: withDeployedApk($device, ${apk.fileName}).finally{} for computation($apk.fileName)")
    }

    log.trace("Undeployed apk $apk.fileName")
    return apkExplorationExceptions
  }

  private ApkExplorationException deployApk(IDeployableAndroidDevice device, IApk apk)
  {
    try
    {
      // Deployment of apk on device will read some information from logcat, so it has to be cleared to ensure the
      // anticipated commands are not matched against logcat messages from  deployments of previously explored apks.
      device.clearLogcat()
      tryReinstallApk(device, apk)

    } catch (Throwable deployThrowable)
    {
      log.warn("! Caught ${deployThrowable.class.simpleName} in deployApk($device, $apk.fileName). " +
        "Adding as a cause to an ${ApkExplorationException.class.simpleName}. Then adding to the collected exceptions list.")
      return new ApkExplorationException(apk, deployThrowable)
    }
    return null
  }

  private void tryUndeployApk(IDeployableAndroidDevice device, IApk apk) throws DeviceException
  {
    if (cfg.uninstallApk)
    {
      if (device.available)
      {
        log.info("Uninstalling $apk.fileName")
        // This method is called in org.droidmate.exploration.actions.RunnableTerminateExplorationAction.performDeviceActions 
        // but exploration might throw exception before that call is made, so here we make another attempt at doing it. 
        device.closeMonitorServers()
        // KNOWN BUG [clear package]/2 times out (120 sec) even though device.available returned true!
        device.clearPackage(apk.packageName)
        device.uninstallApk(apk.packageName, /* ignoreFailure = */ false)
      }
      else
        log.info("Device not available. Skipping uninstalling $apk.fileName")

    } else
    {
      // If the apk is not uninstalled, some of its monitored services might remain, interfering with monitored
      // logcat messages expectations for next explored apk, making DroidMate throw an assertion error.
    }
  }

  private void tryReinstallApk(IDeployableAndroidDevice device, IApk apk) throws DeviceException
  {
    log.info("Reinstalling {}", apk.fileName)
    /* The apk is uninstalled before installation to ensure:
     - any cache will be purged.
     - a different version of the same app can be installed, if necessary (without uninstall, an error will be issued about
     certificates not matching (or something like that))
    */
    device.uninstallApk(apk.packageName, /* ignoreFailure  = */ true)
    device.installApk(apk)
  }

}
