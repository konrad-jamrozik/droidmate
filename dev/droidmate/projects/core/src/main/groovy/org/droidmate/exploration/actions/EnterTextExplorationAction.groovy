// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
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
class EnterTextExplorationAction extends ExplorationAction
{

  private static final long serialVersionUID = 1

  String textToEnter

  Widget widget

  @Override
  String toString()
  {
    return super.toString()
  }

  @Override
  String toShortString()
  {
    String paddedTextToEnter = "${textToEnter}".padRight(22, ' ')
    "EnterTxt: ${paddedTextToEnter} / resId: ${widget.resourceId}"
  }

  @Override
  String toTabulatedString()
  {
    String paddedTextToEnter = "${textToEnter}".padRight(22, ' ')
    return "EnterTxt: ${paddedTextToEnter} / " + widget.toTabulatedString(false)
  }
}
