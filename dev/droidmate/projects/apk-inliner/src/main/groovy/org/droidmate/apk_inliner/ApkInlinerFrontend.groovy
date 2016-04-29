// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.apk_inliner

import groovy.util.logging.Slf4j
import joptsimple.OptionParser
import joptsimple.OptionSet
import joptsimple.ValueConverter
import org.droidmate.common.BuildConstants

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