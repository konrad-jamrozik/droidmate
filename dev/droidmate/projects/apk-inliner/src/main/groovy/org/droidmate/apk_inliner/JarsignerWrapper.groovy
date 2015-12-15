// Copyright (c) 2013-2015 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.apk_inliner

import groovy.util.logging.Slf4j
import org.droidmate.common.DroidmateException
import org.droidmate.common.ISysCmdExecutor
import org.droidmate.common.SysCmdExecutorException

import java.nio.file.Path

import static java.nio.file.Files.isRegularFile

@Slf4j

public class JarsignerWrapper implements IJarsignerWrapper
{

  private final ISysCmdExecutor sysCmdExecutor
  private final Path jarsignerPath
  private final Path debugKeystore

  JarsignerWrapper(ISysCmdExecutor sysCmdExecutor,
                   Path jarsignerPath,
                   Path debugKeystore)
  {
    this.jarsignerPath = jarsignerPath
    this.debugKeystore = debugKeystore
    this.sysCmdExecutor = sysCmdExecutor

    assert isRegularFile(this.jarsignerPath)
    assert isRegularFile(this.debugKeystore)
    assert this.sysCmdExecutor != null
  }

  @Override
  public ApkPath signWithDebugKey(ApkPath apk) throws DroidmateException
  {
    assert apk != null

    String commandDescription = String.format("Executing jarsigner to sign apk %s.", apk.toRealPath().toString())

    try
    {

      // this command is based on:
      // http://developer.android.com/tools/publishing/app-signing.html#debugmode
      // http://developer.android.com/tools/publishing/app-signing.html#signapp
      sysCmdExecutor.execute(commandDescription, jarsignerPath.toRealPath().toString(),
        "-sigalg MD5withRSA -digestalg SHA1",
        "-keystore", debugKeystore.toRealPath().toString(),
        "-storepass android -keypass android ",
        apk.toRealPath().toString(),
        "androiddebugkey")

    } catch (SysCmdExecutorException e)
    {
      throw new DroidmateException(e)
    }

    assert isRegularFile(apk.path)
    return apk
  }
}