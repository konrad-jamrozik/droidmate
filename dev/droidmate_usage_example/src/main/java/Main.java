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
    // org.droidmate.common.DroidmateException: Directory apks doesn't exist or is not a directory.
    // KJA current work:
    // logback is broken. Prints messages twice, shows thread id, shows debug. Hm. Fix logback runtime deployment.
    DroidmateFrontend.main();
  }
}
