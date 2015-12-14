Author: Konrad Jamrozik
Last updated: June 20, 2015

This gradle-based project compiles into .jar file containing constants that have to be shared between projects in "..", 
both their sources and their build scripts. Note that most of these projects are gradle- and IntelliJ-based.

For example, the constants defined in "apk fixtures" project have to be asserted-against in "droidmate" project. Thus, both
"apk fixtures" and "droidmate" projects have to know these constants, hence they are placed in this project.
	
For the projects build.gradle script to see constants from this project, following has to be added to it:
	
  buildscript {
    dependencies {
      classpath files("$rootDir/../init/build/libs/init.jar")
    }
  }

For the projects source code to see constants from this project, following has to be added to the build.gradle script:

  dependencies { 
    compile files("$rootDir/../init/build/libs/init.jar") 
  }
	
This project also contains resources that are pulled by two or more projects during the :processResources task. The resources
are located in ./shared_resoruces

=== NOT WORKING SOLUTIONS ===

> Below follows a simplier solution that I tried but it didn't work out due to limitations also described here:

A simplier alternative solution is to just add another sourceSet resource directory in build.gradle, e.g. like that:

  sourceSets.main.resources.srcDir(project(":projects").file("resources"))
  
where, as one can see from the snippet above, the directory with shared resources would live under the "projects" project.

This solution unfortunately doesn't work, because when applied, IntelliJ fails a Gradle project refresh, complaining on the dir being
outside the project content root. Manual addition of the content root in IntelliJ doesn't help, as it doesn't get recognized :(