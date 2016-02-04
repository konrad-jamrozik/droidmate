// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common;

public interface ISysCmdExecutor {

  String[] execute(String commandDescription, String... cmdLineParams) throws SysCmdExecutorException

  String[] executeWithoutTimeout(String commandDescription, String... cmdLineParams) throws SysCmdExecutorException

  String[] executeWithTimeout(String commandDescription, int timeout, String... cmdLineParams) throws SysCmdExecutorException

}
