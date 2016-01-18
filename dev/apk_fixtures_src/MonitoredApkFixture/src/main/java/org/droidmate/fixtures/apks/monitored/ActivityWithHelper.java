// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.fixtures.apks.monitored;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncInfo;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class ActivityWithHelper extends Activity
{
  public void crashActivity(String tag)
  {
    Log.i(tag, "Crashing the app");
    throw new RuntimeException("Crash! Throwing new RuntimeException. Log tag: "+ tag);
  }

  public void launchGooglePlay(String tag)
  {
    Log.i(tag, "Opening Google Play");
    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps"));
    startActivity(intent);
  }

  protected void launchHome(String tag)
  {
    Log.i(tag, "Exiting the app");
    // Based on: http://stackoverflow.com/a/3226743/986533
    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  protected void callAPI_Camera_open(String tag)
  {
    Log.i(tag, "===== Calling Camera.open()");
    // The call below requires permission: android.permission.CAMERA
    Camera camera = Camera.open();
    Log.i(tag, "===== Camera.open() returned: " + camera);
  }

  protected void callAPI_URL_openConnection(String tag)
  {
    try
    {
      Log.i(tag, "===== Calling new URL(\"http://www.google.com\").openConnection()");
      final URL url = new URL("http://www.google.com");
      URLConnection conn = url.openConnection();
      Log.i(tag, "===== new URL(\"http://www.google.com\").openConnection() returned" + conn);

    } catch (IOException e)
    {
      Log.wtf("URL.openConnection() threw an IOException", e);
    }
  }

  protected void callAPI_AudioManager_isBluetoothA2dpOn(String tag)
  {
    final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    Log.i(tag, "===== Calling AudioManager.isBluetoothA2dpOn()");
    audioManager.isBluetoothA2dpOn();
  }

  protected void callAPI_ContentResolver_getCurrentSyncs(String tag)
  {
    Log.i(tag, "===== Calling ContentResolver.getCurrentSyncs()");
    // The calls below requires permission: android.permission.READ_SYNC_STATS
    final List<SyncInfo> currentSyncs = ContentResolver.getCurrentSyncs();
    Log.i(tag, "===== ContentResolver.getCurrentSyncs() returned: " + currentSyncs);
  }

  protected void callAPI_ConnectivityManager_getActiveNetworkInfo(String tag)
  {
    final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    Log.i(tag, "===== Calling ConnectivityManager.getActiveNetworkInfo()");
    // The call below requires permission: android.permission.ACCESS_NETWORK_STATE
    final NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
    Log.i(tag, "===== ConnectivityManager.getActiveNetworkInfo() returned:" + activeNetworkInfo);
  }

  protected void callAPI_TelephonyManager_getCellLocation(String tag)
  {
    final TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    // The call below requires permission: android.permission.ACCESS_COARSE_LOCATION
    Log.i(tag, "===== Calling TelephonyManager.getCellLocation()");
    final CellLocation cellLocation = tm.getCellLocation();
    Log.i(tag, "===== TelephonyManager.getCellLocation() returned: " + cellLocation);
  }
}
