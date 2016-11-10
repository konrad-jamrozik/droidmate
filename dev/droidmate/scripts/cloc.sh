#!/bin/bash

# For archived cloc counts, see:
# C:\my\local\repos\chair\droidmate\resources\cloc_coverage_reports

function deb()
{
  find $dev_root $aptle $skip $test $java_groovy_kotlin -print
}

dev_root="../.."

# aptle: avoid "path too long" error.
aptle="-iregex .*classes.* -prune -o" 
skip='( ! -iregex .*nu\/xom.* ! -iregex .*\/\(build\|gen\)\/.* )'
# TODO: account for droidmate_usage_examples (a project beside apk_fixtures_src and droidmate)
test='-iregex .*\/\(test\|androidTest\|apk_fixtures_src\)\/.*'
java_groovy_kotlin="-iregex .*\w\.\(java\|groovy\|kt\)"
gradle="-iregex .*\w\.gradle"
sh="-iregex .*\w\.sh"

function count()
{
  $1 | xargs cat | sed '/^\s*$/d' | wc -l
}

# Count lines of code
function cloc()
{
   src=$(count "find $dev_root $aptle $skip ! $test $java_groovy_kotlin -print")
 tests=$(count "find $dev_root $aptle $skip   $test $java_groovy_kotlin -print")
gradle=$(count "find $dev_root $aptle $skip $gradle -print")
    sh=$(count "find $dev_root $aptle $skip $sh -print")

total=$(($src + $gradle + $sh + $tests))

echo "Timestamp: "$(date +"%d %b %Y %T")
echo
echo "Number of nonempty lines in files located under <repo>/droidmate/dev:"
echo
echo "src    : $(printf "%5d" $src)"
echo "tests  : $(printf "%5d" $tests)"
echo "gradle : $(printf "%5d" $gradle)"
echo "sh     : $(printf "%5d" $sh)"
echo "         -----"
echo "total  : $(printf "%5d" $total)"
echo
}

# Enables calling bash functions from command line.
# Example usage: <this script name> cloc
# Reference: http://stackoverflow.com/a/16159057/986533
$@


