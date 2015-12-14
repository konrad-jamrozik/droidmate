// Copyright (c) 2013-2015 Saarland University
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.tools

import org.droidmate.android_sdk.Apk

import java.nio.file.Path

interface IApksProvider
{

  List<Apk> getApks(Path apksDir, int apksLimit, List<String> apksNames)
}