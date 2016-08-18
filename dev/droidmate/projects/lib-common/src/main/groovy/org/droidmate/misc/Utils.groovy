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

package org.droidmate.misc

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.SystemUtils
import org.slf4j.Logger

import java.nio.file.Files
import java.nio.file.Paths

@Slf4j
public class Utils
{

  // WISH DRY-violation with `core` Utils class.
  public static String quoteIfIsPathToExecutable(String path)
  {
    if (SystemUtils.IS_OS_WINDOWS)
    {
      if (Files.isExecutable(Paths.get(path)))
        return '"' + path + '"';
      else
        return path;
    } else
    {
      return path;
    }
  }

  public static String[] quoteAbsolutePaths(String[] stringArray)
  {
    stringArray.eachWithIndex {it, idx ->
      if (new File(it).isAbsolute())
        stringArray[idx] = '"' + it + '"'
    }
    return stringArray
  }

  // WISH make an extension method (on Closure?) and move to github/utilities. The same with this.retryOnFalse()
  public static <T> T retryOnException(Closure<T> target, Class retryableExceptionClass, int attempts, int delay, String targetName) throws Throwable
  {
    assert attempts > 0
    int attemptsLeft = attempts
    Throwable exception = null
    boolean succeeded = false
    T out = null

    while (!succeeded && attemptsLeft > 0)
    {
      try
      {
        out = target.call()
        succeeded = true
        exception = null
      } catch (Throwable e)
      {
        if (retryableExceptionClass.isAssignableFrom(e.class))
        {
          exception = e
          attemptsLeft--

          if (attemptsLeft > 0)
          {
            log.trace("Discarded $e from \"$targetName\". Sleeping for $delay and retrying.")
            sleep(delay)
          }
          else
            log.trace("Discarded $e from \"$targetName\". Giving up.")
        } else
          throw e
      }
    }

    if (succeeded)
    {
      assert exception == null
      return out
    } else
    {
      assert exception != null
      throw exception
    }
  }

  public static Boolean retryOnFalse(Closure<Boolean> target, int attempts, int delay) throws Throwable
  {
    assert attempts > 0
    int attemptsLeft = attempts

    Boolean succeeded = target.call()
    attemptsLeft--
    while (!succeeded && attemptsLeft > 0)
    {
      sleep(delay)
      succeeded = target.call()
      attemptsLeft--
    }

    assert (attemptsLeft > 0).implies(succeeded)
    return succeeded
  }

  @Deprecated
  // Used by old exploration code
  public static boolean attempt(int attempts, Logger log, String description, Closure recover = null, Closure compute)
  {
    int attemptsLeft = attempts
    boolean succeeded = false

    Exception lastException = null
    while (!succeeded && attemptsLeft > 0)
    {
      attemptsLeft--

      String attemptsCounter = "${attempts - attemptsLeft}/$attempts attempt"
      // toremove log.trace("Making $attemptsCounter at '$description'")

      try
      {
        succeeded = compute()
        lastException = null
      }
      catch (Exception e)
      {
        succeeded = false
        lastException = e
      }

      if (!succeeded)
      {
        if (lastException != null && attemptsLeft > 0)
        {
          // toremove log.warn("Got exception from failed $attemptsCounter of executing '$description'. Exception msg: $lastException.message $LogbackConstants.err_log_msg")
          // WISH Log it to a separate, new file, like "attempts" or "soft exceptions" or "non-fatal exceptions" or something. Maybe classify exceptions in multiple levels: attempt fail (e.g. launch main act), expl action fail (e.g. all attempts exhausted), apk expl fail (e.g. no clickable stuff - this is not exception as of right now), entire expl fail (assertion fail)
          // toremove log.error(exceptions, "Got exception from failed $attemptsCounter of executing '$description':\n", lastException)
        }
        if (lastException == null)
        // toremove log.warn("$attemptsCounter at '$description' failed.")

          if (recover != null)
          {
            // toremove log.trace("Recovering from a failed attempt $attemptsCounter at '$description'")
            recover()
          }

        if (attemptsLeft > 0)
          sleep(2000)
      }

    }

    assert succeeded || attemptsLeft == 0
    assert succeeded.implies(lastException == null)

    if (lastException != null)
      throw lastException

    return succeeded
  }
}
