// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.frontend

import groovy.util.logging.Slf4j
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.ApkExplorationException
import org.droidmate.exceptions.ApkExplorationExceptionsCollection
import org.droidmate.exceptions.DeviceException
import org.droidmate.exceptions.ThrowablesCollection

import static org.droidmate.common.logging.Markers.exceptions

@Slf4j
class ExceptionHandler implements IExceptionHandler
{
  @Override
  int handle(Throwable e)
  {
    def returnCode = internalHandle(e)
    log.error("$LogbackConstants.err_log_msg")
    return returnCode
  }

  @SuppressWarnings("GroovyUnusedDeclaration") // Actually used, thanks to groovy's dispatch on method param type.
  private static int internalHandle(ApkExplorationExceptionsCollection e)
  {
    logApkExplorationExceptionsCollection(e)
    return 1
  }

  @SuppressWarnings("GroovyUnusedDeclaration") // Actually used, thanks to groovy's dispatch on method param type.
  private static int internalHandle(ThrowablesCollection e)
  {
    logThrowablesCollection(e)
    return 2
  }


  @SuppressWarnings("GroovyUnusedDeclaration") // Actually used, thanks to groovy's dispatch on method param type.
  private static int internalHandle(DeviceException e)
  {
    logDeviceException(e)
    return 3
  }

  private static int internalHandle(Throwable e)
  {
    logThrowable(e)
    return 4
  }


  private static void logApkExplorationExceptionsCollection(ApkExplorationExceptionsCollection e)
  {
    String message = ""
    message += "Exploration exceptions have been thrown during DroidMate run. Count: ${e.exceptions.size()}. \n"
    message += "--- The exceptions list, in format \"offending app file name | the exception\" \n"
    message = e.exceptions.inject(message) {acc, val -> return acc + "${val.instanceName} | ${val.exception}\n"}
    message += "--- End of the list\n"

    log.error("$message")

    log.error(exceptions, message)
    log.error(exceptions, "--- Now the exception collection details will be given:")
    log.error(exceptions, "", e)
    log.error(exceptions, "--- Now the details of the exceptions wrapped in each of the ${ApkExplorationException.simpleName}s in the exceptions collection will be listed:")
    e.exceptions.eachWithIndex {it, int i ->

      assert it.suppressed.size() == 0
      log.error(exceptions, "Exception index: ${i + 1} out of ${e.exceptions.size()}. Offending app file path: $it.apkPath:\n", it.exception)
    }

    log.error(exceptions, "--- The details of all the exploration exceptions have been listed.")

    assert e.suppressed.size() == 0
  }

  private static void logThrowablesCollection(ThrowablesCollection e)
  {
    assert e.throwables.size() == 2
    assert e.throwables[0] instanceof ApkExplorationExceptionsCollection
    assert !(e.throwables[1] instanceof ApkExplorationExceptionsCollection)


    def message = "${e.throwables[0].class.simpleName} as well as ${e.throwables[1].class.simpleName} " +
      "were thrown during DroidMare run. Each of them will now be logged."
    log.error(message)
    log.error(exceptions, message)

    def throwableDelimiter = "========================================"
    log.error(throwableDelimiter)
    log.error(exceptions, throwableDelimiter)
    e.throwables.each {
      internalHandle(it)
      log.error(throwableDelimiter)
      log.error(exceptions, throwableDelimiter)
    }

    assert e.suppressed.size() == 0
  }


  private static void logDeviceException(DeviceException e)
  {
    String message = "A fatal ${DeviceException.simpleName} was thrown during DroidMate run:"

    log.error("$message $e")
    log.error(exceptions, "$message\n", e)

    logSuppressedIfAny(e, 1)
  }

  private static void logThrowable(Throwable e)
  {
    String message = "A fatal ${Throwable.simpleName} was thrown during DroidMate run. If you cannot diagnose and fix the " +
      "problem yourself by inspecting the logs, this might a bug in the code. Sorry!\n" +
      "In such case, please contact the DroidMate developer, Konrad Jamrozik, at jamrozik@st.cs.uni-saarland.de.\n" +
      "Please include the output dir (by default set to ${Configuration.defaultDroidmateOutputDir}).\n" +
      "A cookie for you, brave human.\n"

    log.error("$message$e")
    log.error(exceptions, message, e)

    logSuppressedIfAny(e, 1)
  }

  private static void logSuppressedIfAny(Throwable throwable, int suppressionDepth)
  {
    int suppressedCount = throwable.suppressed.size()
    if (suppressedCount == 0)
      return

    assert suppressedCount > 0

    def msg = "The '$throwable' has ${suppressedCount} suppressed exceptions at suppression depth of $suppressionDepth. " +
      "Each of them will be now logged."
    log.error(msg)
    log.error(exceptions, msg)

    throwable.suppressed.each {

      def suppressedMsg = "Suppression depth: $suppressionDepth | "
      log.error(suppressedMsg + "$it")
      log.error(exceptions, suppressedMsg, it)

      logSuppressedIfAny(it, suppressionDepth + 1)
    }
  }

}
