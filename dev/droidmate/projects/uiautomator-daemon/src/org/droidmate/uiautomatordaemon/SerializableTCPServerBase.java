// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.uiautomatordaemon;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import org.droidmate.common_android.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public abstract class SerializableTCPServerBase<ServerInputT extends Serializable, ServerOutputT extends Serializable>
{

  private int port;
  ServerSocket serverSocket;

  private String serverStartMessageTag;
  private String serverStartMessage;

  private final static String tagBase = Constants.deviceLogcatTagPrefix + "server_base";

  protected SerializableTCPServerBase(String serverStartMessageTag, String serverStartMessage)
  {
    this.serverStartMessageTag = serverStartMessageTag;
    this.serverStartMessage = serverStartMessage;
  }

  protected abstract ServerOutputT OnServerRequest(ServerInputT input, Exception inputReadEx);

  protected abstract boolean shouldCloseServerSocket(ServerInputT serverInput);

  public Thread start(int port) throws InterruptedException
  {
    this.port = port;
    ServerRunnable serverRunnable = new ServerRunnable();
    Thread serverThread = new Thread(serverRunnable);

    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (serverRunnable)
    {
      if (serverSocket != null) throw new AssertionError();
      serverThread.start();
      serverRunnable.wait();
      if (serverSocket == null) throw new AssertionError();
    }
    Log.i(serverStartMessageTag, serverStartMessage);

    return serverThread;

  }

  @TargetApi(Build.VERSION_CODES.FROYO)
  public void close()
  {
    try
    {
      // KJA
      Log.i("DEBUG_XXX", "serverSocket.close();");
      serverSocket.close();
    } catch (IOException e)
    {
      Log.i("DEBUG_XXX", "Log.wtf(thisClassName, \"Failed to close SerializableTCPServerBase.\");");
      Log.wtf(tagBase, "Failed to close SerializableTCPServerBase.");
    }
  }

  // Used in org.droidmate.uiautomatordaemon.UiAutomatorDaemon.init()
  public boolean isClosed()
  {
    return serverSocket.isClosed();
  }

  private class ServerRunnable implements Runnable
  {

    private final String tagRunnable = Constants.deviceLogcatTagPrefix + "server_runnable" + port;


    public void run()
    {

      Log.d(tagRunnable, "Started ServerRunnable.");
      try
      {

        // Synchronize to ensure the parent thread (the one which started this one) will continue only after the
        // serverSocket is initialized.
        synchronized (this)
        {
          serverSocket = new ServerSocket(port);
          this.notify();
        }

        while (!serverSocket.isClosed())
        {
          // KJA2 DEBUG_XXX
          Log.d(tagRunnable, String.format("Accepting socket from client on port %s...", port));
          Log.i("DEBUG_XXX", "serverSocket.accept()");
          Socket clientSocket = serverSocket.accept();
          Log.v(tagRunnable, "Socket accepted.");

          Log.i("DEBUG_XXX", "ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());");
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
          Log.i("DEBUG_XXX", "Output.flush()");
          output.flush();

          Log.i("DEBUG_XXX", "ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());");
          ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

          ServerInputT serverInput = null;

          Exception serverInputReadEx = null;

          try
          {
            Log.i("DEBUG_XXX", "ServerInputT localVarForSuppressionAnnotation = (ServerInputT) input.readObject();");
            @SuppressWarnings("unchecked") // Without this var here, there is no place to put the "unchecked" suppression warning.
              ServerInputT localVarForSuppressionAnnotation = (ServerInputT) input.readObject();
            serverInput = localVarForSuppressionAnnotation;

          } catch (ClassNotFoundException e)
          {
            Log.i("DEBUG_XXX", "serverInputReadEx = handleInputReadObjectException(input, e); " + e);
            serverInputReadEx = handleInputReadObjectException(input, e);
          } catch (IOException e)
          {
            Log.i("DEBUG_XXX", "serverInputReadEx = handleInputReadObjectException(input, e); " + e);
            serverInputReadEx = handleInputReadObjectException(input, e);
          }

          ServerOutputT serverOutput;
          Log.i("DEBUG_XXX", "serverOutput = OnServerRequest(serverInput, serverInputReadEx);");
          serverOutput = OnServerRequest(serverInput, serverInputReadEx);
          Log.i("DEBUG_XXX", "output.writeObject(serverOutput);");
          output.writeObject(serverOutput);
          Log.i("DEBUG_XXX", "clientSocket.close();");
          clientSocket.close();

          if (shouldCloseServerSocket(serverInput))
            close();
        }

        Log.i(tagRunnable, "Closed ServerRunnable.");

      } catch (SocketTimeoutException e)
      {
        Log.i("DEBUG_XXX", "Log.e(serverRunnableClassName, \"Closing ServerRunnable due to a timeout.\", e); " + e);
        Log.e(tagRunnable, "Closing ServerRunnable due to a timeout.", e);
        close();
      } catch (IOException e)
      {
        Log.i("DEBUG_XXX", "Log.e(serverRunnableClassName, \"Exception was thrown while operating DroidmateServer\", e); " + e);
        Log.e(tagRunnable, "Exception was thrown while operating DroidmateServer", e);
      }
    }

    private Exception handleInputReadObjectException(ObjectInputStream input, Exception e) throws IOException
    {
      Exception serverInputReadEx;
      Log.e(tagRunnable, "Exception was thrown while reading input sent to DroidmateServer from " +
        "client through socket.", e);
      serverInputReadEx = e;
      input.close();
      return serverInputReadEx;
    }
  }

}
