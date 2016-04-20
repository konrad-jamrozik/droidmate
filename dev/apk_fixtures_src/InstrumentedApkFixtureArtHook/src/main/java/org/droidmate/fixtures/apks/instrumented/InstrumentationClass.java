// Copyright (c) 2013-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.fixtures.apks.instrumented;

import android.content.SyncInfo;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;
import de.larma.arthook.*;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Class the add the instrumentation to default methods for testing
 *
 * When requesting for runtime permissions Android 6.0 open a Popup window
 * (resource-id com.android.packageinstaller:id/dialog_container
 *  package com.google.android.packageinstaller) with options:
 *   Deny (com.android.packageinstaller:id/permission_deny_button)
 *   Allow (com.android.packageinstaller:id/permission_allow_button)
 *
 */
public class InstrumentationClass
{
  public static final String API = "Monitored_API_method_call";

  /**
   * Start the API instrumentation (by linking (ArtHook)
   *
   */
  public static void instrument()
  {
    ArtHook.hook(InstrumentationClass.class);
  }

  /**
   * Add a hook to the method {@link android.hardware.Camera#open} for testing.
   *
   * @param cameraId the hardware camera to access, between 0 and
   *     {@link android.hardware.Camera#getNumberOfCameras()}-1.
   * @return a new Camera object, connected, locked and ready for use.
   */
  @Hook("android.hardware.Camera->open")
  public static Camera Camera_open(int cameraId) {
    Log.i(API, "Camera_open_redirection() called statically. cameraId = " + cameraId);

    return OriginalMethod.by(new $() {}).invokeStatic(cameraId);
  }

  /**
   * Add a hook to the public method {@link org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets#advancedMethod}
   * for testing
   *
   * @param _this Instance of {@link org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets} containing the method to be called
   * @param param1 First method param
   * @param param2 Second method param
   * @param param3 Third method param
   *
   * @return A {@link org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets.ReturnObject with the input parameter values
   */
  @Hook("org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets->advancedMethod")
  public static AppInstrumentationTargets.ReturnObject internalMethod_redirection(
    AppInstrumentationTargets _this, int param1, String param2, AppInstrumentationTargets.ParamObject param3)
  {
    final String msg = String.format("advancedMethod() called. _this = %s, param1=%d, param2=%s, param3=%s", _this, param1, param2, param3);
    Log.i(API, msg);

    AppInstrumentationTargets.ReturnObject retObj =
      OriginalMethod.by(new $() {}).invoke(_this, param1, param2, param3);
    Log.i(API, "advancedMethod() returning. retObj = " + retObj);
    return retObj;
  }

  /**
   * Add a hook to the public void method {@link org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets#publicVoidMethod}
   * for testing
   *
   * @param _this Instance of {@link org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets} containing the method to be called
   */
  @Hook("org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets->publicVoidMethod")
  public static void publicVoidMethod_redirection(AppInstrumentationTargets _this)
  {
    Log.i(API, "AppInstrumentationTargets_publicVoidMethod_redirection() called. _this = " + _this);
  }

  /**
   * Add a hook to the protected void method {@link org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets#nonpublicVoidMethod}
   * for testing
   *
   * @param _this Instance of {@link org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets} containing the method to be called
   */
  @Hook("org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets->nonpublicVoidMethod")
  public static void nonpublicVoidMethod_redirection(AppInstrumentationTargets _this)
  {
    Log.i(API, "AppInstrumentationTargets_nonpublicVoidMethod_redirection() called. _this = " + _this);
    // 12-03 14:15:55.135  21476-21476/org.droidmate.fixtures.apks.instrumented W/Instrumentation﹕ Failed to redirect method (...)
  }

  @Hook("org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets-><init>")
  public static AppInstrumentationTargets constructor_redir(AppInstrumentationTargets _this)
  {
    Log.i(API, "constructor_redir() called.");

    return (AppInstrumentationTargets) OriginalMethod.by(new $() {}).invoke(_this);
  }

  /**
   * Add a hook to the method {@link java.net.URL#openConnection} for testing
   *
   * @param _this Instance of {@link java.net.URL} containing the method to be called
   *
   * @return Returns a new connection to the resource referred to by this URL.
   */
  @Hook("java.net.URL->openConnection")
  public static URLConnection URL_openConnection_redirection(URL _this)
  {
    Log.i(API, "URL_openConnection_redirection() called. _this = " + _this);

    return (URLConnection) OriginalMethod.by(new $() {}).invoke(_this);
  }

  /**
   * Add a hook to the method {@link android.net.ConnectivityManager#getActiveNetworkInfo} for testing
   *
   * @param _this Instance of {@link android.net.ConnectivityManager} containing the method to be called
   *
   * @return a {@link NetworkInfo} object for the current default network
   *        or {@code null} if no default network is currently active
   */
  @Hook("android.net.ConnectivityManager->getActiveNetworkInfo")
  public static NetworkInfo ConnectivityManager_getActiveNetworkInfo_redirection(ConnectivityManager _this)
  {
    Log.i(API, "ConnectivityManager_getActiveNetworkInfo_redirection() called. _this = " + _this);

    return (NetworkInfo) OriginalMethod.by(new $() {}).invoke(_this);
  }

  /**
   * Add a hook to the method {@link android.net.ConnectivityManager#isActiveNetworkMetered} for testing
   *
   * @param _this Instance of {@link android.net.ConnectivityManager} containing the method to be called
   *
   * @return {@code true} if large transfers should be avoided, otherwise
   *        {@code false}.
   */
  @Hook("android.net.ConnectivityManager->isActiveNetworkMetered")
  public static boolean ConnectivityManager_isActiveNetworkMetered_redirection(ConnectivityManager _this)
  {
    Log.i(API, "ConnectivityManager_isActiveNetworkMetered_redirection() called. _this = " + _this);

    return OriginalMethod.by(new $() {}).invoke(_this);
  }

  /**
   * Add a hook to the method {@link android.content.ContentResolver#getCurrentSyncs} for testing
   *
   * @return {@code true} if large transfers should be avoided, otherwise
   *        {@code false}.
   */
  @SuppressWarnings("unchecked")
  @Hook("android.content.ContentResolver->getCurrentSyncs")
  public static List<SyncInfo> ContentResolver_getCurrentSyncs_redirection()
  {
    Log.i(API, "ContentResolver_getCurrentSyncs_redirection() called statically.");

    return (List<SyncInfo>) OriginalMethod.by(new $() {}).invokeStatic();
    //Instrumentation.callStaticObjectMethod($.class, ContentResolver.class, 0);
  }

  /**
   * Add a hook to the method {@link android.telephony.TelephonyManager#getCellLocation} for testing.
   *
   * @param _this Instance of {@link android.telephony.TelephonyManager} containing the method to be called
   *
   * @return Current location of the device or null if not available.
   */
  @Hook("android.telephony.TelephonyManager->getCellLocation")
  public static CellLocation TelephonyManager_getCellLocation_redirection(TelephonyManager _this)
  {
    Log.i(API, "TelephonyManager_getCellLocation_redirection() called. _this = " + _this);

    return (CellLocation) OriginalMethod.by(new $() {}).invoke(_this);
  }
}
