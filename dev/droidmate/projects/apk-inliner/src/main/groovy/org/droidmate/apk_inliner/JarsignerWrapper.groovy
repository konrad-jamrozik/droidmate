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