// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

fun <T, TItem> Iterable<T>.uniqueItemsWithFirstOccurrenceIndex(
  extractItems: (T) -> Iterable<TItem>,
  extractUniqueString: (TItem) -> String
): Map<TItem, Int> {

  return this.foldIndexed(

    mapOf<String, Pair<TItem, Int>>(), { index, map, elem ->

    val uniqueStringsToItemsWithIndexes: Map<String, Pair<TItem, Int>> =
      extractItems(elem).associate {
        Pair(
          extractUniqueString(it),
          Pair(it, index + 1)
        )
      }

    val newUniqueStrings = uniqueStringsToItemsWithIndexes.keys.subtract(map.keys)

    val uniqueStringsToNewItemsWithIndexes = uniqueStringsToItemsWithIndexes.filterKeys { it in newUniqueStrings }

    map.plus(uniqueStringsToNewItemsWithIndexes)
    
  }).map { it.value }.toMap()
}

/**
 * Map of counts of how many times given elements appears in this [Iterable].
 */
val <T> Iterable<T>.frequencies: Map<T, Int> get() {
  val grouped: Map<T, List<T>> = this.groupBy { it }
  val frequencies: Map<T, Int> = grouped.mapValues { it.value.size }
  return frequencies
}

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
