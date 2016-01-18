// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.android_sdk

import org.droidmate.common.DroidmateException

import java.nio.file.Path

class AaptWrapperStub implements IAaptWrapper
{

  final List<IApk> apks

  AaptWrapperStub(List<IApk> apks)
  {
    this.apks = apks
  }

  @Override
  String getPackageName(Path apk) throws DroidmateException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String getLaunchableActivityName(Path apk) throws DroidmateException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String getLaunchableActivityComponentName(Path apk) throws DroidmateException
  {
    assert false: "Not yet implemented!"
  }

  @Override
  String getApplicationLabel(Path apk) throws DroidmateException
  {
    assert false: "Not yet implemented!"
  }


  @Override
  List<String> getMetadata(Path path) throws DroidmateException
  {
    def apk = apks.findSingle {it.absolutePath == path.toAbsolutePath().toString()}
    return [apk.packageName, apk.launchableActivityName, apk.launchableActivityComponentName, apk.applicationLabel]
  }
}
