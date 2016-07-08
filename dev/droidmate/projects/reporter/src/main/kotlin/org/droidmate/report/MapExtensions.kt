// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

val <K, V> Map<K, V>.transpose: Map<V, Set<K>> get() {
  val pairs: List<Pair<V, K>> = this.map { Pair(it.value, it.key) }
  return pairs.fold(
    initial = mutableMapOf(),
    operation = { acc: MutableMap<V, MutableSet<K>>, pair: Pair<V, K> ->
      if (!acc.containsKey(pair.first))
        acc.put(pair.first, mutableSetOf())
      acc[pair.first]!!.add(pair.second)
      acc
    }
  )
}