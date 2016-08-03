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

import org.droidmate.deleteDir
import org.droidmate.exploration.data_aggregators.ExplorationOutput2
import org.droidmate.exploration.data_aggregators.IApkExplorationOutput2
import org.droidmate.storage.Storage2
import java.nio.file.Files
import java.nio.file.Path

class OutputDir(val dir: Path) {

  val explorationOutput2: List<IApkExplorationOutput2> by lazy {
    ExplorationOutput2.from(Storage2(dir))
  }

  val notEmptyExplorationOutput2: List<IApkExplorationOutput2> by lazy {
    check(explorationOutput2.isNotEmpty(), { "Check failed: explorationOutput2.isNotEmpty()" })
    explorationOutput2
  }

  fun clearContents()
  {
    if (Files.exists(dir))
    {
      Files.list(dir).forEach {
        if (Files.isDirectory(it))
          it.deleteDir()
        else
          Files.delete(it)
      }
    }
  }
}