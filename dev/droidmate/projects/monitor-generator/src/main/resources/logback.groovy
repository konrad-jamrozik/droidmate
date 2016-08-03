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

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.TRACE
import static ch.qos.logback.core.spi.FilterReply.DENY
import static ch.qos.logback.core.spi.FilterReply.NEUTRAL

final String layoutPattern_bare = "%msg%rEx%n"
final String layoutPattern_levLogEx = "%-5level %-40logger{40} - %msg%rEx%n"

// =======================================================
// Console appenders
// =======================================================

appender("stdout_appender", ConsoleAppender) {
  target = "System.out"
  filter(LevelFilter) {level = ERROR; onMatch = DENY; onMismatch = NEUTRAL}
  encoder(PatternLayoutEncoder) {pattern = layoutPattern_bare}
}

appender("stderr_appender", ConsoleAppender) {
  target = "System.err"
  filter(ThresholdFilter) {level = ERROR}
  encoder(PatternLayoutEncoder) {pattern = layoutPattern_bare}
}

root(TRACE, [
  "stdout_appender",
  "stderr_appender"
])

