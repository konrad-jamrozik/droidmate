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
import android.widget.TextView;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class Activity2 extends Activity
{
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity2);
  }

  public void goToGooglePlayStore(View view) {
    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps")));
  }

  public void crash(View view)
  {
    throw new RuntimeException("Crash! Throwing new RuntimeException.");
  }

  public void goToMainActivity(View view) {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }

  private SecureRandom random = new SecureRandom();

  public void randomizeText(View view)
  {
    TextView tv = (TextView) findViewById(R.id.textView_random);
    // Based on: http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
    tv.setText(new BigInteger(32, random).toString(32));
  }

  public void goToMainActivity3(View view)
  {
    Intent intent = new Intent(this, Activity3.class);
    startActivity(intent);
  }
}