// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common.logging

import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * <p>
 * Markers used by slf4j+logback loggers to log specific information.
 * </p><p>
 *
 * See {@code logback.groovy} for details on logging.
 *
 * </p>
 */
public class Markers
{

  /**
   * Marker for logging detailed exception information.
   */
  public static final Marker exceptions = MarkerFactory.getMarker("MARKER_EXCEPTIONS")

  /**
   * Marker for logging command line executions. Such logs can be copy-pasted to console and executed for
   * quick ad-hoc debugging.
   */
  public static final Marker osCmd = MarkerFactory.getMarker("MARKER_OS_CMD")
  
   /**
   * Marker for DroidMateGUI exploration state results
   */
  public static final Marker gui = MarkerFactory.getMarker("MARKER_GUI")
  

  /**
   * Denotes logs that output data about DroidMate run: input files, configuration, run time + run timestamps, etc.
   */
  public static final Marker runData = MarkerFactory.getMarker("MARKER_RUN_DATA")

  public static List<Marker> getAllMarkers()
  {
    return Markers.fields.collect {it as Marker}
  }
}