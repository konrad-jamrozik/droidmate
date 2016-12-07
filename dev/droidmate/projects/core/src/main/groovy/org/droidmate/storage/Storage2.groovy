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

import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Persistent storage. Allows for serializing to HDD and back.
 */
@Slf4j
class Storage2 implements IStorage2
{

  private static final DateTimeFormatter serializedFileTimestampPattern = DateTimeFormatter.ofPattern("yyyy MMM dd HHmm")

  private final Path droidmateOutputDirPath

  static final String ser2FileExt = ".ser2"

  private String timestamp


  Storage2(Path droidmateOutputDirPath)
  {
    this.droidmateOutputDirPath = droidmateOutputDirPath
  }

  @Override
  void serializeToFile(obj, Path file)
  {
    ObjectOutputStream serOut = new ObjectOutputStream(
      Channels.newOutputStream(FileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)))
    serOut.writeObject(obj)
    serOut.close()
  }

  @Override
  Collection<Path> getSerializedRuns2()
  {
    Collection<Path> paths = Files.list(droidmateOutputDirPath)
      .findAll {Path p -> p.isRegularFile() && p.fileName.toString().endsWith(ser2FileExt)}

    return paths
  }

  @Override
   Object deserialize(Path file)
  {
    ObjectInputStream input =
      new LegacyObjectInputStream(Channels.newInputStream(FileChannel.open(file, StandardOpenOption.READ)))
    def obj = input.readObject()
    input.close()
    return obj
  }

  @Override
  void serialize(obj, String namePart)
  {
    if (timestamp == null)
      timestamp = LocalDateTime.now().format(serializedFileTimestampPattern)
    Path ser2 = getNewPath("$timestamp ${namePart}$ser2FileExt")
    log.info("Serializing ${obj.class.simpleName} to $ser2")
    serializeToFile(obj, ser2)
  }

  private Path getNewPath(String fileName)
  {
    ensureDroidmateOutputDirExists()

    Path path = droidmateOutputDirPath.resolve(fileName)

    if (!Files.exists(path.parent))
    {
      Files.createDirectories(path.parent)
      if (!Files.isDirectory(path.parent))
        assert false
    }

    path = ensurePathDoesntExist(path)
    assert Files.isDirectory(path.parent)
    assert !Files.exists(path)
    return path
  }

  @Override
  void delete(String deletionTargetNamePart)
  {
    Files.list(droidmateOutputDirPath).each {Path p ->
      if (p.fileName.toString().contains(deletionTargetNamePart))
      {
        boolean success = Files.delete(p)
        if (success)
          log.debug("Deleted: " + p.getFileName().toString())
        else
          log.debug("Failed to delete: " + p.getFileName().toString())
      }
    }
  }

  private void ensureDroidmateOutputDirExists()
  {
    assert droidmateOutputDirPath != null

    if (!Files.isDirectory(droidmateOutputDirPath))
    {
      Files.createDirectories(droidmateOutputDirPath)

      if (!Files.isDirectory(droidmateOutputDirPath))
        assert false: "Failed to create droidmate output directory. Path: ${droidmateOutputDirPath.toString()}"

      log.info("Created directory: ${droidmateOutputDirPath.toString()}")
    }

    assert Files.isDirectory(droidmateOutputDirPath)
  }

  private Path makeFallbackOutputFileWithRandomUUIDInName(Path targetOutPath)
  {
    assert targetOutPath != null

    def actualOutPath = targetOutPath

    def fallbackOutPath = droidmateOutputDirPath.resolve("fallback-copy-${UUID.randomUUID()}")
    log.warn("Failed to delete ${actualOutPath.toString()}. Trying to create a pointer to nonexisting file with path: ${fallbackOutPath.toString()}")


    assert !Files.exists(fallbackOutPath): "The ${fallbackOutPath.toString()} exists. This shouldn't be possible, " +
      "as its file path was just created with a random UUID"


    assert Files.isDirectory(fallbackOutPath.parent)
    assert !Files.exists(fallbackOutPath)
    return fallbackOutPath
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

}