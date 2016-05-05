// Copyright (c) 2013-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.fixtures.apks.instrumented;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static android.R.attr.permission;
import static android.util.Log.i;

public class InstrumentedActivity extends Activity
{
  public static final String label_button_android_apis = "Call Android APIs";
  public static final String label_button_app_methods  = "Call app methods";

  public static final String LOG_TAG = InstrumentedActivity.class.getSimpleName();

  private boolean instrumentationIsInitialized = false;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(org.droidmate.fixtures.apks.instrumented.R.layout.activity_instrumented);

    Button button;
    button = (Button) findViewById(org.droidmate.fixtures.apks.instrumented.R.id.button_android_apis);
    button.setText(label_button_android_apis);

    button = (Button) findViewById(org.droidmate.fixtures.apks.instrumented.R.id.button_app_methods);
    button.setText(label_button_app_methods);

    if (!instrumentationIsInitialized)
    {
      instrumentationIsInitialized = true;
      InstrumentationClass.instrument();
    }
  }

  private boolean hasPermission(String permission)
  {
    // The call below requires permission: android.permission.CAMERA
    i(LOG_TAG, "Application requested permission " + permission);
    int hasPermission = this.checkSelfPermission(permission);
    if (hasPermission != PackageManager.PERMISSION_GRANTED)
    {
      i(LOG_TAG, "Missing runtime permission " + permission + ", requesting and finishing method");
      this.requestPermissions(new String[] {permission}, 1);
      return false;
    }

    return true;
  }

  public void callAndroidAPIs(View view)
  {
    i(LOG_TAG, "====== Calling instrumented Android APIs ======");

    // The calls below require the following permissions:
    //    android.permission.CAMERA
    //    android.permission.ACCESS_NETWORK_STATE
    if (!hasPermission(Manifest.permission.CAMERA))
      return;

    if (!hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
      return;

    try
    {
      Camera cam = Camera.open(0);
      Log.i(LOG_TAG, "Camera.open() returned: " + cam);

      if (cam != null)
        cam.release();
    }
    catch(Exception e){
      Log.wtf(LOG_TAG, "Camera.open() threw an Exception", e);
    }

    try
    {
      final URL url = new URL("http://www.google.com");
      URLConnection conn = url.openConnection();
      Log.i(LOG_TAG, "URL.openConnection() returned: " + conn);

    } catch (IOException e)
    {
      Log.wtf("URL.openConnection() threw an IOException", e);
    }

    final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    try
    {
      final NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
      Log.i(LOG_TAG, "ConnectivityManager.getActiveNetworkInfo() returned:" + activeNetworkInfo);
    }
    catch(Exception e)
    {
      Log.wtf(LOG_TAG, "ConnectivityManager.getActiveNetworkInfo() threw an Exception", e);
    }

    try
    {
      final boolean activeNetworkMetered = cm.isActiveNetworkMetered();
      Log.i(LOG_TAG, "ConnectivityManager.isActiveNetworkMetered() returned:" + activeNetworkMetered);
    }
    catch(Exception e)
    {
      Log.wtf(LOG_TAG, "ConnectivityManager.isActiveNetworkMetered() threw an Exception", e);
    }

    try
    {
    // The calls below require permission: android.permission.READ_SYNC_STATS
      final List<SyncInfo> currentSyncs = ContentResolver.getCurrentSyncs();
      Log.i(LOG_TAG, "ContentResolver.getCurrentSyncs() returned: " + currentSyncs);
    }
    catch(Exception e)
    {
      Log.wtf(LOG_TAG, "ContentResolver.getCurrentSyncs() threw an Exception", e);
    }

    try
    {
      final TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
      // The calls below require permission: android.permission.ACCESS_COARSE_LOCATION
      CellLocation cellLocation = tm.getCellLocation();
      Log.i(LOG_TAG, "TelephonyManager.getCellLocation() returned: " + cellLocation);
    }
    catch(Exception e)
    {
      Log.wtf(LOG_TAG, "TelephonyManager.getCellLocation() threw an Exception", e);
    }

    i(LOG_TAG, "====== DONE calling instrumented Android APIs ======");
  }

  public void callAppMethods(View view)
  {
    i(LOG_TAG, "====== Calling instrumented app methods ======");

    i(LOG_TAG, "Calling constructor");
    AppInstrumentationTargets appInstrTargets = new AppInstrumentationTargets();

    i(LOG_TAG, "Calling publicVoidMethod");
    appInstrTargets.publicVoidMethod();

    i(LOG_TAG, "Calling nonpublicVoidMethod");
    appInstrTargets.nonpublicVoidMethod();

    i(LOG_TAG, "Calling advancedMethodCaller()");
    AppInstrumentationTargets.ReturnObject returnObject = appInstrTargets.advancedMethodCaller();
    i(LOG_TAG, "advancedMethodCaller returned: " + returnObject);

    i(LOG_TAG, "====== DONE calling instrumented app methods ======");
  }
}
