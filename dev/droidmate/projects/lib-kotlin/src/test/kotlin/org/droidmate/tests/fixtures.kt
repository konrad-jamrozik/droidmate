// DroidMate, an automated execution generator for Android apps.
// Copyright (C) 2012-2016 Konrad Jamrozik
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
// email: jamrozik@st.cs.uni-saarland.de
// web: www.droidmate.org
package org.droidmate.tests

import com.konradjamrozik.Resource
import org.droidmate.extractedPath
import org.droidmate.extractedText
import java.nio.file.Path

/**
 * The metadata to the run used for this fixture is located in directory located in the same dir as this fixture.
 */
val fixture_monitoredSer2: Path by lazy { Resource("fixtures/serialized_results/2016 Aug 19 2158 org.droidmate.fixtures.apks.monitored.ser2").extractedPath }
val fixture_aaptBadgingDump: String by lazy { Resource("fixtures/f_aaptBadgingDump.txt").extractedText }

// tsa == TestSubjectApp

// @formatter:off
val windowDump_nexus7_2013_home_empty            : String by lazy { Resource("fixtures/window_dumps/nexus7_2013_home_empty.xml").extractedText }
val windowDump_nexus7_2013_home_removed_systemui : String by lazy { Resource("fixtures/window_dumps/nexus7_2013_home_empty_removed_systemui.xml").extractedText }
val windowDump_nexus7_avd_raw            : String by lazy { Resource("fixtures/window_dumps/nexus7_2013_avd_api_23_home_raw.xml").extractedText }
val windowDump_nexus7_avd_noframe        : String by lazy { Resource("fixtures/window_dumps/nexus7_2013_avd_api_23_home_dm_no_frame.xml").extractedText }
// Older fixtures, migrated from :projects:core
val windowDump_app_stopped_dialogbox     : String by lazy { Resource("fixtures/window_dumps/f_app_stopped_dialogbox_nexus7vert.xml").extractedText }
val windowDump_app_stopped_OK_disabled   : String by lazy { Resource("fixtures/window_dumps/f_app_stopped_OK_disabled.xml").extractedText }
val windowDump_nexus7_home_screen        : String by lazy { Resource("fixtures/window_dumps/f_nexus7_home_screen.xml").extractedText }
val windowDump_tsa_mainAct               : String by lazy { Resource("fixtures/window_dumps/f_tsa_mainAct_4Jan14.xml").extractedText }
val windowDump_tsa_emptyAct              : String by lazy { Resource("fixtures/window_dumps/f_tsa_empty_activity.xml").extractedText }
val windowDump_tsa_1button               : String by lazy { Resource("fixtures/window_dumps/f_tsa_1button.xml").extractedText }
val windowDump_chrome_offline            : String by lazy { Resource("fixtures/window_dumps/f_chrome_offline_nexus7vert.xml").extractedText }
val windowDump_complActUsing_dialogbox   : String by lazy { Resource("fixtures/window_dumps/f_complete_action_using.xml").extractedText }
// @formatter:on

