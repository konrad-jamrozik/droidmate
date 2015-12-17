// Copyright (c) 2012-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.device.datatypes

import groovy.transform.TypeChecked
import org.droidmate.common.exploration.datatypes.Widget
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import java.awt.*
import java.util.List

import static groovy.transform.TypeCheckingMode.SKIP
import static org.droidmate.device.datatypes.UiautomatorWindowDumpTestHelper.*

// WISH add test checking that widget.canBeClicked or not, depending if it intersects with visible device display bounds.
@TypeChecked(SKIP)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class UiautomatorWindowDumpTest extends DroidmateGroovyTestCase
{

  /**
   * bug: ANR with disabled OK button is invalid
   * https://hg.st.cs.uni-saarland.de/issues/987
   */
  @Test
  void "Has no bug #987"()
  {
    UiautomatorWindowDump gs = newAppHasStoppedDialogOKDisabledWindowDump()
    assert !(gs.validationResult.valid)
  }

  @Test
  void "Uiautomator window dump 'empty' fixture is indeed empty"()
  {
    UiautomatorWindowDump sut = newEmptyActivityWindowDump()

    // Act
    def guiState = sut.guiState

    assert guiState?.actionableWidgets?.isEmpty()
  }

  @Test
  void "Gets GUI state from window dump and parses widgets with negative bounds"()
  {
    // Arrange
    Widget w1 = WidgetTestHelper.newClickableWidget(text: "fake_control", bounds: [-100, -5, 90, -3])
    Widget w2 = WidgetTestHelper.newClickableWidget(text: "dummy_button", bounds: [15, -50379, 93, -50357])
    String inputFixture = createDumpSkeleton(dump(w1) + dump(w2))

    final sut = newWindowDump(inputFixture)

    // Act
    final guiState = sut.getGuiState()

    // Assert

    assert guiState?.widgets?.size() == 2
    assert w1 == guiState.widgets[0]
    assert w2 == guiState.widgets[1]
  }

  @Test
  void "Gets GUI state from 'app has stopped' dialog box"()
  {
    // Act
    final sut = newAppHasStoppedDialogWindowDump()

    final guiState = sut.getGuiState()

    List<Integer> nexus7vertAppHasStoppedDialogBoxBounds = [138,620,661,692] as List<Integer>

    Widget expected = WidgetTestHelper.newClickableWidget(bounds: nexus7vertAppHasStoppedDialogBoxBounds, text: "OK")
    assert guiState?.actionableWidgets?.size() == 1
    assert expected.bounds == guiState?.actionableWidgets[0].bounds
    assert expected.text == guiState?.actionableWidgets[0].text

  }

  @Test
  void "Gets GUI state from home screen"()
  {
    // Arrange

    final sut = newHomeScreenWindowDump()

    // Act

    final guiState = sut.getGuiState()

    // Assert

    assert guiState.isHomeScreen()

  }


  @Test
  void "Parses bounds"()
  {
    assert new Rectangle(100, 150, 1, 3) == Widget.parseBounds("[100,150][101,153]")
  }


}
