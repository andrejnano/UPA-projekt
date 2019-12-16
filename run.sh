#!/bin/sh

# run target does not use a console, so jLine etc. does not work, see https://github.com/jline/jline3/issues/77
. ./gradlew run $@
