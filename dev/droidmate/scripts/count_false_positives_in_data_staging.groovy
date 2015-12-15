// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

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
