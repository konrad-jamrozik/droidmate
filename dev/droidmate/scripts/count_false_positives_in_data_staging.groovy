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

/*
 * !!! WARNING !!!
 * THIS SCRIPT WORKS *WRONG* IF THE SUMMARY CONTAINS MORE THAN ONE USE CASE.
 */


File dataStagingDir = new File("../dataStaging")
List<File> summaries = dataStagingDir.listFiles().findAll { it.name.startsWith("summary")}

println "False positives for API calls only"
summaries.each {
  String pkgName = (it.name - "summary-" - ".txt")
  String onlyApis = it.readLines().takeWhile { !it.contains("[API call, event]") }.join("\n")
  println pkgName.padRight(34) + " " + onlyApis.count("None!")
}

println ""
println "False positives for [API call, event] pairs"
summaries.each {
  String pkgName = (it.name - "summary-" - ".txt")
  String apiAndEvents = it.readLines().dropWhile { !it.contains("[API call, event]") }.join("\n")
  println pkgName.padRight(34) + " " + apiAndEvents.count("None!")
}

println ""
println "False positives:"
println ""

summaries.each {
  String pkgName = (it.name - "summary-" - ".txt")
  println pkgName
  it.readLines().each { if (it.contains("None!")) println it }
  println ""
}
