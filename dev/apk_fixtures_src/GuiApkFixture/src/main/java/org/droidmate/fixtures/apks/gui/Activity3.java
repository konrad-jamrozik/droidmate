// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.fixtures.apks.gui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class Activity3 extends Activity
{
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity3);
  }

  public void goToGooglePlayStore(View view) {
    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps")));
  }

  public void crash(View view)
  {
    throw new RuntimeException("Crash! Throwing new RuntimeException.");
  }
}