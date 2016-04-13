// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.init

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * <p>
 * This class contains values that are required by multiple projects being part of DroidMate
 * (i.e. projects located under the same dir as this one, e.g. "droidmate" and "apk_fixtures_src")
 * and/or both by their sources and gradle.build scripts.
 * </p>
 */
// Explanation of @SuppressWarnings("GroovyUnusedDeclaration"):
// this class is used in most of the projects located in the same dir as the project owning this class. These projects have to be
// loaded in separate IntelliJ instance.
@SuppressWarnings("GroovyUnusedDeclaration")
public class InitConstants
{

  /**
   * Example value of ANDROID_HOME on Windows: "c:\Program Files (x86)\Android\android-sdk"
   */
  public static final Path androidSdkDir = getEnvDir("ANDROID_HOME")
  static Path getEnvDir(String variable)
  {
    String value = System.getenv(variable)
    assert value?.size() > 0 : "System.getenv($variable) should be a string denoting a directory. It is instead: $value"

    Path dir = Paths.get(value)
    assert Files.isDirectory(dir) : "System.getenv($variable) should be a path pointing to an existing directory. " +
      "The faulty path: ${dir.toString()}"
    return dir
  }
  
  /**
   * Required by other constants.
   */
  public static final String android_platform_version = "19"

  /**
   * Required by droidmate project Configuration.
   */
  public static final String build_tools_version = "19.1.0"

  /**
   * Required by droidmate project gradle build scripts to obtain references to uiautomator.jar and android.jar.
   */

  public static final Path android_platform_dir = Paths.get(androidSdkDir.toString(), "platforms/android-$android_platform_version")

  public static final String apks_dir = "apks"

  /** This string makes an assumption that the apk inliner adds the "-inlined" suffix. */
  public static
  final String monitored_inlined_apk_fixture_name = "MonitoredApkFixture-debug-inlined.apk"

  public static final String monitor_generator_res_name_monitor_template = "monitorTemplate.txt"

  public static final Path monitor_generator_output_relative_path = Paths.get("temp/generated_Monitor.java")
  
  public static final Path apk_inliner_param_input_default      = Paths.get("input-apks")
  public static final Path apk_inliner_param_output_dir_default = Paths.get("output-apks")

  public static final String apk_inliner_param_input      = "-input"
  public static final String apk_inliner_param_output_dir = "-outputDir"

  public static final String AVD_dir_for_temp_files = "/data/local/tmp/"

  /**
   * <p>
   * Denotes name of directory containing apk fixtures for testing. The handle to this path is expected to be obtained
   * in following ways:
   *
   * </p><p>
   * From a {@code build.gradle} script:<br/>
   * {@code n ew File(sourceSets.test.resources.srcDirs[0], <this_var_reference>)}
   *
   * </p><p>
   * From compiled source code:<br/>
   * {@code Paths.get (ClassLoader.getSystemResource(<this_var_reference>).toUri())}
   *
   * </p>
   */
  public static final String apk_fixtures = "fixtures/apks"

  // @formatter:off
  /**
   * <p> Contains files to be pulled to resources dir of the projects depending on these resources. The pulling should happen
   * in gradle.build of the dependent projects as follows:
   *
   * <pre><code>
   * task pullRes(type: Copy) {
   *   File res = file(InitConstants.resourceName)
   *   assert res.file
   *
   *   from res
   *   into sourceSets.main.resources.srcDirs[0]
   * }
   *
   * processResources.dependsOn "pullRes"
   *
   * </code></pre>
   * </p>
   */
  // @formatter:on
  private static final String sharedResourcesDirName = "shared_resources" 
  public static final String appGuardApisListFileName = "appguard_apis.txt"

  /**
   * <p>
   *  Required by:
   *  - monitor-generator project to generate the monitor.java file.
   *  - core project to filter out APIs that are present in AppGuard (as opposed to the APIs present in PScout)
   *  </p>
   */
  public static final String appGuardApisListInInit = "$sharedResourcesDirName${File.separator}$appGuardApisListFileName"

  static {
    Path test = Paths.get(androidSdkDir.toString(), "platforms/android-$android_platform_version")
    test.toString()
    assert Files.isDirectory(androidSdkDir)
    assert Files.isDirectory(android_platform_dir)
  }

  //region Constants for testing
  public static final String test_temp_dir_name = "temp_dir_for_tests"
  //endregion


}
