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

// org.droidmate.monitor.MonitorSrcTemplate:REMOVE_LINES
package org.droidmate.monitor;
// org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES
// package org.droidmate.monitor;
// org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import org.droidmate.apis.Api;
import org.droidmate.misc.MonitorConstants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

// org.droidmate.monitor.MonitorSrcTemplate:API_19_UNCOMMENT_LINES
// import de.uds.infsec.instrumentation.Instrumentation;
// import de.uds.infsec.instrumentation.annotation.Redirect;
// import de.uds.infsec.instrumentation.util.Signature;
// org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES

// org.droidmate.monitor.MonitorSrcTemplate:API_23_UNCOMMENT_LINES
// import de.larma.arthook.*;
// org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES

// org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES
// import org.droidmate.monitor.IMonitorHook;
// import org.droidmate.monitor.MonitorHook;
// org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES

/**<p>
 * This class will be used by {@code MonitorGenerator} to create {@code Monitor.java} deployed on the device. This class will be
 * first copied by appropriate gradle task of monitor-generator project to its resources dir. Then it will be handled to
 * {@code org.droidmate.monitor.MonitorSrcTemplate} for further processing.
 *
 * </p><p>
 * Note that the final generated version of this file, after running {@code :projects:monitor-generator:build}, will be placed in
 * <pre><code>
 *   [repo root]\dev\droidmate\projects\monitor-generator\monitor-apk-scaffolding\src\org\droidmate\monitor_generator\generated\Monitor.java
 * </code></pre>
 *
 * </p><p>
 * To check if the process of converting this file to a proper {@code Monitor.java} works correctly, see:
 * {@code org.droidmate.monitor.MonitorGeneratorFrontendTest#Generates DroidMate monitor()}.
 *
 * </p><p>
 * Note: The resulting class deployed to the device will be compiled with legacy ant script from Android SDK that supports only
 * Java 5.
 *
 * </p><p>
 *   See also:<br/>
 *     {@code org.droidmate.monitor.MonitorSrcTemplate}<br/>
 *     {@code org.droidmate.monitor.RedirectionsGenerator}
 * </p>
 */
@SuppressLint("NewApi")
@SuppressWarnings("Convert2Diamond")
// !!! DUPLICATION WARNING !!! of class name and location with the build.gradle script of monitor-generator
// org.droidmate.monitor.MonitorSrcTemplate:REMOVE_LINES
public class MonitorJavaTemplate
// org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES
// public class Monitor
// org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES
{
  //region Class init code
  // org.droidmate.monitor.MonitorSrcTemplate:REMOVE_LINES
  public MonitorJavaTemplate()
  // org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES
  // public Monitor()
  // org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES
  {
    Log.v(MonitorConstants.tag_mjt, MonitorConstants.msg_ctor_start);
    try
    {
      server = startMonitorTCPServer();
      Log.i(MonitorConstants.tag_mjt, MonitorConstants.msg_ctor_success + server.port);

    } catch (Throwable e)
    {
      Log.e(MonitorConstants.tag_mjt, MonitorConstants.msg_ctor_failure, e);
    }
  }

  private static MonitorTcpServer server;
  private static Context          context;

  /**
   * Called by the inlined Application class when the inlined AUE launches activity, as done by
   * org.droidmate.exploration.device.IRobustDevice#launchApp(org.droidmate.android_sdk.IApk)
   */
  @SuppressWarnings("unused")
  public void init(android.content.Context initContext)
  {
    Log.v(MonitorConstants.tag_mjt, "init(): entering");
    context = initContext;
    if (server == null)
    {
      Log.w(MonitorConstants.tag_mjt, "init(): didn't set context for MonitorTcpServer, as the server is null.");
    }
    else
    {
      server.context = context;
    }

    // org.droidmate.monitor.MonitorSrcTemplate:API_19_UNCOMMENT_LINES
    // Instrumentation.processClass(Monitor.class);
    // org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES
    
    // org.droidmate.monitor.MonitorSrcTemplate:API_23_UNCOMMENT_LINES
    // ArtHook.hook(Monitor.class);
    // org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES

    redirectConstructors();

    // org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES
    // monitorHook.init(context);
    // org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES

    Log.d(MonitorConstants.tag_mjt, MonitorConstants.msgPrefix_init_success + context.getPackageName());
  }
  //endregion

  //region TCP server code

  @SuppressWarnings("ConstantConditions")
  private static MonitorTcpServer startMonitorTCPServer() throws Throwable
  {
    Log.v(MonitorConstants.tag_mjt, "startMonitorTCPServer(): entering");

    MonitorTcpServer tcpServer = new MonitorTcpServer();

    Thread serverThread = null;
    Integer portUsed = null;

    final Iterator<Integer> portsIterator = MonitorConstants.serverPorts.iterator();
    
    while (portsIterator.hasNext() && serverThread == null)
    {
      int port = portsIterator.next();
      serverThread = tcpServer.tryStart(port);
      if (serverThread != null)
        portUsed = port;
    }
    if (serverThread == null)
    {
      if (portsIterator.hasNext()) throw new AssertionError();
      throw new Exception("startMonitorTCPServer(): no available ports.");
    }

    if (serverThread == null) throw new AssertionError();
    if (portUsed == null) throw new AssertionError();
    if (tcpServer.isClosed()) throw new AssertionError();

    Log.d(MonitorConstants.tag_mjt, "startMonitorTCPServer(): SUCCESS portUsed: " + portUsed + " PID: " + getPid());
    return tcpServer;
  }

  static class MonitorTcpServer extends TcpServerBase<String, ArrayList<ArrayList<String>>>
  {

    public Context context;

    protected MonitorTcpServer()
    {
      super();
    }

    @Override
    protected ArrayList<ArrayList<String>> OnServerRequest(String input)
    {
      synchronized (currentLogs)
      {
        validateLogsAreNotFromMonitor(currentLogs);

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

          Log.d(MonitorConstants.tag_srv, "getTime: " + time);
          return new ArrayList<ArrayList<String>>(Collections.singletonList(payload));

        } else if (MonitorConstants.srvCmd_close.equals(input))
        {
          // org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES
          // monitorHook.finalizeMonitorHook();
          // org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES
          
          // In addition to the logic above, this command is handled in 
          // org.droidmate.monitor.MonitorJavaTemplate.MonitorTcpServer.shouldCloseServerSocket
          
          return new ArrayList<ArrayList<String>>();

        } else
        {
          Log.e(MonitorConstants.tag_srv, "! Unexpected command from DroidMate TCP client. The command: " + input);
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
     * This method ensures the logs do not come from messages logged by the MonitorTcpServer or 
     * MonitorJavaTemplate itself. This would be a bug and thus it will cause an assertion failure in this method.
     *
     * </p>
     * @param currentLogs
     * Currently recorded set of monitored logs that will be validated, causing AssertionError if validation fails.
     */
    private void validateLogsAreNotFromMonitor(List<ArrayList<String>> currentLogs)
    {
      for (ArrayList<String> log : currentLogs)
      {
        // ".get(2)" gets the payload. For details, see the doc of the param passed to this method.
        String msgPayload = log.get(2);
        failOnLogsFromMonitorTCPServerOrMonitorJavaTemplate(msgPayload);

      }
    }

    private void failOnLogsFromMonitorTCPServerOrMonitorJavaTemplate(String msgPayload)
    {
      if (msgPayload.contains(MonitorConstants.tag_srv) || msgPayload.contains(MonitorConstants.tag_mjt))
        throw new AssertionError(
          "Attempt to log a message whose payload contains " +
            MonitorConstants.tag_srv + " or " + MonitorConstants.tag_mjt + ". The message payload: " + msgPayload);
    }

    @Override
    protected boolean shouldCloseServerSocket(String serverInput)
    {
      return MonitorConstants.srvCmd_close.equals(serverInput);
    }
  }

  // !!! DUPLICATION WARNING !!! with org.droidmate.uiautomator_daemon.UiautomatorDaemonTcpServerBase
  static abstract class TcpServerBase<ServerInputT extends Serializable, ServerOutputT extends Serializable>
  {
    int port;
    private ServerSocket    serverSocket          = null;
    private SocketException serverSocketException = null;

    protected TcpServerBase()
    {
      super();
    }

    protected abstract ServerOutputT OnServerRequest(ServerInputT input);

    protected abstract boolean shouldCloseServerSocket(ServerInputT serverInput);

    public Thread tryStart(int port) throws Exception
    {
      Log.v(MonitorConstants.tag_srv, String.format("tryStart(port:%d): entering", port));
      this.serverSocket = null;
      this.serverSocketException = null;
      this.port = port;

      MonitorServerRunnable monitorServerRunnable = new MonitorServerRunnable();
      Thread serverThread = new Thread(monitorServerRunnable);
      // For explanation why this synchronization is necessary, see MonitorServerRunnable.run() method synchronized {} block.
      synchronized (monitorServerRunnable)
      {
        if (!(serverSocket == null && serverSocketException == null)) throw new AssertionError();
        serverThread.start();
        monitorServerRunnable.wait();
        // Either a serverSocket has been established, or an exception was thrown, but not both.
        //noinspection SimplifiableBooleanExpression
        if (!(serverSocket != null ^ serverSocketException != null)) throw new AssertionError();
      }
      if (serverSocketException != null)
      {

        if ("bind failed: EADDRINUSE (Address already in use)".equals(serverSocketException.getCause().getMessage()))
        {
          Log.v(MonitorConstants.tag_srv, "tryStart(port:"+port+"): FAILURE Failed to start TCP server because " +
            "'bind failed: EADDRINUSE (Address already in use)'. " +
            "Returning null Thread.");

          return null;

        } else
        {
          throw new Exception(String.format("Failed to start monitor TCP server thread for port %s. " +
              "Cause of this exception is the one returned by the failed thread.", port),
            serverSocketException);
        }
      }
      
      Log.d(MonitorConstants.tag_srv, "tryStart(port:"+port+"): SUCCESS");
      return serverThread;
    }

    public void closeServerSocket()
    {
      try
      {
        serverSocket.close();
        Log.d(MonitorConstants.tag_srv, String.format("serverSocket.close(): SUCCESS port %s", port));
        
      } catch (IOException e)
      {
        Log.e(MonitorConstants.tag_srv, String.format("serverSocket.close(): FAILURE port %s", port));
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

        Log.v(MonitorConstants.tag_run, String.format("run(): entering port:%d", port));
        try
        {

          // Synchronize to ensure the parent thread (the one which started this one) will continue only after one of these two
          // is true:
          // - serverSocket was successfully initialized 
          // - exception was thrown and assigned to a field and  this thread exitted
          synchronized (this)
          {
            try
            {
              Log.v(MonitorConstants.tag_run, String.format("serverSocket = new ServerSocket(%d)", port));
              serverSocket = new ServerSocket(port);
              Log.v(MonitorConstants.tag_run, String.format("serverSocket = new ServerSocket(%d): SUCCESS", port));
            } catch (SocketException e)
            {
              serverSocketException = e;
            }

            if (serverSocketException != null)
            {
              Log.d(MonitorConstants.tag_run, "serverSocket = new ServerSocket("+port+"): FAILURE " +
                "aborting further thread execution.");
              this.notify();
              return;
            } else
            {
              this.notify();
            }
          }

          if (serverSocket == null) throw new AssertionError();
          if (serverSocketException != null) throw new AssertionError();

          while (!serverSocket.isClosed())
          {
            Log.v(MonitorConstants.tag_run, String.format("clientSocket = serverSocket.accept() / port:%d", port));
            Socket clientSocket = serverSocket.accept();
            Log.v(MonitorConstants.tag_run, String.format("clientSocket = serverSocket.accept(): SUCCESS / port:%d", port));

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
              Log.e(MonitorConstants.tag_run, "! serverInput = input.readObject(): FAILURE " +
                "while reading from clientSocket on port "+port +". Closing server socket.", e);
              closeServerSocket();
              break;
            }

            ServerOutputT serverOutput;
            Log.d(MonitorConstants.tag_run, String.format("OnServerRequest(%s) / port:%d", serverInput, port));
            serverOutput = OnServerRequest(serverInput);
            output.writeObject(serverOutput);
            clientSocket.close();

            if (shouldCloseServerSocket(serverInput))
            {
              Log.v(MonitorConstants.tag_run, String.format("shouldCloseServerSocket(): true / port:%d", port));
              closeServerSocket();
            }
          }
          
          if (!serverSocket.isClosed()) throw new AssertionError();

          Log.v(MonitorConstants.tag_run, String.format("serverSocket.isClosed() / port:%d", port));

        } catch (SocketTimeoutException e)
        {
          Log.e(MonitorConstants.tag_run, "! Closing monitor TCP server due to a timeout.", e);
          closeServerSocket();
        } catch (IOException e)
        {
          Log.e(MonitorConstants.tag_run, "! Exception was thrown while operating monitor TCP server.", e);
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

  static String convert(Object param)
  {
    if (param == null)
      return "null";

    String paramStr;
    if (param.getClass().isArray())
    {
      StringBuilder sb = new StringBuilder("[");
      boolean first = true;

      Object[] objects = convertToObjectArray(param);

      for (Object obj : objects)
      {

        if (!first)
          sb.append(",");
        first = false;

        sb.append(String.format("%s", obj));
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

  // Copied from http://stackoverflow.com/a/16428065/986533
  private static Object[] convertToObjectArray(Object array)
  {
    Class ofArray = array.getClass().getComponentType();
    if (ofArray.isPrimitive())
    {
      List<Object> ar = new ArrayList<>();
      int length = Array.getLength(array);
      for (int i = 0; i < length; i++)
      {
        ar.add(Array.get(array, i));
      }
      return ar.toArray();
    } else
    {
      return (Object[]) array;
    }
  }

  private static final SimpleDateFormat monitor_time_formatter = new SimpleDateFormat(MonitorConstants.monitor_time_formatter_pattern, MonitorConstants.monitor_time_formatter_locale);

  /**
   * <p>
   * Called by monitor code to log Android API calls. Calls to this methods are generated in:
   * <pre>
   * org.droidmate.monitor.RedirectionsGenerator#generateCtorCallsAndTargets(java.util.List)
   * org.droidmate.monitor.RedirectionsGenerator#generateMethodTargets(java.util.List)</pre>
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
//      Log.v(tag_mjt, "addCurrentLogs(" + payload + ")");
      String now = getNowDate();

//      Log.v(tag_mjt, "currentLogs.add(new ArrayList<String>(Arrays.asList(getPid(), now, payload)));");
      currentLogs.add(new ArrayList<String>(Arrays.asList(getPid(), now, payload)));

//      Log.v(tag_mjt, "addCurrentLogs(" + payload + "): DONE");
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
//    Log.v(tag_mjt, "final Date nowDate = new Date(startDate.getTime() + (System.nanoTime() - startNanoTime) / 1000000);");
    final Date nowDate = new Date(startDate.getTime() + (System.nanoTime() - startNanoTime) / 1000000);

//    Log.v(tag_mjt, "final String formattedDate = monitor_time_formatter.format(nowDate);");
    final String formattedDate = monitor_time_formatter.format(nowDate);

//    Log.v(tag_mjt, "return formattedDate;");
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

  //region Hook code
  // org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES
  // public static IMonitorHook monitorHook = new MonitorHook();
  // org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES
  //endregion
  
  //region Generated code

  private static void redirectConstructors()
  {
    // org.droidmate.monitor.MonitorSrcTemplate:REMOVE_LINES
    ClassLoader[] classLoaders = {Thread.currentThread().getContextClassLoader(), MonitorJavaTemplate.class.getClassLoader()};
    // org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES
    // ClassLoader[] classLoaders = {Thread.currentThread().getContextClassLoader(), Monitor.class.getClassLoader()};
    // org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES

    // GENERATED_CODE_INJECTION_POINT:CTOR_REDIR_CALLS
  }

  // GENERATED_CODE_INJECTION_POINT:CTOR_REDIR_TARGETS

  // GENERATED_CODE_INJECTION_POINT:METHOD_REDIR_TARGETS

  //endregion


}

