// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.report

import com.konradjamrozik.isDirectory
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import java.nio.file.Files
import java.nio.file.Path

class ApkViewsFile(val data: IApkExplorationOutput2, dir: Path) : DataFile(buildFilePath(data, dir)) {

  init {
    require(dir.isDirectory)
  }
  
  override fun writeOut() {
    Files.write(file, apkViewsString.toByteArray())
  }

  val apkViewsString: String by lazy { data.apkViewsString }

  private val IApkExplorationOutput2.apkViewsString: String
    get() {
      return "Unique actionable widget\n" +
        this.uniqueActionableWidgets.map { it.uniqueString }.joinToString(separator = "\n") +
        "\n====================\n" +
        "Unique clicked widgets\n" +
        this.uniqueClickedWidgets.map { it.uniqueString }.joinToString(separator = "\n")
    }
  
  companion object {
    
    val fileNameSuffix = "_views.txt"
    
    private fun buildFilePath(data: IApkExplorationOutput2, dir: Path): Path {
      require(dir.isDirectory)
      return dir.resolve("${data.apkFileNameWithUnderscoresForDots}$fileNameSuffix")
    }


  }
}

