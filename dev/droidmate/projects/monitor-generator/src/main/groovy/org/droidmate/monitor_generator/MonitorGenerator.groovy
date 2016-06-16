// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor_generator

import groovy.util.logging.Slf4j
import org.droidmate.apis.ApiMethodSignature

/* KJA2 (hook/scaffolding) use instead projects/monitor-apk-scaffolding
KJA2 (hook/libmonitor) create "lib-monitor" project with Java-7-compatible classes used in Monitor.java. Move ApiLogcatMessage to it.
*/

@Slf4j
class MonitorGenerator implements IMonitorGenerator
{

  private final IRedirectionsGenerator redirectionsGenerator
  private final MonitorSrcTemplate     monitorSrcTemplate

  MonitorGenerator(
    IRedirectionsGenerator redirectionsGenerator,
    MonitorSrcTemplate monitorSrcTemplate)
  {
    this.redirectionsGenerator = redirectionsGenerator
    this.monitorSrcTemplate = monitorSrcTemplate
  }

  @Override
  String generate(List<ApiMethodSignature> signatures)
  {
    def (String genCtorCalls, String genCtorTargets) = redirectionsGenerator.generateCtorCallsAndTargets(signatures)
    String genMethodTargets = redirectionsGenerator.generateMethodTargets(signatures)

    return monitorSrcTemplate.injectGeneratedCode(genCtorCalls, genCtorTargets, genMethodTargets)
  }


}


