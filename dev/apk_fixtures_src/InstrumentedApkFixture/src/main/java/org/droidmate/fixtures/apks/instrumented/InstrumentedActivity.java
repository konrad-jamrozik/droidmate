// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.fixtures.apks.instrumented;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncInfo;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.droidmate.fixtures.apks.lib.ApkFixturesConstants;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

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
    setContentView(R.layout.activity_instrumented);

    Button button;
    button = (Button) findViewById(R.id.button_android_apis);
    button.setText(label_button_android_apis);

    button = (Button) findViewById(R.id.button_app_methods);
    button.setText(label_button_app_methods);

    if (!instrumentationIsInitialized)
    {
      instrumentationIsInitialized = true;
      InstrumentationClass.instrument();
    }
  }

  public void callAndroidAPIs(View view)
  {
    Log.i(LOG_TAG, "====== Calling instrumented Android APIs ======");
    // The call below requires permission: android.permission.CAMERA
    Camera cam = Camera.open(0);
    Log.i(LOG_TAG, "Camera.open() returned: " + cam);
    cam.release();

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
    // The calls below require permission: android.permission.ACCESS_NETWORK_STATE
    final NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
    Log.i(LOG_TAG, "ConnectivityManager.getActiveNetworkInfo() returned:" + activeNetworkInfo);

    final boolean activeNetworkMetered = cm.isActiveNetworkMetered();
    Log.i(LOG_TAG, "ConnectivityManager.isActiveNetworkMetered() returned:" + activeNetworkMetered);

    // The calls below require permission: android.permission.READ_SYNC_STATS
    final List<SyncInfo> currentSyncs = ContentResolver.getCurrentSyncs();
    Log.i(LOG_TAG, "ContentResolver.getCurrentSyncs() returned: " + currentSyncs);

    final TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    // The calls below require permission: android.permission.ACCESS_COARSE_LOCATION
    final CellLocation cellLocation = tm.getCellLocation();
    Log.i(LOG_TAG, "TelephonyManager.getCellLocation() returned: " + cellLocation);

    Log.i(LOG_TAG, "====== DONE calling instrumented Android APIs ======");
  }

  private AppInstrumentationTargets appInstrTargets = new AppInstrumentationTargets();

  public void callAppMethods(View view)
  {
    Log.i(LOG_TAG, "====== Calling instrumented app methods ======");

    Log.i(LOG_TAG, "Calling publicVoidMethod");
    appInstrTargets.publicVoidMethod();

    Log.i(LOG_TAG, "Calling nonpublicVoidMethod");
    appInstrTargets.nonpublicVoidMethod();

    Log.i(LOG_TAG, "Calling advancedMethodCaller()");
    AppInstrumentationTargets.ReturnObject returnObject = appInstrTargets.advancedMethodCaller();
    Log.i(LOG_TAG, "advancedMethodCaller returned: " + returnObject);

    Log.i(LOG_TAG, "====== DONE calling instrumented app methods ======");
  }
}
