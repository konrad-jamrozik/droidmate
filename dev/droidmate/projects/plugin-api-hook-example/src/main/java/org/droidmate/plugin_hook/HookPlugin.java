// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.plugin_hook;

import org.jetbrains.annotations.NotNull;

public class HookPlugin implements IHookPlugin
{

  @Override
  public void before(@NotNull String objectClass)
  {
    // KJA current work
    System.out.println("objectClass from hook: "+ objectClass);
  }
}
