// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated

import groovy.transform.TypeChecked
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.Storage
import org.droidmate.exploration.actions.ExplorationActionTestHelper
import org.droidmate.misc.TimeProvider
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.droidmate.test_base.FilesystemTestFixtures
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters

import static org.droidmate.deprecated_still_used.VerifiableDeviceAction.newClickGuiVerifiableDeviceAction
import static org.droidmate.deprecated_still_used.VerifiableDeviceAction.newPressBackToAppVerifiableDeviceAction
import static org.droidmate.device.datatypes.GuiStateTestHelper.newEmptyGuiState
import static org.droidmate.exploration.actions.ExplorationAction.newPressBackExplorationAction

@Deprecated
@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class ExplorationActionTranslatorTest extends DroidmateGroovyTestCase
{
  @Test
  void "Translates 'widget click' exploration action to device action with no expectation"()
  {
    def sut = getSut(FilesystemTestFixtures.apkFixture_simple_packageName, FilesystemTestFixtures.apkFixture_simple_launchableActivityComponentName)

    def widgetExplAction = ExplorationActionTestHelper.newWidgetClickExplorationAction()
    def anyGuiState = newEmptyGuiState()

    // Act
    def verDevActs = sut.translate(widgetExplAction)

    assert verDevActs?.size() == 1
    assert verDevActs[0] == newClickGuiVerifiableDeviceAction(widgetExplAction)
  }

  @Test
  void "Translates 'click back' exploration action to device action expecting a screen of explored app"()
  {
    def sut = getSut(FilesystemTestFixtures.apkFixture_simple_packageName, FilesystemTestFixtures.apkFixture_simple_launchableActivityComponentName)
    def anyGuiState = newEmptyGuiState()

    // Act
    def verDevActs = sut.translate(newPressBackExplorationAction())

    assert verDevActs?.size() == 1
    assert verDevActs[0] == newPressBackToAppVerifiableDeviceAction(FilesystemTestFixtures.apkFixture_simple_packageName)
  }

  private static IExplorationActionToVerifiableDeviceActionsTranslator getSut(
    String packageName, String launchableActivityComponentName)
  {
    def cfg = Configuration.default
    def factory = ExplorationComponentsFactory.build(cfg, new TimeProvider(), new Storage(cfg.droidmateOutputDirPath))
    return factory.createActionsTranslator(packageName, launchableActivityComponentName)
  }
}
