#!/bin/bash

resultFolder="./integtest/r.$$"
mkdir ${resultFolder}
echo "created folder ${resultFolder}"

java -jar ./build/libs/gleeFarm-0.0.1.jar --list ./integtest/list1.txt --result $resultFolder -l "WARN"
