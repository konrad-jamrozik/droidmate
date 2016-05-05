// Copyright (c) 2013-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.fixtures.apks.instrumented;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LogcatActivity extends Activity
{
    public static final String LOGCAT_ACTIVITY_TAG      = "LogcatActivity";
    public static final String LOGCAT_ACTIVITY_JUNK_TAG = "LogcatActivity_junk";

    public static final String label_button_2_logcat_msgs = "Output 2 logcat messages";
    public static final String label_button_3_other_msgs  = "Output 3 other logcat messages";
    public static final String label_button_junk_msgs     = "Output junk logcat messages";

    public static final String button_2_logcat_msgs_msg1 = "message 1 from button 1";
    public static final String button_2_logcat_msgs_msg2 = "message 2 from button 1";
    public static final String button_3_other_msgs_msg1  = "message 1 from button 2";
    public static final String button_3_other_msgs_msg2  = "message 2 from button 2";
    public static final String button_3_other_msgs_msg3  = "message 3 from button 2";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(org.droidmate.fixtures.apks.instrumented.R.layout.activity_logcat);

        Button button;
        button = (Button)findViewById(org.droidmate.fixtures.apks.instrumented.R.id.button_2_logback_msgs);
        button.setText(label_button_2_logcat_msgs);

        button = (Button)findViewById(org.droidmate.fixtures.apks.instrumented.R.id.button_3_other_msgs);
        button.setText(label_button_3_other_msgs);

        button = (Button)findViewById(org.droidmate.fixtures.apks.instrumented.R.id.button_junk_msgs);
        button.setText(label_button_junk_msgs);
    }

    public void button1Clicked(View view)
    {
        Log.i(LOGCAT_ACTIVITY_TAG, button_2_logcat_msgs_msg1);
        Log.i(LOGCAT_ACTIVITY_TAG, button_2_logcat_msgs_msg2);
    }

    public void button2Clicked(View view)
    {
        Log.i(LOGCAT_ACTIVITY_TAG, button_3_other_msgs_msg1);
        Log.i(LOGCAT_ACTIVITY_TAG, button_3_other_msgs_msg2);
        Log.i(LOGCAT_ACTIVITY_TAG, button_3_other_msgs_msg3);

    }

    public void junkButtonClicked(View view)
    {
        Log.i(LOGCAT_ACTIVITY_JUNK_TAG, "garbage");
        Log.i(LOGCAT_ACTIVITY_JUNK_TAG, "trash");
        Log.i(LOGCAT_ACTIVITY_JUNK_TAG, "junk");
        Log.i(LOGCAT_ACTIVITY_JUNK_TAG, "rubbish");

    }
}
