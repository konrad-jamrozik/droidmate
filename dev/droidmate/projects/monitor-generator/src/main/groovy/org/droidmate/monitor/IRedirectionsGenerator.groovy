// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor

import org.droidmate.apis.ApiMethodSignature

interface IRedirectionsGenerator
{

  List<String> generateCtorCallsAndTargets(List<ApiMethodSignature> signatures)

  String generateMethodTargets(List<ApiMethodSignature> signatures)
}

