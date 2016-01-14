// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.AbstractMatcherFilter
import ch.qos.logback.core.spi.FilterReply
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * <p>
 * Logback filter for matching logged message marker against given marker.
 *
 * </p><p>
 * Based on <a href="http://stackoverflow.com/a/8759210/986533">stack overflow answer</a>.
 *
 * </p>
 */
public class MarkerFilter extends AbstractMatcherFilter<ILoggingEvent>
{
  private Marker markerToMatch = null;

  @Override
  public void start()
  {
    if (this.markerToMatch != null)
      super.start();
    else
      addError("Marker to match doesn't exist yet.");
  }

  @Override
  public FilterReply decide(ILoggingEvent event)
  {
    Marker marker = event.getMarker();
    if (!isStarted())
      return FilterReply.NEUTRAL;
    if (marker == null)
      return onMismatch;
    if (markerToMatch.contains(marker))
      return onMatch;
    return onMismatch;
  }

  // SuppressWarnings reason: used in logback.groovy, but not recognized by IntelliJ IDEA.
  @SuppressWarnings("GroovyUnusedDeclaration")
  public void setMarker(String markerStr)
  {
    if (null != markerStr)
      markerToMatch = MarkerFactory.getMarker(markerStr);
  }
}