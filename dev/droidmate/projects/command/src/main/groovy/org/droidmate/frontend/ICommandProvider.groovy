// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.frontend

import org.droidmate.command.DroidmateCommand
import org.droidmate.configuration.Configuration

interface ICommandProvider
{
  DroidmateCommand provide(Configuration cfg)

}