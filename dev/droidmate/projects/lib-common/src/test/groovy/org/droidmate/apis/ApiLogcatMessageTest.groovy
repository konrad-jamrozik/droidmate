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

package org.droidmate.apis

import org.droidmate.misc.DroidmateException
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class ApiLogcatMessageTest
{
  @Test
  public void "Parses simple logcat message payload"()
  {
    String msg = """\
TId: 1 objCls: android.webkit.WebView mthd: loadDataWithBaseURL retCls: void params: java.lang.String null java.lang.String \
<html><head><style_type="text/css">body_{_font-family:_"default_font";_} \
java.lang.String text/html \
java.lang.String UTF-8 \
java.lang.String null \
stacktrace: dalvik.system.VMStack.getThreadStackTrace(Native Method)->dalvik.system.NativeStart.main(Native Method)\
"""
    // Act
    ApiLogcatMessage.from(msg)
  }

  @Test
  public void "Parses no params"()
  {
    String msg = """\
TId: 1 objCls: android.webkit.WebView mthd: methd retCls: void params:  stacktrace: dalvik\
"""
    // Act
    ApiLogcatMessage.from(msg)
  }

  @Test
  public void "Parses param values being empty strings"()
  {
    String msg1 = """\
TId: 1 objCls: android.webkit.WebView mthd: loadDataWithBaseURL retCls: void \
params: \
java.lang.String  \
stacktrace: dalvik.system.VMStack.getThreadStackTrace(Native Method)->dalvik.system.NativeStart.main(Native Method)\
"""
    // Act 1
    ApiLogcatMessage.from(msg1)

    String msg2 = """\
TId: 1 objCls: android.webkit.WebView mthd: loadDataWithBaseURL retCls: void \
params: \
java.lang.String  java.lang.String  java.lang.String  \
stacktrace: dalvik.system.VMStack.getThreadStackTrace(Native Method)->dalvik.system.NativeStart.main(Native Method)\
"""
    // Act 2
    ApiLogcatMessage.from(msg2)
  }

  @Test
  public void "Throws exception on duplicate keyword"()
  {
    String msg = """\
TId: 1 objCls: android.webkit.WebView mthd: methd retCls: void params: java.lang.String retCls: void  <html> stacktrace: dalvik\
"""
    try
    {
      // Act
      ApiLogcatMessage.from(msg)
    } catch (DroidmateException ignored)
    {
      return
    }
    assert false : "No exception was thrown"
  }

  @Test
  public void "Parses simple logcat message payload without thread ID"()
  {
    String msg = """\
objCls: android.webkit.WebView mthd: loadDataWithBaseURL retCls: void params: java.lang.String null java.lang.String \
<html><head><style_type="text/css">body_{_font-family:_"default_font";_} \
java.lang.String text/html \
java.lang.String UTF-8 \
java.lang.String null \
stacktrace: dalvik.system.VMStack.getThreadStackTrace(Native Method)->dalvik.system.NativeStart.main(Native Method)\
"""
    // Act
    ApiLogcatMessage.from(msg)
  }

  @Test
  public void "Parses logcat message payload with params with newlines"()
  {
    String msg = """\
TId: 1 objCls: android.webkit.WebView mthd: loadDataWithBaseURL retCls: void params: java.lang.String null java.lang.String \
<html><head><style_type="text/css">body_{_font-family:_"default_font";_}

@font-face_{
font-family:_"default_font";
src:_url('file:///android_asset/fonts/Roboto-Light.ttf');
}

li_{
background:
url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAcAAAAHCAYAAADEUlfTAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADs\
IAAA7CARUoSoAAAAAZSURBVBhXY2SYeeY/Aw7ABKWxgkEmycAAAOQhAnJHUU4zAAAAAElFTkSuQmCC)
no-repeat_7px_7px_transparent;
list-style-type:_none;
margin:_0;
padding:_0px_0px_1px_18px;
vertical-align:_middle;
}</style></head><body><h3>Version_1.5.5</h3><ul><li>Automatic_backup/restore</li><li>Search_from_article_view</li>\
<li>Black_theme_fixes</li></ul><h3>Version_1.5.2</h3><ul><li>German_and_French_translations</li></ul><h3>Version_1.5.1</h3>\
<ul><li>Minor_bug_fixes</li></ul><h3>Version_1.5</h3><ul><li>Android_L_Support</li><li>Android_2.3+_Support</li>\
<li>Performance_improvements</li><li>High_resolution_'Nearby_mode'_photos</li></ul><h3>Version_1.4.4</h3>\
<ul><li>Performan_TRUNCATED_TO_1000_CHARS \
java.lang.String text/html java.lang.String UTF-8 java.lang.String null \
stacktrace: dalvik.system.VMStack.getThreadStackTrace(Native Method)->\
java.lang.Thread.getStackTrace(Thread.java:579)->\
org.droidmate.monitor.Monitor.getStackTrace(Monitor.java:428)->\
org.droidmate.monitor.Monitor.redir_android_webkit_WebView_loadDataWithBaseURL5(Monitor.java:1901)->\
java.lang.reflect.Method.invokeNative(Native Method)->\
java.lang.reflect.Method.invoke(Method.java:515)->\
android.webkit.WebView.loadDataWithBaseURL(WebView.java)->\
de.a.a.a.a.a(Unknown Source)->\
de.a.a.a.a.b(Unknown Source)->\
animaonline.android.wikiexplorer.activities.MainActivity.onCreate(Unknown Source)->\
android.app.Activity.performCreate(Activity.java:5231)->\
android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1087)->\
android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2148)->\
android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2233)->\
android.app.ActivityThread.access\$800(ActivityThread.java:135)->\
android.app.ActivityThread\$H.handleMessage(ActivityThread.java:1196)->\
android.os.Handler.dispatchMessage(Handler.java:102)->\
android.os.Looper.loop(Looper.java:136)->\
android.app.ActivityThread.main(ActivityThread.java:5001)->\
java.lang.reflect.Method.invokeNative(Native Method)->\
java.lang.reflect.Method.invoke(Method.java:515)->\
com.android.internal.os.ZygoteInit\$MethodAndArgsCaller.run(ZygoteInit.java:785)->\
com.android.internal.os.ZygoteInit.main(ZygoteInit.java:601)->\
dalvik.system.NativeStart.main(Native Method)\
"""
    // Act
    ApiLogcatMessage.from(msg)
  }

  /**
   * Bug: Parsing message throws StackOverflowError
   * https://hg.st.cs.uni-saarland.de/issues/992
   */
  @Test
  void "Has no bug #992"()
  {
    String msg = """\
TId: 1 objCls: android.webkit.WebView mthd: loadDataWithBaseURL retCls: void \
params: \
java.lang.String http://googleads.g.doubleclick.net:80/mads/gma?preqs=0&session_id=16105713006406524141&u_sd=1.3312501&seq_num=1&u_w=600&msid=com.rhmsoft.fm&js=afma-sdk-a-v6.4.1&ms=jFkmuJb6MadPkpSMXdm_1kMB6nknHkWEA9KfxwhbPtLWyUMBlCe4g0T9HsMljPXWLpFlit0NwRQ7Zq7E_byCD0fCNKO6ox9i75EfX761yQzkXdEKHsz1n8L9bEjR7S3BI6zwu4hIxFmM2OwVzrJVrURr0htiqCrh6Y1uR7KTDRoTSGIGjGk3r4VteL0j5qC6XBwJUf1JlbfxXvnQ7EPqDVd6VsCPCmWjYbfSTtasGIvLUjqne3icX-7I8gM9BiDxWWVOlQMQbpPdxgnCIEXX9ZqaGJMEnwAnwvu3EFkfizBi-_MlVUB-6eTBRYOylh6MU7pSYjDXI-kv01Ua3xXx9Q&mv=80430000.com.android.vending&bas_off=0&format=468x60_as&oar=0&net=wi&app_name=11700047.android.com.rhmsoft.fm&hl=en&gnt=0&u_h=905&bas_on=0&ptime=0&u_audio=1&aims=s&adinfo=AI4ME0tAfmigiGBY9LmWrAabrqiUBRQd4CVQkmxAMGrXE663ZH8dwhHk20JqIdWhdZW_4I9CJbX5IMt9MZbRhkBP4p2GeavstM7i-DVWrM8RZwlWhMa3p1841nKooudN7BWneJ_S_ItTYIOPEdEAMd33ybZLFcgiMA0NfWVjNV_bAeDDv_c1QWkMfaeMoDnWyfbLCgC3YI4wXKYggoPfLAuKl7DA1rga-nuKju3UmALpSI6T-pD_A_Q7euurAJG7wR2hi3gXOKMEb6BClXzZXXgnZUUcYdZ7tT5yWaggqJFu86kQ-7wMmLMoHDMx_fZ_TRUNCATED_TO_1000_CHARS \
java.lang.String <!doctype_html><html><head><meta_charset=\"UTF-8\"><link_href=\"http://fonts.googleapis.com/css?family=Open+Sans:300,400|Slabo+27px:400&lang=en\"_rel=\"stylesheet\"_type=\"text/css\"><style>a{color:#ffffff}body,table,div,ul,li{margin:0;padding:0}body{font-family:\"Slabo_27px\",\"Times_New_Roman\",serif;}</style><script>(function(){window.ss=function(){};}).call(this);(function(){var_c=this;var_e=String.prototype.trim?function(a){return_a.trim()}:function(a){return_a.replace(/^[\\s\\xa0]+|[\\s\\xa0]+\$/g,\"\")},f=function(a,b){return_a<b?-1:a>b?1:0};var_g;a:{var_p=c.navigator;if(p){var_q=p.userAgent;if(q){g=q;break_a}}g=\"\"}var_r=function(a){return-1!=g.indexOf(a)};var_t=r(\"Opera\")||r(\"OPR\"),u=r(\"Trident\")||r(\"MSIE\"),v=r(\"Edge\"),w=r(\"Gecko\")&&!(-1!=g.toLowerCase().indexOf(\"webkit\")&&!r(\"Edge\"))&&!(r(\"Trident\")||r(\"MSIE\"))&&!r(\"Edge\"),x=-1!=g.toLowerCase().indexOf(\"webkit\")&&!r(\"Edge\"),y=function(){var_a=g;if(w)return/rv\\:([^\\);]+)(\\)|;)/.exec(a);if(v)return/Edge\\/([\\d\\.]+)/.exec(a);if(u)return/\\b(?:MSIE|rv_TRUNCATED_TO_1000_CHARS \
stacktrace: dalvik"""
    // Act
    ApiLogcatMessage.from(msg)
  }
}
