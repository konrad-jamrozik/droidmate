
  Copyright (c) 2012-2015 Saarland University
  All right reserved.

  Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de

  This file is part of the "DroidMate" project.

  www.droidmate.org

   Date of last full review: 7 Dec 2015

## About this file ##

This file explains common tasks required in usage of DroidMate. Before reading this file, please first read the `README.md` located in repo's root.

**Important note** Repo's root is denoted as `.` (dot) in this file.

## How to run DroidMate? ##

Currently running DroidMate is supported only from IntelliJ IDEA. IntelliJ `droidmate` project has a set of run configs whose name starts with `Explore apks`. They serve as a documentation by example.

DroidMate `main()` is also being run by tests in `DroidmateFrontendTest`.

## How to prepare apks for DroidMate? ##

DroidMate cannot run on normal apks, they first have to be `inlined`. To inline a set of apks, do the following:

* Copy them to `dev/droidmate/projects/apk-inliner/input-apks`
* Run the task
`./dev/droidmate/gradlew :projects:core:prepareInlinedApks`
or  `./dev/droidmate/gradlew :p:c:pIA` for short.
The apks will be placed in `dev/droidmate/apks/inlined`
* Run DroidMate with cmd option of `-apksDir=apks/inlined` to use these apks.


## How to obtain apks? ##

* Manually from Google Play Store, e.g. by using http://apk-dl.com/
* After DroidMate is successfuly built, apk fixtures can be found in `./dev/droidmate/projects/core/src/test/resources/fixtures/apks`. Their sources are available in the `./dev/apk-fixtures-src` project.