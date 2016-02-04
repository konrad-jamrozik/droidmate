// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.logcat

import org.droidmate.apis.IApi

/**
 * <p>
 * A log of monitored Android API call in form of a logcat message.
 * </p><p>
 *
 * The contract is the message was sent by a monitored apk to Android device's logcat and was read from logcat into an instance
 * implementing this interface.
 * </p>
 */
public interface IApiLogcatMessage extends ITimeFormattedLogcatMessage, IApi
{
}


