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

package org.droidmate.tests_misc

import groovy.mock.interceptor.CallSpec
import groovy.mock.interceptor.MockFor
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.io.FileUtils
import org.droidmate.android_sdk.IAdbWrapper
import org.droidmate.common.DroidmateException
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.lang.reflect.Method

import static groovy.transform.TypeCheckingMode.SKIP

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TypeChecked
@RunWith(JUnit4.class)
class GroovyTest
{
  /*
    Groovy mocks reference:
      http://groovy.codehaus.org/Groovy+Mocks
      http://groovy.codehaus.org/Groovy+way+to+implement+interfaces
   */

  @Test
  void t1_mockUsingMap()
  {
    IGroovySubject mock = [
      getStringVal: {return "mockString"},
      getIntVal: {return 42},
      getStringVal1Param: {x -> return "mock: $x" as String}] as IGroovySubject

    assert mock.getStringVal() == "mockString"
    assert mock.getIntVal() == 42
    assert mock.getStringVal1Param("abc") == "mock: abc"
  }

  @Test
  void t2_mockAllMethodsByReflection()
  {

    Map mockMap = [:]
    IGroovySubject.methods.each {Method m ->
      if (m.returnType == int)
        mockMap[m.name] = {return 0}
      else
        mockMap[m.name] = {return null}
    }

    IGroovySubject mock = mockMap as IGroovySubject

    assert mock.getStringVal() == null
    assert mock.getIntVal() == 0
    assert mock.getStringVal1Param("abc") == null
  }

  @Test
  void t3_mockAllMethodsByReflection_withOverrides()
  {
    Map<String, Closure> methodStubs = [
      getIntVal: {return 42},
      getStringVal1Param: {x -> return "mock: $x" as String}]

    Map mockMap = [:]
    IGroovySubject.methods.each {Method m ->

      if (methodStubs.containsKey(m.name))
        mockMap[m.name] = methodStubs[m.name]
      else if (m.returnType == int)
        mockMap[m.name] = {return 0}
      else
        mockMap[m.name] = {return null}
    }
    IGroovySubject mock = mockMap as IGroovySubject

    assert mock.getStringVal() == null
    assert mock.getIntVal() == 42
    assert mock.getStringVal1Param("abc") == "mock: abc"
  }

  @Test
  void t4_returnTypes()
  {
    IAdbWrapper.methods.each {Method m ->
      println "${m.name}: ${m.returnType}"
    }
  }

  @Test(expected = MissingMethodException)
  @TypeChecked(SKIP)
  void t5_closureWrapping()
  {
    Closure subject = {String a, String b -> println a + " " + b}

    Closure wrappedSubject = {Object[] args -> println "pre"; subject(args)}

    wrappedSubject("a", "b")
  }
/*
@Rule
04	  public TemporaryFolder temporaryFolder = new TemporaryFolder();
05
06	  @Test
07	  public void testRun() throws IOException {
08	    assertTrue( temporaryFolder.newFolder().exists() );
09	  }
 */

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @TypeChecked(SKIP)
  @Test
  void t6_getXmlFileTagValue()
  {
    println temporaryFolder.getRoot().absolutePath

    String xmlInput = """
<main>
  <sub>blah</sub>
  <uh>0</uh>
  <zigzag />
</main>
"""
    File xmlInputFile = temporaryFolder.newFile()
    FileUtils.writeStringToFile(xmlInputFile, xmlInput)

    GPathResult xml = new XmlSlurper().parse(xmlInputFile)
    def uh = xml.breadthFirst().find {it.name() == "uh"}
    assert uh == "0"
  }

  @Test
  void t7_mockAbstractClassFieldAndMethodWithMap()
  {

    // Act
    def ags = [getStrs: {return ["a"]}, method1: {return "b"}] as AbstractGroovySubject

    assert ags.strs == ["a"]
    assert ags.method1() == "b"
  }

  @TypeChecked(SKIP)
  @Test
  void t8_overrideMethodForAllInstancesOfGivenClass()
  {
    String.metaClass.size = {return 3}

    String s1 = "a"
    String s2 = "bc"
    assert s1.size() == 3
    assert s2.size() == 3
  }

  @Test
  void t9_groovyLogging()
  {
    LoggedClass.loggedMethod()
  }

  @Ignore("Doesn't work: see comment in the code")
  @TypeChecked(SKIP)
  @Test
  void t10_doInvokeMethodOnInstanceThroughClass()
  {

    def mc = new ModifiedClass()

    boolean wrapperWorks = false
    // Doesn't work! :(
    // Read: http://groovy.codehaus.org/Dynamic+Groovy
    // http://stackoverflow.com/questions/2444907/is-it-possible-to-replace-groovy-method-for-existing-object
    // This solves the problem by introducing a new class, because a reference to delegate has to be obtained
    // http://groovy.codehaus.org/Decorator+Pattern
    ModifiedClass.metaClass.invokeMethod = {Object recv, String name, Object[] args ->
      wrapperWorks = true;
      return recv.invokeMethod(name, args)
    }
    mc.modifiedMethod(42)
  }

  @Test
  void t10_interceptMethodCallUsingProxyMetaClassWithInterceptor()
  {
    def proxy = ProxyMetaClass.getInstance(ClassWithPrivateMethod)

    /* Implement your own interceptor here.
     Reference:
     http://groovy.codehaus.org/Decorator+Pattern, see "Decorating with an Interceptor"
     http://groovy.codehaus.org/JN3515-Interception
     http://groovy.codehaus.org/Using+the+Proxy+Meta+Class
    */
    def interceptor = new BenchmarkInterceptor()

    proxy.interceptor = interceptor
    proxy.use {
      def sut = new ClassWithPrivateMethod()
      // Act
      sut.publicMethod()
    }
    println interceptor.calls
    println interceptor.statistic()
  }

  @TypeChecked(SKIP)
  @Ignore("Worked in Groovy 2.3.7, doesn't work in 2.4.3")
  @Test
  void t11_interceptMethodCallUsingMetaClass()
  {

    boolean calledInterceptedMethod = false

    // Reference:
    // http://www.radomirml.com/blog/2009/03/23/intercepting-method-call-in-groovy-and-invoking-the-original-method/

    def originalMethod = ClassWithPrivateMethod.metaClass.getMetaMethod("privateMethod", [] as Object[])
    assert originalMethod != null

    ClassWithPrivateMethod.metaClass.privateMethod = {->
      calledInterceptedMethod = true
      println "Called intercepted method!"
      def result = originalMethod.invoke(delegate, [] as Object[])
      result
    }
    def sut = new ClassWithPrivateMethod()

    // Act
    sut.publicMethod()


    assert calledInterceptedMethod
  }

  @TypeChecked(SKIP)
  @Test
  void t12_modifyMethodOfExistingMapBasedMock()
  {
    int counter = 0
    def mapBasedMock = [publicMethod: {++counter; println "++counter: $counter"}] as ClassWithPrivateMethod

    mapBasedMock.publicMethod()
    mapBasedMock.publicMethod()

    mapBasedMock.metaClass.publicMethod = {--counter; println "--counter: $counter"}

    mapBasedMock.publicMethod()
    mapBasedMock.publicMethod()
    mapBasedMock.publicMethod()

    assert counter == -1

    counter = 0

    mapBasedMock = [publicMethod: {++counter; println "++counter: $counter"}] as MockedInterface

    mapBasedMock.publicMethod()
    mapBasedMock.publicMethod()

    mapBasedMock.metaClass.publicMethod = {--counter; println "--counter: $counter"}

    mapBasedMock.publicMethod()
    mapBasedMock.publicMethod()
    mapBasedMock.publicMethod()

    // Replacement doesn't work!
    assert counter == 5

  }

  @Test
  void t13_assertAndFinally()
  {
    try
    {
      assert false
    }
    finally
    {
      return
    }
  }

  @TypeChecked(SKIP)
  @Test
  void t14_parametrizedMock()
  {
    def mock = new MockFor(ClassWith3methods)
    mock.demand.foo(1) {}
    mock.demand.baz(1) {}


    useMock1([[name: "bar", behavior: {-> println "mockeey! 1"}]]) {
      def obj = new ClassWith3methods()
      obj.foo()
      obj.bar()
      obj.baz()
    }
    useMock2([[name: "bar", behavior: {-> println "mockeey! 2"}]]) {
      def obj = new ClassWith3methods()
      obj.foo()
      obj.bar()
      obj.baz()
    }
  }

  @TypeChecked(SKIP)
  def useMock1(List customDemands, Closure closure)
  {
    def mock = new MockFor(ClassWith3methods)
    mock.demand.with {

      foo(1) {}

      def demand = delegate
      customDemands.each {Map customDemand ->
        demand.invokeMethod(customDemand["name"] as String, [1, customDemand["behavior"]])
      }

      baz(1) {}
    }
    mock.use closure
  }

  @TypeChecked(SKIP)
  def useMock2(List customDemands, Closure closure)
  {
    def mock = new MockFor(ClassWith3methods)
    mock.demand.with {

      foo(1) {}

      customDemands.each {Map customDemand ->
        recorded << new CallSpec(name: customDemand.name, behavior: customDemand.behavior, range: 1..1)
      }

      baz(1) {}
    }
    mock.use closure
  }

  @SuppressWarnings("GroovyInArgumentCheck")
  @Test
  void "Using 'in' operator for type checks"()
  {
    def ex = new Exception()
    def err = new Error()
    def dmex = new DroidmateException()
    assert ex in Throwable
    assert dmex in Throwable
    assert dmex in Exception
    assert err in Throwable
    assert !(err in DroidmateException)
    assert !(ex in Error)
    // these asserts worked, but they are commented out because the types they used have since been deleted.
//    def guiExplAct = GuiExplorationAction.createPressBackGuiExplorationAction()
//    def termExplAct = ExplorationAction.getTerminateExplorationAction()
//    assert guiExplAct in GuiExplorationAction
//    assert guiExplAct in ExplorationAction
//    assert guiExplAct.class in [GuiExplorationAction, ExplorationAction]
//    assert termExplAct in ExplorationAction
//    assert !(termExplAct in GuiExplorationAction)
  }

  // Reference: http://groovy.329449.n5.nabble.com/metaclass-how-to-replace-and-restore-a-method-td5042834.html
  @TypeChecked(SKIP)
  @Test
  void "use categories for testing"()
  {
    def cm3m = new ClassWith3methods()
    use(ClassWith3methodsTestCategory)
      {
        assert "TESTED foo!" == cm3m.foo()
        assert "bar" == cm3m.bar()
      }

    assert "foo" == cm3m.foo()
    assert "bar" == cm3m.bar()
  }

  @TypeChecked(SKIP)
  @Test
  void "use categories for testing, wrapping existing method"()
  {
    def cm3m = new ClassWith3methods()
    ClassWith3methodsTestCategory2.origFoo = ClassWith3methods.metaClass.getMetaMethod("foo", new Object[0])
    use(ClassWith3methodsTestCategory2)
      {
        assert "TESTED foo!" == cm3m.foo()
        assert "foo" == cm3m.foo()
        assert "foo" == cm3m.foo()
        assert "bar" == cm3m.bar()
      }

    assert "foo" == cm3m.foo()
    assert "bar" == cm3m.bar()
  }
}

//region Helpers

interface MockedInterface
{

  public void publicMethod()
}

class ClassWith3methodsTestCategory
{


  static String foo(ClassWith3methods cm3m)
  {
    println "TESTED foo!"
    return "TESTED foo!"
  }

}

class ClassWith3methodsTestCategory2
{

  static MetaMethod origFoo

  static int call_count = 0
  static String foo(ClassWith3methods cm3m)
  {
    call_count++
    if (call_count == 1)
    {
      println "TESTED foo!"
      return "TESTED foo!"
    }



    return origFoo.invoke(cm3m, new Object[0])
  }

}

class ClassWith3methods
{

  public String foo()
  {
    println "foo"
    return "foo"
  }

  public String bar()
  {
    println "bar"
    return "bar"
  }

  public String baz()
  {
    println "baz"
    return "baz"
  }
}


class ClassWithPrivateMethod
{


  public void publicMethod()
  {
    println "In public method"
    privateMethod()
  }

  private void privateMethod()
  {
    println "In private method"
  }
}

class ModifiedClass
{

  public int modifiedMethod(int x)
  {
    println "Called the method"
    return x
  }
}

@Slf4j
class LoggedClass
{

  public static void loggedMethod()
  {
    log.info("hah!")
  }

}

abstract class AbstractGroovySubject
{

  List<String> strs

  abstract String method1()
}

interface IGroovySubject
{

  String getStringVal()

  int getIntVal()

  String getStringVal1Param(String p)
}

//endregion Helpers
