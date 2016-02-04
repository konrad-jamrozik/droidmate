// Copyright (c) 2012-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.fixtures.apks.monitored;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class Activity1 extends ActivityWithHelper
{
  public static final String TAG = Activity1.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity1);
  }

  public void callAPIURLOpenConnection(View view)
  {
    callAPI_URL_openConnection(TAG);
  }

  // Based on: http://developer.android.com/training/basics/firstapp/starting-activity.html
  public void launchActivity2(View view)
  {
    Intent intent = new Intent(this, Activity2.class);
    Log.i(TAG, "Launching activity 2");
    startActivity(intent);
  }

  public void launchHome(View view)
  {
    launchHome(TAG);
  }

  // For interactive analysis how intents look like.
//    Intent i = new Intent(Intent.ACTION_CALL, Uri.fromParts("sms","123456","Maciek"));
//    Object o = i;
//    if (o instanceof Intent)
//    {
//      Intent x= (Intent) o;
//      Log.i("XXX", "STRI: "+ x.toString());
//      Log.i("XXX", "URI0: "+ x.toUri(0));
//      Log.i("XXX", "URI1: "+ x.toUri(Intent.URI_INTENT_SCHEME));
//    }
}
