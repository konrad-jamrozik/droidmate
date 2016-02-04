// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.device.datatypes

import groovy.util.logging.Slf4j

import static org.droidmate.device.datatypes.UiautomatorWindowDumpTestHelper.*

@Slf4j
class UnreliableDeviceGuiSnapshotProvider implements IUnreliableDeviceGuiSnapshotProvider
{


  private final IDeviceGuiSnapshot originalGuiSnapshot

  private final IDeviceGuiSnapshot       nullGuiSnapshot                    = newNullWindowDump()
  private final IDeviceGuiSnapshot       emptyGuiSnapshot                   = newEmptyWindowDump()
  private final IDeviceGuiSnapshot       appHasStoppedOKDisabledGuiSnapshot = newAppHasStoppedDialogOKDisabledWindowDump()
  private final IDeviceGuiSnapshot       appHasStoppedGuiSnapshot           = newAppHasStoppedDialogWindowDump()
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
