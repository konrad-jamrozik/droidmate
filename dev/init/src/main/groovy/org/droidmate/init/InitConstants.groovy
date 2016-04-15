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
 * This class contains values that are required by multiple projects being part of DroidMate
 * (i.e. projects located under the same dir as this one, e.g. "droidmate" and "apk_fixtures_src")
 * and/or both by their sources and gradle.build scripts.
 * </p>
 */
// Explanation of @SuppressWarnings("GroovyUnusedDeclaration"):
// this class is used in most of the projects located in the same dir as the project owning this class. These projects have to be
// loaded in separate IntelliJ instance.
public class InitConstants
{
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
  }
}
