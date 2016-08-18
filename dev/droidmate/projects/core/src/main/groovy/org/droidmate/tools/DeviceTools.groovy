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

package org.droidmate.tools

import groovy.util.logging.Slf4j
import org.droidmate.android_sdk.AaptWrapper
import org.droidmate.android_sdk.AdbWrapper
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.configuration.Configuration
import org.droidmate.misc.SysCmdExecutor

@Slf4j
class DeviceTools implements IDeviceTools
{

  IAaptWrapper           aapt
  IAndroidDeviceDeployer deviceDeployer
  IApkDeployer           apkDeployer


  public DeviceTools(Configuration cfg = Configuration.default, Map substitutes = [:])
  {
    def sysCmdExecutor = new SysCmdExecutor()

    aapt = substitutes[IAaptWrapper] as IAaptWrapper ?: new AaptWrapper(cfg, sysCmdExecutor)

    def adbWrapper = substitutes[IAdbWrapper] as IAdbWrapper ?: new AdbWrapper(cfg, sysCmdExecutor)

    def deviceFactory = substitutes[IAndroidDeviceFactory] as IAndroidDeviceFactory ?:
      new AndroidDeviceFactory(cfg, adbWrapper)

    deviceDeployer = new AndroidDeviceDeployer(cfg, adbWrapper, deviceFactory)

    apkDeployer = new ApkDeployer(cfg)
  }
}
