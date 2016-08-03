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
package org.droidmate.device.model

import java.awt.*

/**
 * Provides an interface with device specific methods using Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: Product
 *
 * @author Nataniel Borges Jr.
 */
public interface IDeviceModel extends Serializable
{

  /**
   * Get the name of the top level package on the device's home screen
   */
  String getAndroidLauncherPackageName()


  /**
   * Get the size of the device screen. Currently used only for testing purposes.
   */
  Dimension getDeviceDisplayDimensionsForTesting()
}
