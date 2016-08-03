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

package org.droidmate.common

import com.google.common.base.Joiner
import com.google.common.base.Stopwatch
import groovy.util.logging.Slf4j
import org.apache.commons.exec.*
import org.droidmate.common.logging.Markers

import java.util.concurrent.TimeUnit

@Slf4j
public class SysCmdExecutor implements ISysCmdExecutor
{
  /** Timeout for executing system commands, in milliseconds. Zero or negative value means no timeout. */
  // App that often requires more than one minute for "adb start": net.zedge.android_v4.10.2-inlined.apk
  public int sysCmdExecuteTimeout = 1000 * 60 * 2
  private static final int TIMEOUT_REACHED_ZONE = 100

  /*
   * References:
   * http://commons.apache.org/exec/apidocs/index.html
   * http://commons.apache.org/exec/tutorial.html
   * http://blog.sanaulla.info/2010/09/07/execute-external-process-from-within-jvm-using-apache-commons-exec-library/
   */

  /**
   * Executes a cmd line tool with {@code cmdLineParams} as the command line parameters for it.<br/>
   * <br/>
   *
   * @return The captured stdout and stderr of the executed command. The returned 2-cell String array has stdout value
   *         in cell 0 and stderr value in cell 1.
   *
   */
  @Override
  public String[] execute(String commandDescription, String... cmdLineParams) throws SysCmdExecutorException
  {
    return executeWithTimeout(commandDescription, sysCmdExecuteTimeout, cmdLineParams)
  }

  @Override
  public String[] executeWithoutTimeout(String commandDescription, String... cmdLineParams)
    throws SysCmdExecutorException
  {
    return executeWithTimeout(commandDescription, -1, cmdLineParams);
  }


  @Override
  public String[] executeWithTimeout(String commandDescription, int timeout, String... cmdLineParams)
    throws SysCmdExecutorException
  {
    assert cmdLineParams.length >= 1: "At least one command line parameters has to be given, denoting the executable.";

    // If the command string to be executed is a file path to an executable (as opposed to plain command e.g. "java"),
    // then it should be quoted so spaces in it are handled properly.
    cmdLineParams[0] = Utils.quoteIfIsPathToExecutable(cmdLineParams[0]);

    // If a parameter is an absolute path it might contain spaces in it and if yes, the parameter has to be quoted
    // to be properly interpreted.
    String[] quotedCmdLineParamsTail = Utils.quoteAbsolutePaths(cmdLineParams.drop(1))

    // Prepare the command to execute.
    String commandLine = Joiner.on(" ").join([cmdLineParams[0], *quotedCmdLineParamsTail])

    CommandLine command = CommandLine.parse(commandLine)

    // Prepare the process stdout and stderr listeners.
    ByteArrayOutputStream processStdoutStream = new ByteArrayOutputStream()
    ByteArrayOutputStream processStderrStream = new ByteArrayOutputStream()
    PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(processStdoutStream, processStderrStream)

    // Prepare the process executor.
    DefaultExecutor executor = new DefaultExecutor()

    executor.setStreamHandler(pumpStreamHandler)

    if (timeout > 0)
    {
      // Attach the process timeout.
      ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout)
      executor.setWatchdog(watchdog)
    }

    // Only exit value of 0 is allowed for the call to return successfully.
    executor.setExitValue(0)

    log.trace(commandDescription)
    log.trace("Timeout: {} ms", timeout)
    log.trace("Command:")
    log.trace(commandLine)
    log.trace(Markers.osCmd, commandLine)

    Stopwatch executionTimeStopwatch = Stopwatch.createStarted()

    Integer exitValue = null
    try
    {
      exitValue = executor.execute(command)

    } catch (ExecuteException e)
    {
      throw new SysCmdExecutorException(String.format("Failed to execute a system command.\n"
        + "Command: %s\n"
        + "Captured exit value: %d\n"
        + "Execution time: %s\n"
        + "Captured stdout: %s\n"
        + "Captured stderr: %s",
        command.toString(),
        e.getExitValue(),
        getExecutionTimeMsg(executionTimeStopwatch, timeout, e.getExitValue(), commandDescription),
        processStdoutStream.toString() ?: "<stdout is empty>",
        processStderrStream.toString() ?: "<stderr is empty>"),
        e)

    } catch (IOException e)
    {
      throw new SysCmdExecutorException(String.format("Failed to execute a system command.\n"
        + "Command: %s\n"
        + "Captured stdout: %s\n"
        + "Captured stderr: %s", command.toString(), processStdoutStream.toString() ?: "<stdout is empty>",
        processStderrStream.toString() ?: "<stderr is empty>"),
        e)
    } finally
    {
      log.trace("Captured stdout:")
      log.trace(processStdoutStream.toString())

      log.trace("Captured stderr:")
      log.trace(processStderrStream.toString())
    }
    log.trace("Captured exit value: " + exitValue)
    log.trace("DONE executing system command")

    return [processStdoutStream.toString(), processStderrStream.toString()]
  }

  private static String getExecutionTimeMsg(Stopwatch executionTimeStopwatch, int timeout, int exitValue, String commandDescription)
  {
    long mills = executionTimeStopwatch.elapsed(TimeUnit.MILLISECONDS)
    long seconds = executionTimeStopwatch.elapsed(TimeUnit.SECONDS)

    // WISH here instead I could determine if the process was killed by watchdog with
    // org.apache.commons.exec.ExecuteWatchdog.killedProcess
    // For more, see comment of org.apache.commons.exec.ExecuteWatchdog
    if (mills >= (timeout - TIMEOUT_REACHED_ZONE) && mills <= (timeout + TIMEOUT_REACHED_ZONE))
    {
      String returnedString = seconds + " seconds. The execution time was +- ${TIMEOUT_REACHED_ZONE} " +
        "milliseconds of the execution timeout.";

      if (exitValue != 0)
        returnedString += " Reaching the timeout might be the cause of the process returning non-zero value." +
          " Try increasing the timeout (by changing appropriate cmd line parameter) or, if this doesn't help, " +
          "be aware the process might not be terminating at all."

      log.debug("The command with description \"$commandDescription\" executed for $returnedString")

      return returnedString
    }

    return seconds + " seconds"

  }
}

