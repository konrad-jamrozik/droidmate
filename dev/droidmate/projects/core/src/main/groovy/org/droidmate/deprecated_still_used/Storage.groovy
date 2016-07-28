// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org

package org.droidmate.deprecated_still_used

import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/// !!! DUPLICATION WARNING !!! org.droidmate.filesystem.Storage2
/**
 * {@link org.droidmate.deprecated_still_used.ExplorationOutput} persistent storage. Allows for serializing {@link IApkExplorationOutput} to HDD and back.
 */
@Slf4j
@Deprecated
@TypeChecked
// KJA to remove soon
public class Storage implements IStorage
{

  private static final DateTimeFormatter serializedFileTimestampPattern = DateTimeFormatter.ofPattern("yyyy MMM dd HHmm")

  private final Path droidmateOutputDirPath

  static final String serFileExt = ".ser"

  private String timestamp

  Storage(Path droidmateOutputDirPath)
  {
    this.droidmateOutputDirPath = droidmateOutputDirPath
  }

  // !!! DUPLICATION WARNING !!! with org.droidmate.filesystem.WritableDirectory.getWriter
  @Override
  Writer getWriter(String targetName)
  {
    Path path = getNewPath(targetName)
    Writer pathWriter = Channels.newWriter(
      FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW),
      StandardCharsets.UTF_8.name())
    return new BufferedWriter(pathWriter)
  }

  @Override
  void serialize(IApkExplorationOutput apkExplorationOutput, String nameSuffix = "")
  {
    if (timestamp == null)
      timestamp = LocalDateTime.now().format(serializedFileTimestampPattern)
    Path ser = getNewPath("$timestamp ${apkExplorationOutput.appPackageName}$nameSuffix$serFileExt")
    log.info("Serializing exploration output of apk $apkExplorationOutput.appPackageName to $ser")
    apkExplorationOutput.verifyCompletedDataIntegrity()
    serializeToFile(apkExplorationOutput, ser)
  }

  private static void serializeToFile(IApkExplorationOutput apkExplorationOutput, Path ser)
  {
    ObjectOutputStream serOut = new ObjectOutputStream(
      Channels.newOutputStream(FileChannel.open(ser, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)))
    serOut.writeObject(apkExplorationOutput)
    serOut.close()
  }

  // KJA to remove
  @Override
  ExplorationOutput deserializeAll()
  {
    getSerializedRuns().collect {deserializeApkExplorationOutput(it)} as ExplorationOutput
  }

  @Override
  Collection<Path> getSerializedRuns()
  {
    Collection<Path> paths = Files.list(droidmateOutputDirPath)
      .findAll {Path p -> p.fileName.toString().endsWith(serFileExt)}

    return paths
  }

  @Override
  void deleteEmpty()
  {
    assert Files.isDirectory(droidmateOutputDirPath)
    Collection<Path> paths = Files.list(droidmateOutputDirPath)
      .findAll {Path p -> Files.isRegularFile(p)}

    paths.each {Path p ->
      if (Files.size(p) == 0)
        Files.delete(p)
    }
  }

  @Override
  public IApkExplorationOutput deserializeApkExplorationOutput(Path serPath)
  {
    ObjectInputStream serInput =
      new ObjectInputStream(Channels.newInputStream(FileChannel.open(serPath, StandardOpenOption.READ)))
    def aeo = serInput.readObject() as IApkExplorationOutput
    serInput.close()

    aeo.verifyCompletedDataIntegrity()

    log.info("Deserialized exploration output of $aeo.appPackageName from $serPath")
    return aeo
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