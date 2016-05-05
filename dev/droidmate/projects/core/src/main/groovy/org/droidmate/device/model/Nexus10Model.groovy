// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.model

import java.awt.*

/**
 * Provides device specific methods for a Google Nexus 10 device using Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: ConcreteProduct <br/>
 *
 * @author Nataniel Borges Jr.
 */
class Nexus10Model extends NexusModel
{
  @Override
  Dimension getDeviceDisplayDimensionsForTesting()
  {
    return new Dimension(1600, 2485)
  }
}
