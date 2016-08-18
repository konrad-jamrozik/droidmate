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

package org.droidmate.apk_inliner

import groovy.util.logging.Slf4j
import joptsimple.OptionParser
import joptsimple.OptionSet
import joptsimple.ValueConverter
import org.droidmate.misc.BuildConstants

import java.nio.file.FileSystems
import java.nio.file.Path

import static org.droidmate.apk_inliner.PathValueConverter.pathIn

@Slf4j
public class ApkInlinerFrontend
{

  public static void main(String[] args)
  {
    try
    {
      def (Path inputPath, Path outputPath) = parseArgs(args)
      IApkInliner apkInliner = ApkInliner.build()
      apkInliner.inline(inputPath, outputPath)

    } catch (Exception e)
    {
      handleException(e)
    }

  }

  static List<Path> parseArgs(String[] args)
  {
    assert args?.length == 0 || args[0][0] == "-"

    OptionParser parser = new OptionParser()

    String inputParam = BuildConstants.apk_inliner_param_input.drop(1)
    String outputParam = BuildConstants.apk_inliner_param_output_dir.drop(1)

    ValueConverter<Path> path = pathIn(FileSystems.default)
    parser.accepts(inputParam).withOptionalArg().defaultsTo(path.convert(BuildConstants.apk_inliner_param_input_default)).withValuesConvertedBy(path)
    parser.accepts(outputParam).withRequiredArg().defaultsTo(path.convert(BuildConstants.apk_inliner_param_output_dir_default)).withValuesConvertedBy(path)

    OptionSet options = parser.parse(args)

    Path inputPath = (Path) options.valueOf(inputParam)
    Path outputPath = (Path) options.valueOf(outputParam)

    return [inputPath, outputPath]
  }

  static handleException = {Exception e ->
    log.error("Exception was thrown and propagated to the frontend.", e)
    System.exit(1)
  }

}