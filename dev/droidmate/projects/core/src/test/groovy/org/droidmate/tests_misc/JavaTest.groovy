// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.tests_misc

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import groovy.transform.TypeChecked
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TypeChecked
@RunWith(JUnit4)
/**
 * @see org.droidmate.logging.LogbackAppendersTest
 */
class JavaTest
{

  @Test
  void test_manyScannersOnOneReaderReadingLinesThrowException()
  {
    Reader r = new StringReader(String.format("line 1%n line 2 %n line 3"))
    Scanner s1 = new Scanner(r)
    println s1.nextLine()
    Scanner s2 = new Scanner(r)

    try
    {
      println s2.nextLine()
    } catch (NoSuchElementException ignored)
    {
      return
    }

    assert false
  }

  @Test
  void test_manyReadLinesOnOneReaderWork()
  {
    Reader r = new StringReader(String.format("line 1%n line 2 %n line 3"))
    println r.readLine()
    println r.readLine()
  }

  @Test
  void "serializes and deserializes"()
  {
    FileSystem fs = Jimfs.newFileSystem(Configuration.unix())
    Path tmpDir = fs.getPath("/tmp")
    Files.createDirectory(tmpDir)
    Path tmpFile = tmpDir.resolve("employee.ser")

    println "SERIALIZING"

    Employee empl = new Employee();
    empl.name = "Reyan Ali";
    empl.address = "Phokka Kuan, Ambehta Peer";
    empl.SSN = 11122333;
    empl.number = 101;
    try
    {
      ObjectOutputStream out = tmpFile.newObjectOutputStream()
      out.writeObject(empl)
      out.close()
      System.out.printf("Serialized data is saved in /tmp/employee.ser");
    }catch(IOException e)
    {
      e.printStackTrace();
    }

    println ""
    println "DESERIALIZING"

    Employee empl2
    try
    {
      ObjectInputStream inpStr = tmpFile.newObjectInputStream()
      empl2 = (Employee) inpStr.readObject();
      inpStr.close()
    }catch(IOException e)
    {
      e.printStackTrace();
      return;
    }catch(ClassNotFoundException c)
    {
      System.out.println("Employee class not found");
      c.printStackTrace();
      return;
    }
    System.out.println("Deserialized Employee...");
    System.out.println("Name: " + empl2.name);
    System.out.println("Address: " + empl2.address);
    System.out.println("SSN: " + empl2.SSN);
    System.out.println("Number: " + empl2.number);
  }

}

public class Employee implements Serializable
{
  public String name;
  public String address;
  public transient int SSN;
  public int number;
  public void mailCheck()
  {
    System.out.println("Mailing a check to " + name
      + " " + address);
  }
}
