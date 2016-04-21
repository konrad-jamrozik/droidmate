// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.frontend

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.ApkExplorationException
import org.droidmate.android_sdk.ExplorationException
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.configuration.Configuration
import org.droidmate.exceptions.ThrowablesCollection
import org.droidmate.exceptions.UnexpectedIfElseFallthroughError

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

  private static int internalHandle(Throwable e)
  {
    assert e.suppressed.length == 0

    switch (e)
    {
      case ApkExplorationException:
        logApkExplorationException(e as ApkExplorationException)
        return 1

      case ExplorationException:
        logExplorationException(e as ExplorationException)
        return 2

      case ThrowablesCollection:
        logThrowablesCollection(e as ThrowablesCollection)
        return 3

      case Throwable:
        logThrowable(e)
        return 4

      default:
        throw new UnexpectedIfElseFallthroughError()
    }
  }

  private static void logApkExplorationException(ApkExplorationException e)
  {
    String message = "An ${e.class.simpleName} was thrown during DroidMate run, pertaining to ${e.apk.fileName}:"

    log.error("$message $e")
    log.error(exceptions, "$message\n", e)
  }

  private static void logExplorationException(ExplorationException e)
  {
    String message = "An ${e.class.simpleName} was thrown during DroidMate run:"

    log.error("$message $e")
    log.error(exceptions, "$message\n", e)
  }

  private static void logThrowablesCollection(ThrowablesCollection e)
  {
    assert !(e.throwables.empty)
    assert e.cause == null
    assert e.suppressed.length == 0

    assert e.throwables.every {it instanceof ExplorationException}

    def message = "A nonempty ${e.class.simpleName} was thrown during DroidMate run. " +
      "Each of the ${e.throwables.size()} ${Throwable.simpleName}s will now be logged."
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
  }

  private static void logThrowable(Throwable e)
  {
    String message = "An unhandled exception of ${e.class.simpleName} was thrown during DroidMate run. If you cannot diagnose " +
      "and fix the problem yourself by inspecting the logs, this might a bug in the code. Sorry!\n" +
      "In such case, please contact the DroidMate developer, Konrad Jamrozik, at jamrozik@st.cs.uni-saarland.de.\n" +
      "Please include the output dir (by default set to ${Configuration.defaultDroidmateOutputDir}).\n" +
      "A cookie for you, brave human.\n"

    log.error("$message$e")
    log.error(exceptions, message, e)

  }


}
