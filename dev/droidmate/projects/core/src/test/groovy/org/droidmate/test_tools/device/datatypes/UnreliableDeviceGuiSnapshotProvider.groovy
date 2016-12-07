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
package org.droidmate.test_tools.device.datatypes

import groovy.util.logging.Slf4j
import org.droidmate.device.datatypes.IDeviceGuiSnapshot

@Slf4j
class UnreliableDeviceGuiSnapshotProvider implements IUnreliableDeviceGuiSnapshotProvider
{


  private final IDeviceGuiSnapshot originalGuiSnapshot

  private final IDeviceGuiSnapshot       nullGuiSnapshot                    = UiautomatorWindowDumpTestHelper.newNullWindowDump()
  private final IDeviceGuiSnapshot       emptyGuiSnapshot                   = UiautomatorWindowDumpTestHelper.newEmptyWindowDump()
  private final IDeviceGuiSnapshot       appHasStoppedOKDisabledGuiSnapshot = UiautomatorWindowDumpTestHelper.newAppHasStoppedDialogOKDisabledWindowDump()
  private final IDeviceGuiSnapshot       appHasStoppedGuiSnapshot           = UiautomatorWindowDumpTestHelper.newAppHasStoppedDialogWindowDump()
  private final List<IDeviceGuiSnapshot> guiSnapshotsSequence               =
    [nullGuiSnapshot,
     emptyGuiSnapshot,
     appHasStoppedOKDisabledGuiSnapshot,
     appHasStoppedGuiSnapshot
    ]

  private IDeviceGuiSnapshot currentGuiSnapshot = guiSnapshotsSequence.first()

  boolean okOnAppHasStoppedWasPressed = false


  UnreliableDeviceGuiSnapshotProvider(IDeviceGuiSnapshot originalGuiSnapshot)
  {
    this.originalGuiSnapshot = originalGuiSnapshot
  }

  @Override
  void pressOkOnAppHasStopped()
  {
    assert !this.okOnAppHasStoppedWasPressed
    assert guiSnapshotsSequence.last() == currentGuiSnapshot
    this.okOnAppHasStoppedWasPressed = true

    this.currentGuiSnapshot = originalGuiSnapshot
  }

  @Override
  IDeviceGuiSnapshot getCurrentWithoutChange()
  {
    return this.currentGuiSnapshot
  }

  @Override
  IDeviceGuiSnapshot provide()
  {
    log.trace("provide($currentGuiSnapshot)")

    IDeviceGuiSnapshot out = this.currentGuiSnapshot

    if (currentGuiSnapshot != guiSnapshotsSequence.last() && currentGuiSnapshot != originalGuiSnapshot)
      this.currentGuiSnapshot = this.guiSnapshotsSequence[guiSnapshotsSequence.indexOf(currentGuiSnapshot) + 1]

    return out
  }
}
