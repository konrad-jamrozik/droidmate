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
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity
{
    private static int activityIdCounter = 0;
    private Integer activityId = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (activityId == null)
            activityId = activityIdCounter++;

        Log.i(LogTags.Lifecycle, "onCreate() ID:" + activityId);

        setContentView(R.layout.activity_main);

    /*
    Creating button by adding to XML layout based on:
    http://www.connorgarvey.com/blog/?p=93
     */
        Button button = (Button) findViewById(R.id.button_crash);
    /*
    Adding click listener based on:
    http://developer.android.com/guide/topics/ui/controls/button.html#ClickListener
     */
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                throw new RuntimeException("Crash! Throwing new RuntimeException.");
            }
        });
    }

    public void goToGooglePlayStore(View view)
    {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps")));
    }

    // Based on: http://developer.android.com/training/basics/firstapp/starting-activity.html
    public void goToActivity2(View view)
    {
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }

    public void secApi(View view)
    {
        try {
            final URL url = new URL("http://www.google.com");
            URLConnection conn = url.openConnection();
            Log.i(LogTags.TSA, "URL.openConnection() returned: " + conn);

        } catch (Exception e) {
            Log.wtf(LogTags.TSA, e);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.i(LogTags.Lifecycle, "onStart() ID:" + activityId);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.i(LogTags.Lifecycle, "onRestart() ID:" + activityId);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i(LogTags.Lifecycle, "onResume() ID:" + activityId);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.i(LogTags.Lifecycle, "onStop() ID:" + activityId);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.i(LogTags.Lifecycle, "onDestroy() ID:" + activityId);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i(LogTags.Lifecycle, "onPause() ID:" + activityId);
    }
}
