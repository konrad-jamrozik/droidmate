// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

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
   * the {@code device}.
   *
   * </p><p>
   * Exception handling strategy:
   * Any DeviceException thrown inside this method is given as cause to an ApkExplorationException.
   * All such ApkExplorationExceptions are collected and returned as a list from the method. The method might throw a Throwable
   * instead of returning an ApkExplorationExceptions list in two cases:
   * case 1: computation throws a Throwable and finally block doesn't throw.
   * case 2: computation throws a Throwable and finally block also throws a Throwable.
   * - if a DeviceException occurs before the computation is called, the computation won't be called.
   * - if a Throwable is thrown during computation, what happens with it depends on the result of operations in finally block:
   * -- if finally-block exits cleanly, the Throwable is just rethrown
   * -- if finally-block throws any Throwable, the previous throwable is suppressed by the Throwable from the finally block.
   * Next,the Throwable from the finally-block is thrown.
   */
  @Override
  public List<ApkExplorationException> withDeployedApk(IDeployableAndroidDevice device, IApk apk, Closure<DeviceException> computation)
  {
    log.debug("withDeployedApk(device, $apk.fileName, computation)")

    assert device != null
    Assert.checkClosureFirstParameterSignature(computation, IApk)

    List<ApkExplorationException> apkExplorationExceptions = []

    deployApk(device, apk, apkExplorationExceptions)
    if (!apkExplorationExceptions.empty)
    {
      assert apkExplorationExceptions.size() == 1
      return apkExplorationExceptions
    }

    Throwable savedTryThrowable = null
    try
    {
      def deviceException = computation(apk)
      if (deviceException != null)
        apkExplorationExceptions << new ApkExplorationException(apk, deviceException)

    } catch (Throwable tryThrowable)
    {
      log.debug("! Caught ${tryThrowable.class.simpleName} in withDeployedApk.computation(apk). Rethrowing.")
      assert apkExplorationExceptions.empty
      savedTryThrowable = tryThrowable
      throw savedTryThrowable

    } finally
    {
      log.debug("Finalizing: withDeployedApk.finally {} for computation(apk)")
      try
      {
        tryUndeployApk(device, apk)

      } catch (DeviceException e)
      {
        // KJA
        log.debug("! Caught ${e.class.simpleName} in withDeployedApk() in tryTearDown(apk). Adding to apk exploration exceptions list. Also, dding suppressed exception, if any.")
        def explorationEx = new ApkExplorationException(apk, e)

        if (savedTryThrowable != null)
          explorationEx.addSuppressed(explorationEx)

        apkExplorationExceptions << explorationEx
      }
      catch (Throwable tearDownThrowable)
      {
        log.debug("! Caught ${tearDownThrowable.class.simpleName} in tryTearDown(apk). Adding suppressed exception, if any, and rethrowing.")
        // KJA not true, might be one apk expl exception
        assert apkExplorationExceptions.empty
        if (savedTryThrowable != null)
          tearDownThrowable.addSuppressed(savedTryThrowable)
        throw tearDownThrowable
      }
      log.debug("Finalizing DONE: withDeployedApk.finally {} for computation(apk)")
    }

    log.trace("Undeployed apk {}", apk.fileName)
    return apkExplorationExceptions
  }

  private void deployApk(IDeployableAndroidDevice device, IApk apk, List<ApkExplorationException> apkExplorationExceptions)
  {
    try
    {
      // Deployment of apk on device will read some information from logcat, so it has to be cleared to ensure the
      // anticipated commands are not matched against logcat messages from previous deployments.
      device.clearLogcat()
      tryReinstallApk(device, apk)

    } catch (DeviceException e)
    {
      log.debug("! Caught ${e.class.simpleName} in withDeployedApk() pre-computation phase. Adding to apk exploration exceptions list and skipping exploration of ${apk.fileName}}.")
      apkExplorationExceptions << new ApkExplorationException(apk, e)
    }
  }

  private void tryUndeployApk(IDeployableAndroidDevice device, IApk apk) throws DeviceException
  {
    device.clearLogcat() // Do so, so the logcat messages sent from the uninstalled apk won't interfere with the next one.

    if (cfg.uninstallApk)
    {
      log.info("Uninstalling $apk.fileName")
      device.clearPackage(apk.packageName)
      device.uninstallApk(apk.packageName, /* warnAboutFailure = */ true)
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
    device.uninstallApk(apk.packageName, /* warnAboutFailure  = */ false)
    device.installApk(apk)
  }

}
