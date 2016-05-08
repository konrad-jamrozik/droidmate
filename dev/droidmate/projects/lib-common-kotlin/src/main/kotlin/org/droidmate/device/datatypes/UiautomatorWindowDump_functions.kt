// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.ByteArrayOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory


/**
 * DroidMate obtains Android device's window hierarchy dump via
 * android.support.test.uiautomator.UiDevice#dumpWindowHierarchy(java.io.File)
 * called in org.droidmate.uiautomator2daemon.UiAutomatorDaemonDriver
 *
 * When run on AVD this dump will contain a frame that has to be stripped. This function takes care of that.
 *
 * Implemented with help of:
 * http://stackoverflow.com/a/3717875/986533
 * https://docs.oracle.com/javase/tutorial/jaxp/xslt/xpath.html
 * http://www.xpathtester.com/xpath
 * https://docs.oracle.com/javase/7/docs/api/javax/xml/parsers/package-summary.html
 * https://docs.oracle.com/javase/7/docs/api/javax/xml/xpath/package-summary.html
 */
fun stripAVDframe(windowHierarchyDump: String): String {

  val doc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(windowHierarchyDump.byteInputStream())
  val xpath: XPathExpression = XPathFactory.newInstance().newXPath().compile("/hierarchy/node[@package=\"com.android.systemui\"]");
  val nodeList: NodeList = xpath.evaluate(doc, XPathConstants.NODESET) as NodeList

  for (i in 0..nodeList.length - 1) {
    val currNode = nodeList.item(i)
    currNode.parentNode.removeChild(currNode)
  }

  val baos = ByteArrayOutputStream()
  TransformerFactory.newInstance().newTransformer().transform(DOMSource(doc), StreamResult(baos))
  return baos.toString(Charsets.UTF_8.name()) 
}