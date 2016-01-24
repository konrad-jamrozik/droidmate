// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.github.konrad_jamrozik.FileSystemsOperations
import com.google.common.collect.Table
import org.codehaus.groovy.runtime.NioGroovyMethods
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

fun Path.text(): String {
  return NioGroovyMethods.getText(this)
}

fun Path.copyDirContentsRecursivelyToDirInDifferentFileSystem(destDir: Path): Unit {
  FileSystemsOperations().copyDirContentsRecursivelyToDirInDifferentFileSystem(this, destDir)
}

fun <R, C, V> Table<R, C, V>.writeOut(file: Path) {

  val DEBUGlog: Logger = LoggerFactory.getLogger("Table")
  DEBUGlog.info("DEBUG Writing out to $file")

  val headerRowString = this.columnKeySet().joinToString(separator = "\t")

  val dataRowsStrings: List<String> = this.rowMap().map {
    val rowValues = it.value.values
    rowValues.joinToString(separator = "\t")
  }

  val tableString = headerRowString + "\n" + dataRowsStrings.joinToString(separator = "\n")

  Files.write(file, tableString.toByteArray())
}

fun <T, TItem> Iterable<T>.uniqueCountByTime(extractTime: (T) -> Long, extractItems: (T) -> Iterable<TItem>): Map<Long, Int> {

  val timedItems: Map<Long, Iterable<TItem>> = this.toMap { Pair(extractTime(it), extractItems(it)) }
  // KJA current work
  return  timedItems.mapValues { it.value.count() }
}
