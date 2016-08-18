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
package org.droidmate.storage

import groovy.util.logging.Slf4j
import org.droidmate.misc.Assert

import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

@Slf4j
class WritableDirectory implements IWritableDirectory
{

  @Delegate
  Path dir

  WritableDirectory(Path dir)
  {
    this.dir = dir

    if (!Files.exists(dir))
      Files.createDirectories(dir)

    assert Files.exists(dir)
    assert Files.isDirectory(dir)
    assert Files.isWritable(dir)
  }

  @Override
  public void withWriterFor(String fileName, Closure closure)
  {
    Writer writer = this.getWriter(fileName)
    Assert.checkClosureFirstParameterSignature(closure, Writer)
    writer.withWriter { closure(writer) }
  }

  private Writer getWriter(String fileName)
  {
    Path path = getNewPath(fileName)

    Writer pathWriter = Channels.newWriter(
      FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW),
      StandardCharsets.UTF_8.name())

    return new BufferedWriter(pathWriter)
  }


  private Path getNewPath(String fileName)
  {
    Path path = this.dir.resolve(fileName)

    path = ensurePathDoesntExist(path)

    assert Files.isDirectory(path.parent)
    assert !Files.exists(path)
    return path
  }

  private Path ensurePathDoesntExist(Path path)
  {
    assert path != null

    if (Files.exists(path))
    {
      log.trace("Deleting ${path.toString()}")
      Files.delete(path)
      if (Files.exists(path))
      {
        //noinspection GroovyAssignmentToMethodParameter
        path = makeFallbackOutputFileWithRandomUUIDInName(path)
      }
    }

    assert Files.isDirectory(path.parent)
    assert !Files.exists(path)
    return path
  }

  private Path makeFallbackOutputFileWithRandomUUIDInName(Path path)
  {
    assert path != null

    def actualOutPath = path

    def fallbackOutPath = this.dir.resolve("fallback-copy-${UUID.randomUUID()}")
    log.warn("Failed to delete ${actualOutPath.toString()}. " +
      "Trying to create a pointer to a nonexisting file with path: ${fallbackOutPath.toString()}")


    assert !Files.exists(fallbackOutPath): "The ${fallbackOutPath.toString()} exists. This shouldn't be possible, " +
      "as its file path was just created with a random UUID"


    assert Files.isDirectory(fallbackOutPath.parent)
    assert !Files.exists(fallbackOutPath)
    return fallbackOutPath
  }

}
