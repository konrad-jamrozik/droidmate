// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
import org.droidmate.frontend.DroidmateFrontend;

public class Main
{
  public static void main(String[] args)
  {
    // KJA current work:
    /*
    java.io.IOException: No URL found for path appguard_apis.txt that starts with 'file' protocol. The found URLs:
jar:file:/C:/Users/Konrad%20Jamrozik/.m2/repository/org/droidmate/core/dev/core-dev.jar!/appguard_apis.txt
	@ org.droidmate.configuration.ConfigurationBuilder.bindDirsAndResources
     */
    DroidmateFrontend.main();
  }
}
