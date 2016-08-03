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


