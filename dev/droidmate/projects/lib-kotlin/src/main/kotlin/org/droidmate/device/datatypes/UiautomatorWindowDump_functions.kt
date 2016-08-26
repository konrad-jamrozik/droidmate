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

package org.droidmate.device.datatypes

import org.w3c.dom.Document
import org.w3c.dom.Node
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
 * THIS FUNCTION IS BROKEN, DO NOT USE IT. See below.
 * 
 * DroidMate obtains Android device's window hierarchy dump via
 * android.support.test.uiautomator.UiDevice#dumpWindowHierarchy(java.io.File)
 * called in org.droidmate.uiautomator2daemon.UiAutomatorDaemonDriver
 *
 * This dump contains as first children of the <hierarchy> node some nodes with com.android.systemui package.
 * They are deleted by this function. Interestingly, they are not present if the window hierarchy is obtained with monitor tool
 * from Android SDK.
 *
 * Implemented with help of:
 * http://stackoverflow.com/a/3717875/986533
 * https://docs.oracle.com/javase/tutorial/jaxp/xslt/xpath.html
 * http://www.xpathtester.com/xpath
 * https://docs.oracle.com/javase/7/docs/api/javax/xml/parsers/package-summary.html
 * https://docs.oracle.com/javase/7/docs/api/javax/xml/xpath/package-summary.html
 *
 */
fun removeSystemuiNodes(windowHierarchyDump: String): String {

  val doc: Document = getDumpDocument(windowHierarchyDump)
  val nodesToRemove: NodeList = getNodesToRemove(doc)

  for (i in 0..nodesToRemove.length - 1) {
    removeNode(nodesToRemove.item(i))
  }

  val outputString = writeDocToString(doc)
  return outputString 
}

private fun getDumpDocument(windowHierarchyDump: String) = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(windowHierarchyDump.byteInputStream())

private fun getNodesToRemove(windowHierarchyDumpDocument: Document): NodeList {
  val nodesToRemoveXpath: XPathExpression = XPathFactory.newInstance().newXPath().compile("/hierarchy/node[@package=\"com.android.systemui\"]")
  val nodesToRemove: NodeList = nodesToRemoveXpath.evaluate(windowHierarchyDumpDocument, XPathConstants.NODESET) as NodeList
  return nodesToRemove
}

private fun removeNode(currNodeToBeRemoved: Node) {
  // Current node is removed by informing its parent that its child, being current node, is o be removed.
  currNodeToBeRemoved.parentNode.removeChild(currNodeToBeRemoved)
}

private fun writeDocToString(windowHierarchyDumpDocument: Document): String {
  val baos = ByteArrayOutputStream()
  TransformerFactory.newInstance().newTransformer().transform(DOMSource(windowHierarchyDumpDocument), StreamResult(baos))
  val outputString = baos.toString(Charsets.UTF_8.name())
  return outputString
}

