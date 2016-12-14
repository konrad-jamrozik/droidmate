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
package org.droidmate.android_sdk

import groovy.util.logging.Slf4j
import org.droidmate.logging.Markers

@Slf4j
 class ApkExplorationException extends ExplorationException
{

  private static final long serialVersionUID = 1

  final IApk apk
  final boolean stopFurtherApkExplorations

  ApkExplorationException(IApk apk, Throwable cause, boolean stopFurtherApkExplorations = false)
  {
    super(cause)
    this.apk = apk
    this.stopFurtherApkExplorations = stopFurtherApkExplorations

    assert apk != null
    assert cause != null

    if (this.shouldStopFurtherApkExplorations())
    {
      log.warn(Markers.appHealth, 
        "An ${this.class.simpleName} demanding stopping further apk explorations was just constructed!")
    }
  }

  boolean shouldStopFurtherApkExplorations()
  {
    if (this.stopFurtherApkExplorations)
      return true

    if (this.cause instanceof DeviceException)
      if ((this.cause as DeviceException).stopFurtherApkExplorations)
        return true

    return false
  }
}
