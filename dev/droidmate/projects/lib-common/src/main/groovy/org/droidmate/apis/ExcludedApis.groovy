// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.apis

import groovy.transform.Immutable

/**
 * <p>
 * A set of API method call signatures to be excluded when computing results for the BoxMate papers, e.g. for CCS 2015.
 *
 * </p><p>
 * The input used to compute these results is the set of logcat logs of API method calls obtained during exploration.
 *
 * </p><p>
 * The excluded method calls have been manually added by me to PScout API mapping to monitor intents. With their exclusion, the
 * method calls based on Intents are no longer considered, except these of ContentResolver-s.
 *
 * </p><p>
 * For details on which methods have been manually added by me, see:<br/>
 * <pre>droidmate\dev\droidmate\projects\monitor-generator\src\main\resources\jellybean_publishedapimapping_modified.txt</pre>
 *
 * </p><p>
 * Note that not all API method signatures that should be excluded have been excluded, as I incorrectly forgot to exclude following method calls:
 *
 * <pre><code>
 * android.content.ContextWrapper.removeStickyBroadcast(android.content.Intent)
 * android.content.ContextWrapper.sendStickyBroadcast(android.content.Intent)
 * android.content.ContextWrapper.sendStickyOrderedBroadcast(android.content.Intent, android.content.BroadcastReceiver, android.os.Handler, int, java.lang.String, android.os.Bundle)</code></pre>
 *
 * </p><p>
 * However, I am keeping the list as it is, to maintain compatibility with the claims made in the paper on the API counts and
 * lists. Related: {@code org.droidmate.monitor.ApiListsStats}
 *
 * </p>
 */
@Immutable
class ExcludedApis
{

 boolean contains(String methodName)
 {
  return [
    "startActiv",
    "startIntentSender",
    "startService",
    "stopService",
    "bindService",
    "unbindService",
    "sendBroadcast",
    "sendOrderedBroadcast"]
    .any {methodName.startsWith(it)}
 }
}
