// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.init
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
// Explanation of @SuppressWarnings("GroovyUnusedDeclaration"):
// the file is intended to serve as a template, see the comment above.
@SuppressWarnings("GroovyUnusedDeclaration")
public class LocalInitConstantsTemplate
{
  /**
   * The "init" project of DroidMate. The project has to be built from gradle cmd line to generate init.jar, used by other
   * DroidMate projects. See README file in this project dir for details.
   */
  public static final String init_project_dir_path = "c:/my/local/repos/chair/droidmate/dev/init"

  /**
   * The "main" project of DroidMate.
   */
  public static final String droidmate_project_dir_path = (init_project_dir_path - "/init") + "/droidmate"

  /**
   * Java sources of modules not deployed to any device use jdk 8. For example, apk-inliner.
   */
  public static final String jdk8_path = "C:/Program Files/Java/jdk1.8.0_25"

  /**
   * Java sources deployed to Android devices have to be build with jdk 7 at most.
   */
  public static final String jdk7_path = "C:/Program Files/Java/jdk1.7.0_71"

  /**
   * Apk fixtures are being built with legacy Android ant scripts, which support jdk 6 at most.
   */
  public static final String jdk6_path = "C:/Program Files/Java/jdk1.6.0_45"

  public static final File android_sdk_dir = new File("c:/Program Files (x86)/Android/android-sdk/")
}
