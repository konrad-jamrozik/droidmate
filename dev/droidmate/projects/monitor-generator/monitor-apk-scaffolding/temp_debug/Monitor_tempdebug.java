package org.droidmate.monitor_generator.generated;

import android.util.Log;
import de.uds.infsec.instrumentation.Instrumentation;
import de.uds.infsec.instrumentation.annotation.Redirect;
import de.uds.infsec.instrumentation.util.Signature;

public class Monitor {

  private static final String MONITOR = "Monitor";
  private static final String API = "Monitored_API_method_call";

  public Monitor() {
    Log.i(MONITOR, "Monitor constructed.");
  }

  public void init(android.content.Context context) {
    Instrumentation.processClass(Monitor.class);

    // WISH hard-coded
    applyHardcodedCtorRedirects();

    Log.i(MONITOR, "Monitor initialized for package " + context.getPackageName());
  }

  // WISH hard-coded values, no time to do it properly before the USENIX '14 deadline.
  //region Hard-coded ctor redirects

  private static String redirMethodIdPrefix = "Lorg/droidmate/monitor_generator/generated/Monitor;->hardcoded_redir_";


  private static String[] ctorDefs = new String[]{
    "Landroid/webkit/WebView;-><init>(Landroid/content/Context;)V",
    "Landroid/webkit/WebView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;IZ)V",
    "Landroid/webkit/WebView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;II)V",
  };

  private static String[] ctorRedirMethodsSuffixesDefs = new String[]{
    "webview_ctor1(Landroid/webkit/WebView;Landroid/content/Context;)V",
    "webview_ctor41(Landroid/webkit/WebView;Landroid/content/Context;Landroid/util/AttributeSet;IZ)V",
    "webview_ctor42(Landroid/webkit/WebView;Landroid/content/Context;Landroid/util/AttributeSet;II)V",
  };

  private static void applyHardcodedCtorRedirects()
  {
    if (ctorDefs.length != ctorRedirMethodsSuffixesDefs.length)
      throw new AssertionError("ctorDefs.length != ctorRedirMethodsSuffixesDefs.length");

    ClassLoader[] classLoaders = {Thread.currentThread().getContextClassLoader(), Monitor.class.getClassLoader()};

    for (int i = 0; i < ctorDefs.length; i++)
    {
      Instrumentation.redirectMethod(
        Signature.fromIdentifier(ctorDefs[i], classLoaders),
        Signature.fromIdentifier(redirMethodIdPrefix + ctorRedirMethodsSuffixesDefs[i], classLoaders)
      );
    }
  }

  public static void hardcoded_redir_webview_ctor1(android.webkit.WebView _this, android.content.Context p0)
  {
    Log.i(API, "android.webkit.WebView|<init>|void|android.content.Context");
    class ${};
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  public static void hardcoded_redir_webview_ctor41(android.webkit.WebView _this, android.content.Context p0, android.util.AttributeSet p1, int p2, boolean p3)
  {
    Log.i(API, "android.webkit.WebView|<init>|void|int;int;int");
    class ${};
    Instrumentation.callVoidMethod($.class, _this, p0,p1,p2,p3);
  }

  public static void hardcoded_redir_webview_ctor42(android.webkit.WebView _this, android.content.Context p0, android.util.AttributeSet p1, int p2, int p3)
  {
    Log.i(API, "android.webkit.WebView|<init>|void|int;int;int");
    class ${};
    Instrumentation.callVoidMethod($.class, _this, p0,p1,p2,p3);
  }

  // Line 542 in https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/webkit/WebView.java
//  protected WebView(Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes,
//                    Map<String, Object> javaScriptInterfaces, boolean privateBrowsing)
  public static void hardcoded_redir_webview_ctor6(android.webkit.WebView _this, android.content.Context p0, android.util.AttributeSet p1, int p2, int p3, java.util.Map p4, boolean p5)
  {
    Log.i(API, "android.webkit.WebView|<init>|void|int;int;int;int;int;int");
    class ${};
    Instrumentation.callVoidMethod($.class, _this, p0,p1,p2,p3,p4,p5);
  }
  //endregion Hard-coded ctor redirects

  @Redirect("android.accounts.AccountManager->addAccount")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_addAccount7(android.accounts.AccountManager _this, java.lang.String p0, java.lang.String p1, java.lang.String[] p2, android.os.Bundle p3, android.app.Activity p4, android.accounts.AccountManagerCallback p5, android.os.Handler p6)
  {
    Log.i(API, "android.accounts.AccountManager|addAccount|android.accounts.AccountManagerFuture|java.lang.String;java.lang.String;java.lang.String[];android.os.Bundle;android.app.Activity;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4, p5, p6);
  }

  @Redirect("android.accounts.AccountManager->addAccountExplicitly")
  public static boolean redirection_of_android_accounts_AccountManager_addAccountExplicitly3(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1, android.os.Bundle p2)
  {
    Log.i(API, "android.accounts.AccountManager|addAccountExplicitly|boolean|android.accounts.Account;java.lang.String;android.os.Bundle");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.accounts.AccountManager->addOnAccountsUpdatedListener")
  public static void redirection_of_android_accounts_AccountManager_addOnAccountsUpdatedListener3(android.accounts.AccountManager _this, android.accounts.OnAccountsUpdateListener p0, android.os.Handler p1, boolean p2)
  {
    Log.i(API, "android.accounts.AccountManager|addOnAccountsUpdatedListener|void|android.accounts.OnAccountsUpdateListener;android.os.Handler;boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.accounts.AccountManager->blockingGetAuthToken")
  public static java.lang.String redirection_of_android_accounts_AccountManager_blockingGetAuthToken3(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1, boolean p2)
  {
    Log.i(API, "android.accounts.AccountManager|blockingGetAuthToken|java.lang.String|android.accounts.Account;java.lang.String;boolean");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.accounts.AccountManager->clearPassword")
  public static void redirection_of_android_accounts_AccountManager_clearPassword1(android.accounts.AccountManager _this, android.accounts.Account p0)
  {
    Log.i(API, "android.accounts.AccountManager|clearPassword|void|android.accounts.Account");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.accounts.AccountManager->confirmCredentials")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_confirmCredentials5(android.accounts.AccountManager _this, android.accounts.Account p0, android.os.Bundle p1, android.app.Activity p2, android.accounts.AccountManagerCallback p3, android.os.Handler p4)
  {
    Log.i(API, "android.accounts.AccountManager|confirmCredentials|android.accounts.AccountManagerFuture|android.accounts.Account;android.os.Bundle;android.app.Activity;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.accounts.AccountManager->editProperties")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_editProperties4(android.accounts.AccountManager _this, java.lang.String p0, android.app.Activity p1, android.accounts.AccountManagerCallback p2, android.os.Handler p3)
  {
    Log.i(API, "android.accounts.AccountManager|editProperties|android.accounts.AccountManagerFuture|java.lang.String;android.app.Activity;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.accounts.AccountManager->getAccounts")
  public static android.accounts.Account[] redirection_of_android_accounts_AccountManager_getAccounts0(android.accounts.AccountManager _this)
  {
    Log.i(API, "android.accounts.AccountManager|getAccounts|android.accounts.Account[]|");
    class $ {}
    return (android.accounts.Account[]) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.accounts.AccountManager->getAccountsByType")
  public static android.accounts.Account[] redirection_of_android_accounts_AccountManager_getAccountsByType1(android.accounts.AccountManager _this, java.lang.String p0)
  {
    Log.i(API, "android.accounts.AccountManager|getAccountsByType|android.accounts.Account[]|java.lang.String");
    class $ {}
    return (android.accounts.Account[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.accounts.AccountManager->getAccountsByTypeAndFeatures")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_getAccountsByTypeAndFeatures4(android.accounts.AccountManager _this, java.lang.String p0, java.lang.String[] p1, android.accounts.AccountManagerCallback p2, android.os.Handler p3)
  {
    Log.i(API, "android.accounts.AccountManager|getAccountsByTypeAndFeatures|android.accounts.AccountManagerFuture|java.lang.String;java.lang.String[];android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.accounts.AccountManager->getAuthToken")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_getAuthToken5(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1, boolean p2, android.accounts.AccountManagerCallback p3, android.os.Handler p4)
  {
    Log.i(API, "android.accounts.AccountManager|getAuthToken|android.accounts.AccountManagerFuture|android.accounts.Account;java.lang.String;boolean;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.accounts.AccountManager->getAuthToken")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_getAuthToken6(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1, android.os.Bundle p2, boolean p3, android.accounts.AccountManagerCallback p4, android.os.Handler p5)
  {
    Log.i(API, "android.accounts.AccountManager|getAuthToken|android.accounts.AccountManagerFuture|android.accounts.Account;java.lang.String;android.os.Bundle;boolean;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.accounts.AccountManager->getAuthToken")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_getAuthToken6(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1, android.os.Bundle p2, android.app.Activity p3, android.accounts.AccountManagerCallback p4, android.os.Handler p5)
  {
    Log.i(API, "android.accounts.AccountManager|getAuthToken|android.accounts.AccountManagerFuture|android.accounts.Account;java.lang.String;android.os.Bundle;android.app.Activity;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.accounts.AccountManager->getAuthTokenByFeatures")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_getAuthTokenByFeatures8(android.accounts.AccountManager _this, java.lang.String p0, java.lang.String p1, java.lang.String[] p2, android.app.Activity p3, android.os.Bundle p4, android.os.Bundle p5, android.accounts.AccountManagerCallback p6, android.os.Handler p7)
  {
    Log.i(API, "android.accounts.AccountManager|getAuthTokenByFeatures|android.accounts.AccountManagerFuture|java.lang.String;java.lang.String;java.lang.String[];android.app.Activity;android.os.Bundle;android.os.Bundle;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4, p5, p6, p7);
  }

  @Redirect("android.accounts.AccountManager->getAuthTokenLabel")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_getAuthTokenLabel4(android.accounts.AccountManager _this, java.lang.String p0, java.lang.String p1, android.accounts.AccountManagerCallback p2, android.os.Handler p3)
  {
    Log.i(API, "android.accounts.AccountManager|getAuthTokenLabel|android.accounts.AccountManagerFuture|java.lang.String;java.lang.String;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.accounts.AccountManager->getPassword")
  public static java.lang.String redirection_of_android_accounts_AccountManager_getPassword1(android.accounts.AccountManager _this, android.accounts.Account p0)
  {
    Log.i(API, "android.accounts.AccountManager|getPassword|java.lang.String|android.accounts.Account");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.accounts.AccountManager->getUserData")
  public static java.lang.String redirection_of_android_accounts_AccountManager_getUserData2(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1)
  {
    Log.i(API, "android.accounts.AccountManager|getUserData|java.lang.String|android.accounts.Account;java.lang.String");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.accounts.AccountManager->hasFeatures")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_hasFeatures4(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String[] p1, android.accounts.AccountManagerCallback p2, android.os.Handler p3)
  {
    Log.i(API, "android.accounts.AccountManager|hasFeatures|android.accounts.AccountManagerFuture|android.accounts.Account;java.lang.String[];android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.accounts.AccountManager->invalidateAuthToken")
  public static void redirection_of_android_accounts_AccountManager_invalidateAuthToken2(android.accounts.AccountManager _this, java.lang.String p0, java.lang.String p1)
  {
    Log.i(API, "android.accounts.AccountManager|invalidateAuthToken|void|java.lang.String;java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.accounts.AccountManager->peekAuthToken")
  public static java.lang.String redirection_of_android_accounts_AccountManager_peekAuthToken2(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1)
  {
    Log.i(API, "android.accounts.AccountManager|peekAuthToken|java.lang.String|android.accounts.Account;java.lang.String");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.accounts.AccountManager->removeAccount")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_removeAccount3(android.accounts.AccountManager _this, android.accounts.Account p0, android.accounts.AccountManagerCallback p1, android.os.Handler p2)
  {
    Log.i(API, "android.accounts.AccountManager|removeAccount|android.accounts.AccountManagerFuture|android.accounts.Account;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.accounts.AccountManager->setAuthToken")
  public static void redirection_of_android_accounts_AccountManager_setAuthToken3(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1, java.lang.String p2)
  {
    Log.i(API, "android.accounts.AccountManager|setAuthToken|void|android.accounts.Account;java.lang.String;java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.accounts.AccountManager->setPassword")
  public static void redirection_of_android_accounts_AccountManager_setPassword2(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1)
  {
    Log.i(API, "android.accounts.AccountManager|setPassword|void|android.accounts.Account;java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.accounts.AccountManager->setUserData")
  public static void redirection_of_android_accounts_AccountManager_setUserData3(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1, java.lang.String p2)
  {
    Log.i(API, "android.accounts.AccountManager|setUserData|void|android.accounts.Account;java.lang.String;java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.accounts.AccountManager->updateCredentials")
  public static android.accounts.AccountManagerFuture redirection_of_android_accounts_AccountManager_updateCredentials6(android.accounts.AccountManager _this, android.accounts.Account p0, java.lang.String p1, android.os.Bundle p2, android.app.Activity p3, android.accounts.AccountManagerCallback p4, android.os.Handler p5)
  {
    Log.i(API, "android.accounts.AccountManager|updateCredentials|android.accounts.AccountManagerFuture|android.accounts.Account;java.lang.String;android.os.Bundle;android.app.Activity;android.accounts.AccountManagerCallback;android.os.Handler");
    class $ {}
    return (android.accounts.AccountManagerFuture) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.app.ActivityManager->getRecentTasks")
  public static java.util.List redirection_of_android_app_ActivityManager_getRecentTasks2(android.app.ActivityManager _this, int p0, int p1)
  {
    Log.i(API, "android.app.ActivityManager|getRecentTasks|java.util.List|int;int");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.app.ActivityManager->getRunningTasks")
  public static java.util.List redirection_of_android_app_ActivityManager_getRunningTasks1(android.app.ActivityManager _this, int p0)
  {
    Log.i(API, "android.app.ActivityManager|getRunningTasks|java.util.List|int");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.app.ActivityManager->killBackgroundProcesses")
  public static void redirection_of_android_app_ActivityManager_killBackgroundProcesses1(android.app.ActivityManager _this, java.lang.String p0)
  {
    Log.i(API, "android.app.ActivityManager|killBackgroundProcesses|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.app.ActivityManager->moveTaskToFront")
  public static void redirection_of_android_app_ActivityManager_moveTaskToFront2(android.app.ActivityManager _this, int p0, int p1)
  {
    Log.i(API, "android.app.ActivityManager|moveTaskToFront|void|int;int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.app.ActivityManager->moveTaskToFront")
  public static void redirection_of_android_app_ActivityManager_moveTaskToFront3(android.app.ActivityManager _this, int p0, int p1, android.os.Bundle p2)
  {
    Log.i(API, "android.app.ActivityManager|moveTaskToFront|void|int;int;android.os.Bundle");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.app.ActivityManager->restartPackage")
  public static void redirection_of_android_app_ActivityManager_restartPackage1(android.app.ActivityManager _this, java.lang.String p0)
  {
    Log.i(API, "android.app.ActivityManager|restartPackage|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.app.AlarmManager->setTimeZone")
  public static void redirection_of_android_app_AlarmManager_setTimeZone1(android.app.AlarmManager _this, java.lang.String p0)
  {
    Log.i(API, "android.app.AlarmManager|setTimeZone|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.app.DownloadManager->addCompletedDownload")
  public static long redirection_of_android_app_DownloadManager_addCompletedDownload7(android.app.DownloadManager _this, java.lang.String p0, java.lang.String p1, boolean p2, java.lang.String p3, java.lang.String p4, long p5, boolean p6)
  {
    Log.i(API, "android.app.DownloadManager|addCompletedDownload|long|java.lang.String;java.lang.String;boolean;java.lang.String;java.lang.String;long;boolean");
    class $ {}
    return (long) Instrumentation.callLongMethod($.class, _this, p0, p1, p2, p3, p4, p5, p6);
  }

  @Redirect("android.app.DownloadManager->enqueue")
  public static long redirection_of_android_app_DownloadManager_enqueue1(android.app.DownloadManager _this, android.app.DownloadManager.Request p0)
  {
    Log.i(API, "android.app.DownloadManager|enqueue|long|android.app.DownloadManager.Request");
    class $ {}
    return (long) Instrumentation.callLongMethod($.class, _this, p0);
  }

  @Redirect("android.app.DownloadManager->getUriForDownloadedFile")
  public static android.net.Uri redirection_of_android_app_DownloadManager_getUriForDownloadedFile1(android.app.DownloadManager _this, long p0)
  {
    Log.i(API, "android.app.DownloadManager|getUriForDownloadedFile|android.net.Uri|long");
    class $ {}
    return (android.net.Uri) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.app.KeyguardManager->exitKeyguardSecurely")
  public static void redirection_of_android_app_KeyguardManager_exitKeyguardSecurely1(android.app.KeyguardManager _this, android.app.KeyguardManager.OnKeyguardExitResult p0)
  {
    Log.i(API, "android.app.KeyguardManager|exitKeyguardSecurely|void|android.app.KeyguardManager.OnKeyguardExitResult");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.app.KeyguardManager$KeyguardLock->disableKeyguard")
  public static void redirection_of_android_app_KeyguardManager_KeyguardLock_disableKeyguard0(android.app.KeyguardManager.KeyguardLock _this)
  {
    Log.i(API, "android.app.KeyguardManager$KeyguardLock|disableKeyguard|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.app.KeyguardManager$KeyguardLock->reenableKeyguard")
  public static void redirection_of_android_app_KeyguardManager_KeyguardLock_reenableKeyguard0(android.app.KeyguardManager.KeyguardLock _this)
  {
    Log.i(API, "android.app.KeyguardManager$KeyguardLock|reenableKeyguard|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.app.NotificationManager->notify")
  public static void redirection_of_android_app_NotificationManager_notify2(android.app.NotificationManager _this, int p0, android.app.Notification p1)
  {
    Log.i(API, "android.app.NotificationManager|notify|void|int;android.app.Notification");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.app.NotificationManager->notify")
  public static void redirection_of_android_app_NotificationManager_notify3(android.app.NotificationManager _this, java.lang.String p0, int p1, android.app.Notification p2)
  {
    Log.i(API, "android.app.NotificationManager|notify|void|java.lang.String;int;android.app.Notification");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.app.WallpaperManager->clear")
  public static void redirection_of_android_app_WallpaperManager_clear0(android.app.WallpaperManager _this)
  {
    Log.i(API, "android.app.WallpaperManager|clear|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.app.WallpaperManager->setBitmap")
  public static void redirection_of_android_app_WallpaperManager_setBitmap1(android.app.WallpaperManager _this, android.graphics.Bitmap p0)
  {
    Log.i(API, "android.app.WallpaperManager|setBitmap|void|android.graphics.Bitmap");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.app.WallpaperManager->setResource")
  public static void redirection_of_android_app_WallpaperManager_setResource1(android.app.WallpaperManager _this, int p0)
  {
    Log.i(API, "android.app.WallpaperManager|setResource|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.app.WallpaperManager->setStream")
  public static void redirection_of_android_app_WallpaperManager_setStream1(android.app.WallpaperManager _this, java.io.InputStream p0)
  {
    Log.i(API, "android.app.WallpaperManager|setStream|void|java.io.InputStream");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.app.WallpaperManager->suggestDesiredDimensions")
  public static void redirection_of_android_app_WallpaperManager_suggestDesiredDimensions2(android.app.WallpaperManager _this, int p0, int p1)
  {
    Log.i(API, "android.app.WallpaperManager|suggestDesiredDimensions|void|int;int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->connect")
  public static boolean redirection_of_android_bluetooth_BluetoothA2dp_connect1(android.bluetooth.BluetoothA2dp _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|connect|boolean|android.bluetooth.BluetoothDevice");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->disconnect")
  public static boolean redirection_of_android_bluetooth_BluetoothA2dp_disconnect1(android.bluetooth.BluetoothA2dp _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|disconnect|boolean|android.bluetooth.BluetoothDevice");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->getConnectedDevices")
  public static java.util.List redirection_of_android_bluetooth_BluetoothA2dp_getConnectedDevices0(android.bluetooth.BluetoothA2dp _this)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|getConnectedDevices|java.util.List|");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->getConnectionState")
  public static int redirection_of_android_bluetooth_BluetoothA2dp_getConnectionState1(android.bluetooth.BluetoothA2dp _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|getConnectionState|int|android.bluetooth.BluetoothDevice");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->getDevicesMatchingConnectionStates")
  public static java.util.List redirection_of_android_bluetooth_BluetoothA2dp_getDevicesMatchingConnectionStates1(android.bluetooth.BluetoothA2dp _this, int[] p0)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|getDevicesMatchingConnectionStates|java.util.List|int[]");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->getPriority")
  public static int redirection_of_android_bluetooth_BluetoothA2dp_getPriority1(android.bluetooth.BluetoothA2dp _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|getPriority|int|android.bluetooth.BluetoothDevice");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->isA2dpPlaying")
  public static boolean redirection_of_android_bluetooth_BluetoothA2dp_isA2dpPlaying1(android.bluetooth.BluetoothA2dp _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|isA2dpPlaying|boolean|android.bluetooth.BluetoothDevice");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->isEnabled")
  public static boolean redirection_of_android_bluetooth_BluetoothA2dp_isEnabled0(android.bluetooth.BluetoothA2dp _this)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|isEnabled|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothA2dp->setPriority")
  public static boolean redirection_of_android_bluetooth_BluetoothA2dp_setPriority2(android.bluetooth.BluetoothA2dp _this, android.bluetooth.BluetoothDevice p0, int p1)
  {
    Log.i(API, "android.bluetooth.BluetoothA2dp|setPriority|boolean|android.bluetooth.BluetoothDevice;int");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->cancelDiscovery")
  public static boolean redirection_of_android_bluetooth_BluetoothAdapter_cancelDiscovery0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|cancelDiscovery|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->disable")
  public static boolean redirection_of_android_bluetooth_BluetoothAdapter_disable0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|disable|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->enable")
  public static boolean redirection_of_android_bluetooth_BluetoothAdapter_enable0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|enable|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->getAddress")
  public static java.lang.String redirection_of_android_bluetooth_BluetoothAdapter_getAddress0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|getAddress|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->getBondedDevices")
  public static java.util.Set redirection_of_android_bluetooth_BluetoothAdapter_getBondedDevices0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|getBondedDevices|java.util.Set|");
    class $ {}
    return (java.util.Set) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->getConnectionState")
  public static int redirection_of_android_bluetooth_BluetoothAdapter_getConnectionState0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|getConnectionState|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->getName")
  public static java.lang.String redirection_of_android_bluetooth_BluetoothAdapter_getName0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|getName|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->getProfileConnectionState")
  public static int redirection_of_android_bluetooth_BluetoothAdapter_getProfileConnectionState1(android.bluetooth.BluetoothAdapter _this, int p0)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|getProfileConnectionState|int|int");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->getScanMode")
  public static int redirection_of_android_bluetooth_BluetoothAdapter_getScanMode0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|getScanMode|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->getState")
  public static int redirection_of_android_bluetooth_BluetoothAdapter_getState0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|getState|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->getUuids")
  public static android.os.ParcelUuid[] redirection_of_android_bluetooth_BluetoothAdapter_getUuids0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|getUuids|android.os.ParcelUuid[]|");
    class $ {}
    return (android.os.ParcelUuid[]) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->isDiscovering")
  public static boolean redirection_of_android_bluetooth_BluetoothAdapter_isDiscovering0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|isDiscovering|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->isEnabled")
  public static boolean redirection_of_android_bluetooth_BluetoothAdapter_isEnabled0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|isEnabled|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->listenUsingInsecureRfcommWithServiceRecord")
  public static android.bluetooth.BluetoothServerSocket redirection_of_android_bluetooth_BluetoothAdapter_listenUsingInsecureRfcommWithServiceRecord2(android.bluetooth.BluetoothAdapter _this, java.lang.String p0, java.util.UUID p1)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|listenUsingInsecureRfcommWithServiceRecord|android.bluetooth.BluetoothServerSocket|java.lang.String;java.util.UUID");
    class $ {}
    return (android.bluetooth.BluetoothServerSocket) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->listenUsingRfcommWithServiceRecord")
  public static android.bluetooth.BluetoothServerSocket redirection_of_android_bluetooth_BluetoothAdapter_listenUsingRfcommWithServiceRecord2(android.bluetooth.BluetoothAdapter _this, java.lang.String p0, java.util.UUID p1)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|listenUsingRfcommWithServiceRecord|android.bluetooth.BluetoothServerSocket|java.lang.String;java.util.UUID");
    class $ {}
    return (android.bluetooth.BluetoothServerSocket) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->setName")
  public static boolean redirection_of_android_bluetooth_BluetoothAdapter_setName1(android.bluetooth.BluetoothAdapter _this, java.lang.String p0)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|setName|boolean|java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothAdapter->startDiscovery")
  public static boolean redirection_of_android_bluetooth_BluetoothAdapter_startDiscovery0(android.bluetooth.BluetoothAdapter _this)
  {
    Log.i(API, "android.bluetooth.BluetoothAdapter|startDiscovery|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothDevice->createInsecureRfcommSocketToServiceRecord")
  public static android.bluetooth.BluetoothSocket redirection_of_android_bluetooth_BluetoothDevice_createInsecureRfcommSocketToServiceRecord1(android.bluetooth.BluetoothDevice _this, java.util.UUID p0)
  {
    Log.i(API, "android.bluetooth.BluetoothDevice|createInsecureRfcommSocketToServiceRecord|android.bluetooth.BluetoothSocket|java.util.UUID");
    class $ {}
    return (android.bluetooth.BluetoothSocket) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothDevice->createRfcommSocketToServiceRecord")
  public static android.bluetooth.BluetoothSocket redirection_of_android_bluetooth_BluetoothDevice_createRfcommSocketToServiceRecord1(android.bluetooth.BluetoothDevice _this, java.util.UUID p0)
  {
    Log.i(API, "android.bluetooth.BluetoothDevice|createRfcommSocketToServiceRecord|android.bluetooth.BluetoothSocket|java.util.UUID");
    class $ {}
    return (android.bluetooth.BluetoothSocket) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothDevice->fetchUuidsWithSdp")
  public static boolean redirection_of_android_bluetooth_BluetoothDevice_fetchUuidsWithSdp0(android.bluetooth.BluetoothDevice _this)
  {
    Log.i(API, "android.bluetooth.BluetoothDevice|fetchUuidsWithSdp|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothDevice->getBluetoothClass")
  public static android.bluetooth.BluetoothClass redirection_of_android_bluetooth_BluetoothDevice_getBluetoothClass0(android.bluetooth.BluetoothDevice _this)
  {
    Log.i(API, "android.bluetooth.BluetoothDevice|getBluetoothClass|android.bluetooth.BluetoothClass|");
    class $ {}
    return (android.bluetooth.BluetoothClass) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothDevice->getBondState")
  public static int redirection_of_android_bluetooth_BluetoothDevice_getBondState0(android.bluetooth.BluetoothDevice _this)
  {
    Log.i(API, "android.bluetooth.BluetoothDevice|getBondState|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothDevice->getName")
  public static java.lang.String redirection_of_android_bluetooth_BluetoothDevice_getName0(android.bluetooth.BluetoothDevice _this)
  {
    Log.i(API, "android.bluetooth.BluetoothDevice|getName|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothDevice->getUuids")
  public static android.os.ParcelUuid[] redirection_of_android_bluetooth_BluetoothDevice_getUuids0(android.bluetooth.BluetoothDevice _this)
  {
    Log.i(API, "android.bluetooth.BluetoothDevice|getUuids|android.os.ParcelUuid[]|");
    class $ {}
    return (android.os.ParcelUuid[]) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->connect")
  public static boolean redirection_of_android_bluetooth_BluetoothHeadset_connect1(android.bluetooth.BluetoothHeadset _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|connect|boolean|android.bluetooth.BluetoothDevice");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->disconnect")
  public static boolean redirection_of_android_bluetooth_BluetoothHeadset_disconnect1(android.bluetooth.BluetoothHeadset _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|disconnect|boolean|android.bluetooth.BluetoothDevice");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->getConnectedDevices")
  public static java.util.List redirection_of_android_bluetooth_BluetoothHeadset_getConnectedDevices0(android.bluetooth.BluetoothHeadset _this)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|getConnectedDevices|java.util.List|");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->getConnectionState")
  public static int redirection_of_android_bluetooth_BluetoothHeadset_getConnectionState1(android.bluetooth.BluetoothHeadset _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|getConnectionState|int|android.bluetooth.BluetoothDevice");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->getDevicesMatchingConnectionStates")
  public static java.util.List redirection_of_android_bluetooth_BluetoothHeadset_getDevicesMatchingConnectionStates1(android.bluetooth.BluetoothHeadset _this, int[] p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|getDevicesMatchingConnectionStates|java.util.List|int[]");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->getPriority")
  public static int redirection_of_android_bluetooth_BluetoothHeadset_getPriority1(android.bluetooth.BluetoothHeadset _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|getPriority|int|android.bluetooth.BluetoothDevice");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->isAudioConnected")
  public static boolean redirection_of_android_bluetooth_BluetoothHeadset_isAudioConnected1(android.bluetooth.BluetoothHeadset _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|isAudioConnected|boolean|android.bluetooth.BluetoothDevice");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->isEnabled")
  public static boolean redirection_of_android_bluetooth_BluetoothHeadset_isEnabled0(android.bluetooth.BluetoothHeadset _this)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|isEnabled|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->setPriority")
  public static boolean redirection_of_android_bluetooth_BluetoothHeadset_setPriority2(android.bluetooth.BluetoothHeadset _this, android.bluetooth.BluetoothDevice p0, int p1)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|setPriority|boolean|android.bluetooth.BluetoothDevice;int");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->startVoiceRecognition")
  public static boolean redirection_of_android_bluetooth_BluetoothHeadset_startVoiceRecognition1(android.bluetooth.BluetoothHeadset _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|startVoiceRecognition|boolean|android.bluetooth.BluetoothDevice");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHeadset->stopVoiceRecognition")
  public static boolean redirection_of_android_bluetooth_BluetoothHeadset_stopVoiceRecognition1(android.bluetooth.BluetoothHeadset _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHeadset|stopVoiceRecognition|boolean|android.bluetooth.BluetoothDevice");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHealth->connectChannelToSource")
  public static boolean redirection_of_android_bluetooth_BluetoothHealth_connectChannelToSource2(android.bluetooth.BluetoothHealth _this, android.bluetooth.BluetoothDevice p0, android.bluetooth.BluetoothHealthAppConfiguration p1)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|connectChannelToSource|boolean|android.bluetooth.BluetoothDevice;android.bluetooth.BluetoothHealthAppConfiguration");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1);
  }

  @Redirect("android.bluetooth.BluetoothHealth->disconnectChannel")
  public static boolean redirection_of_android_bluetooth_BluetoothHealth_disconnectChannel3(android.bluetooth.BluetoothHealth _this, android.bluetooth.BluetoothDevice p0, android.bluetooth.BluetoothHealthAppConfiguration p1, int p2)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|disconnectChannel|boolean|android.bluetooth.BluetoothDevice;android.bluetooth.BluetoothHealthAppConfiguration;int");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.bluetooth.BluetoothHealth->getConnectedDevices")
  public static java.util.List redirection_of_android_bluetooth_BluetoothHealth_getConnectedDevices0(android.bluetooth.BluetoothHealth _this)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|getConnectedDevices|java.util.List|");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothHealth->getConnectionState")
  public static int redirection_of_android_bluetooth_BluetoothHealth_getConnectionState1(android.bluetooth.BluetoothHealth _this, android.bluetooth.BluetoothDevice p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|getConnectionState|int|android.bluetooth.BluetoothDevice");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHealth->getDevicesMatchingConnectionStates")
  public static java.util.List redirection_of_android_bluetooth_BluetoothHealth_getDevicesMatchingConnectionStates1(android.bluetooth.BluetoothHealth _this, int[] p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|getDevicesMatchingConnectionStates|java.util.List|int[]");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothHealth->getMainChannelFd")
  public static android.os.ParcelFileDescriptor redirection_of_android_bluetooth_BluetoothHealth_getMainChannelFd2(android.bluetooth.BluetoothHealth _this, android.bluetooth.BluetoothDevice p0, android.bluetooth.BluetoothHealthAppConfiguration p1)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|getMainChannelFd|android.os.ParcelFileDescriptor|android.bluetooth.BluetoothDevice;android.bluetooth.BluetoothHealthAppConfiguration");
    class $ {}
    return (android.os.ParcelFileDescriptor) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.bluetooth.BluetoothHealth->isEnabled")
  public static boolean redirection_of_android_bluetooth_BluetoothHealth_isEnabled0(android.bluetooth.BluetoothHealth _this)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|isEnabled|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.bluetooth.BluetoothHealth->registerSinkAppConfiguration")
  public static boolean redirection_of_android_bluetooth_BluetoothHealth_registerSinkAppConfiguration3(android.bluetooth.BluetoothHealth _this, java.lang.String p0, int p1, android.bluetooth.BluetoothHealthCallback p2)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|registerSinkAppConfiguration|boolean|java.lang.String;int;android.bluetooth.BluetoothHealthCallback");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.bluetooth.BluetoothHealth->unregisterAppConfiguration")
  public static boolean redirection_of_android_bluetooth_BluetoothHealth_unregisterAppConfiguration1(android.bluetooth.BluetoothHealth _this, android.bluetooth.BluetoothHealthAppConfiguration p0)
  {
    Log.i(API, "android.bluetooth.BluetoothHealth|unregisterAppConfiguration|boolean|android.bluetooth.BluetoothHealthAppConfiguration");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.bluetooth.BluetoothSocket->connect")
  public static void redirection_of_android_bluetooth_BluetoothSocket_connect0(android.bluetooth.BluetoothSocket _this)
  {
    Log.i(API, "android.bluetooth.BluetoothSocket|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.content.ContentProviderClient->bulkInsert")
  public static int redirection_of_android_content_ContentProviderClient_bulkInsert2(android.content.ContentProviderClient _this, android.net.Uri p0, android.content.ContentValues[] p1)
  {
    Log.i(API, "android.content.ContentProviderClient|bulkInsert|int|android.net.Uri;android.content.ContentValues[]");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0, p1);
  }

  @Redirect("android.content.ContentProviderClient->delete")
  public static int redirection_of_android_content_ContentProviderClient_delete3(android.content.ContentProviderClient _this, android.net.Uri p0, java.lang.String p1, java.lang.String[] p2)
  {
    Log.i(API, "android.content.ContentProviderClient|delete|int|android.net.Uri;java.lang.String;java.lang.String[]");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.content.ContentProviderClient->insert")
  public static android.net.Uri redirection_of_android_content_ContentProviderClient_insert2(android.content.ContentProviderClient _this, android.net.Uri p0, android.content.ContentValues p1)
  {
    Log.i(API, "android.content.ContentProviderClient|insert|android.net.Uri|android.net.Uri;android.content.ContentValues");
    class $ {}
    return (android.net.Uri) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.content.ContentProviderClient->openFile")
  public static android.os.ParcelFileDescriptor redirection_of_android_content_ContentProviderClient_openFile2(android.content.ContentProviderClient _this, android.net.Uri p0, java.lang.String p1)
  {
    Log.i(API, "android.content.ContentProviderClient|openFile|android.os.ParcelFileDescriptor|android.net.Uri;java.lang.String");
    class $ {}
    return (android.os.ParcelFileDescriptor) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.content.ContentProviderClient->openFile")
  public static android.os.ParcelFileDescriptor redirection_of_android_content_ContentProviderClient_openFile3(android.content.ContentProviderClient _this, android.net.Uri p0, java.lang.String p1, android.os.CancellationSignal p2)
  {
    Log.i(API, "android.content.ContentProviderClient|openFile|android.os.ParcelFileDescriptor|android.net.Uri;java.lang.String;android.os.CancellationSignal");
    class $ {}
    return (android.os.ParcelFileDescriptor) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.content.ContentProviderClient->query")
  public static android.database.Cursor redirection_of_android_content_ContentProviderClient_query5(android.content.ContentProviderClient _this, android.net.Uri p0, java.lang.String[] p1, java.lang.String p2, java.lang.String[] p3, java.lang.String p4)
  {
    Log.i(API, "android.content.ContentProviderClient|query|android.database.Cursor|android.net.Uri;java.lang.String[];java.lang.String;java.lang.String[];java.lang.String");
    class $ {}
    return (android.database.Cursor) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.content.ContentProviderClient->query")
  public static android.database.Cursor redirection_of_android_content_ContentProviderClient_query6(android.content.ContentProviderClient _this, android.net.Uri p0, java.lang.String[] p1, java.lang.String p2, java.lang.String[] p3, java.lang.String p4, android.os.CancellationSignal p5)
  {
    Log.i(API, "android.content.ContentProviderClient|query|android.database.Cursor|android.net.Uri;java.lang.String[];java.lang.String;java.lang.String[];java.lang.String;android.os.CancellationSignal");
    class $ {}
    return (android.database.Cursor) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.content.ContentProviderClient->update")
  public static int redirection_of_android_content_ContentProviderClient_update4(android.content.ContentProviderClient _this, android.net.Uri p0, android.content.ContentValues p1, java.lang.String p2, java.lang.String[] p3)
  {
    Log.i(API, "android.content.ContentProviderClient|update|int|android.net.Uri;android.content.ContentValues;java.lang.String;java.lang.String[]");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.content.ContentResolver->addPeriodicSync")
  public static void redirection_of_android_content_ContentResolver_addPeriodicSync4(android.accounts.Account p0, java.lang.String p1, android.os.Bundle p2, long p3)
  {
    Log.i(API, "android.content.ContentResolver|addPeriodicSync|void|android.accounts.Account;java.lang.String;android.os.Bundle;long");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.content.ContentResolver.class, p0, p1, p2, p3);
  }

  @Redirect("android.content.ContentResolver->applyBatch")
  public static android.content.ContentProviderResult[] redirection_of_android_content_ContentResolver_applyBatch2(android.content.ContentResolver _this, java.lang.String p0, java.util.ArrayList p1)
  {
    Log.i(API, "android.content.ContentResolver|applyBatch|android.content.ContentProviderResult[]|java.lang.String;java.util.ArrayList");
    class $ {}
    return (android.content.ContentProviderResult[]) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.content.ContentResolver->bulkInsert")
  public static int redirection_of_android_content_ContentResolver_bulkInsert2(android.content.ContentResolver _this, android.net.Uri p0, android.content.ContentValues[] p1)
  {
    Log.i(API, "android.content.ContentResolver|bulkInsert|int|android.net.Uri;android.content.ContentValues[]");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0, p1);
  }

  @Redirect("android.content.ContentResolver->delete")
  public static int redirection_of_android_content_ContentResolver_delete3(android.content.ContentResolver _this, android.net.Uri p0, java.lang.String p1, java.lang.String[] p2)
  {
    Log.i(API, "android.content.ContentResolver|delete|int|android.net.Uri;java.lang.String;java.lang.String[]");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.content.ContentResolver->getCurrentSync")
  public static android.content.SyncInfo redirection_of_android_content_ContentResolver_getCurrentSync0()
  {
    Log.i(API, "android.content.ContentResolver|getCurrentSync|android.content.SyncInfo|");
    class $ {}
    return (android.content.SyncInfo) Instrumentation.callStaticObjectMethod($.class, android.content.ContentResolver.class, 0);
  }

  @Redirect("android.content.ContentResolver->getCurrentSyncs")
  public static java.util.List redirection_of_android_content_ContentResolver_getCurrentSyncs0()
  {
    Log.i(API, "android.content.ContentResolver|getCurrentSyncs|java.util.List|");
    class $ {}
    return (java.util.List) Instrumentation.callStaticObjectMethod($.class, android.content.ContentResolver.class, 0);
  }

  @Redirect("android.content.ContentResolver->getIsSyncable")
  public static int redirection_of_android_content_ContentResolver_getIsSyncable2(android.accounts.Account p0, java.lang.String p1)
  {
    Log.i(API, "android.content.ContentResolver|getIsSyncable|int|android.accounts.Account;java.lang.String");
    class $ {}
    return (int) Instrumentation.callStaticIntMethod($.class, android.content.ContentResolver.class, p0, p1);
  }

  @Redirect("android.content.ContentResolver->getMasterSyncAutomatically")
  public static boolean redirection_of_android_content_ContentResolver_getMasterSyncAutomatically0()
  {
    Log.i(API, "android.content.ContentResolver|getMasterSyncAutomatically|boolean|");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.content.ContentResolver.class, 0);
  }

  @Redirect("android.content.ContentResolver->getPeriodicSyncs")
  public static java.util.List redirection_of_android_content_ContentResolver_getPeriodicSyncs2(android.accounts.Account p0, java.lang.String p1)
  {
    Log.i(API, "android.content.ContentResolver|getPeriodicSyncs|java.util.List|android.accounts.Account;java.lang.String");
    class $ {}
    return (java.util.List) Instrumentation.callStaticObjectMethod($.class, android.content.ContentResolver.class, p0, p1);
  }

  @Redirect("android.content.ContentResolver->getSyncAutomatically")
  public static boolean redirection_of_android_content_ContentResolver_getSyncAutomatically2(android.accounts.Account p0, java.lang.String p1)
  {
    Log.i(API, "android.content.ContentResolver|getSyncAutomatically|boolean|android.accounts.Account;java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.content.ContentResolver.class, p0, p1);
  }

  @Redirect("android.content.ContentResolver->insert")
  public static android.net.Uri redirection_of_android_content_ContentResolver_insert2(android.content.ContentResolver _this, android.net.Uri p0, android.content.ContentValues p1)
  {
    Log.i(API, "android.content.ContentResolver|insert|android.net.Uri|android.net.Uri;android.content.ContentValues");
    class $ {}
    return (android.net.Uri) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.content.ContentResolver->isSyncActive")
  public static boolean redirection_of_android_content_ContentResolver_isSyncActive2(android.accounts.Account p0, java.lang.String p1)
  {
    Log.i(API, "android.content.ContentResolver|isSyncActive|boolean|android.accounts.Account;java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.content.ContentResolver.class, p0, p1);
  }

  @Redirect("android.content.ContentResolver->isSyncPending")
  public static boolean redirection_of_android_content_ContentResolver_isSyncPending2(android.accounts.Account p0, java.lang.String p1)
  {
    Log.i(API, "android.content.ContentResolver|isSyncPending|boolean|android.accounts.Account;java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.content.ContentResolver.class, p0, p1);
  }

  @Redirect("android.content.ContentResolver->openFileDescriptor")
  public static android.os.ParcelFileDescriptor redirection_of_android_content_ContentResolver_openFileDescriptor2(android.content.ContentResolver _this, android.net.Uri p0, java.lang.String p1)
  {
    Log.i(API, "android.content.ContentResolver|openFileDescriptor|android.os.ParcelFileDescriptor|android.net.Uri;java.lang.String");
    class $ {}
    return (android.os.ParcelFileDescriptor) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.content.ContentResolver->openFileDescriptor")
  public static android.os.ParcelFileDescriptor redirection_of_android_content_ContentResolver_openFileDescriptor3(android.content.ContentResolver _this, android.net.Uri p0, java.lang.String p1, android.os.CancellationSignal p2)
  {
    Log.i(API, "android.content.ContentResolver|openFileDescriptor|android.os.ParcelFileDescriptor|android.net.Uri;java.lang.String;android.os.CancellationSignal");
    class $ {}
    return (android.os.ParcelFileDescriptor) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.content.ContentResolver->openInputStream")
  public static java.io.InputStream redirection_of_android_content_ContentResolver_openInputStream1(android.content.ContentResolver _this, android.net.Uri p0)
  {
    Log.i(API, "android.content.ContentResolver|openInputStream|java.io.InputStream|android.net.Uri");
    class $ {}
    return (java.io.InputStream) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.content.ContentResolver->query")
  public static android.database.Cursor redirection_of_android_content_ContentResolver_query5(android.content.ContentResolver _this, android.net.Uri p0, java.lang.String[] p1, java.lang.String p2, java.lang.String[] p3, java.lang.String p4)
  {
    Log.i(API, "android.content.ContentResolver|query|android.database.Cursor|android.net.Uri;java.lang.String[];java.lang.String;java.lang.String[];java.lang.String");
    class $ {}
    return (android.database.Cursor) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.content.ContentResolver->query")
  public static android.database.Cursor redirection_of_android_content_ContentResolver_query6(android.content.ContentResolver _this, android.net.Uri p0, java.lang.String[] p1, java.lang.String p2, java.lang.String[] p3, java.lang.String p4, android.os.CancellationSignal p5)
  {
    Log.i(API, "android.content.ContentResolver|query|android.database.Cursor|android.net.Uri;java.lang.String[];java.lang.String;java.lang.String[];java.lang.String;android.os.CancellationSignal");
    class $ {}
    return (android.database.Cursor) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.content.ContentResolver->registerContentObserver")
  public static void redirection_of_android_content_ContentResolver_registerContentObserver3(android.content.ContentResolver _this, android.net.Uri p0, boolean p1, android.database.ContentObserver p2)
  {
    Log.i(API, "android.content.ContentResolver|registerContentObserver|void|android.net.Uri;boolean;android.database.ContentObserver");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.content.ContentResolver->removePeriodicSync")
  public static void redirection_of_android_content_ContentResolver_removePeriodicSync3(android.accounts.Account p0, java.lang.String p1, android.os.Bundle p2)
  {
    Log.i(API, "android.content.ContentResolver|removePeriodicSync|void|android.accounts.Account;java.lang.String;android.os.Bundle");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.content.ContentResolver.class, p0, p1, p2);
  }

  @Redirect("android.content.ContentResolver->setIsSyncable")
  public static void redirection_of_android_content_ContentResolver_setIsSyncable3(android.accounts.Account p0, java.lang.String p1, int p2)
  {
    Log.i(API, "android.content.ContentResolver|setIsSyncable|void|android.accounts.Account;java.lang.String;int");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.content.ContentResolver.class, p0, p1, p2);
  }

  @Redirect("android.content.ContentResolver->setMasterSyncAutomatically")
  public static void redirection_of_android_content_ContentResolver_setMasterSyncAutomatically1(boolean p0)
  {
    Log.i(API, "android.content.ContentResolver|setMasterSyncAutomatically|void|boolean");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.content.ContentResolver.class, p0);
  }

  @Redirect("android.content.ContentResolver->setSyncAutomatically")
  public static void redirection_of_android_content_ContentResolver_setSyncAutomatically3(android.accounts.Account p0, java.lang.String p1, boolean p2)
  {
    Log.i(API, "android.content.ContentResolver|setSyncAutomatically|void|android.accounts.Account;java.lang.String;boolean");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.content.ContentResolver.class, p0, p1, p2);
  }

  @Redirect("android.content.ContentResolver->update")
  public static int redirection_of_android_content_ContentResolver_update4(android.content.ContentResolver _this, android.net.Uri p0, android.content.ContentValues p1, java.lang.String p2, java.lang.String[] p3)
  {
    Log.i(API, "android.content.ContentResolver|update|int|android.net.Uri;android.content.ContentValues;java.lang.String;java.lang.String[]");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.content.ContextWrapper->clearWallpaper")
  public static void redirection_of_android_content_ContextWrapper_clearWallpaper0(android.content.ContextWrapper _this)
  {
    Log.i(API, "android.content.ContextWrapper|clearWallpaper|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.content.ContextWrapper->removeStickyBroadcast")
  public static void redirection_of_android_content_ContextWrapper_removeStickyBroadcast1(android.content.ContextWrapper _this, android.content.Intent p0)
  {
    Log.i(API, "android.content.ContextWrapper|removeStickyBroadcast|void|android.content.Intent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.content.ContextWrapper->sendStickyBroadcast")
  public static void redirection_of_android_content_ContextWrapper_sendStickyBroadcast1(android.content.ContextWrapper _this, android.content.Intent p0)
  {
    Log.i(API, "android.content.ContextWrapper|sendStickyBroadcast|void|android.content.Intent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.content.ContextWrapper->sendStickyOrderedBroadcast")
  public static void redirection_of_android_content_ContextWrapper_sendStickyOrderedBroadcast6(android.content.ContextWrapper _this, android.content.Intent p0, android.content.BroadcastReceiver p1, android.os.Handler p2, int p3, java.lang.String p4, android.os.Bundle p5)
  {
    Log.i(API, "android.content.ContextWrapper|sendStickyOrderedBroadcast|void|android.content.Intent;android.content.BroadcastReceiver;android.os.Handler;int;java.lang.String;android.os.Bundle");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.content.ContextWrapper->setWallpaper")
  public static void redirection_of_android_content_ContextWrapper_setWallpaper1(android.content.ContextWrapper _this, android.graphics.Bitmap p0)
  {
    Log.i(API, "android.content.ContextWrapper|setWallpaper|void|android.graphics.Bitmap");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.content.ContextWrapper->setWallpaper")
  public static void redirection_of_android_content_ContextWrapper_setWallpaper1(android.content.ContextWrapper _this, java.io.InputStream p0)
  {
    Log.i(API, "android.content.ContextWrapper|setWallpaper|void|java.io.InputStream");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.hardware.Camera->open")
  public static android.hardware.Camera redirection_of_android_hardware_Camera_open0()
  {
    Log.i(API, "android.hardware.Camera|open|android.hardware.Camera|");
    class $ {}
    return (android.hardware.Camera) Instrumentation.callStaticObjectMethod($.class, android.hardware.Camera.class, 0);
  }

  @Redirect("android.hardware.Camera->open")
  public static android.hardware.Camera redirection_of_android_hardware_Camera_open1(int p0)
  {
    Log.i(API, "android.hardware.Camera|open|android.hardware.Camera|int");
    class $ {}
    return (android.hardware.Camera) Instrumentation.callStaticObjectMethod($.class, android.hardware.Camera.class, p0);
  }

  @Redirect("android.inputmethodservice.KeyboardView->onHoverEvent")
  public static boolean redirection_of_android_inputmethodservice_KeyboardView_onHoverEvent1(android.inputmethodservice.KeyboardView _this, android.view.MotionEvent p0)
  {
    Log.i(API, "android.inputmethodservice.KeyboardView|onHoverEvent|boolean|android.view.MotionEvent");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.inputmethodservice.KeyboardView->onLongPress")
  public static boolean redirection_of_android_inputmethodservice_KeyboardView_onLongPress1(android.inputmethodservice.KeyboardView _this, android.inputmethodservice.Keyboard.Key p0)
  {
    Log.i(API, "android.inputmethodservice.KeyboardView|onLongPress|boolean|android.inputmethodservice.Keyboard.Key");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.inputmethodservice.KeyboardView->onTouchEvent")
  public static boolean redirection_of_android_inputmethodservice_KeyboardView_onTouchEvent1(android.inputmethodservice.KeyboardView _this, android.view.MotionEvent p0)
  {
    Log.i(API, "android.inputmethodservice.KeyboardView|onTouchEvent|boolean|android.view.MotionEvent");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.inputmethodservice.KeyboardView->setKeyboard")
  public static void redirection_of_android_inputmethodservice_KeyboardView_setKeyboard1(android.inputmethodservice.KeyboardView _this, android.inputmethodservice.Keyboard p0)
  {
    Log.i(API, "android.inputmethodservice.KeyboardView|setKeyboard|void|android.inputmethodservice.Keyboard");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->addGpsStatusListener")
  public static boolean redirection_of_android_location_LocationManager_addGpsStatusListener1(android.location.LocationManager _this, android.location.GpsStatus.Listener p0)
  {
    Log.i(API, "android.location.LocationManager|addGpsStatusListener|boolean|android.location.GpsStatus.Listener");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->addNmeaListener")
  public static boolean redirection_of_android_location_LocationManager_addNmeaListener1(android.location.LocationManager _this, android.location.GpsStatus.NmeaListener p0)
  {
    Log.i(API, "android.location.LocationManager|addNmeaListener|boolean|android.location.GpsStatus.NmeaListener");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->addProximityAlert")
  public static void redirection_of_android_location_LocationManager_addProximityAlert5(android.location.LocationManager _this, double p0, double p1, float p2, long p3, android.app.PendingIntent p4)
  {
    Log.i(API, "android.location.LocationManager|addProximityAlert|void|double;double;float;long;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.location.LocationManager->addTestProvider")
  public static void redirection_of_android_location_LocationManager_addTestProvider10(android.location.LocationManager _this, java.lang.String p0, boolean p1, boolean p2, boolean p3, boolean p4, boolean p5, boolean p6, boolean p7, int p8, int p9)
  {
    Log.i(API, "android.location.LocationManager|addTestProvider|void|java.lang.String;boolean;boolean;boolean;boolean;boolean;boolean;boolean;int;int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
  }

  @Redirect("android.location.LocationManager->clearTestProviderEnabled")
  public static void redirection_of_android_location_LocationManager_clearTestProviderEnabled1(android.location.LocationManager _this, java.lang.String p0)
  {
    Log.i(API, "android.location.LocationManager|clearTestProviderEnabled|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->clearTestProviderLocation")
  public static void redirection_of_android_location_LocationManager_clearTestProviderLocation1(android.location.LocationManager _this, java.lang.String p0)
  {
    Log.i(API, "android.location.LocationManager|clearTestProviderLocation|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->clearTestProviderStatus")
  public static void redirection_of_android_location_LocationManager_clearTestProviderStatus1(android.location.LocationManager _this, java.lang.String p0)
  {
    Log.i(API, "android.location.LocationManager|clearTestProviderStatus|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->getBestProvider")
  public static java.lang.String redirection_of_android_location_LocationManager_getBestProvider2(android.location.LocationManager _this, android.location.Criteria p0, boolean p1)
  {
    Log.i(API, "android.location.LocationManager|getBestProvider|java.lang.String|android.location.Criteria;boolean");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.location.LocationManager->getLastKnownLocation")
  public static android.location.Location redirection_of_android_location_LocationManager_getLastKnownLocation1(android.location.LocationManager _this, java.lang.String p0)
  {
    Log.i(API, "android.location.LocationManager|getLastKnownLocation|android.location.Location|java.lang.String");
    class $ {}
    return (android.location.Location) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->getProvider")
  public static android.location.LocationProvider redirection_of_android_location_LocationManager_getProvider1(android.location.LocationManager _this, java.lang.String p0)
  {
    Log.i(API, "android.location.LocationManager|getProvider|android.location.LocationProvider|java.lang.String");
    class $ {}
    return (android.location.LocationProvider) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->getProviders")
  public static java.util.List redirection_of_android_location_LocationManager_getProviders1(android.location.LocationManager _this, boolean p0)
  {
    Log.i(API, "android.location.LocationManager|getProviders|java.util.List|boolean");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->getProviders")
  public static java.util.List redirection_of_android_location_LocationManager_getProviders2(android.location.LocationManager _this, android.location.Criteria p0, boolean p1)
  {
    Log.i(API, "android.location.LocationManager|getProviders|java.util.List|android.location.Criteria;boolean");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.location.LocationManager->isProviderEnabled")
  public static boolean redirection_of_android_location_LocationManager_isProviderEnabled1(android.location.LocationManager _this, java.lang.String p0)
  {
    Log.i(API, "android.location.LocationManager|isProviderEnabled|boolean|java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->removeTestProvider")
  public static void redirection_of_android_location_LocationManager_removeTestProvider1(android.location.LocationManager _this, java.lang.String p0)
  {
    Log.i(API, "android.location.LocationManager|removeTestProvider|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.location.LocationManager->requestLocationUpdates")
  public static void redirection_of_android_location_LocationManager_requestLocationUpdates4(android.location.LocationManager _this, java.lang.String p0, long p1, float p2, android.location.LocationListener p3)
  {
    Log.i(API, "android.location.LocationManager|requestLocationUpdates|void|java.lang.String;long;float;android.location.LocationListener");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.location.LocationManager->requestLocationUpdates")
  public static void redirection_of_android_location_LocationManager_requestLocationUpdates4(android.location.LocationManager _this, long p0, float p1, android.location.Criteria p2, android.app.PendingIntent p3)
  {
    Log.i(API, "android.location.LocationManager|requestLocationUpdates|void|long;float;android.location.Criteria;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.location.LocationManager->requestLocationUpdates")
  public static void redirection_of_android_location_LocationManager_requestLocationUpdates4(android.location.LocationManager _this, java.lang.String p0, long p1, float p2, android.app.PendingIntent p3)
  {
    Log.i(API, "android.location.LocationManager|requestLocationUpdates|void|java.lang.String;long;float;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.location.LocationManager->requestLocationUpdates")
  public static void redirection_of_android_location_LocationManager_requestLocationUpdates5(android.location.LocationManager _this, long p0, float p1, android.location.Criteria p2, android.location.LocationListener p3, android.os.Looper p4)
  {
    Log.i(API, "android.location.LocationManager|requestLocationUpdates|void|long;float;android.location.Criteria;android.location.LocationListener;android.os.Looper");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.location.LocationManager->requestLocationUpdates")
  public static void redirection_of_android_location_LocationManager_requestLocationUpdates5(android.location.LocationManager _this, java.lang.String p0, long p1, float p2, android.location.LocationListener p3, android.os.Looper p4)
  {
    Log.i(API, "android.location.LocationManager|requestLocationUpdates|void|java.lang.String;long;float;android.location.LocationListener;android.os.Looper");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.location.LocationManager->requestSingleUpdate")
  public static void redirection_of_android_location_LocationManager_requestSingleUpdate2(android.location.LocationManager _this, android.location.Criteria p0, android.app.PendingIntent p1)
  {
    Log.i(API, "android.location.LocationManager|requestSingleUpdate|void|android.location.Criteria;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.location.LocationManager->requestSingleUpdate")
  public static void redirection_of_android_location_LocationManager_requestSingleUpdate2(android.location.LocationManager _this, java.lang.String p0, android.app.PendingIntent p1)
  {
    Log.i(API, "android.location.LocationManager|requestSingleUpdate|void|java.lang.String;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.location.LocationManager->requestSingleUpdate")
  public static void redirection_of_android_location_LocationManager_requestSingleUpdate3(android.location.LocationManager _this, java.lang.String p0, android.location.LocationListener p1, android.os.Looper p2)
  {
    Log.i(API, "android.location.LocationManager|requestSingleUpdate|void|java.lang.String;android.location.LocationListener;android.os.Looper");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.location.LocationManager->requestSingleUpdate")
  public static void redirection_of_android_location_LocationManager_requestSingleUpdate3(android.location.LocationManager _this, android.location.Criteria p0, android.location.LocationListener p1, android.os.Looper p2)
  {
    Log.i(API, "android.location.LocationManager|requestSingleUpdate|void|android.location.Criteria;android.location.LocationListener;android.os.Looper");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.location.LocationManager->sendExtraCommand")
  public static boolean redirection_of_android_location_LocationManager_sendExtraCommand3(android.location.LocationManager _this, java.lang.String p0, java.lang.String p1, android.os.Bundle p2)
  {
    Log.i(API, "android.location.LocationManager|sendExtraCommand|boolean|java.lang.String;java.lang.String;android.os.Bundle");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.location.LocationManager->setTestProviderEnabled")
  public static void redirection_of_android_location_LocationManager_setTestProviderEnabled2(android.location.LocationManager _this, java.lang.String p0, boolean p1)
  {
    Log.i(API, "android.location.LocationManager|setTestProviderEnabled|void|java.lang.String;boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.location.LocationManager->setTestProviderLocation")
  public static void redirection_of_android_location_LocationManager_setTestProviderLocation2(android.location.LocationManager _this, java.lang.String p0, android.location.Location p1)
  {
    Log.i(API, "android.location.LocationManager|setTestProviderLocation|void|java.lang.String;android.location.Location");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.location.LocationManager->setTestProviderStatus")
  public static void redirection_of_android_location_LocationManager_setTestProviderStatus4(android.location.LocationManager _this, java.lang.String p0, int p1, android.os.Bundle p2, long p3)
  {
    Log.i(API, "android.location.LocationManager|setTestProviderStatus|void|java.lang.String;int;android.os.Bundle;long");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.media.AsyncPlayer->play")
  public static void redirection_of_android_media_AsyncPlayer_play4(android.media.AsyncPlayer _this, android.content.Context p0, android.net.Uri p1, boolean p2, int p3)
  {
    Log.i(API, "android.media.AsyncPlayer|play|void|android.content.Context;android.net.Uri;boolean;int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.media.AsyncPlayer->stop")
  public static void redirection_of_android_media_AsyncPlayer_stop0(android.media.AsyncPlayer _this)
  {
    Log.i(API, "android.media.AsyncPlayer|stop|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.AudioManager->isBluetoothA2dpOn")
  public static boolean redirection_of_android_media_AudioManager_isBluetoothA2dpOn0(android.media.AudioManager _this)
  {
    Log.i(API, "android.media.AudioManager|isBluetoothA2dpOn|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.media.AudioManager->isWiredHeadsetOn")
  public static boolean redirection_of_android_media_AudioManager_isWiredHeadsetOn0(android.media.AudioManager _this)
  {
    Log.i(API, "android.media.AudioManager|isWiredHeadsetOn|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.media.AudioManager->setBluetoothScoOn")
  public static void redirection_of_android_media_AudioManager_setBluetoothScoOn1(android.media.AudioManager _this, boolean p0)
  {
    Log.i(API, "android.media.AudioManager|setBluetoothScoOn|void|boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.AudioManager->setMicrophoneMute")
  public static void redirection_of_android_media_AudioManager_setMicrophoneMute1(android.media.AudioManager _this, boolean p0)
  {
    Log.i(API, "android.media.AudioManager|setMicrophoneMute|void|boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.AudioManager->setMode")
  public static void redirection_of_android_media_AudioManager_setMode1(android.media.AudioManager _this, int p0)
  {
    Log.i(API, "android.media.AudioManager|setMode|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.AudioManager->setParameter")
  public static void redirection_of_android_media_AudioManager_setParameter2(android.media.AudioManager _this, java.lang.String p0, java.lang.String p1)
  {
    Log.i(API, "android.media.AudioManager|setParameter|void|java.lang.String;java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.media.AudioManager->setParameters")
  public static void redirection_of_android_media_AudioManager_setParameters1(android.media.AudioManager _this, java.lang.String p0)
  {
    Log.i(API, "android.media.AudioManager|setParameters|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.AudioManager->setSpeakerphoneOn")
  public static void redirection_of_android_media_AudioManager_setSpeakerphoneOn1(android.media.AudioManager _this, boolean p0)
  {
    Log.i(API, "android.media.AudioManager|setSpeakerphoneOn|void|boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.AudioManager->startBluetoothSco")
  public static void redirection_of_android_media_AudioManager_startBluetoothSco0(android.media.AudioManager _this)
  {
    Log.i(API, "android.media.AudioManager|startBluetoothSco|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.AudioManager->stopBluetoothSco")
  public static void redirection_of_android_media_AudioManager_stopBluetoothSco0(android.media.AudioManager _this)
  {
    Log.i(API, "android.media.AudioManager|stopBluetoothSco|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.MediaPlayer->pause")
  public static void redirection_of_android_media_MediaPlayer_pause0(android.media.MediaPlayer _this)
  {
    Log.i(API, "android.media.MediaPlayer|pause|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.MediaPlayer->release")
  public static void redirection_of_android_media_MediaPlayer_release0(android.media.MediaPlayer _this)
  {
    Log.i(API, "android.media.MediaPlayer|release|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.MediaPlayer->reset")
  public static void redirection_of_android_media_MediaPlayer_reset0(android.media.MediaPlayer _this)
  {
    Log.i(API, "android.media.MediaPlayer|reset|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.MediaPlayer->setWakeMode")
  public static void redirection_of_android_media_MediaPlayer_setWakeMode2(android.media.MediaPlayer _this, android.content.Context p0, int p1)
  {
    Log.i(API, "android.media.MediaPlayer|setWakeMode|void|android.content.Context;int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.media.MediaPlayer->start")
  public static void redirection_of_android_media_MediaPlayer_start0(android.media.MediaPlayer _this)
  {
    Log.i(API, "android.media.MediaPlayer|start|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.MediaPlayer->stop")
  public static void redirection_of_android_media_MediaPlayer_stop0(android.media.MediaPlayer _this)
  {
    Log.i(API, "android.media.MediaPlayer|stop|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.MediaRecorder->setAudioSource")
  public static void redirection_of_android_media_MediaRecorder_setAudioSource1(android.media.MediaRecorder _this, int p0)
  {
    Log.i(API, "android.media.MediaRecorder|setAudioSource|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.MediaRecorder->setVideoSource")
  public static void redirection_of_android_media_MediaRecorder_setVideoSource1(android.media.MediaRecorder _this, int p0)
  {
    Log.i(API, "android.media.MediaRecorder|setVideoSource|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.Ringtone->play")
  public static void redirection_of_android_media_Ringtone_play0(android.media.Ringtone _this)
  {
    Log.i(API, "android.media.Ringtone|play|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.Ringtone->setStreamType")
  public static void redirection_of_android_media_Ringtone_setStreamType1(android.media.Ringtone _this, int p0)
  {
    Log.i(API, "android.media.Ringtone|setStreamType|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.Ringtone->setUri")
  public static void redirection_of_android_media_Ringtone_setUri1(android.media.Ringtone _this, android.net.Uri p0)
  {
    Log.i(API, "android.media.Ringtone|setUri|void|android.net.Uri");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.media.Ringtone->stop")
  public static void redirection_of_android_media_Ringtone_stop0(android.media.Ringtone _this)
  {
    Log.i(API, "android.media.Ringtone|stop|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.RingtoneManager->getRingtone")
  public static android.media.Ringtone redirection_of_android_media_RingtoneManager_getRingtone1(android.media.RingtoneManager _this, int p0)
  {
    Log.i(API, "android.media.RingtoneManager|getRingtone|android.media.Ringtone|int");
    class $ {}
    return (android.media.Ringtone) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.media.RingtoneManager->getRingtone")
  public static android.media.Ringtone redirection_of_android_media_RingtoneManager_getRingtone2(android.content.Context p0, android.net.Uri p1)
  {
    Log.i(API, "android.media.RingtoneManager|getRingtone|android.media.Ringtone|android.content.Context;android.net.Uri");
    class $ {}
    return (android.media.Ringtone) Instrumentation.callStaticObjectMethod($.class, android.media.RingtoneManager.class, p0, p1);
  }

  @Redirect("android.media.RingtoneManager->setActualDefaultRingtoneUri")
  public static void redirection_of_android_media_RingtoneManager_setActualDefaultRingtoneUri3(android.content.Context p0, int p1, android.net.Uri p2)
  {
    Log.i(API, "android.media.RingtoneManager|setActualDefaultRingtoneUri|void|android.content.Context;int;android.net.Uri");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.media.RingtoneManager.class, p0, p1, p2);
  }

  @Redirect("android.media.RingtoneManager->stopPreviousRingtone")
  public static void redirection_of_android_media_RingtoneManager_stopPreviousRingtone0(android.media.RingtoneManager _this)
  {
    Log.i(API, "android.media.RingtoneManager|stopPreviousRingtone|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.media.effect.EffectContext->release")
  public static void redirection_of_android_media_effect_EffectContext_release0(android.media.effect.EffectContext _this)
  {
    Log.i(API, "android.media.effect.EffectContext|release|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.ConnectivityManager->getActiveNetworkInfo")
  public static android.net.NetworkInfo redirection_of_android_net_ConnectivityManager_getActiveNetworkInfo0(android.net.ConnectivityManager _this)
  {
    Log.i(API, "android.net.ConnectivityManager|getActiveNetworkInfo|android.net.NetworkInfo|");
    class $ {}
    return (android.net.NetworkInfo) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.net.ConnectivityManager->getAllNetworkInfo")
  public static android.net.NetworkInfo[] redirection_of_android_net_ConnectivityManager_getAllNetworkInfo0(android.net.ConnectivityManager _this)
  {
    Log.i(API, "android.net.ConnectivityManager|getAllNetworkInfo|android.net.NetworkInfo[]|");
    class $ {}
    return (android.net.NetworkInfo[]) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.net.ConnectivityManager->getNetworkInfo")
  public static android.net.NetworkInfo redirection_of_android_net_ConnectivityManager_getNetworkInfo1(android.net.ConnectivityManager _this, int p0)
  {
    Log.i(API, "android.net.ConnectivityManager|getNetworkInfo|android.net.NetworkInfo|int");
    class $ {}
    return (android.net.NetworkInfo) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.net.ConnectivityManager->getNetworkPreference")
  public static int redirection_of_android_net_ConnectivityManager_getNetworkPreference0(android.net.ConnectivityManager _this)
  {
    Log.i(API, "android.net.ConnectivityManager|getNetworkPreference|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.net.ConnectivityManager->isActiveNetworkMetered")
  public static boolean redirection_of_android_net_ConnectivityManager_isActiveNetworkMetered0(android.net.ConnectivityManager _this)
  {
    Log.i(API, "android.net.ConnectivityManager|isActiveNetworkMetered|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.net.ConnectivityManager->requestRouteToHost")
  public static boolean redirection_of_android_net_ConnectivityManager_requestRouteToHost2(android.net.ConnectivityManager _this, int p0, int p1)
  {
    Log.i(API, "android.net.ConnectivityManager|requestRouteToHost|boolean|int;int");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.ConnectivityManager->setNetworkPreference")
  public static void redirection_of_android_net_ConnectivityManager_setNetworkPreference1(android.net.ConnectivityManager _this, int p0)
  {
    Log.i(API, "android.net.ConnectivityManager|setNetworkPreference|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.net.ConnectivityManager->startUsingNetworkFeature")
  public static int redirection_of_android_net_ConnectivityManager_startUsingNetworkFeature2(android.net.ConnectivityManager _this, int p0, java.lang.String p1)
  {
    Log.i(API, "android.net.ConnectivityManager|startUsingNetworkFeature|int|int;java.lang.String");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.ConnectivityManager->stopUsingNetworkFeature")
  public static int redirection_of_android_net_ConnectivityManager_stopUsingNetworkFeature2(android.net.ConnectivityManager _this, int p0, java.lang.String p1)
  {
    Log.i(API, "android.net.ConnectivityManager|stopUsingNetworkFeature|int|int;java.lang.String");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.nsd.NsdManager->init")
  public static void redirection_of_android_net_nsd_NsdManager_init0(android.net.nsd.NsdManager _this)
  {
    Log.i(API, "android.net.nsd.NsdManager|init|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.nsd.NsdManager->setEnabled")
  public static void redirection_of_android_net_nsd_NsdManager_setEnabled1(android.net.nsd.NsdManager _this, boolean p0)
  {
    Log.i(API, "android.net.nsd.NsdManager|setEnabled|void|boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.net.sip.SipAudioCall->close")
  public static void redirection_of_android_net_sip_SipAudioCall_close0(android.net.sip.SipAudioCall _this)
  {
    Log.i(API, "android.net.sip.SipAudioCall|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.sip.SipAudioCall->close")
  public static void redirection_of_android_net_sip_SipAudioCall_close1(android.net.sip.SipAudioCall _this, boolean p0)
  {
    Log.i(API, "android.net.sip.SipAudioCall|close|void|boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.net.sip.SipAudioCall->endCall")
  public static void redirection_of_android_net_sip_SipAudioCall_endCall0(android.net.sip.SipAudioCall _this)
  {
    Log.i(API, "android.net.sip.SipAudioCall|endCall|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.sip.SipAudioCall->onError")
  public static void redirection_of_android_net_sip_SipAudioCall_onError2(android.net.sip.SipAudioCall _this, int p0, java.lang.String p1)
  {
    Log.i(API, "android.net.sip.SipAudioCall|onError|void|int;java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.sip.SipAudioCall->setSpeakerMode")
  public static void redirection_of_android_net_sip_SipAudioCall_setSpeakerMode1(android.net.sip.SipAudioCall _this, boolean p0)
  {
    Log.i(API, "android.net.sip.SipAudioCall|setSpeakerMode|void|boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.net.sip.SipAudioCall->startAudio")
  public static void redirection_of_android_net_sip_SipAudioCall_startAudio0(android.net.sip.SipAudioCall _this)
  {
    Log.i(API, "android.net.sip.SipAudioCall|startAudio|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.sip.SipManager->close")
  public static void redirection_of_android_net_sip_SipManager_close1(android.net.sip.SipManager _this, java.lang.String p0)
  {
    Log.i(API, "android.net.sip.SipManager|close|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.net.sip.SipManager->createSipSession")
  public static android.net.sip.SipSession redirection_of_android_net_sip_SipManager_createSipSession2(android.net.sip.SipManager _this, android.net.sip.SipProfile p0, android.net.sip.SipSession.Listener p1)
  {
    Log.i(API, "android.net.sip.SipManager|createSipSession|android.net.sip.SipSession|android.net.sip.SipProfile;android.net.sip.SipSession.Listener");
    class $ {}
    return (android.net.sip.SipSession) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.sip.SipManager->getSessionFor")
  public static android.net.sip.SipSession redirection_of_android_net_sip_SipManager_getSessionFor1(android.net.sip.SipManager _this, android.content.Intent p0)
  {
    Log.i(API, "android.net.sip.SipManager|getSessionFor|android.net.sip.SipSession|android.content.Intent");
    class $ {}
    return (android.net.sip.SipSession) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.net.sip.SipManager->isOpened")
  public static boolean redirection_of_android_net_sip_SipManager_isOpened1(android.net.sip.SipManager _this, java.lang.String p0)
  {
    Log.i(API, "android.net.sip.SipManager|isOpened|boolean|java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.net.sip.SipManager->isRegistered")
  public static boolean redirection_of_android_net_sip_SipManager_isRegistered1(android.net.sip.SipManager _this, java.lang.String p0)
  {
    Log.i(API, "android.net.sip.SipManager|isRegistered|boolean|java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.net.sip.SipManager->makeAudioCall")
  public static android.net.sip.SipAudioCall redirection_of_android_net_sip_SipManager_makeAudioCall4(android.net.sip.SipManager _this, android.net.sip.SipProfile p0, android.net.sip.SipProfile p1, android.net.sip.SipAudioCall.Listener p2, int p3)
  {
    Log.i(API, "android.net.sip.SipManager|makeAudioCall|android.net.sip.SipAudioCall|android.net.sip.SipProfile;android.net.sip.SipProfile;android.net.sip.SipAudioCall.Listener;int");
    class $ {}
    return (android.net.sip.SipAudioCall) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.net.sip.SipManager->makeAudioCall")
  public static android.net.sip.SipAudioCall redirection_of_android_net_sip_SipManager_makeAudioCall4(android.net.sip.SipManager _this, java.lang.String p0, java.lang.String p1, android.net.sip.SipAudioCall.Listener p2, int p3)
  {
    Log.i(API, "android.net.sip.SipManager|makeAudioCall|android.net.sip.SipAudioCall|java.lang.String;java.lang.String;android.net.sip.SipAudioCall.Listener;int");
    class $ {}
    return (android.net.sip.SipAudioCall) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.net.sip.SipManager->open")
  public static void redirection_of_android_net_sip_SipManager_open1(android.net.sip.SipManager _this, android.net.sip.SipProfile p0)
  {
    Log.i(API, "android.net.sip.SipManager|open|void|android.net.sip.SipProfile");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.net.sip.SipManager->open")
  public static void redirection_of_android_net_sip_SipManager_open3(android.net.sip.SipManager _this, android.net.sip.SipProfile p0, android.app.PendingIntent p1, android.net.sip.SipRegistrationListener p2)
  {
    Log.i(API, "android.net.sip.SipManager|open|void|android.net.sip.SipProfile;android.app.PendingIntent;android.net.sip.SipRegistrationListener");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.net.sip.SipManager->register")
  public static void redirection_of_android_net_sip_SipManager_register3(android.net.sip.SipManager _this, android.net.sip.SipProfile p0, int p1, android.net.sip.SipRegistrationListener p2)
  {
    Log.i(API, "android.net.sip.SipManager|register|void|android.net.sip.SipProfile;int;android.net.sip.SipRegistrationListener");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.net.sip.SipManager->setRegistrationListener")
  public static void redirection_of_android_net_sip_SipManager_setRegistrationListener2(android.net.sip.SipManager _this, java.lang.String p0, android.net.sip.SipRegistrationListener p1)
  {
    Log.i(API, "android.net.sip.SipManager|setRegistrationListener|void|java.lang.String;android.net.sip.SipRegistrationListener");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.sip.SipManager->takeAudioCall")
  public static android.net.sip.SipAudioCall redirection_of_android_net_sip_SipManager_takeAudioCall2(android.net.sip.SipManager _this, android.content.Intent p0, android.net.sip.SipAudioCall.Listener p1)
  {
    Log.i(API, "android.net.sip.SipManager|takeAudioCall|android.net.sip.SipAudioCall|android.content.Intent;android.net.sip.SipAudioCall.Listener");
    class $ {}
    return (android.net.sip.SipAudioCall) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.sip.SipManager->unregister")
  public static void redirection_of_android_net_sip_SipManager_unregister2(android.net.sip.SipManager _this, android.net.sip.SipProfile p0, android.net.sip.SipRegistrationListener p1)
  {
    Log.i(API, "android.net.sip.SipManager|unregister|void|android.net.sip.SipProfile;android.net.sip.SipRegistrationListener");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.wifi.WifiManager->addNetwork")
  public static int redirection_of_android_net_wifi_WifiManager_addNetwork1(android.net.wifi.WifiManager _this, android.net.wifi.WifiConfiguration p0)
  {
    Log.i(API, "android.net.wifi.WifiManager|addNetwork|int|android.net.wifi.WifiConfiguration");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0);
  }

  @Redirect("android.net.wifi.WifiManager->disableNetwork")
  public static boolean redirection_of_android_net_wifi_WifiManager_disableNetwork1(android.net.wifi.WifiManager _this, int p0)
  {
    Log.i(API, "android.net.wifi.WifiManager|disableNetwork|boolean|int");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.net.wifi.WifiManager->disconnect")
  public static boolean redirection_of_android_net_wifi_WifiManager_disconnect0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|disconnect|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->enableNetwork")
  public static boolean redirection_of_android_net_wifi_WifiManager_enableNetwork2(android.net.wifi.WifiManager _this, int p0, boolean p1)
  {
    Log.i(API, "android.net.wifi.WifiManager|enableNetwork|boolean|int;boolean");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.wifi.WifiManager->getConfiguredNetworks")
  public static java.util.List redirection_of_android_net_wifi_WifiManager_getConfiguredNetworks0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|getConfiguredNetworks|java.util.List|");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->getConnectionInfo")
  public static android.net.wifi.WifiInfo redirection_of_android_net_wifi_WifiManager_getConnectionInfo0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|getConnectionInfo|android.net.wifi.WifiInfo|");
    class $ {}
    return (android.net.wifi.WifiInfo) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->getDhcpInfo")
  public static android.net.DhcpInfo redirection_of_android_net_wifi_WifiManager_getDhcpInfo0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|getDhcpInfo|android.net.DhcpInfo|");
    class $ {}
    return (android.net.DhcpInfo) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->getScanResults")
  public static java.util.List redirection_of_android_net_wifi_WifiManager_getScanResults0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|getScanResults|java.util.List|");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->getWifiState")
  public static int redirection_of_android_net_wifi_WifiManager_getWifiState0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|getWifiState|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->isWifiEnabled")
  public static boolean redirection_of_android_net_wifi_WifiManager_isWifiEnabled0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|isWifiEnabled|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->pingSupplicant")
  public static boolean redirection_of_android_net_wifi_WifiManager_pingSupplicant0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|pingSupplicant|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->reassociate")
  public static boolean redirection_of_android_net_wifi_WifiManager_reassociate0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|reassociate|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->reconnect")
  public static boolean redirection_of_android_net_wifi_WifiManager_reconnect0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|reconnect|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->removeNetwork")
  public static boolean redirection_of_android_net_wifi_WifiManager_removeNetwork1(android.net.wifi.WifiManager _this, int p0)
  {
    Log.i(API, "android.net.wifi.WifiManager|removeNetwork|boolean|int");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.net.wifi.WifiManager->saveConfiguration")
  public static boolean redirection_of_android_net_wifi_WifiManager_saveConfiguration0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|saveConfiguration|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->setCountryCode")
  public static void redirection_of_android_net_wifi_WifiManager_setCountryCode2(android.net.wifi.WifiManager _this, java.lang.String p0, boolean p1)
  {
    Log.i(API, "android.net.wifi.WifiManager|setCountryCode|void|java.lang.String;boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.net.wifi.WifiManager->setWifiEnabled")
  public static boolean redirection_of_android_net_wifi_WifiManager_setWifiEnabled1(android.net.wifi.WifiManager _this, boolean p0)
  {
    Log.i(API, "android.net.wifi.WifiManager|setWifiEnabled|boolean|boolean");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0);
  }

  @Redirect("android.net.wifi.WifiManager->startScan")
  public static boolean redirection_of_android_net_wifi_WifiManager_startScan0(android.net.wifi.WifiManager _this)
  {
    Log.i(API, "android.net.wifi.WifiManager|startScan|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager->updateNetwork")
  public static int redirection_of_android_net_wifi_WifiManager_updateNetwork1(android.net.wifi.WifiManager _this, android.net.wifi.WifiConfiguration p0)
  {
    Log.i(API, "android.net.wifi.WifiManager|updateNetwork|int|android.net.wifi.WifiConfiguration");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this, p0);
  }

  @Redirect("android.net.wifi.WifiManager$MulticastLock->acquire")
  public static void redirection_of_android_net_wifi_WifiManager_MulticastLock_acquire0(android.net.wifi.WifiManager.MulticastLock _this)
  {
    Log.i(API, "android.net.wifi.WifiManager$MulticastLock|acquire|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager$MulticastLock->finalize")
  public static void redirection_of_android_net_wifi_WifiManager_MulticastLock_finalize0(android.net.wifi.WifiManager.MulticastLock _this)
  {
    Log.i(API, "android.net.wifi.WifiManager$MulticastLock|finalize|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager$MulticastLock->release")
  public static void redirection_of_android_net_wifi_WifiManager_MulticastLock_release0(android.net.wifi.WifiManager.MulticastLock _this)
  {
    Log.i(API, "android.net.wifi.WifiManager$MulticastLock|release|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager$WifiLock->acquire")
  public static void redirection_of_android_net_wifi_WifiManager_WifiLock_acquire0(android.net.wifi.WifiManager.WifiLock _this)
  {
    Log.i(API, "android.net.wifi.WifiManager$WifiLock|acquire|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager$WifiLock->finalize")
  public static void redirection_of_android_net_wifi_WifiManager_WifiLock_finalize0(android.net.wifi.WifiManager.WifiLock _this)
  {
    Log.i(API, "android.net.wifi.WifiManager$WifiLock|finalize|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.wifi.WifiManager$WifiLock->release")
  public static void redirection_of_android_net_wifi_WifiManager_WifiLock_release0(android.net.wifi.WifiManager.WifiLock _this)
  {
    Log.i(API, "android.net.wifi.WifiManager$WifiLock|release|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.net.wifi.p2p.WifiP2pManager->initialize")
  public static android.net.wifi.p2p.WifiP2pManager.Channel redirection_of_android_net_wifi_p2p_WifiP2pManager_initialize3(android.net.wifi.p2p.WifiP2pManager _this, android.content.Context p0, android.os.Looper p1, android.net.wifi.p2p.WifiP2pManager.ChannelListener p2)
  {
    Log.i(API, "android.net.wifi.p2p.WifiP2pManager|initialize|android.net.wifi.p2p.WifiP2pManager.Channel|android.content.Context;android.os.Looper;android.net.wifi.p2p.WifiP2pManager.ChannelListener");
    class $ {}
    return (android.net.wifi.p2p.WifiP2pManager.Channel) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.nfc.NfcAdapter->disableForegroundDispatch")
  public static void redirection_of_android_nfc_NfcAdapter_disableForegroundDispatch1(android.nfc.NfcAdapter _this, android.app.Activity p0)
  {
    Log.i(API, "android.nfc.NfcAdapter|disableForegroundDispatch|void|android.app.Activity");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.NfcAdapter->disableForegroundNdefPush")
  public static void redirection_of_android_nfc_NfcAdapter_disableForegroundNdefPush1(android.nfc.NfcAdapter _this, android.app.Activity p0)
  {
    Log.i(API, "android.nfc.NfcAdapter|disableForegroundNdefPush|void|android.app.Activity");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.NfcAdapter->dispatch")
  public static void redirection_of_android_nfc_NfcAdapter_dispatch1(android.nfc.NfcAdapter _this, android.nfc.Tag p0)
  {
    Log.i(API, "android.nfc.NfcAdapter|dispatch|void|android.nfc.Tag");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.NfcAdapter->enableForegroundDispatch")
  public static void redirection_of_android_nfc_NfcAdapter_enableForegroundDispatch4(android.nfc.NfcAdapter _this, android.app.Activity p0, android.app.PendingIntent p1, android.content.IntentFilter[] p2, java.lang.String[][] p3)
  {
    Log.i(API, "android.nfc.NfcAdapter|enableForegroundDispatch|void|android.app.Activity;android.app.PendingIntent;android.content.IntentFilter[];java.lang.String[][]");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.nfc.NfcAdapter->enableForegroundNdefPush")
  public static void redirection_of_android_nfc_NfcAdapter_enableForegroundNdefPush2(android.nfc.NfcAdapter _this, android.app.Activity p0, android.nfc.NdefMessage p1)
  {
    Log.i(API, "android.nfc.NfcAdapter|enableForegroundNdefPush|void|android.app.Activity;android.nfc.NdefMessage");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.NfcAdapter->setBeamPushUris")
  public static void redirection_of_android_nfc_NfcAdapter_setBeamPushUris2(android.nfc.NfcAdapter _this, android.net.Uri[] p0, android.app.Activity p1)
  {
    Log.i(API, "android.nfc.NfcAdapter|setBeamPushUris|void|android.net.Uri[];android.app.Activity");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.NfcAdapter->setBeamPushUrisCallback")
  public static void redirection_of_android_nfc_NfcAdapter_setBeamPushUrisCallback2(android.nfc.NfcAdapter _this, android.nfc.NfcAdapter.CreateBeamUrisCallback p0, android.app.Activity p1)
  {
    Log.i(API, "android.nfc.NfcAdapter|setBeamPushUrisCallback|void|android.nfc.NfcAdapter.CreateBeamUrisCallback;android.app.Activity");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.NfcAdapter->setNdefPushMessage")
  public static void redirection_of_android_nfc_NfcAdapter_setNdefPushMessage3(android.nfc.NfcAdapter _this, android.nfc.NdefMessage p0, android.app.Activity p1, android.app.Activity[] p2)
  {
    Log.i(API, "android.nfc.NfcAdapter|setNdefPushMessage|void|android.nfc.NdefMessage;android.app.Activity;android.app.Activity[]");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.nfc.NfcAdapter->setNdefPushMessageCallback")
  public static void redirection_of_android_nfc_NfcAdapter_setNdefPushMessageCallback3(android.nfc.NfcAdapter _this, android.nfc.NfcAdapter.CreateNdefMessageCallback p0, android.app.Activity p1, android.app.Activity[] p2)
  {
    Log.i(API, "android.nfc.NfcAdapter|setNdefPushMessageCallback|void|android.nfc.NfcAdapter.CreateNdefMessageCallback;android.app.Activity;android.app.Activity[]");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.nfc.NfcAdapter->setOnNdefPushCompleteCallback")
  public static void redirection_of_android_nfc_NfcAdapter_setOnNdefPushCompleteCallback3(android.nfc.NfcAdapter _this, android.nfc.NfcAdapter.OnNdefPushCompleteCallback p0, android.app.Activity p1, android.app.Activity[] p2)
  {
    Log.i(API, "android.nfc.NfcAdapter|setOnNdefPushCompleteCallback|void|android.nfc.NfcAdapter.OnNdefPushCompleteCallback;android.app.Activity;android.app.Activity[]");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.nfc.tech.IsoDep->close")
  public static void redirection_of_android_nfc_tech_IsoDep_close0(android.nfc.tech.IsoDep _this)
  {
    Log.i(API, "android.nfc.tech.IsoDep|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.IsoDep->connect")
  public static void redirection_of_android_nfc_tech_IsoDep_connect0(android.nfc.tech.IsoDep _this)
  {
    Log.i(API, "android.nfc.tech.IsoDep|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.IsoDep->getTimeout")
  public static int redirection_of_android_nfc_tech_IsoDep_getTimeout0(android.nfc.tech.IsoDep _this)
  {
    Log.i(API, "android.nfc.tech.IsoDep|getTimeout|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.IsoDep->reconnect")
  public static void redirection_of_android_nfc_tech_IsoDep_reconnect0(android.nfc.tech.IsoDep _this)
  {
    Log.i(API, "android.nfc.tech.IsoDep|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.IsoDep->setTimeout")
  public static void redirection_of_android_nfc_tech_IsoDep_setTimeout1(android.nfc.tech.IsoDep _this, int p0)
  {
    Log.i(API, "android.nfc.tech.IsoDep|setTimeout|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.IsoDep->transceive")
  public static byte[] redirection_of_android_nfc_tech_IsoDep_transceive1(android.nfc.tech.IsoDep _this, byte[] p0)
  {
    Log.i(API, "android.nfc.tech.IsoDep|transceive|byte[]|byte[]");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareClassic->authenticate")
  public static boolean redirection_of_android_nfc_tech_MifareClassic_authenticate3(android.nfc.tech.MifareClassic _this, int p0, byte[] p1, boolean p2)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|authenticate|boolean|int;byte[];boolean");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.nfc.tech.MifareClassic->authenticateSectorWithKeyA")
  public static boolean redirection_of_android_nfc_tech_MifareClassic_authenticateSectorWithKeyA2(android.nfc.tech.MifareClassic _this, int p0, byte[] p1)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|authenticateSectorWithKeyA|boolean|int;byte[]");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.tech.MifareClassic->authenticateSectorWithKeyB")
  public static boolean redirection_of_android_nfc_tech_MifareClassic_authenticateSectorWithKeyB2(android.nfc.tech.MifareClassic _this, int p0, byte[] p1)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|authenticateSectorWithKeyB|boolean|int;byte[]");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.tech.MifareClassic->close")
  public static void redirection_of_android_nfc_tech_MifareClassic_close0(android.nfc.tech.MifareClassic _this)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.MifareClassic->connect")
  public static void redirection_of_android_nfc_tech_MifareClassic_connect0(android.nfc.tech.MifareClassic _this)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.MifareClassic->decrement")
  public static void redirection_of_android_nfc_tech_MifareClassic_decrement2(android.nfc.tech.MifareClassic _this, int p0, int p1)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|decrement|void|int;int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.tech.MifareClassic->getTimeout")
  public static int redirection_of_android_nfc_tech_MifareClassic_getTimeout0(android.nfc.tech.MifareClassic _this)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|getTimeout|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.MifareClassic->increment")
  public static void redirection_of_android_nfc_tech_MifareClassic_increment2(android.nfc.tech.MifareClassic _this, int p0, int p1)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|increment|void|int;int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.tech.MifareClassic->readBlock")
  public static byte[] redirection_of_android_nfc_tech_MifareClassic_readBlock1(android.nfc.tech.MifareClassic _this, int p0)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|readBlock|byte[]|int");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareClassic->reconnect")
  public static void redirection_of_android_nfc_tech_MifareClassic_reconnect0(android.nfc.tech.MifareClassic _this)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.MifareClassic->restore")
  public static void redirection_of_android_nfc_tech_MifareClassic_restore1(android.nfc.tech.MifareClassic _this, int p0)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|restore|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareClassic->setTimeout")
  public static void redirection_of_android_nfc_tech_MifareClassic_setTimeout1(android.nfc.tech.MifareClassic _this, int p0)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|setTimeout|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareClassic->transceive")
  public static byte[] redirection_of_android_nfc_tech_MifareClassic_transceive1(android.nfc.tech.MifareClassic _this, byte[] p0)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|transceive|byte[]|byte[]");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareClassic->transfer")
  public static void redirection_of_android_nfc_tech_MifareClassic_transfer1(android.nfc.tech.MifareClassic _this, int p0)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|transfer|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareClassic->writeBlock")
  public static void redirection_of_android_nfc_tech_MifareClassic_writeBlock2(android.nfc.tech.MifareClassic _this, int p0, byte[] p1)
  {
    Log.i(API, "android.nfc.tech.MifareClassic|writeBlock|void|int;byte[]");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.tech.MifareUltralight->close")
  public static void redirection_of_android_nfc_tech_MifareUltralight_close0(android.nfc.tech.MifareUltralight _this)
  {
    Log.i(API, "android.nfc.tech.MifareUltralight|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.MifareUltralight->connect")
  public static void redirection_of_android_nfc_tech_MifareUltralight_connect0(android.nfc.tech.MifareUltralight _this)
  {
    Log.i(API, "android.nfc.tech.MifareUltralight|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.MifareUltralight->getTimeout")
  public static int redirection_of_android_nfc_tech_MifareUltralight_getTimeout0(android.nfc.tech.MifareUltralight _this)
  {
    Log.i(API, "android.nfc.tech.MifareUltralight|getTimeout|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.MifareUltralight->readPages")
  public static byte[] redirection_of_android_nfc_tech_MifareUltralight_readPages1(android.nfc.tech.MifareUltralight _this, int p0)
  {
    Log.i(API, "android.nfc.tech.MifareUltralight|readPages|byte[]|int");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareUltralight->reconnect")
  public static void redirection_of_android_nfc_tech_MifareUltralight_reconnect0(android.nfc.tech.MifareUltralight _this)
  {
    Log.i(API, "android.nfc.tech.MifareUltralight|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.MifareUltralight->setTimeout")
  public static void redirection_of_android_nfc_tech_MifareUltralight_setTimeout1(android.nfc.tech.MifareUltralight _this, int p0)
  {
    Log.i(API, "android.nfc.tech.MifareUltralight|setTimeout|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareUltralight->transceive")
  public static byte[] redirection_of_android_nfc_tech_MifareUltralight_transceive1(android.nfc.tech.MifareUltralight _this, byte[] p0)
  {
    Log.i(API, "android.nfc.tech.MifareUltralight|transceive|byte[]|byte[]");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.MifareUltralight->writePage")
  public static void redirection_of_android_nfc_tech_MifareUltralight_writePage2(android.nfc.tech.MifareUltralight _this, int p0, byte[] p1)
  {
    Log.i(API, "android.nfc.tech.MifareUltralight|writePage|void|int;byte[]");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.tech.Ndef->close")
  public static void redirection_of_android_nfc_tech_Ndef_close0(android.nfc.tech.Ndef _this)
  {
    Log.i(API, "android.nfc.tech.Ndef|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.Ndef->connect")
  public static void redirection_of_android_nfc_tech_Ndef_connect0(android.nfc.tech.Ndef _this)
  {
    Log.i(API, "android.nfc.tech.Ndef|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.Ndef->getNdefMessage")
  public static android.nfc.NdefMessage redirection_of_android_nfc_tech_Ndef_getNdefMessage0(android.nfc.tech.Ndef _this)
  {
    Log.i(API, "android.nfc.tech.Ndef|getNdefMessage|android.nfc.NdefMessage|");
    class $ {}
    return (android.nfc.NdefMessage) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.Ndef->makeReadOnly")
  public static boolean redirection_of_android_nfc_tech_Ndef_makeReadOnly0(android.nfc.tech.Ndef _this)
  {
    Log.i(API, "android.nfc.tech.Ndef|makeReadOnly|boolean|");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.Ndef->reconnect")
  public static void redirection_of_android_nfc_tech_Ndef_reconnect0(android.nfc.tech.Ndef _this)
  {
    Log.i(API, "android.nfc.tech.Ndef|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.Ndef->writeNdefMessage")
  public static void redirection_of_android_nfc_tech_Ndef_writeNdefMessage1(android.nfc.tech.Ndef _this, android.nfc.NdefMessage p0)
  {
    Log.i(API, "android.nfc.tech.Ndef|writeNdefMessage|void|android.nfc.NdefMessage");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.NdefFormatable->close")
  public static void redirection_of_android_nfc_tech_NdefFormatable_close0(android.nfc.tech.NdefFormatable _this)
  {
    Log.i(API, "android.nfc.tech.NdefFormatable|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NdefFormatable->connect")
  public static void redirection_of_android_nfc_tech_NdefFormatable_connect0(android.nfc.tech.NdefFormatable _this)
  {
    Log.i(API, "android.nfc.tech.NdefFormatable|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NdefFormatable->format")
  public static void redirection_of_android_nfc_tech_NdefFormatable_format1(android.nfc.tech.NdefFormatable _this, android.nfc.NdefMessage p0)
  {
    Log.i(API, "android.nfc.tech.NdefFormatable|format|void|android.nfc.NdefMessage");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.NdefFormatable->format")
  public static void redirection_of_android_nfc_tech_NdefFormatable_format2(android.nfc.tech.NdefFormatable _this, android.nfc.NdefMessage p0, boolean p1)
  {
    Log.i(API, "android.nfc.tech.NdefFormatable|format|void|android.nfc.NdefMessage;boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.nfc.tech.NdefFormatable->formatReadOnly")
  public static void redirection_of_android_nfc_tech_NdefFormatable_formatReadOnly1(android.nfc.tech.NdefFormatable _this, android.nfc.NdefMessage p0)
  {
    Log.i(API, "android.nfc.tech.NdefFormatable|formatReadOnly|void|android.nfc.NdefMessage");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.NdefFormatable->reconnect")
  public static void redirection_of_android_nfc_tech_NdefFormatable_reconnect0(android.nfc.tech.NdefFormatable _this)
  {
    Log.i(API, "android.nfc.tech.NdefFormatable|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcA->close")
  public static void redirection_of_android_nfc_tech_NfcA_close0(android.nfc.tech.NfcA _this)
  {
    Log.i(API, "android.nfc.tech.NfcA|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcA->connect")
  public static void redirection_of_android_nfc_tech_NfcA_connect0(android.nfc.tech.NfcA _this)
  {
    Log.i(API, "android.nfc.tech.NfcA|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcA->getTimeout")
  public static int redirection_of_android_nfc_tech_NfcA_getTimeout0(android.nfc.tech.NfcA _this)
  {
    Log.i(API, "android.nfc.tech.NfcA|getTimeout|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcA->reconnect")
  public static void redirection_of_android_nfc_tech_NfcA_reconnect0(android.nfc.tech.NfcA _this)
  {
    Log.i(API, "android.nfc.tech.NfcA|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcA->setTimeout")
  public static void redirection_of_android_nfc_tech_NfcA_setTimeout1(android.nfc.tech.NfcA _this, int p0)
  {
    Log.i(API, "android.nfc.tech.NfcA|setTimeout|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.NfcA->transceive")
  public static byte[] redirection_of_android_nfc_tech_NfcA_transceive1(android.nfc.tech.NfcA _this, byte[] p0)
  {
    Log.i(API, "android.nfc.tech.NfcA|transceive|byte[]|byte[]");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.NfcB->close")
  public static void redirection_of_android_nfc_tech_NfcB_close0(android.nfc.tech.NfcB _this)
  {
    Log.i(API, "android.nfc.tech.NfcB|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcB->connect")
  public static void redirection_of_android_nfc_tech_NfcB_connect0(android.nfc.tech.NfcB _this)
  {
    Log.i(API, "android.nfc.tech.NfcB|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcB->reconnect")
  public static void redirection_of_android_nfc_tech_NfcB_reconnect0(android.nfc.tech.NfcB _this)
  {
    Log.i(API, "android.nfc.tech.NfcB|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcB->transceive")
  public static byte[] redirection_of_android_nfc_tech_NfcB_transceive1(android.nfc.tech.NfcB _this, byte[] p0)
  {
    Log.i(API, "android.nfc.tech.NfcB|transceive|byte[]|byte[]");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.NfcF->close")
  public static void redirection_of_android_nfc_tech_NfcF_close0(android.nfc.tech.NfcF _this)
  {
    Log.i(API, "android.nfc.tech.NfcF|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcF->connect")
  public static void redirection_of_android_nfc_tech_NfcF_connect0(android.nfc.tech.NfcF _this)
  {
    Log.i(API, "android.nfc.tech.NfcF|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcF->getTimeout")
  public static int redirection_of_android_nfc_tech_NfcF_getTimeout0(android.nfc.tech.NfcF _this)
  {
    Log.i(API, "android.nfc.tech.NfcF|getTimeout|int|");
    class $ {}
    return (int) Instrumentation.callIntMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcF->reconnect")
  public static void redirection_of_android_nfc_tech_NfcF_reconnect0(android.nfc.tech.NfcF _this)
  {
    Log.i(API, "android.nfc.tech.NfcF|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcF->setTimeout")
  public static void redirection_of_android_nfc_tech_NfcF_setTimeout1(android.nfc.tech.NfcF _this, int p0)
  {
    Log.i(API, "android.nfc.tech.NfcF|setTimeout|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.NfcF->transceive")
  public static byte[] redirection_of_android_nfc_tech_NfcF_transceive1(android.nfc.tech.NfcF _this, byte[] p0)
  {
    Log.i(API, "android.nfc.tech.NfcF|transceive|byte[]|byte[]");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.nfc.tech.NfcV->close")
  public static void redirection_of_android_nfc_tech_NfcV_close0(android.nfc.tech.NfcV _this)
  {
    Log.i(API, "android.nfc.tech.NfcV|close|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcV->connect")
  public static void redirection_of_android_nfc_tech_NfcV_connect0(android.nfc.tech.NfcV _this)
  {
    Log.i(API, "android.nfc.tech.NfcV|connect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcV->reconnect")
  public static void redirection_of_android_nfc_tech_NfcV_reconnect0(android.nfc.tech.NfcV _this)
  {
    Log.i(API, "android.nfc.tech.NfcV|reconnect|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.nfc.tech.NfcV->transceive")
  public static byte[] redirection_of_android_nfc_tech_NfcV_transceive1(android.nfc.tech.NfcV _this, byte[] p0)
  {
    Log.i(API, "android.nfc.tech.NfcV|transceive|byte[]|byte[]");
    class $ {}
    return (byte[]) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("android.os.PowerManager$WakeLock->acquire")
  public static void redirection_of_android_os_PowerManager_WakeLock_acquire0(android.os.PowerManager.WakeLock _this)
  {
    Log.i(API, "android.os.PowerManager$WakeLock|acquire|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.os.PowerManager$WakeLock->acquire")
  public static void redirection_of_android_os_PowerManager_WakeLock_acquire1(android.os.PowerManager.WakeLock _this, long p0)
  {
    Log.i(API, "android.os.PowerManager$WakeLock|acquire|void|long");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.os.PowerManager$WakeLock->finalize")
  public static void redirection_of_android_os_PowerManager_WakeLock_finalize0(android.os.PowerManager.WakeLock _this)
  {
    Log.i(API, "android.os.PowerManager$WakeLock|finalize|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.os.PowerManager$WakeLock->release")
  public static void redirection_of_android_os_PowerManager_WakeLock_release0(android.os.PowerManager.WakeLock _this)
  {
    Log.i(API, "android.os.PowerManager$WakeLock|release|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.os.PowerManager$WakeLock->release")
  public static void redirection_of_android_os_PowerManager_WakeLock_release1(android.os.PowerManager.WakeLock _this, int p0)
  {
    Log.i(API, "android.os.PowerManager$WakeLock|release|void|int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.provider.Browser->addSearchUrl")
  public static void redirection_of_android_provider_Browser_addSearchUrl2(android.content.ContentResolver p0, java.lang.String p1)
  {
    Log.i(API, "android.provider.Browser|addSearchUrl|void|android.content.ContentResolver;java.lang.String");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Browser.class, p0, p1);
  }

  @Redirect("android.provider.Browser->canClearHistory")
  public static boolean redirection_of_android_provider_Browser_canClearHistory1(android.content.ContentResolver p0)
  {
    Log.i(API, "android.provider.Browser|canClearHistory|boolean|android.content.ContentResolver");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Browser.class, p0);
  }

  @Redirect("android.provider.Browser->clearHistory")
  public static void redirection_of_android_provider_Browser_clearHistory1(android.content.ContentResolver p0)
  {
    Log.i(API, "android.provider.Browser|clearHistory|void|android.content.ContentResolver");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Browser.class, p0);
  }

  @Redirect("android.provider.Browser->clearSearches")
  public static void redirection_of_android_provider_Browser_clearSearches1(android.content.ContentResolver p0)
  {
    Log.i(API, "android.provider.Browser|clearSearches|void|android.content.ContentResolver");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Browser.class, p0);
  }

  @Redirect("android.provider.Browser->deleteFromHistory")
  public static void redirection_of_android_provider_Browser_deleteFromHistory2(android.content.ContentResolver p0, java.lang.String p1)
  {
    Log.i(API, "android.provider.Browser|deleteFromHistory|void|android.content.ContentResolver;java.lang.String");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Browser.class, p0, p1);
  }

  @Redirect("android.provider.Browser->deleteHistoryTimeFrame")
  public static void redirection_of_android_provider_Browser_deleteHistoryTimeFrame3(android.content.ContentResolver p0, long p1, long p2)
  {
    Log.i(API, "android.provider.Browser|deleteHistoryTimeFrame|void|android.content.ContentResolver;long;long");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Browser.class, p0, p1, p2);
  }

  @Redirect("android.provider.Browser->getAllBookmarks")
  public static android.database.Cursor redirection_of_android_provider_Browser_getAllBookmarks1(android.content.ContentResolver p0)
  {
    Log.i(API, "android.provider.Browser|getAllBookmarks|android.database.Cursor|android.content.ContentResolver");
    class $ {}
    return (android.database.Cursor) Instrumentation.callStaticObjectMethod($.class, android.provider.Browser.class, p0);
  }

  @Redirect("android.provider.Browser->getAllVisitedUrls")
  public static android.database.Cursor redirection_of_android_provider_Browser_getAllVisitedUrls1(android.content.ContentResolver p0)
  {
    Log.i(API, "android.provider.Browser|getAllVisitedUrls|android.database.Cursor|android.content.ContentResolver");
    class $ {}
    return (android.database.Cursor) Instrumentation.callStaticObjectMethod($.class, android.provider.Browser.class, p0);
  }

  @Redirect("android.provider.Browser->getVisitedHistory")
  public static java.lang.String[] redirection_of_android_provider_Browser_getVisitedHistory1(android.content.ContentResolver p0)
  {
    Log.i(API, "android.provider.Browser|getVisitedHistory|java.lang.String[]|android.content.ContentResolver");
    class $ {}
    return (java.lang.String[]) Instrumentation.callStaticObjectMethod($.class, android.provider.Browser.class, p0);
  }

  @Redirect("android.provider.Browser->truncateHistory")
  public static void redirection_of_android_provider_Browser_truncateHistory1(android.content.ContentResolver p0)
  {
    Log.i(API, "android.provider.Browser|truncateHistory|void|android.content.ContentResolver");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Browser.class, p0);
  }

  @Redirect("android.provider.Browser->updateVisitedHistory")
  public static void redirection_of_android_provider_Browser_updateVisitedHistory3(android.content.ContentResolver p0, java.lang.String p1, boolean p2)
  {
    Log.i(API, "android.provider.Browser|updateVisitedHistory|void|android.content.ContentResolver;java.lang.String;boolean");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Browser.class, p0, p1, p2);
  }

  @Redirect("android.provider.CalendarContract$Attendees->query")
  public static android.database.Cursor redirection_of_android_provider_CalendarContract_Attendees_query3(android.content.ContentResolver p0, long p1, java.lang.String[] p2)
  {
    Log.i(API, "android.provider.CalendarContract$Attendees|query|android.database.Cursor|android.content.ContentResolver;long;java.lang.String[]");
    class $ {}
    return (android.database.Cursor) Instrumentation.callStaticObjectMethod($.class, android.provider.CalendarContract.Attendees.class, p0, p1, p2);
  }

  @Redirect("android.provider.CalendarContract$CalendarAlerts->insert")
  public static android.net.Uri redirection_of_android_provider_CalendarContract_CalendarAlerts_insert6(android.content.ContentResolver p0, long p1, long p2, long p3, long p4, int p5)
  {
    Log.i(API, "android.provider.CalendarContract$CalendarAlerts|insert|android.net.Uri|android.content.ContentResolver;long;long;long;long;int");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.CalendarContract.CalendarAlerts.class, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.provider.CalendarContract$EventDays->query")
  public static android.database.Cursor redirection_of_android_provider_CalendarContract_EventDays_query4(android.content.ContentResolver p0, int p1, int p2, java.lang.String[] p3)
  {
    Log.i(API, "android.provider.CalendarContract$EventDays|query|android.database.Cursor|android.content.ContentResolver;int;int;java.lang.String[]");
    class $ {}
    return (android.database.Cursor) Instrumentation.callStaticObjectMethod($.class, android.provider.CalendarContract.EventDays.class, p0, p1, p2, p3);
  }

  @Redirect("android.provider.CalendarContract$Instances->query")
  public static android.database.Cursor redirection_of_android_provider_CalendarContract_Instances_query4(android.content.ContentResolver p0, java.lang.String[] p1, long p2, long p3)
  {
    Log.i(API, "android.provider.CalendarContract$Instances|query|android.database.Cursor|android.content.ContentResolver;java.lang.String[];long;long");
    class $ {}
    return (android.database.Cursor) Instrumentation.callStaticObjectMethod($.class, android.provider.CalendarContract.Instances.class, p0, p1, p2, p3);
  }

  @Redirect("android.provider.CalendarContract$Instances->query")
  public static android.database.Cursor redirection_of_android_provider_CalendarContract_Instances_query5(android.content.ContentResolver p0, java.lang.String[] p1, long p2, long p3, java.lang.String p4)
  {
    Log.i(API, "android.provider.CalendarContract$Instances|query|android.database.Cursor|android.content.ContentResolver;java.lang.String[];long;long;java.lang.String");
    class $ {}
    return (android.database.Cursor) Instrumentation.callStaticObjectMethod($.class, android.provider.CalendarContract.Instances.class, p0, p1, p2, p3, p4);
  }

  @Redirect("android.provider.CalendarContract$Reminders->query")
  public static android.database.Cursor redirection_of_android_provider_CalendarContract_Reminders_query3(android.content.ContentResolver p0, long p1, java.lang.String[] p2)
  {
    Log.i(API, "android.provider.CalendarContract$Reminders|query|android.database.Cursor|android.content.ContentResolver;long;java.lang.String[]");
    class $ {}
    return (android.database.Cursor) Instrumentation.callStaticObjectMethod($.class, android.provider.CalendarContract.Reminders.class, p0, p1, p2);
  }

  @Redirect("android.provider.CallLog$Calls->getLastOutgoingCall")
  public static java.lang.String redirection_of_android_provider_CallLog_Calls_getLastOutgoingCall1(android.content.Context p0)
  {
    Log.i(API, "android.provider.CallLog$Calls|getLastOutgoingCall|java.lang.String|android.content.Context");
    class $ {}
    return (java.lang.String) Instrumentation.callStaticObjectMethod($.class, android.provider.CallLog.Calls.class, p0);
  }

  @Redirect("android.provider.Contacts$ContactMethods->addPostalLocation")
  public static void redirection_of_android_provider_Contacts_ContactMethods_addPostalLocation4(android.provider.Contacts.ContactMethods _this, android.content.Context p0, long p1, double p2, double p3)
  {
    Log.i(API, "android.provider.Contacts$ContactMethods|addPostalLocation|void|android.content.Context;long;double;double");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.provider.Contacts$People->addToGroup")
  public static android.net.Uri redirection_of_android_provider_Contacts_People_addToGroup3(android.content.ContentResolver p0, long p1, java.lang.String p2)
  {
    Log.i(API, "android.provider.Contacts$People|addToGroup|android.net.Uri|android.content.ContentResolver;long;java.lang.String");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.Contacts.People.class, p0, p1, p2);
  }

  @Redirect("android.provider.Contacts$People->addToGroup")
  public static android.net.Uri redirection_of_android_provider_Contacts_People_addToGroup3(android.content.ContentResolver p0, long p1, long p2)
  {
    Log.i(API, "android.provider.Contacts$People|addToGroup|android.net.Uri|android.content.ContentResolver;long;long");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.Contacts.People.class, p0, p1, p2);
  }

  @Redirect("android.provider.Contacts$People->addToMyContactsGroup")
  public static android.net.Uri redirection_of_android_provider_Contacts_People_addToMyContactsGroup2(android.content.ContentResolver p0, long p1)
  {
    Log.i(API, "android.provider.Contacts$People|addToMyContactsGroup|android.net.Uri|android.content.ContentResolver;long");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.Contacts.People.class, p0, p1);
  }

  @Redirect("android.provider.Contacts$People->createPersonInMyContactsGroup")
  public static android.net.Uri redirection_of_android_provider_Contacts_People_createPersonInMyContactsGroup2(android.content.ContentResolver p0, android.content.ContentValues p1)
  {
    Log.i(API, "android.provider.Contacts$People|createPersonInMyContactsGroup|android.net.Uri|android.content.ContentResolver;android.content.ContentValues");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.Contacts.People.class, p0, p1);
  }

  @Redirect("android.provider.Contacts$People->markAsContacted")
  public static void redirection_of_android_provider_Contacts_People_markAsContacted2(android.content.ContentResolver p0, long p1)
  {
    Log.i(API, "android.provider.Contacts$People|markAsContacted|void|android.content.ContentResolver;long");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Contacts.People.class, p0, p1);
  }

  @Redirect("android.provider.Contacts$People->queryGroups")
  public static android.database.Cursor redirection_of_android_provider_Contacts_People_queryGroups2(android.content.ContentResolver p0, long p1)
  {
    Log.i(API, "android.provider.Contacts$People|queryGroups|android.database.Cursor|android.content.ContentResolver;long");
    class $ {}
    return (android.database.Cursor) Instrumentation.callStaticObjectMethod($.class, android.provider.Contacts.People.class, p0, p1);
  }

  @Redirect("android.provider.Contacts$Settings->getSetting")
  public static java.lang.String redirection_of_android_provider_Contacts_Settings_getSetting3(android.content.ContentResolver p0, java.lang.String p1, java.lang.String p2)
  {
    Log.i(API, "android.provider.Contacts$Settings|getSetting|java.lang.String|android.content.ContentResolver;java.lang.String;java.lang.String");
    class $ {}
    return (java.lang.String) Instrumentation.callStaticObjectMethod($.class, android.provider.Contacts.Settings.class, p0, p1, p2);
  }

  @Redirect("android.provider.Contacts$Settings->setSetting")
  public static void redirection_of_android_provider_Contacts_Settings_setSetting4(android.content.ContentResolver p0, java.lang.String p1, java.lang.String p2, java.lang.String p3)
  {
    Log.i(API, "android.provider.Contacts$Settings|setSetting|void|android.content.ContentResolver;java.lang.String;java.lang.String;java.lang.String");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Contacts.Settings.class, p0, p1, p2, p3);
  }

  @Redirect("android.provider.ContactsContract$Contacts->getLookupUri")
  public static android.net.Uri redirection_of_android_provider_ContactsContract_Contacts_getLookupUri2(android.content.ContentResolver p0, android.net.Uri p1)
  {
    Log.i(API, "android.provider.ContactsContract$Contacts|getLookupUri|android.net.Uri|android.content.ContentResolver;android.net.Uri");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.Contacts.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$Contacts->getLookupUri")
  public static android.net.Uri redirection_of_android_provider_ContactsContract_Contacts_getLookupUri2(long p0, java.lang.String p1)
  {
    Log.i(API, "android.provider.ContactsContract$Contacts|getLookupUri|android.net.Uri|long;java.lang.String");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.Contacts.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$Contacts->markAsContacted")
  public static void redirection_of_android_provider_ContactsContract_Contacts_markAsContacted2(android.content.ContentResolver p0, long p1)
  {
    Log.i(API, "android.provider.ContactsContract$Contacts|markAsContacted|void|android.content.ContentResolver;long");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.ContactsContract.Contacts.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$Data->getContactLookupUri")
  public static android.net.Uri redirection_of_android_provider_ContactsContract_Data_getContactLookupUri2(android.content.ContentResolver p0, android.net.Uri p1)
  {
    Log.i(API, "android.provider.ContactsContract$Data|getContactLookupUri|android.net.Uri|android.content.ContentResolver;android.net.Uri");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.Data.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$Directory->notifyDirectoryChange")
  public static void redirection_of_android_provider_ContactsContract_Directory_notifyDirectoryChange1(android.content.ContentResolver p0)
  {
    Log.i(API, "android.provider.ContactsContract$Directory|notifyDirectoryChange|void|android.content.ContentResolver");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.ContactsContract.Directory.class, p0);
  }

  @Redirect("android.provider.ContactsContract$ProfileSyncState->get")
  public static byte[] redirection_of_android_provider_ContactsContract_ProfileSyncState_get2(android.content.ContentProviderClient p0, android.accounts.Account p1)
  {
    Log.i(API, "android.provider.ContactsContract$ProfileSyncState|get|byte[]|android.content.ContentProviderClient;android.accounts.Account");
    class $ {}
    return (byte[]) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.ProfileSyncState.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$ProfileSyncState->getWithUri")
  public static android.util.Pair redirection_of_android_provider_ContactsContract_ProfileSyncState_getWithUri2(android.content.ContentProviderClient p0, android.accounts.Account p1)
  {
    Log.i(API, "android.provider.ContactsContract$ProfileSyncState|getWithUri|android.util.Pair|android.content.ContentProviderClient;android.accounts.Account");
    class $ {}
    return (android.util.Pair) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.ProfileSyncState.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$ProfileSyncState->newSetOperation")
  public static android.content.ContentProviderOperation redirection_of_android_provider_ContactsContract_ProfileSyncState_newSetOperation2(android.accounts.Account p0, byte[] p1)
  {
    Log.i(API, "android.provider.ContactsContract$ProfileSyncState|newSetOperation|android.content.ContentProviderOperation|android.accounts.Account;byte[]");
    class $ {}
    return (android.content.ContentProviderOperation) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.ProfileSyncState.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$ProfileSyncState->set")
  public static void redirection_of_android_provider_ContactsContract_ProfileSyncState_set3(android.content.ContentProviderClient p0, android.accounts.Account p1, byte[] p2)
  {
    Log.i(API, "android.provider.ContactsContract$ProfileSyncState|set|void|android.content.ContentProviderClient;android.accounts.Account;byte[]");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.ContactsContract.ProfileSyncState.class, p0, p1, p2);
  }

  @Redirect("android.provider.ContactsContract$RawContacts->getContactLookupUri")
  public static android.net.Uri redirection_of_android_provider_ContactsContract_RawContacts_getContactLookupUri2(android.content.ContentResolver p0, android.net.Uri p1)
  {
    Log.i(API, "android.provider.ContactsContract$RawContacts|getContactLookupUri|android.net.Uri|android.content.ContentResolver;android.net.Uri");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.RawContacts.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$SyncState->get")
  public static byte[] redirection_of_android_provider_ContactsContract_SyncState_get2(android.content.ContentProviderClient p0, android.accounts.Account p1)
  {
    Log.i(API, "android.provider.ContactsContract$SyncState|get|byte[]|android.content.ContentProviderClient;android.accounts.Account");
    class $ {}
    return (byte[]) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.SyncState.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$SyncState->getWithUri")
  public static android.util.Pair redirection_of_android_provider_ContactsContract_SyncState_getWithUri2(android.content.ContentProviderClient p0, android.accounts.Account p1)
  {
    Log.i(API, "android.provider.ContactsContract$SyncState|getWithUri|android.util.Pair|android.content.ContentProviderClient;android.accounts.Account");
    class $ {}
    return (android.util.Pair) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.SyncState.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$SyncState->newSetOperation")
  public static android.content.ContentProviderOperation redirection_of_android_provider_ContactsContract_SyncState_newSetOperation2(android.accounts.Account p0, byte[] p1)
  {
    Log.i(API, "android.provider.ContactsContract$SyncState|newSetOperation|android.content.ContentProviderOperation|android.accounts.Account;byte[]");
    class $ {}
    return (android.content.ContentProviderOperation) Instrumentation.callStaticObjectMethod($.class, android.provider.ContactsContract.SyncState.class, p0, p1);
  }

  @Redirect("android.provider.ContactsContract$SyncState->set")
  public static void redirection_of_android_provider_ContactsContract_SyncState_set3(android.content.ContentProviderClient p0, android.accounts.Account p1, byte[] p2)
  {
    Log.i(API, "android.provider.ContactsContract$SyncState|set|void|android.content.ContentProviderClient;android.accounts.Account;byte[]");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.ContactsContract.SyncState.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$Secure->getUriFor")
  public static android.net.Uri redirection_of_android_provider_Settings_Secure_getUriFor1(java.lang.String p0)
  {
    Log.i(API, "android.provider.Settings$Secure|getUriFor|android.net.Uri|java.lang.String");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.Settings.Secure.class, p0);
  }

  @Redirect("android.provider.Settings$Secure->putFloat")
  public static boolean redirection_of_android_provider_Settings_Secure_putFloat3(android.content.ContentResolver p0, java.lang.String p1, float p2)
  {
    Log.i(API, "android.provider.Settings$Secure|putFloat|boolean|android.content.ContentResolver;java.lang.String;float");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.Secure.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$Secure->putInt")
  public static boolean redirection_of_android_provider_Settings_Secure_putInt3(android.content.ContentResolver p0, java.lang.String p1, int p2)
  {
    Log.i(API, "android.provider.Settings$Secure|putInt|boolean|android.content.ContentResolver;java.lang.String;int");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.Secure.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$Secure->putLong")
  public static boolean redirection_of_android_provider_Settings_Secure_putLong3(android.content.ContentResolver p0, java.lang.String p1, long p2)
  {
    Log.i(API, "android.provider.Settings$Secure|putLong|boolean|android.content.ContentResolver;java.lang.String;long");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.Secure.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$Secure->putString")
  public static boolean redirection_of_android_provider_Settings_Secure_putString3(android.content.ContentResolver p0, java.lang.String p1, java.lang.String p2)
  {
    Log.i(API, "android.provider.Settings$Secure|putString|boolean|android.content.ContentResolver;java.lang.String;java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.Secure.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$Secure->setLocationProviderEnabled")
  public static void redirection_of_android_provider_Settings_Secure_setLocationProviderEnabled3(android.content.ContentResolver p0, java.lang.String p1, boolean p2)
  {
    Log.i(API, "android.provider.Settings$Secure|setLocationProviderEnabled|void|android.content.ContentResolver;java.lang.String;boolean");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Settings.Secure.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$System->getUriFor")
  public static android.net.Uri redirection_of_android_provider_Settings_System_getUriFor1(java.lang.String p0)
  {
    Log.i(API, "android.provider.Settings$System|getUriFor|android.net.Uri|java.lang.String");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.Settings.System.class, p0);
  }

  @Redirect("android.provider.Settings$System->putConfiguration")
  public static boolean redirection_of_android_provider_Settings_System_putConfiguration2(android.content.ContentResolver p0, android.content.res.Configuration p1)
  {
    Log.i(API, "android.provider.Settings$System|putConfiguration|boolean|android.content.ContentResolver;android.content.res.Configuration");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.System.class, p0, p1);
  }

  @Redirect("android.provider.Settings$System->putFloat")
  public static boolean redirection_of_android_provider_Settings_System_putFloat3(android.content.ContentResolver p0, java.lang.String p1, float p2)
  {
    Log.i(API, "android.provider.Settings$System|putFloat|boolean|android.content.ContentResolver;java.lang.String;float");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.System.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$System->putInt")
  public static boolean redirection_of_android_provider_Settings_System_putInt3(android.content.ContentResolver p0, java.lang.String p1, int p2)
  {
    Log.i(API, "android.provider.Settings$System|putInt|boolean|android.content.ContentResolver;java.lang.String;int");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.System.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$System->putLong")
  public static boolean redirection_of_android_provider_Settings_System_putLong3(android.content.ContentResolver p0, java.lang.String p1, long p2)
  {
    Log.i(API, "android.provider.Settings$System|putLong|boolean|android.content.ContentResolver;java.lang.String;long");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.System.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$System->putString")
  public static boolean redirection_of_android_provider_Settings_System_putString3(android.content.ContentResolver p0, java.lang.String p1, java.lang.String p2)
  {
    Log.i(API, "android.provider.Settings$System|putString|boolean|android.content.ContentResolver;java.lang.String;java.lang.String");
    class $ {}
    return (boolean) Instrumentation.callStaticBooleanMethod($.class, android.provider.Settings.System.class, p0, p1, p2);
  }

  @Redirect("android.provider.Settings$System->setShowGTalkServiceStatus")
  public static void redirection_of_android_provider_Settings_System_setShowGTalkServiceStatus2(android.content.ContentResolver p0, boolean p1)
  {
    Log.i(API, "android.provider.Settings$System|setShowGTalkServiceStatus|void|android.content.ContentResolver;boolean");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.Settings.System.class, p0, p1);
  }

  @Redirect("android.provider.UserDictionary$Words->addWord")
  public static void redirection_of_android_provider_UserDictionary_Words_addWord4(android.content.Context p0, java.lang.String p1, int p2, int p3)
  {
    Log.i(API, "android.provider.UserDictionary$Words|addWord|void|android.content.Context;java.lang.String;int;int");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.UserDictionary.Words.class, p0, p1, p2, p3);
  }

  @Redirect("android.provider.UserDictionary$Words->addWord")
  public static void redirection_of_android_provider_UserDictionary_Words_addWord5(android.content.Context p0, java.lang.String p1, int p2, java.lang.String p3, java.util.Locale p4)
  {
    Log.i(API, "android.provider.UserDictionary$Words|addWord|void|android.content.Context;java.lang.String;int;java.lang.String;java.util.Locale");
    class $ {}
    Instrumentation.callStaticVoidMethod($.class, android.provider.UserDictionary.Words.class, p0, p1, p2, p3, p4);
  }

  @Redirect("android.provider.VoicemailContract$Status->buildSourceUri")
  public static android.net.Uri redirection_of_android_provider_VoicemailContract_Status_buildSourceUri1(java.lang.String p0)
  {
    Log.i(API, "android.provider.VoicemailContract$Status|buildSourceUri|android.net.Uri|java.lang.String");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.VoicemailContract.Status.class, p0);
  }

  @Redirect("android.provider.VoicemailContract$Voicemails->buildSourceUri")
  public static android.net.Uri redirection_of_android_provider_VoicemailContract_Voicemails_buildSourceUri1(java.lang.String p0)
  {
    Log.i(API, "android.provider.VoicemailContract$Voicemails|buildSourceUri|android.net.Uri|java.lang.String");
    class $ {}
    return (android.net.Uri) Instrumentation.callStaticObjectMethod($.class, android.provider.VoicemailContract.Voicemails.class, p0);
  }

  @Redirect("android.speech.SpeechRecognizer->cancel")
  public static void redirection_of_android_speech_SpeechRecognizer_cancel0(android.speech.SpeechRecognizer _this)
  {
    Log.i(API, "android.speech.SpeechRecognizer|cancel|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.speech.SpeechRecognizer->setRecognitionListener")
  public static void redirection_of_android_speech_SpeechRecognizer_setRecognitionListener1(android.speech.SpeechRecognizer _this, android.speech.RecognitionListener p0)
  {
    Log.i(API, "android.speech.SpeechRecognizer|setRecognitionListener|void|android.speech.RecognitionListener");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.speech.SpeechRecognizer->startListening")
  public static void redirection_of_android_speech_SpeechRecognizer_startListening1(android.speech.SpeechRecognizer _this, android.content.Intent p0)
  {
    Log.i(API, "android.speech.SpeechRecognizer|startListening|void|android.content.Intent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.speech.SpeechRecognizer->stopListening")
  public static void redirection_of_android_speech_SpeechRecognizer_stopListening0(android.speech.SpeechRecognizer _this)
  {
    Log.i(API, "android.speech.SpeechRecognizer|stopListening|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.telephony.SmsManager->sendDataMessage")
  public static void redirection_of_android_telephony_SmsManager_sendDataMessage6(android.telephony.SmsManager _this, java.lang.String p0, java.lang.String p1, short p2, byte[] p3, android.app.PendingIntent p4, android.app.PendingIntent p5)
  {
    Log.i(API, "android.telephony.SmsManager|sendDataMessage|void|java.lang.String;java.lang.String;short;byte[];android.app.PendingIntent;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.telephony.SmsManager->sendMultipartTextMessage")
  public static void redirection_of_android_telephony_SmsManager_sendMultipartTextMessage5(android.telephony.SmsManager _this, java.lang.String p0, java.lang.String p1, java.util.ArrayList p2, java.util.ArrayList p3, java.util.ArrayList p4)
  {
    Log.i(API, "android.telephony.SmsManager|sendMultipartTextMessage|void|java.lang.String;java.lang.String;java.util.ArrayList;java.util.ArrayList;java.util.ArrayList");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.telephony.SmsManager->sendTextMessage")
  public static void redirection_of_android_telephony_SmsManager_sendTextMessage5(android.telephony.SmsManager _this, java.lang.String p0, java.lang.String p1, java.lang.String p2, android.app.PendingIntent p3, android.app.PendingIntent p4)
  {
    Log.i(API, "android.telephony.SmsManager|sendTextMessage|void|java.lang.String;java.lang.String;java.lang.String;android.app.PendingIntent;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.telephony.TelephonyManager->getCellLocation")
  public static android.telephony.CellLocation redirection_of_android_telephony_TelephonyManager_getCellLocation0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getCellLocation|android.telephony.CellLocation|");
    class $ {}
    return (android.telephony.CellLocation) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->getDeviceId")
  public static java.lang.String redirection_of_android_telephony_TelephonyManager_getDeviceId0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getDeviceId|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->getDeviceSoftwareVersion")
  public static java.lang.String redirection_of_android_telephony_TelephonyManager_getDeviceSoftwareVersion0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getDeviceSoftwareVersion|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->getLine1Number")
  public static java.lang.String redirection_of_android_telephony_TelephonyManager_getLine1Number0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getLine1Number|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->getNeighboringCellInfo")
  public static java.util.List redirection_of_android_telephony_TelephonyManager_getNeighboringCellInfo0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getNeighboringCellInfo|java.util.List|");
    class $ {}
    return (java.util.List) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->getSimSerialNumber")
  public static java.lang.String redirection_of_android_telephony_TelephonyManager_getSimSerialNumber0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getSimSerialNumber|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->getSubscriberId")
  public static java.lang.String redirection_of_android_telephony_TelephonyManager_getSubscriberId0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getSubscriberId|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->getVoiceMailAlphaTag")
  public static java.lang.String redirection_of_android_telephony_TelephonyManager_getVoiceMailAlphaTag0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getVoiceMailAlphaTag|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->getVoiceMailNumber")
  public static java.lang.String redirection_of_android_telephony_TelephonyManager_getVoiceMailNumber0(android.telephony.TelephonyManager _this)
  {
    Log.i(API, "android.telephony.TelephonyManager|getVoiceMailNumber|java.lang.String|");
    class $ {}
    return (java.lang.String) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("android.telephony.TelephonyManager->listen")
  public static void redirection_of_android_telephony_TelephonyManager_listen2(android.telephony.TelephonyManager _this, android.telephony.PhoneStateListener p0, int p1)
  {
    Log.i(API, "android.telephony.TelephonyManager|listen|void|android.telephony.PhoneStateListener;int");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.telephony.gsm.SmsManager->sendDataMessage")
  public static void redirection_of_android_telephony_gsm_SmsManager_sendDataMessage6(android.telephony.gsm.SmsManager _this, java.lang.String p0, java.lang.String p1, short p2, byte[] p3, android.app.PendingIntent p4, android.app.PendingIntent p5)
  {
    Log.i(API, "android.telephony.gsm.SmsManager|sendDataMessage|void|java.lang.String;java.lang.String;short;byte[];android.app.PendingIntent;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4, p5);
  }

  @Redirect("android.telephony.gsm.SmsManager->sendMultipartTextMessage")
  public static void redirection_of_android_telephony_gsm_SmsManager_sendMultipartTextMessage5(android.telephony.gsm.SmsManager _this, java.lang.String p0, java.lang.String p1, java.util.ArrayList p2, java.util.ArrayList p3, java.util.ArrayList p4)
  {
    Log.i(API, "android.telephony.gsm.SmsManager|sendMultipartTextMessage|void|java.lang.String;java.lang.String;java.util.ArrayList;java.util.ArrayList;java.util.ArrayList");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.telephony.gsm.SmsManager->sendTextMessage")
  public static void redirection_of_android_telephony_gsm_SmsManager_sendTextMessage5(android.telephony.gsm.SmsManager _this, java.lang.String p0, java.lang.String p1, java.lang.String p2, android.app.PendingIntent p3, android.app.PendingIntent p4)
  {
    Log.i(API, "android.telephony.gsm.SmsManager|sendTextMessage|void|java.lang.String;java.lang.String;java.lang.String;android.app.PendingIntent;android.app.PendingIntent");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1, p2, p3, p4);
  }

  @Redirect("android.view.View->startDrag")
  public static boolean redirection_of_android_view_View_startDrag4(android.view.View _this, android.content.ClipData p0, android.view.View.DragShadowBuilder p1, java.lang.Object p2, int p3)
  {
    Log.i(API, "android.view.View|startDrag|boolean|android.content.ClipData;android.view.View.DragShadowBuilder;java.lang.Object;int");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1, p2, p3);
  }

  @Redirect("android.webkit.WebViewFragment->onCreateView")
  public static android.view.View redirection_of_android_webkit_WebViewFragment_onCreateView3(android.webkit.WebViewFragment _this, android.view.LayoutInflater p0, android.view.ViewGroup p1, android.os.Bundle p2)
  {
    Log.i(API, "android.webkit.WebViewFragment|onCreateView|android.view.View|android.view.LayoutInflater;android.view.ViewGroup;android.os.Bundle");
    class $ {}
    return (android.view.View) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("android.widget.QuickContactBadge->assignContactFromEmail")
  public static void redirection_of_android_widget_QuickContactBadge_assignContactFromEmail2(android.widget.QuickContactBadge _this, java.lang.String p0, boolean p1)
  {
    Log.i(API, "android.widget.QuickContactBadge|assignContactFromEmail|void|java.lang.String;boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.widget.QuickContactBadge->assignContactFromPhone")
  public static void redirection_of_android_widget_QuickContactBadge_assignContactFromPhone2(android.widget.QuickContactBadge _this, java.lang.String p0, boolean p1)
  {
    Log.i(API, "android.widget.QuickContactBadge|assignContactFromPhone|void|java.lang.String;boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.widget.QuickContactBadge->onClick")
  public static void redirection_of_android_widget_QuickContactBadge_onClick1(android.widget.QuickContactBadge _this, android.view.View p0)
  {
    Log.i(API, "android.widget.QuickContactBadge|onClick|void|android.view.View");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.widget.VideoView->onKeyDown")
  public static boolean redirection_of_android_widget_VideoView_onKeyDown2(android.widget.VideoView _this, int p0, android.view.KeyEvent p1)
  {
    Log.i(API, "android.widget.VideoView|onKeyDown|boolean|int;android.view.KeyEvent");
    class $ {}
    return (boolean) Instrumentation.callBooleanMethod($.class, _this, p0, p1);
  }

  @Redirect("android.widget.VideoView->pause")
  public static void redirection_of_android_widget_VideoView_pause0(android.widget.VideoView _this)
  {
    Log.i(API, "android.widget.VideoView|pause|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.widget.VideoView->release")
  public static void redirection_of_android_widget_VideoView_release1(android.widget.VideoView _this, boolean p0)
  {
    Log.i(API, "android.widget.VideoView|release|void|boolean");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.widget.VideoView->resume")
  public static void redirection_of_android_widget_VideoView_resume0(android.widget.VideoView _this)
  {
    Log.i(API, "android.widget.VideoView|resume|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.widget.VideoView->setVideoPath")
  public static void redirection_of_android_widget_VideoView_setVideoPath1(android.widget.VideoView _this, java.lang.String p0)
  {
    Log.i(API, "android.widget.VideoView|setVideoPath|void|java.lang.String");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.widget.VideoView->setVideoURI")
  public static void redirection_of_android_widget_VideoView_setVideoURI1(android.widget.VideoView _this, android.net.Uri p0)
  {
    Log.i(API, "android.widget.VideoView|setVideoURI|void|android.net.Uri");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("android.widget.VideoView->setVideoURI")
  public static void redirection_of_android_widget_VideoView_setVideoURI2(android.widget.VideoView _this, android.net.Uri p0, java.util.Map p1)
  {
    Log.i(API, "android.widget.VideoView|setVideoURI|void|android.net.Uri;java.util.Map");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0, p1);
  }

  @Redirect("android.widget.VideoView->start")
  public static void redirection_of_android_widget_VideoView_start0(android.widget.VideoView _this)
  {
    Log.i(API, "android.widget.VideoView|start|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.widget.VideoView->stopPlayback")
  public static void redirection_of_android_widget_VideoView_stopPlayback0(android.widget.VideoView _this)
  {
    Log.i(API, "android.widget.VideoView|stopPlayback|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("android.widget.VideoView->suspend")
  public static void redirection_of_android_widget_VideoView_suspend0(android.widget.VideoView _this)
  {
    Log.i(API, "android.widget.VideoView|suspend|void|");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this);
  }

  @Redirect("java.net.ServerSocket->bind")
  public static void redirection_of_java_net_ServerSocket_bind1(java.net.ServerSocket _this, java.net.SocketAddress p0)
  {
    Log.i(API, "java.net.ServerSocket|bind|void|java.net.SocketAddress");
    class $ {}
    Instrumentation.callVoidMethod($.class, _this, p0);
  }

  @Redirect("java.net.URL->getContent")
  public static java.lang.Object redirection_of_java_net_URL_getContent0(java.net.URL _this)
  {
    Log.i(API, "java.net.URL|getContent|java.lang.Object|");
    class $ {}
    return (java.lang.Object) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("java.net.URL->openConnection")
  public static java.net.URLConnection redirection_of_java_net_URL_openConnection0(java.net.URL _this)
  {
    Log.i(API, "java.net.URL|openConnection|java.net.URLConnection|");
    class $ {}
    return (java.net.URLConnection) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("java.net.URL->openStream")
  public static java.io.InputStream redirection_of_java_net_URL_openStream0(java.net.URL _this)
  {
    Log.i(API, "java.net.URL|openStream|java.io.InputStream|");
    class $ {}
    return (java.io.InputStream) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("java.net.URLConnection->getInputStream")
  public static java.io.InputStream redirection_of_java_net_URLConnection_getInputStream0(java.net.URLConnection _this)
  {
    Log.i(API, "java.net.URLConnection|getInputStream|java.io.InputStream|");
    class $ {}
    return (java.io.InputStream) Instrumentation.callObjectMethod($.class, _this);
  }

  @Redirect("org.apache.http.impl.client.AbstractHttpClient->execute")
  public static org.apache.http.HttpResponse redirection_of_org_apache_http_impl_client_AbstractHttpClient_execute1(org.apache.http.impl.client.AbstractHttpClient _this, org.apache.http.client.methods.HttpUriRequest p0)
  {
    Log.i(API, "org.apache.http.impl.client.AbstractHttpClient|execute|org.apache.http.HttpResponse|org.apache.http.client.methods.HttpUriRequest");
    class $ {}
    return (org.apache.http.HttpResponse) Instrumentation.callObjectMethod($.class, _this, p0);
  }

  @Redirect("org.apache.http.impl.client.AbstractHttpClient->execute")
  public static org.apache.http.HttpResponse redirection_of_org_apache_http_impl_client_AbstractHttpClient_execute2(org.apache.http.impl.client.AbstractHttpClient _this, org.apache.http.client.methods.HttpUriRequest p0, org.apache.http.protocol.HttpContext p1)
  {
    Log.i(API, "org.apache.http.impl.client.AbstractHttpClient|execute|org.apache.http.HttpResponse|org.apache.http.client.methods.HttpUriRequest;org.apache.http.protocol.HttpContext");
    class $ {}
    return (org.apache.http.HttpResponse) Instrumentation.callObjectMethod($.class, _this, p0, p1);
  }

  @Redirect("org.apache.http.impl.client.AbstractHttpClient->execute")
  public static org.apache.http.HttpResponse redirection_of_org_apache_http_impl_client_AbstractHttpClient_execute3(org.apache.http.impl.client.AbstractHttpClient _this, org.apache.http.HttpHost p0, org.apache.http.HttpRequest p1, org.apache.http.protocol.HttpContext p2)
  {
    Log.i(API, "org.apache.http.impl.client.AbstractHttpClient|execute|org.apache.http.HttpResponse|org.apache.http.HttpHost;org.apache.http.HttpRequest;org.apache.http.protocol.HttpContext");
    class $ {}
    return (org.apache.http.HttpResponse) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("org.apache.http.impl.client.AbstractHttpClient->execute")
  public static <T> T redirection_of_org_apache_http_impl_client_AbstractHttpClient_execute3(org.apache.http.impl.client.AbstractHttpClient _this, org.apache.http.client.methods.HttpUriRequest p0, org.apache.http.client.ResponseHandler<? extends T> p1, org.apache.http.protocol.HttpContext p2)
  {
    Log.i(API, "org.apache.http.impl.client.AbstractHttpClient|execute|<T> T|org.apache.http.client.methods.HttpUriRequest;org.apache.http.client.ResponseHandler<? extends T>;org.apache.http.protocol.HttpContext");
    class $ {}
    return (T) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2);
  }

  @Redirect("org.apache.http.impl.client.AbstractHttpClient->execute")
  public static <T> T redirection_of_org_apache_http_impl_client_AbstractHttpClient_execute4(org.apache.http.impl.client.AbstractHttpClient _this, org.apache.http.HttpHost p0, org.apache.http.HttpRequest p1, org.apache.http.client.ResponseHandler<? extends T> p2, org.apache.http.protocol.HttpContext p3)
  {
    Log.i(API, "org.apache.http.impl.client.AbstractHttpClient|execute|<T> T|org.apache.http.HttpHost;org.apache.http.HttpRequest;org.apache.http.client.ResponseHandler<? extends T>;org.apache.http.protocol.HttpContext");
    class $ {}
    return (T) Instrumentation.callObjectMethod($.class, _this, p0, p1, p2, p3);
  }


}