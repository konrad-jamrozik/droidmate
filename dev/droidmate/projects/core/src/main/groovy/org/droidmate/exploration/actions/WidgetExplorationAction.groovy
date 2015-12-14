// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.exploration.actions

import groovy.transform.Canonical
import groovy.transform.TupleConstructor
import org.droidmate.common.exploration.datatypes.Widget

@Canonical
@TupleConstructor(includeSuperProperties = true)
class WidgetExplorationAction extends ExplorationAction
{

  private static final long serialVersionUID = 1

  Widget  widget
  boolean longClick
  int     delay = 0

  @Override
  String toString()
  {
    return super.toString()
  }

  @Override
  String toShortString()
  {
    "LC? ${longClick ? 1 : 0} " + widget.toShortString()
  }

  @Override
  String toTabulatedString()
  {
    "LC? ${longClick ? 1 : 0} " + widget.toTabulatedString()
  }
}



