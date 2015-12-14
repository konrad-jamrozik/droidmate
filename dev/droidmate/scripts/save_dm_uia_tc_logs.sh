#!/bin/bash

# Save DroidMate uiautomator test case logcat logs

dirPrefix="C:/my/local/repos/chair/droidmate/dev/droidmate/uia_test_cases_logs/"
outFile="${dirPrefix}UiaTestCase_$1.txt"
rm -f $outFile
./read_logcat_dm_uia_tags.sh > $outFile &

terminationString="Test case finished."

echo "Recording logcat messages with DroidMate uiautomator test case tags into $outFile"
echo "This script will terminate when the said file will contain the string: \"$terminationString\""

while [ 1 ];
do 
  sleep 3;
  if grep --quiet "$terminationString" $outFile; then 
    echo "Found \"$terminationString\" Terminating in 2 seconds."
	sleep 2;
	kill $!
	exit 0
  else
    echo -n "."
  fi
done



