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