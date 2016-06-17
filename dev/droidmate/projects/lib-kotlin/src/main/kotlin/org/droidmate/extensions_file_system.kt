// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate

import com.konradjamrozik.FileSystemsOperations
import com.konradjamrozik.createDirIfNotExists
import com.konradjamrozik.isDirectory
import org.codehaus.groovy.runtime.NioGroovyMethods
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.Files.newDirectoryStream
import java.nio.file.Path

val Path.text: String get() {
  return NioGroovyMethods.getText(this)
}

fun Path.deleteDir(): Boolean {
  return NioGroovyMethods.deleteDir(this)
}

fun Path.withExtension(extension: String): Path {
  require(!this.isDirectory)
  return this.resolveSibling(File(this.fileName.toString()).nameWithoutExtension + "." + extension)
}

val Path.fileNames: Iterable<String>
  get() {
    require(this.isDirectory)
    return newDirectoryStream(this).map { it.fileName.toString() }
  }

fun Path.withFiles(vararg files: Path): Path {
  files.asList().copyFilesToDirInDifferentFileSystem(this)
  return this
}

fun FileSystem.dir(dirName: String): Path {
  val dir = this.getPath(dirName)
  dir.createDirIfNotExists()
  return dir
}

fun List<Path>.copyFilesToDirInDifferentFileSystem(destDir: Path): Unit {
  FileSystemsOperations().copyFilesToDirInDifferentFileSystem(this, destDir)
}
