// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

/**
 * Map of counts of how many times given elements appears in this [Iterable].
 */
val <T> Iterable<T>.frequencies: Map<T, Int> get() {
  val grouped: Map<T, List<T>> = this.groupBy { it }
  val frequencies: Map<T, Int> = grouped.mapValues { it.value.size }
  return frequencies
}