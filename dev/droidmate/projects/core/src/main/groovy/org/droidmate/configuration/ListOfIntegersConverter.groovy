// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.configuration

import com.beust.jcommander.IStringConverter
import org.droidmate.common.DroidmateException

public class ListOfIntegersConverter implements IStringConverter<List<Integer>>
{
  @Override
  public List<Integer> convert(String arg)
  {
    assert arg != null

    List<Integer> convertedArg
    try
    {
      convertedArg = arg.tokenize("[,]").collect {it as Integer}
    } catch (Exception e)
    {
      throw new DroidmateException("The string '${arg}' is not a valid value for parameter expecting a list of integers. " +
        "See command line parameters help for examples of correct format.", e);
    }

    assert convertedArg != null
    return convertedArg
  }
}
