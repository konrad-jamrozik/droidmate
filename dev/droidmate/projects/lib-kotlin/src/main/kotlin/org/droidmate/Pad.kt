// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate

class Pad(private val first: String, private val second: String) {

  val firstPadded: String
  val secondPadded: String

  operator fun component1() = firstPadded
  operator fun component2() = secondPadded

  init {
    val padSize = Math.max(first.length, second.length)
    firstPadded = first.padEnd(padSize)
    secondPadded = second.padEnd(padSize)
  }
}