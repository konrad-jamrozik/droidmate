// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device_simulation

class CallCounters implements ICallCounters
{
  Map<String, Map<String, Integer>> packageCounters = [:]

  @Override
  int increment(String packageName, String methodName)
  {
    packageCounters.putIfAbsent(packageName, [:])
    Map<String, Integer> methodCounters = packageCounters[packageName]


    methodCounters.putIfAbsent(methodName, 0)
    methodCounters[methodName] += 1
  }

  @Override
  int get(String packageName, String methodName)
  {
    assert packageCounters.containsKey(packageName)
    Map<String, Integer> methodCounters = packageCounters[packageName]
    assert methodCounters.containsKey(methodName)

    return methodCounters[methodName]
  }
}
