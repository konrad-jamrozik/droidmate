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

  public static final Path android_platform_dir = Paths.get(LocalInitConstants.android_sdk_dir.toString(), "platforms/android-$android_platform_version")

  public static final String apks_dir = "apks"

  public static final File absolute_apks_dir = new File(LocalInitConstants.droidmate_project_dir_path, apks_dir)

  /**
   * The project of DroidMate that builds apk fixtures used in tests.
   */
  // KJA looks like can be made private
  public static final File apk_fixtures_src_project_dir = new File((LocalInitConstants.droidmate_project_dir_path - "/dev/droidmate") + "/dev/apk_fixtures_src")

  /** This string makes an assumption that the apk inliner adds the "-inlined" suffix. */
  public static
  final String monitored_inlined_apk_fixture_name = "MonitoredApkFixture-debug-inlined.apk"

  public static
  final Path monitor_generator_proj_dir = Paths.get(LocalInitConstants.droidmate_project_dir_path, "projects/monitor-generator")

  public static
  final Path monitor_generator_apk_scaffolding_dir = Paths.get(monitor_generator_proj_dir.toString(), "monitor-apk-scaffolding")

  public static final String monitor_generator_res_name_monitor_template = "monitorTemplate.txt"

  public static final Path monitor_generator_generated_monitor =
    Paths.get(monitor_generator_apk_scaffolding_dir.toString(), "src/org/droidmate/monitor_generator/generated/Monitor.java")

  public static final File monitor_generator_apk_scaffolding_local_properties_file =
    new File(monitor_generator_apk_scaffolding_dir.toString(), "local.properties")

  public static final Path uiautomator_daemon_local_properties_file =
    Paths.get(LocalInitConstants.droidmate_project_dir_path.toString(), "/projects/uiautomator-daemon/local.properties")

  // KJA can be made private
  public static final Path apk_fixtures_src_local_properties_file =
    Paths.get(apk_fixtures_src_project_dir.toString(), "local.properties")

  public static final Path apk_inliner_proj_dir                 = Paths.get(LocalInitConstants.droidmate_project_dir_path, "projects/apk-inliner")
  // KJA these 2 are tricky. Have to be made compatible with file() and with default cmd line arg value.
  public static final Path apk_inliner_param_input_default      = apk_inliner_proj_dir.resolve("input-apks")
  public static final Path apk_inliner_param_output_dir_default = apk_inliner_proj_dir.resolve("output-apks")

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
  // KJA can be made relative, so that it works with 'droidmate' project's file() command.
  public static final Path sharedResourcesDir = Paths.get(LocalInitConstants.init_project_dir_path, "shared_resources")

  /**
   * <p>
   *  Required by:
   *  - monitor-generator project to generate the monitor.java file.
   *  - core project to filter out APIs that are present in AppGuard (as opposed to the APIs present in PScout)
   *  </p>
   */
  public static final Path appGuardApisList = sharedResourcesDir.resolve("appguard_apis.txt")


  static {
    Path test = Paths.get(LocalInitConstants.android_sdk_dir.toString(), "platforms/android-$android_platform_version")
    test.toString()
    assert new File(LocalInitConstants.droidmate_project_dir_path).directory
    assert Files.isDirectory(LocalInitConstants.android_sdk_dir)
    assert Files.isDirectory(android_platform_dir)

    assert absolute_apks_dir.directory

    assert apk_fixtures_src_project_dir.directory

    assert Files.isDirectory(monitor_generator_proj_dir)
    assert Files.isDirectory(monitor_generator_apk_scaffolding_dir)
    assert Files.notExists(monitor_generator_generated_monitor) || Files.isWritable(monitor_generator_generated_monitor)

    assert Files.isDirectory(apk_inliner_proj_dir)

    if (!monitor_generator_apk_scaffolding_local_properties_file.exists())
    {
      assert monitor_generator_apk_scaffolding_local_properties_file.createNewFile()
      monitor_generator_apk_scaffolding_local_properties_file.write("sdk.dir=" + LocalInitConstants.android_sdk_dir.path.replace("\\", "\\\\"))
    }

    if (Files.notExists(uiautomator_daemon_local_properties_file))
    {
      assert Files.createFile(uiautomator_daemon_local_properties_file)
      uiautomator_daemon_local_properties_file.write("sdk.dir=" + LocalInitConstants.android_sdk_dir.toString().replace("\\", "\\\\"))
    }

    if (Files.notExists(apk_fixtures_src_local_properties_file))
    {
      assert Files.createFile(apk_fixtures_src_local_properties_file)
      apk_fixtures_src_local_properties_file.write("sdk.dir=" + LocalInitConstants.android_sdk_dir.toString().replace("\\", "\\\\"))
    }

    assert Files.isDirectory(sharedResourcesDir)
    assert Files.isRegularFile(appGuardApisList)
  }

  //region Constants for testing
  public static final String test_temp_dir_name = "temp_dir_for_tests"
  public static final File absolute_path_test_temp_dir = new File(LocalInitConstants.droidmate_project_dir_path, test_temp_dir_name)
  //endregion


}
