// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.tools

import org.droidmate.android_sdk.Apk
import org.droidmate.android_sdk.IAaptWrapper
import org.droidmate.common.BuildConstants
import org.droidmate.configuration.Configuration

class SingleApkFixture
{
  @SuppressWarnings("GrFinalVariableAccess")
  @Delegate
  final Apk apk

  SingleApkFixture(IAaptWrapper aapt, Configuration cfg)
  {
    assert aapt != null
    assert cfg != null
    assert cfg.useApkFixturesDir
    assert cfg.apksNames == [BuildConstants.monitored_inlined_apk_fixture_api19_name]

    ApksProvider apksProvider = new ApksProvider(aapt)

    List<Apk> apks = apksProvider.getApks(cfg.apksDirPath, cfg.apksLimit, cfg.apksNames)
    assert apks.size() == 1

    this.apk = apks.first()
  }


}
