// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.deprecated_still_used

import org.droidmate.device.datatypes.GuiStateLegacy
import org.droidmate.exploration.actions.ResetAppExplorationAction
import org.droidmate.exploration.actions.TerminateExplorationAction
import org.droidmate.exploration.actions.WidgetExplorationAction

@Deprecated
// Based on http://stackoverflow.com/a/10616918/986533
class DeprecatedClassesDeserializer extends ObjectInputStream
{

  public static Map<String, Class> classNameMapping = initClassNameMapping()

  private static Map<String, Class> initClassNameMapping()
  {
    Map<String, Class> res = [
      "org.droidmate.exploration.datatypes.ExplorationOutput"           : ExplorationOutput,
      "org.droidmate.exploration.datatypes.ApkExplorationOutput"        : ApkExplorationOutput,
      "org.droidmate.exploration.datatypes.TimestampedExplorationAction": TimestampedExplorationAction,
      "org.droidmate.exploration.datatypes.ResetAppExplorationAction"   : ResetAppExplorationAction,
      "org.droidmate.exploration.datatypes.WidgetExplorationAction"     : WidgetExplorationAction,
      "org.droidmate.exploration.datatypes.TerminateExplorationAction"  : TerminateExplorationAction,
      "org.droidmate.exploration.datatypes.GuiState"                    : GuiStateLegacy,
    ]
    return Collections.unmodifiableMap(res)
  }

  public DeprecatedClassesDeserializer(InputStream ins) throws IOException
  {
    super(ins)
  }

  @Override
  protected ObjectStreamClass readClassDescriptor()
    throws IOException, ClassNotFoundException
  {
    ObjectStreamClass desc = super.readClassDescriptor()
    if (classNameMapping.containsKey(desc.name))
    {
      return ObjectStreamClass.lookup(classNameMapping[desc.name])
    }
    return desc
  }



}