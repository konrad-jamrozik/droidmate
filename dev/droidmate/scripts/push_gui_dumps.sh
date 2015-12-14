#!/bin/bash


for wd in $(find ../gui_dumps -iname *.xml)
do
  echo "Pushing $wd"
  adb push $wd data/local/tmp
done