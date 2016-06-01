// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.report

import com.konradjamrozik.createDirIfNotExists
import com.konradjamrozik.isDirectory

fun java.nio.file.Path.text(): String {
  return org.codehaus.groovy.runtime.NioGroovyMethods.getText(this)
}

fun java.nio.file.Path.deleteDir(): Boolean {
  return org.codehaus.groovy.runtime.NioGroovyMethods.deleteDir(this)
}

val java.nio.file.Path.fileNames: Iterable<String>
  get() {
    require(this.isDirectory)
    return java.nio.file.Files.newDirectoryStream(this).map { it.fileName.toString() }
  }

fun java.nio.file.Path.withFiles(vararg files: java.nio.file.Path): java.nio.file.Path {
  files.asList().copyFilesToDirInDifferentFileSystem(this)
  return this
}

fun java.nio.file.FileSystem.dir(dirName: String): java.nio.file.Path {
  val dir = this.getPath(dirName)
  dir.createDirIfNotExists()
  return dir
}

fun List<java.nio.file.Path>.copyFilesToDirInDifferentFileSystem(destDir: java.nio.file.Path): Unit {
  com.konradjamrozik.FileSystemsOperations().copyFilesToDirInDifferentFileSystem(this, destDir)
}