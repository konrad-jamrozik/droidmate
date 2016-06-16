// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.monitor

import java.nio.file.Path

class MonitorSrcTemplate
{

  private final static String injectionPoint_ctorCalls      = "GENERATED_CODE_INJECTION_POINT:CTOR_REDIR_CALLS"
  private final static String injectionPoint_ctorTargets    = "GENERATED_CODE_INJECTION_POINT:CTOR_REDIR_TARGETS"
  private final static String injectionPoints_methodTargets = "GENERATED_CODE_INJECTION_POINT:METHOD_REDIR_TARGETS"

  private final String monitorSrcTemplate

  MonitorSrcTemplate(Path monitorSrcTemplatePath, AndroidAPI androidApi)
  {
    StringBuilder builder = new StringBuilder()

    boolean remove = false
    boolean uncomment = false

    //noinspection GroovyAssignabilityCheck // shouldn't be necessary, but IntelliJ is buggy.
    monitorSrcTemplatePath.eachLine {String line ->

      if (line.contains("// org.droidmate.monitor.MonitorSrcTemplate:REMOVE_LINES"))
      {
        remove = true
        uncomment = false
      } else if (line.contains("// org.droidmate.monitor.MonitorSrcTemplate:UNCOMMENT_LINES") ||
        (androidApi == AndroidAPI.API_19 && line.contains("// org.droidmate.monitor.MonitorSrcTemplate:API_19_UNCOMMENT_LINES")) ||
        (androidApi == AndroidAPI.API_23 && line.contains("// org.droidmate.monitor.MonitorSrcTemplate:API_23_UNCOMMENT_LINES"))
      )
      {
        remove = false
        uncomment = true
      } else if (line.contains("// org.droidmate.monitor.MonitorSrcTemplate:KEEP_LINES"))
      {
        remove = false
        uncomment = false
      } else
      {
        if (!remove && !uncomment)
          builder.append(line + "\n")
        else if (!remove && uncomment)
        {
          if (!line.contains("KJA")) // To-do comments Konrad Jamrozik frequently uses. Doesn't want to have them copied.
            builder.append(line.replace("// ", "") + "\n")
        } else
        {
          assert remove
          // Do nothing.
        }
      }
      return null
    }
    this.monitorSrcTemplate = builder.toString()
  }


  String injectGeneratedCode(String genCtorRedirCalls, String genCtorTargets, String genMethodsTargets)
  {
    return monitorSrcTemplate
      .readLines().collect {
      it.contains(injectionPoint_ctorCalls) ? genCtorRedirCalls
        : it.contains(injectionPoint_ctorTargets) ? genCtorTargets
        : it.contains(injectionPoints_methodTargets) ? genMethodsTargets : it
    }
    .join(System.lineSeparator())
  }
}
