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

package org.droidmate.tests_misc

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.OutputStreamAppender
import com.google.common.base.Stopwatch
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.droidmate.common.logging.LogbackConstants
import org.droidmate.logging.LazyFileAppender
import org.droidmate.logging.LogbackAppenders
import org.droidmate.test_base.DroidmateGroovyTestCase
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit

@TypeChecked
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(JUnit4)
class LogbackTest extends DroidmateGroovyTestCase
{
  public static final fileAppenderName = "FileAppenderName"

  @Test
  void t1_rootLoggerHasStdAppenders()
  {
    Logger log = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)
    List<Appender<ILoggingEvent>> appenders = log.iteratorForAppenders().toList()

    assert LogbackAppenders.appender_stdout in appenders*.name
  }

  @Test
  void t2_performanceTest()
  {
    Logger logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)

    def entry = "t"

    measureTime("plain log", 1000) {logger.debug("The new entry is "+entry+".")}
    measureTime("fast log", 1000) {logger.debug("The new entry is {}.", entry);}
    measureTime("plain groovy log", 1000) {LoggedClass.useLog("The new entry is "+entry+".")}
    measureTime("fast groovy log", 1000) {LoggedClass.useLog("The new entry is {}.", entry)}
  }

  @Test
  void t3_creatingLoggerAndAppender()
  {
    Logger foo = createLoggerWithFileAppender("foo", "foo.log", false);
    Logger bar = createLoggerWithFileAppender("bar", "bar.log", false);
    foo.info("test");
    bar.info("bar");
  }

  @SuppressWarnings("GroovyAssignabilityCheck")
  @TypeChecked(TypeCheckingMode.SKIP)
  @Test
  void t4_lazyFileAppenderCreatesFileLazily()
  {
    def logger = createLoggerWithFileAppender("quxLogger", "qux.log", true)
    def fileAppender = logger.getAppender(fileAppenderName)

    assert !(new File(fileAppender.file).exists())
    logger.info("something")
    assert new File(fileAppender.file).exists()

  }

  private static Logger createLoggerWithFileAppender(String loggerName, String fileName, boolean lazyFileAppender) {

    def lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    def ple = createAndSetupPatternLayoutEncoder(lc)
    def fileAppender = createAndSetupFileAppender(fileName, ple, lc, lazyFileAppender)

    Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
    logger.addAppender(fileAppender);
    logger.setLevel(Level.DEBUG);
    logger.setAdditive(false); /* set to true if root should log too */

    return logger;
  }

  private static PatternLayoutEncoder createAndSetupPatternLayoutEncoder(LoggerContext lc)
  {
    PatternLayoutEncoder ple = new PatternLayoutEncoder();

    ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
    ple.setContext(lc);
    ple.start();
    return ple
  }


  @TypeChecked(TypeCheckingMode.SKIP)
  private static OutputStreamAppender<ILoggingEvent> createAndSetupFileAppender(
    String fileName, PatternLayoutEncoder ple, LoggerContext lc, boolean lazy)
  {
    OutputStreamAppender<ILoggingEvent> fileAppender = lazy ?
      new LazyFileAppender<ILoggingEvent>() : new FileAppender<ILoggingEvent>()


    fileAppender.setName(fileAppenderName)
    fileAppender.setFile("${LogbackConstants.LOGS_DIR_PATH}${File.separator}" + fileName);
    fileAppender.setEncoder(ple);
    fileAppender.setContext(lc);
    if (lazy)
      fileAppender.setLazy(true)
    fileAppender.start();

    return fileAppender
  }

  static void measureTime(String name, int iterations, Closure computation)
  {
    def stopwatch = Stopwatch.createStarted()

    for (i in 1..iterations)
      computation()

    stopwatch.stop()

    println "Measured seconds for $name: " + stopwatch.elapsed(TimeUnit.MILLISECONDS)/1000
  }

  @TypeChecked(TypeCheckingMode.SKIP)
  @Slf4j
  static class LoggedClass
  {
    public static useLog(String msg)
    {
      log.debug(msg)
    }

    public static useLog(String format, Object args)
    {
      log.debug(format, args)
    }
  }


}
