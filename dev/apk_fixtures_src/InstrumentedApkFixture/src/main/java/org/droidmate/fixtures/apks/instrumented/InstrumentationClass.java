// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.fixtures.apks.instrumented;

import android.content.ContentResolver;
import android.content.SyncInfo;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;
import de.uds.infsec.instrumentation.Instrumentation;
import de.uds.infsec.instrumentation.annotation.Redirect;
import de.uds.infsec.instrumentation.util.Signature;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class InstrumentationClass
{
  public static final String API = "Monitored_API_method_call";

  public static void instrument()
  {
    Instrumentation.processClass(InstrumentationClass.class);
    applyDebugHardcodedCtorRedirects();

  }

  /** This region serves as an IDE-supported playground for manual prototyping of code expected to be generated into Monitor.java
   * by monitor-generator project.*/
  //region Debug monitor

  private static void applyDebugHardcodedCtorRedirects()
  {
    if (ctorDefs.length != ctorRedirMethodsSuffixesDefs.length)
      throw new AssertionError("ctorDefs.length != ctorRedirMethodsSuffixesDefs.length");

    ClassLoader[] classLoaders = {Thread.currentThread().getContextClassLoader(), InstrumentationClass.class.getClassLoader()};

    for (int i = 0; i < ctorDefs.length; i++)
    {
      Instrumentation.redirectMethod(
        Signature.fromIdentifier(ctorDefs[i], classLoaders),
        Signature.fromIdentifier(redirMethodIdPrefix + ctorRedirMethodsSuffixesDefs[i], classLoaders)
      );
    }

  }

  private static String redirMethodIdPrefix = "Lorg/droidmate/fixtures/apks/instrumented/InstrumentationClass;->manual_ctor_redir_";

  private static String[] ctorDefs = new String[]{
    "Landroid/webkit/WebView;-><init>(Landroid/content/Context;)",
  };

  private static String[] ctorRedirMethodsSuffixesDefs = new String[]{
    "1(Landroid/webkit/WebView;Landroid/content/Context;)V",
  };

  public static void manual_ctor_redir_1(android.webkit.WebView _this, android.content.Context p0)
  { class ${} Instrumentation.callVoidMethod($.class, _this, p0); }

  //endregion Debug monitor


  @Redirect("android.hardware.Camera->open")
  public static Camera Camera_open_redirection(int cameraId)
  {
    Log.i(API, "Camera_open_redirection() called statically. cameraId = " + cameraId);

    class $
    {
    }
    return (Camera) Instrumentation.callStaticObjectMethod($.class, Camera.class, cameraId);
  }

  @Redirect("java.net.URL->openConnection")
  public static URLConnection URL_openConnection_redirection(URL _this)
  {
    Log.i(API, "URL_openConnection_redirection() called. _this = " + _this);

    class $
    {
    }
    return (URLConnection) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.net.ConnectivityManager->getActiveNetworkInfo")
  public static NetworkInfo ConnectivityManager_getActiveNetworkInfo_redirection(ConnectivityManager _this)
  {
    Log.i(API, "ConnectivityManager_getActiveNetworkInfo_redirection() called. _this = " + _this);

    class $
    {
    }
    return (NetworkInfo) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.net.ConnectivityManager->isActiveNetworkMetered")
  public static boolean ConnectivityManager_isActiveNetworkMetered_redirection(ConnectivityManager _this)
  {
    Log.i(API, "ConnectivityManager_isActiveNetworkMetered_redirection() called. _this = " + _this);
    class $ {}
    return Instrumentation.callBooleanMethod($.class, _this);
  }

  @SuppressWarnings("unchecked")
  @Redirect("android.content.ContentResolver->getCurrentSyncs")
  public static List<SyncInfo> ContentResolver_getCurrentSyncs_redirection()
  {
    Log.i(API, "ContentResolver_getCurrentSyncs_redirection() called statically.");

    class $ {}
    return (List<SyncInfo>) Instrumentation.callStaticObjectMethod($.class, ContentResolver.class, 0);
  }

  @Redirect("android.telephony.TelephonyManager->getCellLocation")
  public static CellLocation TelephonyManager_getCellLocation_redirection(TelephonyManager _this)
  {
    Log.i(API, "TelephonyManager_getCellLocation_redirection() called. _this = " + _this);

    class $ {}
    return (CellLocation) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets->advancedMethod")
  public static AppInstrumentationTargets.ReturnObject internalMethod_redirection(
    AppInstrumentationTargets _this, int param1, String param2, AppInstrumentationTargets.ParamObject param3)
  {
    final String msg = String.format("advancedMethod() called. _this = %s, param1=%d, param2=%s, param3=%s", _this, param1, param2, param3);
    Log.i(API, msg);

    class $ {}
    AppInstrumentationTargets.ReturnObject retObj =
      (AppInstrumentationTargets.ReturnObject) Instrumentation.callObjectMethod($.class, _this, param1, param2, param3);
    Log.i(API, "advancedMethod() returning. retObj = " + retObj);
    return retObj;
  }

  @Redirect("org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets->publicVoidMethod")
  public static void publicVoidMethod_redirection(AppInstrumentationTargets _this)
  {
    Log.i(API, "AppInstrumentationTargets_publicVoidMethod_redirection() called. _this = " + _this);
  }

  @Redirect("org.droidmate.fixtures.apks.instrumented.AppInstrumentationTargets->nonpublicVoidMethod")
  public static void nonpublicVoidMethod_redirection(AppInstrumentationTargets _this)
  {
    Log.i(API, "AppInstrumentationTargets_nonpublicVoidMethod_redirection() called. _this = " + _this);
    // 12-03 14:15:55.135  21476-21476/org.droidmate.fixtures.apks.instrumented W/Instrumentation﹕ Failed to redirect method (...)
  }
}
