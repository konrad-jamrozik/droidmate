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

  // KJA current work
  // for first (action,result) pair, get all the unique logs and pair them with the action index (which is 1)
  // for each new (action, result) pair, check if it has any new unique logs. If yes, add them to the final result.
  return emptyMap()
}