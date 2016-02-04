// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.exploration.device

import org.droidmate.exceptions.ForbiddenOperationError
import org.droidmate.logcat.IApiLogcatMessage

class MissingDeviceLogs implements IDeviceLogs, Serializable
{
  private static final long serialVersionUID = 1


  @Override
  List<IApiLogcatMessage> getApiLogs()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  List<IApiLogcatMessage> getApiLogsOrEmpty()
  {
    return []
  }

  @Override
  boolean getReadAnyApiLogsSuccessfully()
  {
    throw new ForbiddenOperationError()
  }

  @Override
  void setApiLogs(List<IApiLogcatMessage> apiLogs)
  {
    throw new ForbiddenOperationError()
  }

  @Override
  public String toString()
  {
    return "N/A (lack of ${IDeviceLogs.class.simpleName})"
  }

}
