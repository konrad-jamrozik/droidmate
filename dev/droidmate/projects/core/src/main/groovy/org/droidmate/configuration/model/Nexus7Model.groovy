// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.configuration.model

import java.awt.Dimension

/**
 * Provides device specific methods for a Google Nexus 7 device using Factory Method Pattern
 * {@link http://www.dofactory.com/net/factory-method-design-pattern}. <br/>
 * Role: ConcreteProduct <br/>
 *
 * @author Nataniel Borges Jr.
 */
class Nexus7Model extends NexusModel
{
  @Override
  Dimension getDeviceVerticalDimensionsForTesting()
  {
    return new Dimension(800, 1205)
  }
}
