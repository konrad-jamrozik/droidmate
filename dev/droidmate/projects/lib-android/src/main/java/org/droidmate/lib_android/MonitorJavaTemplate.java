// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

// org.droidmate.monitor_generator.MonitorSrcTemplate:REMOVE_LINES
package org.droidmate.lib_android;
// org.droidmate.monitor_generator.MonitorSrcTemplate:UNCOMMENT_LINES
// package org.droidmate.monitor_generator.generated;
// org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES

import android.annotation.SuppressLint;
import android.util.Log;
import org.droidmate.common.logcat.Api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

// org.droidmate.monitor_generator.MonitorSrcTemplate:REMOVE_LINES
// org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES

// org.droidmate.monitor_generator.MonitorSrcTemplate:UNCOMMENT_LINES
// import de.uds.infsec.instrumentation.Instrumentation;
// import de.uds.infsec.instrumentation.annotation.Redirect;
// import de.uds.infsec.instrumentation.util.Signature;
// org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES

/**<p>
 * This class will be used by {@code MonitorGenerator} to create {@code Monitor.java} deployed on the device. This class will be
 * first copied by appropriate gradle task of monitor-generator project to its resources dir. Then it will be handled to
 * {@code org.droidmate.monitor_generator.MonitorSrcTemplate} for further processing.
 *
 * </p><p>
 * Note that the final generated version of this file, after running {@code :projects:monitor-generator:build}, will be placed in
 * <pre><code>
 *   [repo root]\dev\droidmate\projects\monitor-generator\monitor-apk-scaffolding\src\org\droidmate\monitor_generator\generated\Monitor.java
 * </code></pre>
 *
 * </p><p>
 * To check if the process of converting this file to a proper {@code Monitor.java} works correctly, see:
 * {@code org.droidmate.monitor_generator.MonitorGeneratorFrontendTest#Generates DroidMate monitor()}.
 *
 * </p><p>
 * Note: The resulting class deployed to the device will be compiled with legacy ant script from Android SDK that supports only
 * Java 5.
 *
 * </p><p>
 *   See also:<br/>
 *     {@code org.droidmate.monitor_generator.MonitorSrcTemplate}<br/>
 *     {@code org.droidmate.monitor_generator.RedirectionsGenerator}
 * </p>
 */
@SuppressLint("NewApi")
@SuppressWarnings("Convert2Diamond")
// !!! DUPLICATION WARNING !!! of class name and location with the build.gradle script of monitor-generator
// org.droidmate.monitor_generator.MonitorSrcTemplate:REMOVE_LINES
public class MonitorJavaTemplate
// org.droidmate.monitor_generator.MonitorSrcTemplate:UNCOMMENT_LINES
// public class Monitor
// org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES
{
  //region Fields

  public static final String tag_init = "Monitor_init";
  public static final String tag_srv  = "Monitor_server";
  public static final String tag_api  = "Monitored_API_method_call";

  public static final String loglevel = "i";

  public static final String msg_ctor_success = "Monitor constructed successfully.";
  public static final String msg_ctor_failure = "Monitor constructed, but failed to start TCP server.";

  // org.droidmate.monitor_generator.MonitorSrcTemplate:REMOVE_LINES
  private static final String stack_trace_frame_delimiter = Api.stack_trace_frame_delimiter;
  // !!! DUPLICATION WARNING !!! org.droidmate.common.logcat.Api.stack_trace_frame_delimiter
  // org.droidmate.monitor_generator.MonitorSrcTemplate:UNCOMMENT_LINES
  // private static final String stack_trace_frame_delimiter = "->";
  // org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES

  /**
   * <p>
   * Example full message:
   * </p><p>
   * {@code Monitor initialized for package org.droidmate.fixtures.apks.monitored}
   * </p>
   */
  public static final String msgPrefix_init_success = "Monitor initialized for package ";

  public static final int srv_port = 59776;

  public static final String srvCmd_connCheck = "connCheck";
  public static final String srvCmd_get_logs  = "getLogs";
  public static final String srvCmd_get_time  = "getTime";
  public static final String srvCmd_close     = "close";


  //endregion

  //region Class init code
  // org.droidmate.monitor_generator.MonitorSrcTemplate:REMOVE_LINES
  public MonitorJavaTemplate()
  // org.droidmate.monitor_generator.MonitorSrcTemplate:UNCOMMENT_LINES
  // public Monitor()
  // org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES
  {
    try
    {
      tryStartMonitorTCPServer();
      Log.i(tag_init, msg_ctor_success);

    } catch (Exception e)
    {
      Log.i(tag_init, msg_ctor_failure);
    }
  }

  public void init(android.content.Context context)
  {
    // org.droidmate.monitor_generator.MonitorSrcTemplate:UNCOMMENT_LINES
    // Instrumentation.processClass(Monitor.class);
    // org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES

    redirectConstructors();

    Log.i(tag_init, msgPrefix_init_success + context.getPackageName());
  }
  //endregion

  //region TCP server code

  private static boolean tryStartMonitorTCPServer() throws Exception
  {
    Log.d(tag_srv, "Starting monitor TCP server...");

    MonitorTCPServer tcpServer = new MonitorTCPServer();

    Thread serverThread;
    try
    {
      serverThread = tcpServer.start(srv_port);
    } catch (Exception e)
    {
      Log.e(tag_srv, "Starting monitor TCP server failed.", e);
      throw e;
    }

    if (serverThread == null) throw new AssertionError();
    if (tcpServer.isClosed()) throw new AssertionError();

    Log.d(tag_srv, "Starting monitor TCP server succeeded.");
    return true;
  }

  static class MonitorTCPServer extends SerializableTCPServerBase<String, ArrayList<ArrayList<String>>>
  {
    private final static String serverClassName = MonitorTCPServer.class.getSimpleName();

    protected MonitorTCPServer()
    {
      super();
    }

    @Override
    protected ArrayList<ArrayList<String>> OnServerRequest(String input)
    {
      synchronized (currentLogs)
      {
        Log.d(serverClassName, "OnServerRequest(" + input + ")");

//        Log.d(serverClassName, "removeSocketInitLogFromMonitorTCPServer(currentLogs);");
//        for (ArrayList<String> currentLog : currentLogs)
//        {
//          Log.d(serverClassName, Arrays.toString(currentLog.toArray()));
//        }

        removeSocketInitLogFromMonitorTCPServer(currentLogs);

        if (Objects.equals(input, srvCmd_connCheck))
        {
          // Do nothing here. The client just wanted to see if a round-trip with server could be established.
          return new ArrayList<ArrayList<String>>();
        } else if (Objects.equals(input, srvCmd_get_logs))
        {
          ArrayList<ArrayList<String>> logsToSend = new ArrayList<ArrayList<String>>(currentLogs);
          currentLogs.clear();

          return logsToSend;

        } else if (Objects.equals(input, srvCmd_get_time))
        {
//          Log.d(serverClassName, "final String time = getNowDate();");
          final String time = getNowDate();
//          Log.d(serverClassName, "final String time = getNowDate();: DONE");

//          Log.d(serverClassName, "final ArrayList<String> payload = new ArrayList<String>(Arrays.asList(time, null, null));");
          final ArrayList<String> payload = new ArrayList<String>(Arrays.asList(time, null, null));

          Log.d(serverClassName, "Sending time: " + time);
          return new ArrayList<ArrayList<String>>(Collections.singletonList(payload));

        } else if (Objects.equals(input, srvCmd_close))
        {
          // Do nothing here. The command will is handled in org.droidmate.lib_android.MonitorJavaTemplate.MonitorTCPServer.shouldCloseServerSocket
          return new ArrayList<ArrayList<String>>();

        } else
        {
          Log.wtf(serverClassName, "Unexpected command from DroidMate TCP client. The command: " + input);
          return new ArrayList<ArrayList<String>>();
        }
      }
    }

    /**
     * <p>
     * Removes calls to {@code Socket.<init>} made by the DroidMate monitor from current set of recorded api logs
     * {@code currentLogs}.
     *
     * </p><p>
     * One of the monitored APIs is {@code Socket.<init>}, and so it is being added to {@code currentLogs} on each invocation.
     * However, this API is also used every time a TCP socket is established when monitor communicates with the host machine.
     * Such socket usage does not belong to the app under monitoring so it should be discarded, which is done in this method.
     *
     * </p>
     * @param currentLogs
     * Currently recorded set of monitored logs, that will have the {@code Socket.<init>} logs caused by monitor removed from it.
     */
    private void removeSocketInitLogFromMonitorTCPServer(List<ArrayList<String>> currentLogs)
    {
      ArrayList<ArrayList<String>> logsToRemove = new ArrayList<ArrayList<String>>();
      for (ArrayList<String> log : currentLogs)
      {
        String msgPayload = log.get(2);
        // !!! DUPLICATION WARNING !!! with org.droidmate.common.logcat.ApiLogcatMessage.ApiLogcatMessagePayload.keyword_stacktrace
        int stacktraceIndex = msgPayload.lastIndexOf("stacktrace: ");

        if (stacktraceIndex == -1)
          throw new AssertionError("The message payload was expected to have a 'stacktrace: ' substring in it");

        String stackTrace = msgPayload.substring(stacktraceIndex);

        String[] frames = stackTrace.split(stack_trace_frame_delimiter);
        if (frames.length >= 2)
        {
          String secondLastFrame = frames[frames.length - 2];
          if (secondLastFrame.startsWith("org.droidmate"))
          {
            if (!secondLastFrame.startsWith("org.droidmate.monitor_generator.generated.Monitor")) throw new AssertionError();
            if (!anyContains(frames, "Socket.<init>")) throw new AssertionError();
            logsToRemove.add(log);
          }
        }
      }

      // Zero logs to remove can happen when the TCP server started to accept socket from client before monitor finished initing.
      // In all other cases there should be one log.
      if (logsToRemove.size() > 1) throw new AssertionError(
        "Expected to remove zero or one logs of Socket.<init>, caused by monitor TCP server. Instead, removed "
          + logsToRemove.size() + " logs.");

      if (logsToRemove.size() == 1)
        currentLogs.remove(logsToRemove.get(0));

    }

    private boolean anyContains(String[] strings, String s)
    {
      for (String string : strings)
      {
        if (string.contains(s))
          return true;
      }
      return false;
    }

    @Override
    protected boolean shouldCloseServerSocket(String serverInput)
    {
      return Objects.equals(serverInput, srvCmd_close);
    }
  }

  // !!! DUPLICATION WARNING !!! with org.droidmate.uiautomatordaemon.SerializableTCPServerBase
  static abstract class SerializableTCPServerBase<ServerInputT extends Serializable, ServerOutputT extends Serializable>
  {

    private int port;
    private ServerSocket    serverSocket          = null;
    private SocketException serverSocketException = null;


    private final static String thisClassName = SerializableTCPServerBase.class.getSimpleName();

    protected SerializableTCPServerBase()
    {
      super();
    }

    protected abstract ServerOutputT OnServerRequest(ServerInputT input);

    protected abstract boolean shouldCloseServerSocket(ServerInputT serverInput);

    public Thread start(int port) throws Exception
    {

      try
      {
        this.port = port;
        MonitorServerRunnable monitorServerRunnable = new MonitorServerRunnable();
        Thread serverThread = new Thread(monitorServerRunnable);

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (monitorServerRunnable)
        {
          if (!(serverSocket == null && serverSocketException == null)) throw new AssertionError();
          serverThread.start();
          monitorServerRunnable.wait();
          if (!(serverSocket != null ^ serverSocketException != null)) throw new AssertionError();
        }

        if (serverSocketException != null)
        {
          throw new Exception(String.format("Failed to start monitor TCP server thread of %s. " +
              "Cause of this exception is the one returned by the failed thread.",
            monitorServerRunnable.runnableClassName),
            serverSocketException);
        }

        return serverThread;

      } catch (InterruptedException e)
      {
        throw e;
      }
    }

    public void closeServerSocket()
    {
      try
      {
        serverSocket.close();
      } catch (IOException e)
      {
        Log.wtf(thisClassName, "Failed to close server socket.");
      }
    }

    public boolean isClosed()
    {
      return serverSocket.isClosed();
    }

    private class MonitorServerRunnable implements Runnable
    {

      public final String runnableClassName = MonitorServerRunnable.class.getSimpleName() + port;

      public void run()
      {

        Log.d(runnableClassName, "Started MonitorServerRunnable.");
        try
        {

          // Synchronize to ensure the parent thread (the one which started this one) will continue only after the
          // serverSocket is initialized.
          synchronized (this)
          {
            Log.d(runnableClassName, String.format("Creating server socket bound to port %s...", port));

            // KNOWN BUG failed cleanup blocks port. See https://hg.st.cs.uni-saarland.de/issues/975
            // KNOWN BUG Double monitor start. See https://hg.st.cs.uni-saarland.de/issues/973
            try
            {
              serverSocket = new ServerSocket(port);
            } catch (SocketException e)
            {
              serverSocketException = e;
            }
            this.notify();
          }

          if (serverSocketException != null)
          {
            Log.d(runnableClassName, String.format("! Failed during startup to bind server socket on port %s. Stopping thread.", port));
            return;
          }

          while (!serverSocket.isClosed())
          {
            Log.d(runnableClassName, String.format("Accepting socket from client on port %s...", port));
            Socket clientSocket = serverSocket.accept();
            Log.d(runnableClassName, "Socket accepted.");

//            Log.v(runnableClassName, "ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());");
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());

          /*
           * Flushing done to prevent client blocking on creation of input stream reading output from this stream. See:
           * org.droidmate.device.SerializableTCPClient.queryServer
           *
           * References:
           * 1. http://stackoverflow.com/questions/8088557/getinputstream-blocks
           * 2. Search for: "Note - The ObjectInputStream constructor blocks until" in:
           * http://docs.oracle.com/javase/7/docs/platform/serialization/spec/input.html
           */
            output.flush();

            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
            ServerInputT serverInput;

            try
            {
              @SuppressWarnings("unchecked") // Without this var here, there is no place to put the "unchecked" suppression warning.
                ServerInputT localVarForSuppressionAnnotation = (ServerInputT) input.readObject();
              serverInput = localVarForSuppressionAnnotation;

            } catch (Exception e)
            {
              Log.e(runnableClassName, "Exception was thrown while reading input sent to MonitorServerRunnable from " +
                "client through socket.", e);
              closeServerSocket();
              break;
            }

            ServerOutputT serverOutput;
            serverOutput = OnServerRequest(serverInput);
//            Log.v(runnableClassName, "output.writeObject(serverOutput);");
            output.writeObject(serverOutput);
//            Log.v(runnableClassName, "clientSocket.close();");
            clientSocket.close();

            if (shouldCloseServerSocket(serverInput))
              closeServerSocket();
          }

          Log.d(runnableClassName, "Closed MonitorServerRunnable.");

        } catch (SocketTimeoutException e)
        {
          Log.e(runnableClassName, "Closing MonitorServerRunnable due to a timeout.", e);
          closeServerSocket();
        } catch (IOException e)
        {
          Log.e(runnableClassName, "Exception was thrown while operating MonitorServerRunnable", e);
        }
      }

    }
  }
  //endregion

  //region Helper code
  private static ArrayList<Integer> ctorHandles = new ArrayList<Integer>();

  private static String getStackTrace()
  {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < stackTrace.length; i++)
    {
      sb.append(stackTrace[i].toString());
      if (i < stackTrace.length - 1)
        sb.append(stack_trace_frame_delimiter);
    }
    return sb.toString();
  }

  private static long getThreadId()
  {
    return Thread.currentThread().getId();
  }

  private static String convert(Object param)
  {
    if (param == null)
      return "null";

    String paramStr;
    if (param.getClass().isArray())
    {
      StringBuilder sb = new StringBuilder("[");
      boolean first = true;

      for (Object item : (Object[]) param)
      {
        if (first)
          first = false;
        else
          sb.append(", ");

        sb.append(String.format("%s", item));
      }
      sb.append("]");

      paramStr = sb.toString();
    } else if (param instanceof android.content.Intent)
    {
      paramStr = ((android.content.Intent) param).toUri(1);
      if (!paramStr.endsWith("end")) throw new AssertionError();

      /*
        Logcat buffer size is 4096 [1]. I have encountered a case in which intent's string extra has eaten up entire log line,
        preventing the remaining parts of the log (in particular, stack trace) to be transferred to DroidMate,
        causing regex match fail. This is how the offending intent value looked like:

          intent:#Intent;action=com.picsart.studio.notification.action;S.extra.result.string=%7B%22response%22%3A%5B%7B%...
          ...<and_so_on_until_entire_line_buffer_was_eaten>

        [1] http://stackoverflow.com/questions/6321555/what-is-the-size-limit-for-logcat
      */
      if (paramStr.length() > 1024)
      {
        paramStr = paramStr.substring(0, 1024 - 24) + "_TRUNCATED_TO_1000_CHARS" + "end";
      }

    } else
    {
      paramStr = String.format("%s", param);
      if (paramStr.length() > 1024)
      {
        paramStr = paramStr.substring(0, 1024 - 24) + "_TRUNCATED_TO_1000_CHARS";
      }
    }

    // !!! DUPLICATION WARNING !!! with: org.droidmate.logcat.Api.spaceEscapeInParamValue
    // solution would be to provide this method with an generated code injection point.
    // end of duplication warning
    return paramStr.replace(" ", "_");
  }

  public static final  String           monitor_time_formatter_pattern = "yyyy-MM-dd HH:mm:ss.SSS";
  public static final  Locale           monitor_time_formatter_locale  = Locale.ENGLISH;
  private static final SimpleDateFormat monitor_time_formatter         = new SimpleDateFormat(monitor_time_formatter_pattern, monitor_time_formatter_locale);

  /**
   * <p>
   * Called by monitor code to log Android API calls. Calls to this methods are generated in:
   * <pre>
   * org.droidmate.monitor_generator.RedirectionsGenerator#generateCtorCallsAndTargets(java.util.List)
   * org.droidmate.monitor_generator.RedirectionsGenerator#generateMethodTargets(java.util.List)</pre>
   * </p>
   * This method has to be accessed in a synchronized manner to ensure proper access to the {@code currentLogs} list and also
   * to ensure calls to {@code SimpleDateFormat.format(new Date())} return correct results.
   * If there was interleaving between threads, the calls non-deterministically returned invalid dates,
   * which caused {@code LocalDateTime.parse()} on the host machine, called by
   * {@code org.droidmate.exploration.device.ApiLogsReader.extractLogcatMessagesFromTcpMessages()}
   * to fail with exceptions like
   * <pre>java.time.format.DateTimeParseException: Text '2015-08-21 019:15:43.607' could not be parsed at index 13</pre>
   *
   * Examples of two different values returned by two consecutive calls to the faulty method,
   * first bad, second good:
   * <pre>
   * 2015-0008-0021 0019:0015:43.809
   * 2015-08-21 19:15:43.809
   *
   * 2015-08-21 19:015:43.804
   * 2015-08-21 19:15:43.804</pre>
   * More examples of faulty output:
   * <pre>
   *   2015-0008-05 09:24:12.163
   *   2015-0008-19 22:49:50.492
   *   2015-08-21 18:50:047.169
   *   2015-08-21 19:03:25.24
   *   2015-08-28 23:03:28.0453</pre>
   */
  @SuppressWarnings("unused") // See javadoc
  private static void addCurrentLogs(String payload)
  {
    synchronized (currentLogs)
    {
//      Log.v(tag_srv, "addCurrentLogs(" + payload + ")");
      String now = getNowDate();

//      Log.v(tag_srv, "currentLogs.add(new ArrayList<String>(Arrays.asList(getPid(), now, payload)));");
      currentLogs.add(new ArrayList<String>(Arrays.asList(getPid(), now, payload)));

//      Log.v(tag_srv, "addCurrentLogs(" + payload + "): DONE");
    }
  }

  /**
   * @see #getNowDate()
   */
  private static final Date startDate     = new Date();
  /**
   * @see #getNowDate()
   */
  private static final long startNanoTime = System.nanoTime();

  /**
   * <p>
   * We use this more complex solution instead of simple {@code new Date()} because the simple solution uses
   * {@code System.currentTimeMillis()} which is imprecise, as described here:
   * http://stackoverflow.com/questions/2978598/will-sytem-currenttimemillis-always-return-a-value-previous-calls<br/>
   * http://stackoverflow.com/a/2979239/986533
   *
   * </p><p>
   * Instead, we construct Date only once ({@link #startDate}), on startup, remembering also its time offset from last boot
   * ({@link #startNanoTime}) and then we add offset to it in {@code System.nanoTime()},  which is precise.
   *
   * </p>
   */
  private static String getNowDate()
  {
//    Log.v(tag_srv, "final Date nowDate = new Date(startDate.getTime() + (System.nanoTime() - startNanoTime) / 1000000);");
    final Date nowDate = new Date(startDate.getTime() + (System.nanoTime() - startNanoTime) / 1000000);

//    Log.v(tag_srv, "final String formattedDate = monitor_time_formatter.format(nowDate);");
    final String formattedDate = monitor_time_formatter.format(nowDate);

//    Log.v(tag_srv, "return formattedDate;");
    return formattedDate;
  }

  private static String getPid()
  {
    return String.valueOf(android.os.Process.myPid());
  }

  /**
   * <p> Contains API logs gathered by monitor, to be transferred to the host machine when appropriate command is read by the
   * TCP server.
   *
   * </p><p>
   * Each log is a 3 element array obeying following contract:<br/>
   * log[0]: process ID of the log<br/>
   * log[1]: timestamp of the log<br/>
   * log[2]: the payload of the log (method name, parameter values, stack trace, etc.)
   *
   * </p>
   * @see MonitorJavaTemplate#addCurrentLogs(java.lang.String)
   */
  final static List<ArrayList<String>> currentLogs = new ArrayList<ArrayList<String>>();

  //endregion

  //region Generated code

  private static void redirectConstructors()
  {
    // org.droidmate.monitor_generator.MonitorSrcTemplate:REMOVE_LINES
    ClassLoader[] classLoaders = {Thread.currentThread().getContextClassLoader(), MonitorJavaTemplate.class.getClassLoader()};
    // org.droidmate.monitor_generator.MonitorSrcTemplate:UNCOMMENT_LINES
    // ClassLoader[] classLoaders = {Thread.currentThread().getContextClassLoader(), Monitor.class.getClassLoader()};
    // org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES

    // GENERATED_CODE_INJECTION_POINT:CTOR_REDIR_CALLS
  }

  // GENERATED_CODE_INJECTION_POINT:CTOR_REDIR_TARGETS

  // GENERATED_CODE_INJECTION_POINT:METHOD_REDIR_TARGETS

  //endregion


}

