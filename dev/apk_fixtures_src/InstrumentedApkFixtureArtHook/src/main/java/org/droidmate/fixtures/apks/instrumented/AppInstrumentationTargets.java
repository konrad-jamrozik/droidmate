// Copyright (c) 2013-2016 Saarland University Software Engineering Chair.
// All right reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.fixtures.apks.instrumented;

public class AppInstrumentationTargets
{
  public void publicVoidMethod()
  {
  }

  void nonpublicVoidMethod()
  {
  }

  public ReturnObject advancedMethodCaller()
  {
    return advancedMethod(11, "paramStr", new ParamObject());
  }

  public ReturnObject advancedMethod(int param1_int, String param2_string, ParamObject param3_paramObj)
  {
    final ReturnObject returnObject = new ReturnObject();
    returnObject.exampleOutput1_string = param2_string + "_output!";
    returnObject.exampleOutput2_int = param1_int + 1000;
    returnObject.exampleOutput3_internalObj = param3_paramObj;
    return returnObject;
  }

  //region Types for the advancedMethod

  public static class ParamObject
  {
    public int exampleField = 43;

    @Override
    public String toString()
    {
      return "ParamObject{" +
        "exampleField=" + exampleField +
        '}';
    }
  }

  public class ReturnObject
  {

    public String      exampleOutput1_string;
    public int         exampleOutput2_int;
    public ParamObject exampleOutput3_internalObj;

    @Override
    public String toString()
    {
      return "ReturnObject{" +
        "exampleOutput1_string='" + exampleOutput1_string + '\'' +
        ", exampleOutput2_int=" + exampleOutput2_int +
        ", exampleOutput3_internalObj=" + exampleOutput3_internalObj +
        '}';
    }
  }
  //endregion


}