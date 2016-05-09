// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.fixtures.apks.monitored;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class Activity4 extends ActivityWithHelper
{
  public static final String TAG = Activity4.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity4);
  }

  public void callAPIAndRelaunchActivity4(View view)
  {
    callAPI_TelephonyManager_getCellLocation(TAG);

    Intent intent = new Intent(this, Activity4.class);
    Log.i(TAG, "Re-launching activity 4");
    startActivity(intent);
  }
}
