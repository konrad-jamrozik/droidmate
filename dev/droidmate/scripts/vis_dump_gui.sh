#!/bin/bash          

echo "After the Monitor GUI starts, please select the device to the left and click on the appropriate button above it to dump the GUI."
# noob explanation on how to run .bat files from Windows' bash script.
# http://en.wikipedia.org/wiki/ComSpec
# http://stackoverflow.com/questions/3395374/run-invoke-windows-batch-script-from-sh-or-bash-script
$COMSPEC /c monitor.bat
