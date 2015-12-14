// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.uiautomatordaemon;

import android.util.Log;

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

  private final static String thisClassName = SerializableTCPServerBase.class.getSimpleName();

  protected SerializableTCPServerBase(String serverStartMessageTag, String serverStartMessage)
  {
    this.serverStartMessageTag = serverStartMessageTag;
    this.serverStartMessage = serverStartMessage;
  }

  protected abstract ServerOutputT OnServerRequest(ServerInputT input, Exception inputReadEx);

  protected abstract boolean shouldCloseServerSocket(ServerInputT serverInput);

  // Used in org.droidmate.uiautomatordaemon.UiAutomatorDaemon.init()
  public Thread start(int port) throws InterruptedException
  {

    try
    {
      this.port = port;
      ServerRunnable serverRunnable = new ServerRunnable();
      Thread serverThread = new Thread(serverRunnable);

      //noinspection SynchronizationOnLocalVariableOrMethodParameter
      synchronized (serverRunnable)
      {
        assert (serverSocket == null);
        serverThread.start();
        serverRunnable.wait();
        assert (serverSocket != null);
      }
      Log.i(serverStartMessageTag, serverStartMessage);

      return serverThread;

    } catch (InterruptedException e)
    {
      throw e;
    }
  }

  public void close()
  {
    try
    {
      serverSocket.close();
    } catch (IOException e)
    {
      Log.wtf(thisClassName, "Failed to close SerializableTCPServerBase.");
    }
  }

  // Used in org.droidmate.uiautomatordaemon.UiAutomatorDaemon.init()
  public boolean isClosed()
  {
    return serverSocket.isClosed();
  }

  private class ServerRunnable implements Runnable
  {

    private final String serverRunnableClassName = ServerRunnable.class.getSimpleName() + port;

    public void run()
    {

      Log.d(serverRunnableClassName, "Started ServerRunnable.");
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
          Log.d(serverRunnableClassName, String.format("Accepting socket from client on port %s...", port));
          Socket clientSocket = serverSocket.accept();
          Log.v(serverRunnableClassName, "Socket accepted.");

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

          ServerInputT serverInput = null;

          Exception serverInputReadEx = null;

          try
          {
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
          serverOutput = OnServerRequest(serverInput, serverInputReadEx);
          output.writeObject(serverOutput);
          clientSocket.close();

          if (shouldCloseServerSocket(serverInput))
            close();
        }

        Log.i(serverRunnableClassName, "Closed ServerRunnable.");

      } catch (SocketTimeoutException e)
      {
        Log.e(serverRunnableClassName, "Closing ServerRunnable due to a timeout.", e);
        close();
      } catch (IOException e)
      {
        Log.e(serverRunnableClassName, "Exception was thrown while operating DroidmateServer", e);
      }
    }

    private Exception handleInputReadObjectException(ObjectInputStream input, Exception e) throws IOException
    {
      Exception serverInputReadEx;
      Log.e(serverRunnableClassName, "Exception was thrown while reading input sent to DroidmateServer from " +
        "client through socket.", e);
      serverInputReadEx = e;
      input.close();
      return serverInputReadEx;
    }
  }

}
