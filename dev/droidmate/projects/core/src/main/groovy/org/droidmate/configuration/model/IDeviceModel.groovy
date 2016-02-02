// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.configuration.model

import org.droidmate.device.datatypes.GuiState

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
   * Determine if the {@link GuiState} is in the device home screen.
   *
   * @param guiState GUI State that will be evaluated
   *
   * @return If the device is currently in it's home screen
   */
  boolean isHomeScreen(GuiState guiState)

  /**
   * Get the name of the top level package on the device's home screen
   *
   * @return If the device is currently in it's home screen
   */
  String getPackageAndroidLauncher()

  /**
   * Determine if the {@link GuiState} possesses a stopped dialog box.
   *
   * @param guiState GUI State that will be evaluated
   *
   * @return If the device currently possess a stopped dialog box
   */
  boolean isAppHasStoppedDialogBox(GuiState guiState)

  /**
   * Determine if the {@link GuiState} has to complete and action using a dialog box
   *
   * @param guiState GUI State that will be evaluated
   *
   * @return If the device has to complete an action using a dialog box
   */
  boolean isCompleteActionUsingDialogBox(GuiState guiState)

  /**
   * Determine if the {@link GuiState} has a "Select a home app" dialog option
   *
   * @param guiState GUI State that will be evaluated
   *
   * @return If the device has a "Select a home app" dialog option
   */
  boolean isSelectAHomeAppDialogBox(GuiState guiState)
}
