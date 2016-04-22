// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.apk_inliner

import com.konradjamrozik.ResourcePath
import groovy.util.logging.Slf4j
import joptsimple.OptionParser
import joptsimple.OptionSet
import org.droidmate.buildsrc.BuildKt
import org.droidmate.common.BuildConstants
import org.droidmate.common.Dex
import org.droidmate.common.Jar
import org.droidmate.common.SysCmdExecutor

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
      def (Path inputPath, Path outputPath) =
      parseArgs(args)
      IApkInliner apkInliner = buildApkInliner()
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
    parser.accepts(inputParam).withOptionalArg().defaultsTo(BuildKt.apk_inliner_param_input_default).withValuesConvertedBy(pathIn(FileSystems.default))
    parser.accepts(outputParam).withRequiredArg().defaultsTo(BuildKt.apk_inliner_param_output_dir_default).withValuesConvertedBy(pathIn(FileSystems.default))

    OptionSet options = parser.parse(args)


    Path inputPath = (Path) options.valueOf(inputParam)
    Path outputPath = (Path) options.valueOf(outputParam)

    return [inputPath, outputPath]
  }

  private static IApkInliner buildApkInliner()
  {
    def sysCmdExecutor = new SysCmdExecutor()

    Jar inlinerJar = new Jar(new ResourcePath("appguard-inliner.jar").path)
    Dex appGuardLoader = new Dex(new ResourcePath("appguard-loader.dex").path)
    String monitorClassName = "org.droidmate.monitor_generator.generated.Monitor"
    String pathToMonitorApkOnAndroidDevice = BuildKt.AVD_dir_for_temp_files + "monitor.apk"

    def jarsignerPath = BuildKt.jarsigner
    def debugKeystorePath = new ResourcePath("debug.keystore").path

    return new ApkInliner(
      sysCmdExecutor,
      new JarsignerWrapper(
        sysCmdExecutor,
        jarsignerPath,
        debugKeystorePath
      ),
      inlinerJar,
      appGuardLoader,
      monitorClassName,
      pathToMonitorApkOnAndroidDevice)
  }


  static handleException = {Exception e ->
    log.error("Exception was thrown and propagated to the frontend.", e)
    System.exit(1)
  }

}