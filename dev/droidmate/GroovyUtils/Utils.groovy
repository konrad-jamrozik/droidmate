// Copyright (c) 2012-2016 Saarland University
// All rights reserved.
//
// Author: Konrad Jamrozik, jamrozik@st.cs.uni-saarland.de
//
// This file is part of the "DroidMate" project.
//
// www.droidmate.org
/**
 * This script file is used internally by the build.gradle script of DroidMate.
 */

private boolean osIsWindows() {
    return System.properties['os.name'].toLowerCase().contains('windows')
}

int executeCommand(String commandName, String commandString) {

    String cmd = { if (osIsWindows()) "cmd /c " else ""}()

    commandString = cmd + commandString
    println "========================="
    println "Executing command named: $commandName"
    println "Command string:"
    println commandString


    def proc = commandString.execute()
    print "executing..."
    proc.consumeProcessOutput()
    proc.waitFor()
    println " DONE"

    println "return code: ${proc.exitValue()}"
    def stderr = proc.err.text
    def stdout = proc.in.text // the input stream to read from the output stream associated with the executed native process.
    if (stderr != "")
    {
        println "----------------- stderr:"
        println stderr
        println "----------------- /stderr"
    }
    else
        println "stderr is empty"
    if (stdout != "")
    {
        println "----------------- stdout:"
        println stdout
        println "----------------- /stdout"
    }
    else
        println "stdout is empty"
    println "========================="
    return proc.exitValue()
}
