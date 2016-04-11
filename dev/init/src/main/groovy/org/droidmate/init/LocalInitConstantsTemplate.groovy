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
 * !!! WARNING !!!<br/>
 * <br/>
 * PLEASE DO NOT modify this file! Copy it and rename the copy instead!<br/>
 * PLEASE DO NOT delete this file! You will erase it from the repository on next push, so other developers won't see it!<br/>
 * PLEASE DO NOT ignore this file from Git repository!
 *
 * </p><p>
 * This class contains values that are dependent on the local dev machine setup and thus have to be setup manually upon
 * first deployment of DroidMate on a new machine.
 * </p><p>
 * To customize this file to match your local setup, please do the following:<br/>
 * 1. make a copy of this class in the same directory, with the "Template" suffix removed from the copy's class name.<br/>
 * 2. In the class you just created, change the values of all the public static final constants to match your local setup.<br/>
 * 3. To confirm the changes you made work, run "gradlew clean build" from the root directory of the current project. If the
 * build completes successfully, you are good to go (you will obtain init.jar in ./build/libs)
 *
 * </p>
 *
 */
public class LocalInitConstantsTemplate
{
  
  // KJA current work
  // KJA add ANDROID_HOME, JAVA6_HOME, JAVA7_HOME and JAVA8_HOME to travis-CI
  
  /**
   * The "init" project of DroidMate. The project has to be built from gradle cmd line to generate init.jar, used by other
   * DroidMate projects. See README file in this project dir for details.
   */
  // KNOWN BUG backward slashes do not work here (\\).
  // KJA handle cases in which slash is given at the end
  // !!! WARNING !!! DO NOT end the path with slash!
  // KJA this probably can be completely removed
  // KJA looks like can be made private
  public static final String init_project_dir_path = "c:/my/local/repos/chair/droidmate/dev/init"

  /**
   * The "main" project of DroidMate.
   */
  // KJA move out from local constants
  // KJA this is mostly used to auto-gen local.properties files with android sdk path. Instead, these paths should be taken from ANDROID_HOME.
  public static final String droidmate_project_dir_path = (init_project_dir_path - "/init") + "/droidmate"

  /**
   * Example value of JAVA8_HOME on Windows: "C:\Program Files\Java\jdk1.8.0_77"
   */
  static String exe = osIsWindows() ? ".exe" : ""
  public static final Path jarsignerPath = resolveFile(getEnvDir("JAVA8_HOME"), "bin/jarsigner$exe")

  static String osIsWindows() {
    if ((System.properties['os.name'] as String).toLowerCase().contains('windows'))
      1 else 0
  }
  /**
   * Example value of JAVA7_HOME on Windows: "C:\Program Files\Java\jdk1.7.0_71"
   */
  public static final Path java7rtJar = resolveFile(getEnvDir("JAVA7_HOME"), "jre/lib/rt.jar")

  /**
   * Example value of JAVA6_HOME on Windows: "C:\Program Files\Java\jdk1.6.0_45"
   */
  public static final Path java6rtJar = resolveFile(getEnvDir("JAVA6_HOME"), "jre/lib/rt.jar")

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
  
  static Path resolveDir(Path path, String subdir)
  {
    assert Files.isDirectory(path)
    assert subdir?.size() > 0
    Path resolved = path.resolve(subdir)
    assert Files.isDirectory(resolved)
    return resolved
  }

  static Path resolveFile(Path path, String filepath)
  {
    assert Files.isDirectory(path)
    assert filepath?.size() > 0
    Path resolved = path.resolve(filepath)
    assert Files.isRegularFile(resolved)
    return resolved
  }
}
