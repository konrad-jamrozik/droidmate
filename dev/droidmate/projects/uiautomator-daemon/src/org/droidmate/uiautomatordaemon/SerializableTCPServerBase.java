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
      Log.v(tagBase, "serverSocket.close();");
      serverSocket.close();
    } catch (IOException e)
    {
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
      Log.d(tagRunnable, "run()");
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
          Log.v(tagRunnable, "serverSocket.accept("+port+")");
          Socket clientSocket = serverSocket.accept();

          Log.v(tagRunnable, "ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());");
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
          Log.v(tagRunnable, "Output.flush()");
          output.flush();

          Log.v(tagRunnable, "input = new ObjectInputStream(clientSocket.getInputStream());");
          ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

          ServerInputT serverInput = null;

          Exception serverInputReadEx = null;

          //noinspection TryWithIdenticalCatches
          try
          {
            Log.v(tagRunnable, "input.readObject();");
            @SuppressWarnings("unchecked") // Without this var here, there is no place to put the "unchecked" suppression warning.
              ServerInputT localVarForSuppressionAnnotation = (ServerInputT) input.readObject();
            serverInput = localVarForSuppressionAnnotation;

          } catch (ClassNotFoundException e)
          {
            serverInputReadEx = handleInputReadObjectException(input, e);
          } catch (IOException e)
          {
            serverInputReadEx = handleInputReadObjectException(input, e);
          }

          ServerOutputT serverOutput;
          Log.v(tagRunnable, "serverOutput = OnServerRequest(serverInput, serverInputReadEx);");
          serverOutput = OnServerRequest(serverInput, serverInputReadEx);
          Log.v(tagRunnable, "output.writeObject(serverOutput);");
          output.writeObject(serverOutput);
          Log.v(tagRunnable, "clientSocket.close();");
          clientSocket.close();

          if (shouldCloseServerSocket(serverInput))
            close();
        }

        Log.i(tagRunnable, "Closed ServerRunnable.");

      } catch (SocketTimeoutException e)
      {
        Log.e(tagRunnable, "Closing ServerRunnable due to a timeout.", e);
        close();
      } catch (IOException e)
      {
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
