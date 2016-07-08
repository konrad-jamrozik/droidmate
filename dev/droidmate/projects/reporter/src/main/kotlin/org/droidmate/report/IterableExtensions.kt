// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

// KJA 1 move entire file to utilities project + tests
/**
 * @return 
 *   Map of counts of how many times given elements appears in this receiver [Iterable].
 */
val <T> Iterable<T>.frequencies: Map<T, Int> get() {
  val grouped: Map<T, List<T>> = this.groupBy { it }
  val frequencies: Map<T, Int> = grouped.mapValues { it.value.size }
  return frequencies
}

/**
 * @return 
 *   A map from unique items to the index of first element in the receiver [Iterable] from which given unique item was
 *   obtained. The indexing starts at 0.
 * 
 * @param extractItems 
 *   A function that is applied to each element of the receiver iterable, converting it to an iterable of items.
 * 
 * @param extractUniqueString 
 *   A function used to remove duplicates from all the items extracted from receiver iterable using [extractItems].
 * 
 */
fun <T, TItem> Iterable<T>.uniqueItemsWithFirstOccurrenceIndex(
  extractItems: (T) -> Iterable<TItem>,
  extractUniqueString: (TItem) -> String
): Map<TItem, Int> {

  return this.foldIndexed(

    mapOf<String, Pair<TItem, Int>>(), { index, accumulatedMap, elem ->

    val uniqueStringsToItemsWithIndexes: Map<String, Pair<TItem, Int>> =
      extractItems(elem).associate {
        Pair(
          extractUniqueString(it),
          Pair(it, index + 1)
        )
      }

    val newUniqueStrings = uniqueStringsToItemsWithIndexes.keys.subtract(accumulatedMap.keys)

    val uniqueStringsToNewItemsWithIndexes = uniqueStringsToItemsWithIndexes.filterKeys { it in newUniqueStrings }

    accumulatedMap.plus(uniqueStringsToNewItemsWithIndexes)
    
  }).map { it.value }.toMap()
}