// Copyright (c) 2012-2016 Saarland University
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
import android.content.Context;
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

  public static final String tag_prefix = "Monitor";
  public static final String tag_init = tag_prefix + "_init";
  public static final String tag_srv  = tag_prefix + "_server";
  public static final String tag_api  = tag_prefix + "_API_method_call";

  public static final String loglevel = "i";

  public static final String msg_ctor_success = "Monitor constructed successfully.";
  public static final String msg_ctor_failure = "Monitor constructed, but failed to start TCP server.";

  private static final String stack_trace_frame_delimiter = Api.stack_trace_frame_delimiter;

  /**
   * <p>
   * Example full message:
   * </p><p>
   * {@code Monitor initialized for package org.droidmate.fixtures.apks.monitored}
   * </p>
   */
  public static final String msgPrefix_init_success = "Monitor initialized for package ";

  // KJA next: this and other constants are to be moved to new class MonitorConstants living in lib-common.
  // KJA next: check if lib-common-android really has to be build with java 6.
  // WISH known limitation: if running droidmate with multiple devices, each will have the same set of server ports.
  // Suggested fix: make monitor.java read the port number from a .txt file; deploy different .txt file to each device.
  public static final List<Integer> serverPorts = Arrays.asList(59701,59702,59703,59704);

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
      server = startMonitorTCPServer();
      Log.i(tag_init, msg_ctor_success);

    } catch (Throwable e)
    {
      Log.i(tag_init, msg_ctor_failure);
    }
  }

  private static MonitorTCPServer server;

  public void init(android.content.Context context)
  {
    if (server == null)
      Log.i(tag_srv, "Init: Didn't set context: MonitorTCPServer is null");
    else
      server.context = context;

    // org.droidmate.monitor_generator.MonitorSrcTemplate:UNCOMMENT_LINES
    // Instrumentation.processClass(Monitor.class);
    // org.droidmate.monitor_generator.MonitorSrcTemplate:KEEP_LINES

    redirectConstructors();

    Log.i(tag_init, msgPrefix_init_success + context.getPackageName());
  }
  //endregion

  //region TCP server code

  @SuppressWarnings("ConstantConditions")
  private static MonitorTCPServer startMonitorTCPServer() throws Throwable
  {
    Log.d(tag_srv, "Starting monitor TCP server...");

    MonitorTCPServer tcpServer = new MonitorTCPServer();

    Thread serverThread = null;
    Integer portUsed = null;

    final Iterator<Integer> portsIterator = serverPorts.iterator();
    try
    {
      while (portsIterator.hasNext() && serverThread == null)
      {
        int port = portsIterator.next();
        serverThread = tcpServer.start(port);
        if (serverThread != null)
          portUsed = port;
      }
      if (serverThread == null)
      {
        if (portsIterator.hasNext()) throw new AssertionError();
        throw new Exception("Tried to start TCP server using all available ports. None worked.");
      }

    } catch (Throwable t)
    {
      Log.e(tag_srv, "Starting monitor TCP server failed.", t);
      throw t;
    }

    if (serverThread == null) throw new AssertionError();
    if (portUsed == null) throw new AssertionError();
    if (tcpServer.isClosed()) throw new AssertionError();

    Log.d(tag_srv, "Starting monitor TCP server succeeded. Port used: " + portUsed + " PID: " + getPid());
    return tcpServer;
  }

  static class MonitorTCPServer extends SerializableTCPServerBase<String, ArrayList<ArrayList<String>>>
  {

    public Context context;

    protected MonitorTCPServer()
    {
      super();
    }

    @Override
    protected ArrayList<ArrayList<String>> OnServerRequest(String input)
    {
      synchronized (currentLogs)
      {
        Log.v(tag_srv, "OnServerRequest(" + input + ")");

        removeSocketInitLogFromMonitorTCPServer(currentLogs);


        if (srvCmd_connCheck.equals(input))
        {
          final ArrayList<String> payload = new ArrayList<String>(Arrays.asList(getPid(), getPackageName(), ""));
          return new ArrayList<ArrayList<String>>(Collections.singletonList(payload));

        } else if (srvCmd_get_logs.equals(input))
        {
          ArrayList<ArrayList<String>> logsToSend = new ArrayList<ArrayList<String>>(currentLogs);
          currentLogs.clear();

          return logsToSend;

        } else if (srvCmd_get_time.equals(input))
        {
          final String time = getNowDate();

          final ArrayList<String> payload = new ArrayList<String>(Arrays.asList(time, null, null));

          Log.d(tag_srv, "Sending time: " + time);
          return new ArrayList<ArrayList<String>>(Collections.singletonList(payload));

        } else if (srvCmd_close.equals(input))
        {
          // Do nothing here. The command will is handled in org.droidmate.lib_android.MonitorJavaTemplate.MonitorTCPServer.shouldCloseServerSocket
          return new ArrayList<ArrayList<String>>();

        } else
        {
          Log.e(tag_srv, "Unexpected command from DroidMate TCP client. The command: " + input);
          return new ArrayList<ArrayList<String>>();
        }
      }
    }

    private String getPackageName()
    {
      if (this.context != null)
        return this.context.getPackageName();
      else
        return "package name unavailable: context is null";
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
      return srvCmd_close.equals(serverInput);
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
      this.serverSocket = null;
      this.serverSocketException = null;
      this.port = port;

      MonitorServerRunnable monitorServerRunnable = new MonitorServerRunnable();
      Thread serverThread = new Thread(monitorServerRunnable);
      synchronized (monitorServerRunnable)
      {
        if (!(serverSocket == null && serverSocketException == null)) throw new AssertionError();
        serverThread.start();
        monitorServerRunnable.wait();
        //noinspection SimplifiableBooleanExpression
        if (!(serverSocket != null ^ serverSocketException != null)) throw new AssertionError();
      }
      if (serverSocketException != null)
      {

        if ("bind failed: EADDRINUSE (Address already in use)".equals(serverSocketException.getCause().getMessage()))
        {
          Log.d(tag_srv, "Failed to start TCP server because 'bind failed: EADDRINUSE (Address already in use)'. " +
            "Returning null Thread.");

          return null;

        } else
        {
          throw new Exception(String.format("Failed to start monitor TCP server thread for port %s. " +
              "Cause of this exception is the one returned by the failed thread.", port),
            serverSocketException);
        }
      }
      return serverThread;
    }

    public void closeServerSocket()
    {
      try
      {
        serverSocket.close();
      } catch (IOException e)
      {
        Log.e(thisClassName, "Failed to close server socket.");
      }
    }

    public boolean isClosed()
    {
      return serverSocket.isClosed();
    }

    private class MonitorServerRunnable implements Runnable
    {


      public void run()
      {

        Log.v(tag_srv, "MonitorServerRunnable.run() using "+port);
        try
        {

          // Synchronize to ensure the parent thread (the one which started this one) will continue only after the
          // serverSocket is initialized.
          synchronized (this)
          {
            Log.d(tag_srv, String.format("Creating server socket bound to port %s...", port));

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
            Log.e(tag_srv, String.format("! Failed during startup to bind server socket on port %s. Stopping thread.", port));
            return;
          }

          while (!serverSocket.isClosed())
          {
            Log.v(tag_srv, String.format("Accepting socket from client on port %s...", port));
            Socket clientSocket = serverSocket.accept();
            Log.v(tag_srv, "Socket accepted.");

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
              Log.e(tag_srv, "Exception was thrown while reading input sent to monitor TCP server from " +
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

          Log.d(tag_srv, "Closed monitor TCP server.");

        } catch (SocketTimeoutException e)
        {
          Log.e(tag_srv, "Closing monitor TCP server due to a timeout.", e);
          closeServerSocket();
        } catch (IOException e)
        {
          Log.e(tag_srv, "Exception was thrown while operating monitor TCP server.", e);
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

