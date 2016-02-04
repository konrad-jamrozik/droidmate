// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.datatypes

public enum ValidationResult {

  OK(true, "The window xml hierarchy dump is well-formed and does not represent an 'app has stopped' dialog box."),
  app_has_stopped_dialog_box_with_OK_button_enabled(true, "The window xml hierarchy dump represents 'app has stopped' dialog box and the 'OK' button is enabled."),
  app_has_stopped_dialog_box_with_OK_button_disabled(false, "The window xml hierarchy dump represents 'app has stopped' dialog box, but the 'OK' button is disabled."),
  missing_root_xml_node_prefix(false, "The window xml hierarchy dump doesn't contain the root node prefix, i.e.: " + UiautomatorWindowDump.rootXmlNodePrefix),
  is_empty(false, "The window xml hierarchy dump is empty (its string length is zero)"),
  is_null(false, "The window xml hierarchy dump is null")

  ValidationResult(boolean valid, String description) {this.valid = valid; this.description = description}
  boolean valid
  String  description
}
