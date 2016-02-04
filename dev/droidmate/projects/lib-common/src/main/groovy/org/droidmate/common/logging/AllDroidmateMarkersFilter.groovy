// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.common.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.AbstractMatcherFilter
import ch.qos.logback.core.spi.FilterReply
import org.slf4j.Marker

/**
 * <p>
 * Logback filter for matching logged message marker against any of the markers defined in {@link Markers}.
 *
 * </p><p>
 * Based on <a href="http://stackoverflow.com/a/8759210/986533">this stack overflow answer</a>.
 *
 * </p>
 */
public class AllDroidmateMarkersFilter extends AbstractMatcherFilter<ILoggingEvent>
{

  @Override
  public FilterReply decide(ILoggingEvent event)
  {
    Marker marker = event.getMarker()
    if (!isStarted())
      return FilterReply.NEUTRAL
    if (marker == null)
      return onMismatch
    if (Markers.getAllMarkers().any { it.contains(marker) })
      return onMatch
    return onMismatch
  }
}
