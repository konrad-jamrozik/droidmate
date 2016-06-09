// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor_generator.generated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import org.droidmate.MonitorConstants;
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

// org.droidmate.monitor_generator.MonitorSrcTemplate:API_19_UNCOMMENT_LINES
// import de.uds.infsec.instrumentation.Instrumentation;
// import de.uds.infsec.instrumentation.annotation.Redirect;
// import de.uds.infsec.instrumentation.util.Signature;

import de.larma.arthook.*;

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
public class Monitor
{
  //region Class init code
  public Monitor()
  {
    try
    {
      server = startMonitorTCPServer();
      Log.i(MonitorConstants.tag_init, MonitorConstants.msg_ctor_success);

    } catch (Throwable e)
    {
      Log.i(MonitorConstants.tag_init, MonitorConstants.msg_ctor_failure);
    }
  }

  private static MonitorTCPServer server;

  public void init(android.content.Context context)
  {
    if (server == null)
      Log.i(MonitorConstants.tag_srv, "Init: Didn't set context: MonitorTCPServer is null");
    else
      server.context = context;

    // org.droidmate.monitor_generator.MonitorSrcTemplate:API_19_UNCOMMENT_LINES
    // Instrumentation.processClass(Monitor.class);
    
    ArtHook.hook(Monitor.class);

    redirectConstructors();

    Log.i(MonitorConstants.tag_init, MonitorConstants.msgPrefix_init_success + context.getPackageName());
  }
  //endregion

  //region TCP server code

  @SuppressWarnings("ConstantConditions")
  private static MonitorTCPServer startMonitorTCPServer() throws Throwable
  {
    Log.d(MonitorConstants.tag_srv, "Starting monitor TCP server...");

    MonitorTCPServer tcpServer = new MonitorTCPServer();

    Thread serverThread = null;
    Integer portUsed = null;

    final Iterator<Integer> portsIterator = MonitorConstants.serverPorts.iterator();
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
      Log.e(MonitorConstants.tag_srv, "Starting monitor TCP server failed.", t);
      throw t;
    }

    if (serverThread == null) throw new AssertionError();
    if (portUsed == null) throw new AssertionError();
    if (tcpServer.isClosed()) throw new AssertionError();

    Log.d(MonitorConstants.tag_srv, "Starting monitor TCP server succeeded. Port used: " + portUsed + " PID: " + getPid());
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
        Log.v(MonitorConstants.tag_srv, "OnServerRequest(" + input + ")");

        removeSocketInitLogFromMonitorTCPServer(currentLogs);


        if (MonitorConstants.srvCmd_connCheck.equals(input))
        {
          final ArrayList<String> payload = new ArrayList<String>(Arrays.asList(getPid(), getPackageName(), ""));
          return new ArrayList<ArrayList<String>>(Collections.singletonList(payload));

        } else if (MonitorConstants.srvCmd_get_logs.equals(input))
        {
          ArrayList<ArrayList<String>> logsToSend = new ArrayList<ArrayList<String>>(currentLogs);
          currentLogs.clear();

          return logsToSend;

        } else if (MonitorConstants.srvCmd_get_time.equals(input))
        {
          final String time = getNowDate();

          final ArrayList<String> payload = new ArrayList<String>(Arrays.asList(time, null, null));

          Log.d(MonitorConstants.tag_srv, "Sending time: " + time);
          return new ArrayList<ArrayList<String>>(Collections.singletonList(payload));

        } else if (MonitorConstants.srvCmd_close.equals(input))
        {
          // Do nothing here. The command is handled in org.droidmate.monitor_template_src.MonitorJavaTemplate.MonitorTCPServer.shouldCloseServerSocket
          return new ArrayList<ArrayList<String>>();

        } else
        {
          Log.e(MonitorConstants.tag_srv, "Unexpected command from DroidMate TCP client. The command: " + input);
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

        String[] frames = stackTrace.split(Api.stack_trace_frame_delimiter);
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
      return MonitorConstants.srvCmd_close.equals(serverInput);
    }
  }

  // !!! DUPLICATION WARNING !!! with org.droidmate.uiautomator_daemon.SerializableTCPServerBase
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
          Log.d(MonitorConstants.tag_srv, "Failed to start TCP server because 'bind failed: EADDRINUSE (Address already in use)'. " +
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

        Log.v(MonitorConstants.tag_srv, "MonitorServerRunnable.run() using " + port);
        try
        {

          // Synchronize to ensure the parent thread (the one which started this one) will continue only after the
          // serverSocket is initialized.
          synchronized (this)
          {
            Log.d(MonitorConstants.tag_srv, String.format("Creating server socket bound to port %s...", port));

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
            Log.e(MonitorConstants.tag_srv, String.format("! Failed during startup to bind server socket on port %s. Stopping thread.", port));
            return;
          }

          // KNOWN BUG undiagnosed. Got here a set of null pointer in a row on com.audible.application_v1.7.0.apk when running using default settings.
          while (!serverSocket.isClosed())
          {
            Log.v(MonitorConstants.tag_srv, String.format("Accepting socket from client on port %s...", port));
            Socket clientSocket = serverSocket.accept();
            Log.v(MonitorConstants.tag_srv, "Socket accepted.");

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
              Log.e(MonitorConstants.tag_srv, "Exception was thrown while reading input sent to monitor TCP server from " +
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

          Log.d(MonitorConstants.tag_srv, "Closed monitor TCP server.");

        } catch (SocketTimeoutException e)
        {
          Log.e(MonitorConstants.tag_srv, "Closing monitor TCP server due to a timeout.", e);
          closeServerSocket();
        } catch (IOException e)
        {
          Log.e(MonitorConstants.tag_srv, "Exception was thrown while operating monitor TCP server.", e);
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
        sb.append(Api.stack_trace_frame_delimiter);
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

  private static final SimpleDateFormat monitor_time_formatter = new SimpleDateFormat(MonitorConstants.monitor_time_formatter_pattern, MonitorConstants.monitor_time_formatter_locale);

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
    ClassLoader[] classLoaders = {Thread.currentThread().getContextClassLoader(), Monitor.class.getClassLoader()};


  }

    @Hook("android.media.AudioRecord-><init>") 
    public static void redir_0_android_media_AudioRecord_ctor5(Object _this, int p0, int p1, int p2, int p3, int p4)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioRecord mthd: <init> retCls: void params: int "+convert(p0)+" int "+convert(p1)+" int "+convert(p2)+" int "+convert(p3)+" int "+convert(p4)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioRecord mthd: <init> retCls: void params: int "+convert(p0)+" int "+convert(p1)+" int "+convert(p2)+" int "+convert(p3)+" int "+convert(p4)+" stacktrace: "+stackTrace+"");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3, p4);
    }
    
    @Hook("java.net.Socket-><init>") 
    public static void redir_1_java_net_Socket_ctor2(Object _this, java.lang.String p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.lang.String "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.lang.String "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("java.net.Socket-><init>") 
    public static void redir_2_java_net_Socket_ctor4(Object _this, java.lang.String p0, int p1, java.net.InetAddress p2, int p3)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.lang.String "+convert(p0)+" int "+convert(p1)+" java.net.InetAddress "+convert(p2)+" int "+convert(p3)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.lang.String "+convert(p0)+" int "+convert(p1)+" java.net.InetAddress "+convert(p2)+" int "+convert(p3)+" stacktrace: "+stackTrace+"");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3);
    }
    
    @Hook("java.net.Socket-><init>") 
    public static void redir_3_java_net_Socket_ctor3(Object _this, java.lang.String p0, int p1, boolean p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.lang.String "+convert(p0)+" int "+convert(p1)+" boolean "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.lang.String "+convert(p0)+" int "+convert(p1)+" boolean "+convert(p2)+" stacktrace: "+stackTrace+"");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    
    @Hook("java.net.Socket-><init>") 
    public static void redir_4_java_net_Socket_ctor2(Object _this, java.net.InetAddress p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.net.InetAddress "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.net.InetAddress "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("java.net.Socket-><init>") 
    public static void redir_5_java_net_Socket_ctor4(Object _this, java.net.InetAddress p0, int p1, java.net.InetAddress p2, int p3)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.net.InetAddress "+convert(p0)+" int "+convert(p1)+" java.net.InetAddress "+convert(p2)+" int "+convert(p3)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.net.InetAddress "+convert(p0)+" int "+convert(p1)+" java.net.InetAddress "+convert(p2)+" int "+convert(p3)+" stacktrace: "+stackTrace+"");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3);
    }
    
    @Hook("java.net.Socket-><init>") 
    public static void redir_6_java_net_Socket_ctor3(Object _this, java.net.InetAddress p0, int p1, boolean p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.net.InetAddress "+convert(p0)+" int "+convert(p1)+" boolean "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.Socket mthd: <init> retCls: void params: java.net.InetAddress "+convert(p0)+" int "+convert(p1)+" boolean "+convert(p2)+" stacktrace: "+stackTrace+"");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    


    @Hook("android.app.ActivityThread->installContentProviders") 
    public static void redir_android_app_ActivityThread_installContentProviders2(Object _this, android.content.Context p0, java.util.List p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.app.ActivityThread mthd: installContentProviders retCls: void params: android.content.Context "+convert(p0)+" java.util.List "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.app.ActivityThread mthd: installContentProviders retCls: void params: android.content.Context "+convert(p0)+" java.util.List "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.app.ActivityThread");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.app.Activity->onResume") 
    public static void redir_android_app_Activity_onResume0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.app.Activity mthd: onResume retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.app.Activity mthd: onResume retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.app.Activity");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.app.Activity->startActivityForResult") 
    public static void redir_android_app_Activity_startActivityForResult2(Object _this, android.content.Intent p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.app.Activity mthd: startActivityForResult retCls: void params: android.content.Intent "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.app.Activity mthd: startActivityForResult retCls: void params: android.content.Intent "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.app.Activity");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.app.Activity->startActivityFromChild") 
    public static void redir_android_app_Activity_startActivityFromChild3(Object _this, android.app.Activity p0, android.content.Intent p1, int p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.app.Activity mthd: startActivityFromChild retCls: void params: android.app.Activity "+convert(p0)+" android.content.Intent "+convert(p1)+" int "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.app.Activity mthd: startActivityFromChild retCls: void params: android.app.Activity "+convert(p0)+" android.content.Intent "+convert(p1)+" int "+convert(p2)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.app.Activity");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    
    @Hook("android.app.Activity->startActivityIfNeeded") 
    public static boolean redir_android_app_Activity_startActivityIfNeeded2(Object _this, android.content.Intent p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.app.Activity mthd: startActivityIfNeeded retCls: boolean params: android.content.Intent "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.app.Activity mthd: startActivityIfNeeded retCls: boolean params: android.content.Intent "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.app.Activity");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.app.ActivityManager->getRecentTasks") 
    public static java.util.List redir_android_app_ActivityManager_getRecentTasks2(Object _this, int p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.app.ActivityManager mthd: getRecentTasks retCls: java.util.List params: int "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.app.ActivityManager mthd: getRecentTasks retCls: java.util.List params: int "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.app.ActivityManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.app.ActivityManager->getRunningTasks") 
    public static java.util.List redir_android_app_ActivityManager_getRunningTasks1(Object _this, int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.app.ActivityManager mthd: getRunningTasks retCls: java.util.List params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.app.ActivityManager mthd: getRunningTasks retCls: java.util.List params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.app.ActivityManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.bluetooth.BluetoothHeadset->startVoiceRecognition") 
    public static boolean redir_android_bluetooth_BluetoothHeadset_startVoiceRecognition1(Object _this, android.bluetooth.BluetoothDevice p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.bluetooth.BluetoothHeadset mthd: startVoiceRecognition retCls: boolean params: android.bluetooth.BluetoothDevice "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.bluetooth.BluetoothHeadset mthd: startVoiceRecognition retCls: boolean params: android.bluetooth.BluetoothDevice "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.bluetooth.BluetoothHeadset");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.bluetooth.BluetoothHeadset->stopVoiceRecognition") 
    public static boolean redir_android_bluetooth_BluetoothHeadset_stopVoiceRecognition1(Object _this, android.bluetooth.BluetoothDevice p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.bluetooth.BluetoothHeadset mthd: stopVoiceRecognition retCls: boolean params: android.bluetooth.BluetoothDevice "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.bluetooth.BluetoothHeadset mthd: stopVoiceRecognition retCls: boolean params: android.bluetooth.BluetoothDevice "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.bluetooth.BluetoothHeadset");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.content.ContentProviderClient->bulkInsert") 
    public static int redir_android_content_ContentProviderClient_bulkInsert2(Object _this, android.net.Uri p0, android.content.ContentValues[] p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: bulkInsert retCls: int params: android.net.Uri "+convert(p0)+" android.content.ContentValues[] "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: bulkInsert retCls: int params: android.net.Uri "+convert(p0)+" android.content.ContentValues[] "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentProviderClient");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.content.ContentProviderClient->delete") 
    public static int redir_android_content_ContentProviderClient_delete3(Object _this, android.net.Uri p0, java.lang.String p1, java.lang.String[] p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: delete retCls: int params: android.net.Uri "+convert(p0)+" java.lang.String "+convert(p1)+" java.lang.String[] "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: delete retCls: int params: android.net.Uri "+convert(p0)+" java.lang.String "+convert(p1)+" java.lang.String[] "+convert(p2)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentProviderClient");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    
    @Hook("android.content.ContentProviderClient->insert") 
    public static android.net.Uri redir_android_content_ContentProviderClient_insert2(Object _this, android.net.Uri p0, android.content.ContentValues p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: insert retCls: android.net.Uri params: android.net.Uri "+convert(p0)+" android.content.ContentValues "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: insert retCls: android.net.Uri params: android.net.Uri "+convert(p0)+" android.content.ContentValues "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentProviderClient");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.content.ContentProviderClient->openFile") 
    public static android.os.ParcelFileDescriptor redir_android_content_ContentProviderClient_openFile2(Object _this, android.net.Uri p0, java.lang.String p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: openFile retCls: android.os.ParcelFileDescriptor params: android.net.Uri "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: openFile retCls: android.os.ParcelFileDescriptor params: android.net.Uri "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentProviderClient");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.content.ContentProviderClient->query") 
    public static android.database.Cursor redir_android_content_ContentProviderClient_query5(Object _this, android.net.Uri p0, java.lang.String[] p1, java.lang.String p2, java.lang.String[] p3, java.lang.String p4)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: query retCls: android.database.Cursor params: android.net.Uri "+convert(p0)+" java.lang.String[] "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String[] "+convert(p3)+" java.lang.String "+convert(p4)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: query retCls: android.database.Cursor params: android.net.Uri "+convert(p0)+" java.lang.String[] "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String[] "+convert(p3)+" java.lang.String "+convert(p4)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentProviderClient");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3, p4);
    }
    
    @Hook("android.content.ContentProviderClient->update") 
    public static int redir_android_content_ContentProviderClient_update4(Object _this, android.net.Uri p0, android.content.ContentValues p1, java.lang.String p2, java.lang.String[] p3)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: update retCls: int params: android.net.Uri "+convert(p0)+" android.content.ContentValues "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String[] "+convert(p3)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentProviderClient mthd: update retCls: int params: android.net.Uri "+convert(p0)+" android.content.ContentValues "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String[] "+convert(p3)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentProviderClient");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3);
    }
    
    @Hook("android.content.ContentResolver->bulkInsert") 
    public static int redir_android_content_ContentResolver_bulkInsert2(Object _this, android.net.Uri p0, android.content.ContentValues[] p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentResolver mthd: bulkInsert retCls: int params: android.net.Uri "+convert(p0)+" android.content.ContentValues[] "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentResolver mthd: bulkInsert retCls: int params: android.net.Uri "+convert(p0)+" android.content.ContentValues[] "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentResolver");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.content.ContentResolver->delete") 
    public static int redir_android_content_ContentResolver_delete3(Object _this, android.net.Uri p0, java.lang.String p1, java.lang.String[] p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentResolver mthd: delete retCls: int params: android.net.Uri "+convert(p0)+" java.lang.String "+convert(p1)+" java.lang.String[] "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentResolver mthd: delete retCls: int params: android.net.Uri "+convert(p0)+" java.lang.String "+convert(p1)+" java.lang.String[] "+convert(p2)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentResolver");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    
    @Hook("android.content.ContentResolver->insert") 
    public static android.net.Uri redir_android_content_ContentResolver_insert2(Object _this, android.net.Uri p0, android.content.ContentValues p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentResolver mthd: insert retCls: android.net.Uri params: android.net.Uri "+convert(p0)+" android.content.ContentValues "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentResolver mthd: insert retCls: android.net.Uri params: android.net.Uri "+convert(p0)+" android.content.ContentValues "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentResolver");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.content.ContentResolver->openFileDescriptor") 
    public static android.os.ParcelFileDescriptor redir_android_content_ContentResolver_openFileDescriptor2(Object _this, android.net.Uri p0, java.lang.String p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentResolver mthd: openFileDescriptor retCls: android.os.ParcelFileDescriptor params: android.net.Uri "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentResolver mthd: openFileDescriptor retCls: android.os.ParcelFileDescriptor params: android.net.Uri "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentResolver");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.content.ContentResolver->openInputStream") 
    public static java.io.InputStream redir_android_content_ContentResolver_openInputStream1(Object _this, android.net.Uri p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentResolver mthd: openInputStream retCls: java.io.InputStream params: android.net.Uri "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentResolver mthd: openInputStream retCls: java.io.InputStream params: android.net.Uri "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentResolver");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.content.ContentResolver->query") 
    public static android.database.Cursor redir_android_content_ContentResolver_query5(Object _this, android.net.Uri p0, java.lang.String[] p1, java.lang.String p2, java.lang.String[] p3, java.lang.String p4)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentResolver mthd: query retCls: android.database.Cursor params: android.net.Uri "+convert(p0)+" java.lang.String[] "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String[] "+convert(p3)+" java.lang.String "+convert(p4)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentResolver mthd: query retCls: android.database.Cursor params: android.net.Uri "+convert(p0)+" java.lang.String[] "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String[] "+convert(p3)+" java.lang.String "+convert(p4)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentResolver");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3, p4);
    }
    
    @Hook("android.content.ContentResolver->registerContentObserver") 
    public static void redir_android_content_ContentResolver_registerContentObserver3(Object _this, android.net.Uri p0, boolean p1, android.database.ContentObserver p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentResolver mthd: registerContentObserver retCls: void params: android.net.Uri "+convert(p0)+" boolean "+convert(p1)+" android.database.ContentObserver "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentResolver mthd: registerContentObserver retCls: void params: android.net.Uri "+convert(p0)+" boolean "+convert(p1)+" android.database.ContentObserver "+convert(p2)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentResolver");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    
    @Hook("android.content.ContentResolver->update") 
    public static int redir_android_content_ContentResolver_update4(Object _this, android.net.Uri p0, android.content.ContentValues p1, java.lang.String p2, java.lang.String[] p3)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.content.ContentResolver mthd: update retCls: int params: android.net.Uri "+convert(p0)+" android.content.ContentValues "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String[] "+convert(p3)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.content.ContentResolver mthd: update retCls: int params: android.net.Uri "+convert(p0)+" android.content.ContentValues "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String[] "+convert(p3)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.content.ContentResolver");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3);
    }
    
    @Hook("android.hardware.Camera->open") 
    public static android.hardware.Camera redir_android_hardware_Camera_open1(int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.hardware.Camera mthd: open retCls: android.hardware.Camera params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.hardware.Camera mthd: open retCls: android.hardware.Camera params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.hardware.Camera");
        return OriginalMethod.by(new $() {}).invokeStatic(p0);
    }
    
    @Hook("android.location.LocationManager->addGpsStatusListener") 
    public static boolean redir_android_location_LocationManager_addGpsStatusListener1(Object _this, android.location.GpsStatus.Listener p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: addGpsStatusListener retCls: boolean params: android.location.GpsStatus.Listener "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: addGpsStatusListener retCls: boolean params: android.location.GpsStatus.Listener "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->addNmeaListener") 
    public static boolean redir_android_location_LocationManager_addNmeaListener1(Object _this, android.location.GpsStatus.NmeaListener p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: addNmeaListener retCls: boolean params: android.location.GpsStatus.NmeaListener "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: addNmeaListener retCls: boolean params: android.location.GpsStatus.NmeaListener "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->addProximityAlert") 
    public static void redir_android_location_LocationManager_addProximityAlert5(Object _this, double p0, double p1, float p2, long p3, android.app.PendingIntent p4)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: addProximityAlert retCls: void params: double "+convert(p0)+" double "+convert(p1)+" float "+convert(p2)+" long "+convert(p3)+" android.app.PendingIntent "+convert(p4)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: addProximityAlert retCls: void params: double "+convert(p0)+" double "+convert(p1)+" float "+convert(p2)+" long "+convert(p3)+" android.app.PendingIntent "+convert(p4)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3, p4);
    }
    
    @Hook("android.location.LocationManager->addTestProvider") 
    public static void redir_android_location_LocationManager_addTestProvider10(Object _this, java.lang.String p0, boolean p1, boolean p2, boolean p3, boolean p4, boolean p5, boolean p6, boolean p7, int p8, int p9)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: addTestProvider retCls: void params: java.lang.String "+convert(p0)+" boolean "+convert(p1)+" boolean "+convert(p2)+" boolean "+convert(p3)+" boolean "+convert(p4)+" boolean "+convert(p5)+" boolean "+convert(p6)+" boolean "+convert(p7)+" int "+convert(p8)+" int "+convert(p9)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: addTestProvider retCls: void params: java.lang.String "+convert(p0)+" boolean "+convert(p1)+" boolean "+convert(p2)+" boolean "+convert(p3)+" boolean "+convert(p4)+" boolean "+convert(p5)+" boolean "+convert(p6)+" boolean "+convert(p7)+" int "+convert(p8)+" int "+convert(p9)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
    }
    
    @Hook("android.location.LocationManager->clearTestProviderEnabled") 
    public static void redir_android_location_LocationManager_clearTestProviderEnabled1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: clearTestProviderEnabled retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: clearTestProviderEnabled retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->clearTestProviderLocation") 
    public static void redir_android_location_LocationManager_clearTestProviderLocation1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: clearTestProviderLocation retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: clearTestProviderLocation retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->clearTestProviderStatus") 
    public static void redir_android_location_LocationManager_clearTestProviderStatus1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: clearTestProviderStatus retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: clearTestProviderStatus retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->getBestProvider") 
    public static java.lang.String redir_android_location_LocationManager_getBestProvider2(Object _this, android.location.Criteria p0, boolean p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: getBestProvider retCls: java.lang.String params: android.location.Criteria "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: getBestProvider retCls: java.lang.String params: android.location.Criteria "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.location.LocationManager->getLastKnownLocation") 
    public static android.location.Location redir_android_location_LocationManager_getLastKnownLocation1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: getLastKnownLocation retCls: android.location.Location params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: getLastKnownLocation retCls: android.location.Location params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->getProvider") 
    public static android.location.LocationProvider redir_android_location_LocationManager_getProvider1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: getProvider retCls: android.location.LocationProvider params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: getProvider retCls: android.location.LocationProvider params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->getProviders") 
    public static java.util.List redir_android_location_LocationManager_getProviders2(Object _this, android.location.Criteria p0, boolean p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: getProviders retCls: java.util.List params: android.location.Criteria "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: getProviders retCls: java.util.List params: android.location.Criteria "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.location.LocationManager->getProviders") 
    public static java.util.List redir_android_location_LocationManager_getProviders1(Object _this, boolean p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: getProviders retCls: java.util.List params: boolean "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: getProviders retCls: java.util.List params: boolean "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->isProviderEnabled") 
    public static boolean redir_android_location_LocationManager_isProviderEnabled1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: isProviderEnabled retCls: boolean params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: isProviderEnabled retCls: boolean params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->removeTestProvider") 
    public static void redir_android_location_LocationManager_removeTestProvider1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: removeTestProvider retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: removeTestProvider retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.location.LocationManager->requestLocationUpdates") 
    public static void redir_android_location_LocationManager_requestLocationUpdates4(Object _this, long p0, float p1, android.location.Criteria p2, android.app.PendingIntent p3)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: long "+convert(p0)+" float "+convert(p1)+" android.location.Criteria "+convert(p2)+" android.app.PendingIntent "+convert(p3)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: long "+convert(p0)+" float "+convert(p1)+" android.location.Criteria "+convert(p2)+" android.app.PendingIntent "+convert(p3)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3);
    }
    
    @Hook("android.location.LocationManager->requestLocationUpdates") 
    public static void redir_android_location_LocationManager_requestLocationUpdates5(Object _this, long p0, float p1, android.location.Criteria p2, android.location.LocationListener p3, android.os.Looper p4)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: long "+convert(p0)+" float "+convert(p1)+" android.location.Criteria "+convert(p2)+" android.location.LocationListener "+convert(p3)+" android.os.Looper "+convert(p4)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: long "+convert(p0)+" float "+convert(p1)+" android.location.Criteria "+convert(p2)+" android.location.LocationListener "+convert(p3)+" android.os.Looper "+convert(p4)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3, p4);
    }
    
    @Hook("android.location.LocationManager->requestLocationUpdates") 
    public static void redir_android_location_LocationManager_requestLocationUpdates4(Object _this, java.lang.String p0, long p1, float p2, android.app.PendingIntent p3)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: java.lang.String "+convert(p0)+" long "+convert(p1)+" float "+convert(p2)+" android.app.PendingIntent "+convert(p3)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: java.lang.String "+convert(p0)+" long "+convert(p1)+" float "+convert(p2)+" android.app.PendingIntent "+convert(p3)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3);
    }
    
    @Hook("android.location.LocationManager->requestLocationUpdates") 
    public static void redir_android_location_LocationManager_requestLocationUpdates4(Object _this, java.lang.String p0, long p1, float p2, android.location.LocationListener p3)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: java.lang.String "+convert(p0)+" long "+convert(p1)+" float "+convert(p2)+" android.location.LocationListener "+convert(p3)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: java.lang.String "+convert(p0)+" long "+convert(p1)+" float "+convert(p2)+" android.location.LocationListener "+convert(p3)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3);
    }
    
    @Hook("android.location.LocationManager->requestLocationUpdates") 
    public static void redir_android_location_LocationManager_requestLocationUpdates5(Object _this, java.lang.String p0, long p1, float p2, android.location.LocationListener p3, android.os.Looper p4)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: java.lang.String "+convert(p0)+" long "+convert(p1)+" float "+convert(p2)+" android.location.LocationListener "+convert(p3)+" android.os.Looper "+convert(p4)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestLocationUpdates retCls: void params: java.lang.String "+convert(p0)+" long "+convert(p1)+" float "+convert(p2)+" android.location.LocationListener "+convert(p3)+" android.os.Looper "+convert(p4)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3, p4);
    }
    
    @Hook("android.location.LocationManager->requestSingleUpdate") 
    public static void redir_android_location_LocationManager_requestSingleUpdate2(Object _this, android.location.Criteria p0, android.app.PendingIntent p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestSingleUpdate retCls: void params: android.location.Criteria "+convert(p0)+" android.app.PendingIntent "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestSingleUpdate retCls: void params: android.location.Criteria "+convert(p0)+" android.app.PendingIntent "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.location.LocationManager->requestSingleUpdate") 
    public static void redir_android_location_LocationManager_requestSingleUpdate3(Object _this, android.location.Criteria p0, android.location.LocationListener p1, android.os.Looper p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestSingleUpdate retCls: void params: android.location.Criteria "+convert(p0)+" android.location.LocationListener "+convert(p1)+" android.os.Looper "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestSingleUpdate retCls: void params: android.location.Criteria "+convert(p0)+" android.location.LocationListener "+convert(p1)+" android.os.Looper "+convert(p2)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    
    @Hook("android.location.LocationManager->requestSingleUpdate") 
    public static void redir_android_location_LocationManager_requestSingleUpdate2(Object _this, java.lang.String p0, android.app.PendingIntent p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestSingleUpdate retCls: void params: java.lang.String "+convert(p0)+" android.app.PendingIntent "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestSingleUpdate retCls: void params: java.lang.String "+convert(p0)+" android.app.PendingIntent "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.location.LocationManager->requestSingleUpdate") 
    public static void redir_android_location_LocationManager_requestSingleUpdate3(Object _this, java.lang.String p0, android.location.LocationListener p1, android.os.Looper p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: requestSingleUpdate retCls: void params: java.lang.String "+convert(p0)+" android.location.LocationListener "+convert(p1)+" android.os.Looper "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: requestSingleUpdate retCls: void params: java.lang.String "+convert(p0)+" android.location.LocationListener "+convert(p1)+" android.os.Looper "+convert(p2)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    
    @Hook("android.location.LocationManager->sendExtraCommand") 
    public static boolean redir_android_location_LocationManager_sendExtraCommand3(Object _this, java.lang.String p0, java.lang.String p1, android.os.Bundle p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: sendExtraCommand retCls: boolean params: java.lang.String "+convert(p0)+" java.lang.String "+convert(p1)+" android.os.Bundle "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: sendExtraCommand retCls: boolean params: java.lang.String "+convert(p0)+" java.lang.String "+convert(p1)+" android.os.Bundle "+convert(p2)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    
    @Hook("android.location.LocationManager->setTestProviderEnabled") 
    public static void redir_android_location_LocationManager_setTestProviderEnabled2(Object _this, java.lang.String p0, boolean p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: setTestProviderEnabled retCls: void params: java.lang.String "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: setTestProviderEnabled retCls: void params: java.lang.String "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.location.LocationManager->setTestProviderLocation") 
    public static void redir_android_location_LocationManager_setTestProviderLocation2(Object _this, java.lang.String p0, android.location.Location p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: setTestProviderLocation retCls: void params: java.lang.String "+convert(p0)+" android.location.Location "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: setTestProviderLocation retCls: void params: java.lang.String "+convert(p0)+" android.location.Location "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.location.LocationManager->setTestProviderStatus") 
    public static void redir_android_location_LocationManager_setTestProviderStatus4(Object _this, java.lang.String p0, int p1, android.os.Bundle p2, long p3)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.location.LocationManager mthd: setTestProviderStatus retCls: void params: java.lang.String "+convert(p0)+" int "+convert(p1)+" android.os.Bundle "+convert(p2)+" long "+convert(p3)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.location.LocationManager mthd: setTestProviderStatus retCls: void params: java.lang.String "+convert(p0)+" int "+convert(p1)+" android.os.Bundle "+convert(p2)+" long "+convert(p3)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.location.LocationManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3);
    }
    
    @Hook("android.media.AudioManager->isBluetoothA2dpOn") 
    public static boolean redir_android_media_AudioManager_isBluetoothA2dpOn0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: isBluetoothA2dpOn retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: isBluetoothA2dpOn retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.media.AudioManager->isWiredHeadsetOn") 
    public static boolean redir_android_media_AudioManager_isWiredHeadsetOn0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: isWiredHeadsetOn retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: isWiredHeadsetOn retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.media.AudioManager->setBluetoothScoOn") 
    public static void redir_android_media_AudioManager_setBluetoothScoOn1(Object _this, boolean p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: setBluetoothScoOn retCls: void params: boolean "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: setBluetoothScoOn retCls: void params: boolean "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.media.AudioManager->setMicrophoneMute") 
    public static void redir_android_media_AudioManager_setMicrophoneMute1(Object _this, boolean p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: setMicrophoneMute retCls: void params: boolean "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: setMicrophoneMute retCls: void params: boolean "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.media.AudioManager->setMode") 
    public static void redir_android_media_AudioManager_setMode1(Object _this, int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: setMode retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: setMode retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.media.AudioManager->setParameter") 
    public static void redir_android_media_AudioManager_setParameter2(Object _this, java.lang.String p0, java.lang.String p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: setParameter retCls: void params: java.lang.String "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: setParameter retCls: void params: java.lang.String "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.media.AudioManager->setParameters") 
    public static void redir_android_media_AudioManager_setParameters1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: setParameters retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: setParameters retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.media.AudioManager->setSpeakerphoneOn") 
    public static void redir_android_media_AudioManager_setSpeakerphoneOn1(Object _this, boolean p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: setSpeakerphoneOn retCls: void params: boolean "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: setSpeakerphoneOn retCls: void params: boolean "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.media.AudioManager->startBluetoothSco") 
    public static void redir_android_media_AudioManager_startBluetoothSco0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: startBluetoothSco retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: startBluetoothSco retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.media.AudioManager->stopBluetoothSco") 
    public static void redir_android_media_AudioManager_stopBluetoothSco0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.AudioManager mthd: stopBluetoothSco retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.AudioManager mthd: stopBluetoothSco retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.AudioManager");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.media.MediaPlayer->setWakeMode") 
    public static void redir_android_media_MediaPlayer_setWakeMode2(Object _this, android.content.Context p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.MediaPlayer mthd: setWakeMode retCls: void params: android.content.Context "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.MediaPlayer mthd: setWakeMode retCls: void params: android.content.Context "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.MediaPlayer");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.media.MediaRecorder->setAudioSource") 
    public static void redir_android_media_MediaRecorder_setAudioSource1(Object _this, int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.MediaRecorder mthd: setAudioSource retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.MediaRecorder mthd: setAudioSource retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.MediaRecorder");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.media.MediaRecorder->setVideoSource") 
    public static void redir_android_media_MediaRecorder_setVideoSource1(Object _this, int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.media.MediaRecorder mthd: setVideoSource retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.media.MediaRecorder mthd: setVideoSource retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.media.MediaRecorder");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.net.ConnectivityManager->requestRouteToHost") 
    public static boolean redir_android_net_ConnectivityManager_requestRouteToHost2(Object _this, int p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: requestRouteToHost retCls: boolean params: int "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: requestRouteToHost retCls: boolean params: int "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.ConnectivityManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.net.ConnectivityManager->setNetworkPreference") 
    public static void redir_android_net_ConnectivityManager_setNetworkPreference1(Object _this, int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: setNetworkPreference retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: setNetworkPreference retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.ConnectivityManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.net.ConnectivityManager->startUsingNetworkFeature") 
    public static int redir_android_net_ConnectivityManager_startUsingNetworkFeature2(Object _this, int p0, java.lang.String p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: startUsingNetworkFeature retCls: int params: int "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: startUsingNetworkFeature retCls: int params: int "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.ConnectivityManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.net.ConnectivityManager->stopUsingNetworkFeature") 
    public static int redir_android_net_ConnectivityManager_stopUsingNetworkFeature2(Object _this, int p0, java.lang.String p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: stopUsingNetworkFeature retCls: int params: int "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: stopUsingNetworkFeature retCls: int params: int "+convert(p0)+" java.lang.String "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.ConnectivityManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.net.ConnectivityManager->tether") 
    public static int redir_android_net_ConnectivityManager_tether1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: tether retCls: int params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: tether retCls: int params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.ConnectivityManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.net.ConnectivityManager->untether") 
    public static int redir_android_net_ConnectivityManager_untether1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: untether retCls: int params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.ConnectivityManager mthd: untether retCls: int params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.ConnectivityManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.net.wifi.WifiManager$MulticastLock->acquire") 
    public static void redir_android_net_wifi_WifiManager_MulticastLock_acquire0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager$MulticastLock mthd: acquire retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager$MulticastLock mthd: acquire retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager$MulticastLock");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager$MulticastLock->release") 
    public static void redir_android_net_wifi_WifiManager_MulticastLock_release0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager$MulticastLock mthd: release retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager$MulticastLock mthd: release retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager$MulticastLock");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager$WifiLock->acquire") 
    public static void redir_android_net_wifi_WifiManager_WifiLock_acquire0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager$WifiLock mthd: acquire retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager$WifiLock mthd: acquire retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager$WifiLock");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager$WifiLock->release") 
    public static void redir_android_net_wifi_WifiManager_WifiLock_release0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager$WifiLock mthd: release retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager$WifiLock mthd: release retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager$WifiLock");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager->addNetwork") 
    public static int redir_android_net_wifi_WifiManager_addNetwork1(Object _this, android.net.wifi.WifiConfiguration p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: addNetwork retCls: int params: android.net.wifi.WifiConfiguration "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: addNetwork retCls: int params: android.net.wifi.WifiConfiguration "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.net.wifi.WifiManager->disableNetwork") 
    public static boolean redir_android_net_wifi_WifiManager_disableNetwork1(Object _this, int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: disableNetwork retCls: boolean params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: disableNetwork retCls: boolean params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.net.wifi.WifiManager->disconnect") 
    public static boolean redir_android_net_wifi_WifiManager_disconnect0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: disconnect retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: disconnect retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager->enableNetwork") 
    public static boolean redir_android_net_wifi_WifiManager_enableNetwork2(Object _this, int p0, boolean p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: enableNetwork retCls: boolean params: int "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: enableNetwork retCls: boolean params: int "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.net.wifi.WifiManager->initializeMulticastFiltering") 
    public static boolean redir_android_net_wifi_WifiManager_initializeMulticastFiltering0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: initializeMulticastFiltering retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: initializeMulticastFiltering retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager->pingSupplicant") 
    public static boolean redir_android_net_wifi_WifiManager_pingSupplicant0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: pingSupplicant retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: pingSupplicant retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager->reassociate") 
    public static boolean redir_android_net_wifi_WifiManager_reassociate0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: reassociate retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: reassociate retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager->reconnect") 
    public static boolean redir_android_net_wifi_WifiManager_reconnect0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: reconnect retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: reconnect retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager->removeNetwork") 
    public static boolean redir_android_net_wifi_WifiManager_removeNetwork1(Object _this, int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: removeNetwork retCls: boolean params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: removeNetwork retCls: boolean params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.net.wifi.WifiManager->saveConfiguration") 
    public static boolean redir_android_net_wifi_WifiManager_saveConfiguration0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: saveConfiguration retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: saveConfiguration retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.net.wifi.WifiManager->setWifiApEnabled") 
    public static boolean redir_android_net_wifi_WifiManager_setWifiApEnabled2(Object _this, android.net.wifi.WifiConfiguration p0, boolean p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: setWifiApEnabled retCls: boolean params: android.net.wifi.WifiConfiguration "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: setWifiApEnabled retCls: boolean params: android.net.wifi.WifiConfiguration "+convert(p0)+" boolean "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.net.wifi.WifiManager->setWifiEnabled") 
    public static boolean redir_android_net_wifi_WifiManager_setWifiEnabled1(Object _this, boolean p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: setWifiEnabled retCls: boolean params: boolean "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: setWifiEnabled retCls: boolean params: boolean "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.net.wifi.WifiManager->startScan") 
    public static boolean redir_android_net_wifi_WifiManager_startScan0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: startScan retCls: boolean params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.net.wifi.WifiManager mthd: startScan retCls: boolean params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.net.wifi.WifiManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.os.PowerManager$WakeLock->acquire") 
    public static void redir_android_os_PowerManager_WakeLock_acquire0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.os.PowerManager$WakeLock mthd: acquire retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.os.PowerManager$WakeLock mthd: acquire retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.os.PowerManager$WakeLock");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.os.PowerManager$WakeLock->acquire") 
    public static void redir_android_os_PowerManager_WakeLock_acquire1(Object _this, long p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.os.PowerManager$WakeLock mthd: acquire retCls: void params: long "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.os.PowerManager$WakeLock mthd: acquire retCls: void params: long "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.os.PowerManager$WakeLock");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.os.PowerManager$WakeLock->release") 
    public static void redir_android_os_PowerManager_WakeLock_release0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.os.PowerManager$WakeLock mthd: release retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.os.PowerManager$WakeLock mthd: release retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.os.PowerManager$WakeLock");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.os.PowerManager$WakeLock->release") 
    public static void redir_android_os_PowerManager_WakeLock_release1(Object _this, int p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.os.PowerManager$WakeLock mthd: release retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.os.PowerManager$WakeLock mthd: release retCls: void params: int "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.os.PowerManager$WakeLock");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.speech.SpeechRecognizer->cancel") 
    public static void redir_android_speech_SpeechRecognizer_cancel0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: cancel retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: cancel retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.speech.SpeechRecognizer");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.speech.SpeechRecognizer->handleCancelMessage") 
    public static void redir_android_speech_SpeechRecognizer_handleCancelMessage0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: handleCancelMessage retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: handleCancelMessage retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.speech.SpeechRecognizer");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.speech.SpeechRecognizer->handleStartListening") 
    public static void redir_android_speech_SpeechRecognizer_handleStartListening1(Object _this, android.content.Intent p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: handleStartListening retCls: void params: android.content.Intent "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: handleStartListening retCls: void params: android.content.Intent "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.speech.SpeechRecognizer");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.speech.SpeechRecognizer->handleStopMessage") 
    public static void redir_android_speech_SpeechRecognizer_handleStopMessage0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: handleStopMessage retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: handleStopMessage retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.speech.SpeechRecognizer");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.speech.SpeechRecognizer->startListening") 
    public static void redir_android_speech_SpeechRecognizer_startListening1(Object _this, android.content.Intent p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: startListening retCls: void params: android.content.Intent "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: startListening retCls: void params: android.content.Intent "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.speech.SpeechRecognizer");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.speech.SpeechRecognizer->stopListening") 
    public static void redir_android_speech_SpeechRecognizer_stopListening0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: stopListening retCls: void params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.speech.SpeechRecognizer mthd: stopListening retCls: void params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.speech.SpeechRecognizer");
        OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getCellLocation") 
    public static android.telephony.CellLocation redir_android_telephony_TelephonyManager_getCellLocation0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getCellLocation retCls: android.telephony.CellLocation params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getCellLocation retCls: android.telephony.CellLocation params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getDeviceId") 
    public static java.lang.String redir_android_telephony_TelephonyManager_getDeviceId0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getDeviceId retCls: java.lang.String params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getDeviceId retCls: java.lang.String params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getDeviceSoftwareVersion") 
    public static java.lang.String redir_android_telephony_TelephonyManager_getDeviceSoftwareVersion0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getDeviceSoftwareVersion retCls: java.lang.String params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getDeviceSoftwareVersion retCls: java.lang.String params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getLine1Number") 
    public static java.lang.String redir_android_telephony_TelephonyManager_getLine1Number0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getLine1Number retCls: java.lang.String params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getLine1Number retCls: java.lang.String params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getNeighboringCellInfo") 
    public static java.util.List redir_android_telephony_TelephonyManager_getNeighboringCellInfo0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getNeighboringCellInfo retCls: java.util.List params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getNeighboringCellInfo retCls: java.util.List params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getSimSerialNumber") 
    public static java.lang.String redir_android_telephony_TelephonyManager_getSimSerialNumber0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getSimSerialNumber retCls: java.lang.String params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getSimSerialNumber retCls: java.lang.String params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getSubscriberId") 
    public static java.lang.String redir_android_telephony_TelephonyManager_getSubscriberId0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getSubscriberId retCls: java.lang.String params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getSubscriberId retCls: java.lang.String params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getVoiceMailAlphaTag") 
    public static java.lang.String redir_android_telephony_TelephonyManager_getVoiceMailAlphaTag0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getVoiceMailAlphaTag retCls: java.lang.String params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getVoiceMailAlphaTag retCls: java.lang.String params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->getVoiceMailNumber") 
    public static java.lang.String redir_android_telephony_TelephonyManager_getVoiceMailNumber0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getVoiceMailNumber retCls: java.lang.String params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: getVoiceMailNumber retCls: java.lang.String params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("android.telephony.TelephonyManager->listen") 
    public static void redir_android_telephony_TelephonyManager_listen2(Object _this, android.telephony.PhoneStateListener p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: listen retCls: void params: android.telephony.PhoneStateListener "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.telephony.TelephonyManager mthd: listen retCls: void params: android.telephony.PhoneStateListener "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.telephony.TelephonyManager");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("android.webkit.WebView->loadDataWithBaseURL") 
    public static void redir_android_webkit_WebView_loadDataWithBaseURL5(Object _this, java.lang.String p0, java.lang.String p1, java.lang.String p2, java.lang.String p3, java.lang.String p4)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.webkit.WebView mthd: loadDataWithBaseURL retCls: void params: java.lang.String "+convert(p0)+" java.lang.String "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String "+convert(p3)+" java.lang.String "+convert(p4)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.webkit.WebView mthd: loadDataWithBaseURL retCls: void params: java.lang.String "+convert(p0)+" java.lang.String "+convert(p1)+" java.lang.String "+convert(p2)+" java.lang.String "+convert(p3)+" java.lang.String "+convert(p4)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.webkit.WebView");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2, p3, p4);
    }
    
    @Hook("android.webkit.WebView->loadUrl") 
    public static void redir_android_webkit_WebView_loadUrl1(Object _this, java.lang.String p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.webkit.WebView mthd: loadUrl retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.webkit.WebView mthd: loadUrl retCls: void params: java.lang.String "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.webkit.WebView");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("android.webkit.WebView->loadUrl") 
    public static void redir_android_webkit_WebView_loadUrl2(Object _this, java.lang.String p0, java.util.Map p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: android.webkit.WebView mthd: loadUrl retCls: void params: java.lang.String "+convert(p0)+" java.util.Map "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: android.webkit.WebView mthd: loadUrl retCls: void params: java.lang.String "+convert(p0)+" java.util.Map "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("android.webkit.WebView");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("java.net.DatagramSocket->connect") 
    public static void redir_java_net_DatagramSocket_connect2(Object _this, java.net.InetAddress p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.DatagramSocket mthd: connect retCls: void params: java.net.InetAddress "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.DatagramSocket mthd: connect retCls: void params: java.net.InetAddress "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("java.net.DatagramSocket");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("java.net.DatagramSocket->connect") 
    public static void redir_java_net_DatagramSocket_connect1(Object _this, java.net.SocketAddress p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.DatagramSocket mthd: connect retCls: void params: java.net.SocketAddress "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.DatagramSocket mthd: connect retCls: void params: java.net.SocketAddress "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("java.net.DatagramSocket");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("java.net.MulticastSocket->joinGroup") 
    public static void redir_java_net_MulticastSocket_joinGroup1(Object _this, java.net.InetAddress p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.MulticastSocket mthd: joinGroup retCls: void params: java.net.InetAddress "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.MulticastSocket mthd: joinGroup retCls: void params: java.net.InetAddress "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("java.net.MulticastSocket");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("java.net.MulticastSocket->joinGroup") 
    public static void redir_java_net_MulticastSocket_joinGroup2(Object _this, java.net.SocketAddress p0, java.net.NetworkInterface p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.MulticastSocket mthd: joinGroup retCls: void params: java.net.SocketAddress "+convert(p0)+" java.net.NetworkInterface "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.MulticastSocket mthd: joinGroup retCls: void params: java.net.SocketAddress "+convert(p0)+" java.net.NetworkInterface "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("java.net.MulticastSocket");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("java.net.Socket->connect") 
    public static void redir_java_net_Socket_connect1(Object _this, java.net.SocketAddress p0)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.Socket mthd: connect retCls: void params: java.net.SocketAddress "+convert(p0)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.Socket mthd: connect retCls: void params: java.net.SocketAddress "+convert(p0)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("java.net.Socket");
        OriginalMethod.by(new $() {}).invoke(_this, p0);
    }
    
    @Hook("java.net.Socket->connect") 
    public static void redir_java_net_Socket_connect2(Object _this, java.net.SocketAddress p0, int p1)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.Socket mthd: connect retCls: void params: java.net.SocketAddress "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.Socket mthd: connect retCls: void params: java.net.SocketAddress "+convert(p0)+" int "+convert(p1)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("java.net.Socket");
        OriginalMethod.by(new $() {}).invoke(_this, p0, p1);
    }
    
    @Hook("java.net.URL->openConnection") 
    public static java.net.URLConnection redir_java_net_URL_openConnection0(Object _this)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: java.net.URL mthd: openConnection retCls: java.net.URLConnection params:  stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: java.net.URL mthd: openConnection retCls: java.net.URLConnection params:  stacktrace: "+stackTrace+"");
        hookPlugin.before("java.net.URL");
        return OriginalMethod.by(new $() {}).invoke(_this);
    }
    
    @Hook("org.apache.http.impl.client.AbstractHttpClient->execute") 
    public static org.apache.http.HttpResponse redir_org_apache_http_impl_client_AbstractHttpClient_execute3(Object _this, org.apache.http.HttpHost p0, org.apache.http.HttpRequest p1, org.apache.http.protocol.HttpContext p2)
    {
        String stackTrace = getStackTrace();
        long threadId = getThreadId();
        Log.i("Monitor_API_method_call", "TId: "+threadId+" objCls: org.apache.http.impl.client.AbstractHttpClient mthd: execute retCls: org.apache.http.HttpResponse params: org.apache.http.HttpHost "+convert(p0)+" org.apache.http.HttpRequest "+convert(p1)+" org.apache.http.protocol.HttpContext "+convert(p2)+" stacktrace: "+stackTrace+""); 
        addCurrentLogs("TId: "+threadId+" objCls: org.apache.http.impl.client.AbstractHttpClient mthd: execute retCls: org.apache.http.HttpResponse params: org.apache.http.HttpHost "+convert(p0)+" org.apache.http.HttpRequest "+convert(p1)+" org.apache.http.protocol.HttpContext "+convert(p2)+" stacktrace: "+stackTrace+"");
        hookPlugin.before("org.apache.http.impl.client.AbstractHttpClient");
        return OriginalMethod.by(new $() {}).invoke(_this, p0, p1, p2);
    }
    


  //endregion


}
