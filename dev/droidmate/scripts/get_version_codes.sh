#!/bin/bash

# Extracts package name, versionCode and versionName from all .apk files in dir given as parameter.
#
# Example parameter value (include the quotation symbols): 
# "C:\my\local\repos\chair\droidmate\dev\droidmate\projects\core\artifacts\apks_ccs2014\all"

path=$(echo $1 | sed 's/\\/\//g')
for f in $path/*.apk
do
  # Example of aapt dump badging $f | grep package:
  # package: name='at.markushi.expensemanager' versionCode='22' versionName='2.2.3'
  pkgLine=$(aapt dump badging $f | grep package:) 

  # Example of result:
  # at.markushi.expensemanager 22 2.2.3
  echo $pkgLine | tr \'= ' ' | cut -d' ' -f4,8,12
done