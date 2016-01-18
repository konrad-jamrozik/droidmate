// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
package org.droidmate.command

import groovy.util.logging.Slf4j
import org.droidmate.configuration.Configuration
import org.droidmate.deprecated_still_used.*

@Slf4j
class ExtractDataFromPreviousRunCommand extends DroidmateCommand
{

  final IStorage storage

  final IExplorationOutputAnalysisPersister persister

  ExtractDataFromPreviousRunCommand(IStorage storage, IExplorationOutputAnalysisPersister persister)
  {
    this.storage = storage
    this.persister = persister
  }

  static ExtractDataFromPreviousRunCommand build(Configuration cfg)
  {
    def storage = new Storage(cfg.droidmateOutputDirPath)

    def extractor = new ExplorationOutputDataExtractor(cfg.compareRuns, cfg)
    def persister = new ExplorationOutputAnalysisPersister(cfg, extractor, storage)

    return new ExtractDataFromPreviousRunCommand(storage, persister)
  }

  @Override
  void execute(Configuration cfg)
  {
    log.info("Deserializing and extracting data from previous apk exploration outputs.")
    ExplorationOutput explorationOutput = storage.deserializeAll()
    explorationOutput.sort {it.appPackageName}

    persister.persist(explorationOutput)
  }
}
