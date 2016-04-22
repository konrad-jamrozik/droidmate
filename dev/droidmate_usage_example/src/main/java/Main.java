// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
import org.droidmate.frontend.DroidmateFrontend;

public class Main
{
  public static void main(String[] args)
  {
    // KJA current work: cannot find BuildKt from buildsrc. The problem seems to be that
    // C:\Users\Konrad Jamrozik\.m2\repository\org\droidmate\core\dev\core-dev.jar
    // doesn't contain BuildKt, while
    // C:\Users\Konrad Jamrozik\.m2\repository\org\droidmate\droidmate\dev\droidmate-dev.zip
    // contains it.
    //
    // This is a fundamental problem of this kind that the projects should not directly depend
    // on buildsrc. Only gradle scripts should do it. I will have to proxy the references through
    // resources or something. Ungh!
    //
    // KJA one idea I have is to copy all constants to resource dir of lib-common project and then read them into 
    // a SharedConstants class, which then will be used by all the projects, instead of BuildKt. 
    
    DroidmateFrontend.main("aa");
  }
}
