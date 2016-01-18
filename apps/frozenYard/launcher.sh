#!/bin/bash

dbFileName="./integtest/db/public.sqlite"
if [ -f  "$dbFileName" ]
  then
    obsoleteDbFileName=$dbFileName".old."$$
    mv  "$dbFileName" "$obsoleteDbFileName"
    echo "Saved previous db version as $obsoleteDbFileName"
fi

basicDbFileName="./integtest/db/basic_public.sqlite"
if [ -f  "$basicDbFileName" ]
then
  cp $basicDbFileName $dbFileName
  echo "Recreated $dbFileName"
else
  echo "Failed to find $basicDbFileName"
  exit
fi

execStr="java -jar ./build/libs/frozenYard-0.0.1.jar "
execStr=${execStr}" --json ./integtest/shortfiles/s2015111_234339.bg.if.json "
execStr=${execStr}" --db $dbFileName "
execStr=${execStr}" -l DEBUG"

time ${execStr}


