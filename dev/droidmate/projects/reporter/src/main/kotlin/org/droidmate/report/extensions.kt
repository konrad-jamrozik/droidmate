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
import java.nio.file.Files
import java.nio.file.Path

fun Path.text(): String {
  return NioGroovyMethods.getText(this)
}

fun Path.copyDirContentsRecursivelyToDirInDifferentFileSystem(destDir: Path): Unit {
  FileSystemsOperations().copyDirContentsRecursivelyToDirInDifferentFileSystem(this, destDir)
}

fun <R, C, V> Table<R, C, V>.writeOut(file: Path) {

  val headerRowString = this.columnKeySet().joinToString(separator = "\t")

  val dataRowsStrings: List<String> = this.rowMap().map {
    val rowValues = it.value.values
    rowValues.joinToString(separator = "\t")
  }

  val tableString = headerRowString + "\n" + dataRowsStrings.joinToString(separator = "\n")

  Files.write(file, tableString.toByteArray())
}