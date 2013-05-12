#!/bin/bash
#
# Must be done after the application is signed
PATH_ZIP_ALIGN=/home/gunnarro/applications/android-sdk-linux/tools
APP_PATH=/home/gunnarro/code/github/smsfilter
# - f foroverride existing smsfilter.apk file
# - v for verbose
# - 4 for 4 byte perfomance optimization
$PATH_ZIP_ALIGN/zipalign -f -v 4 $APP_PATH/smsfilter.apk $APP_PATH/release/smsfilter.apk

