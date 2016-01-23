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

#cat ./integtest/db/test.sql | sqlite3 $dbFileName


execStr="java -jar ./build/libs/frozenYard-0.0.2.jar "
#execStr=${execStr}" --json ./integtest/shortfiles/s2015110_234339.bg.if.json "
execStr=${execStr}" --db $dbFileName "
execStr=${execStr}" --list ./integtest/list02.txt"
execStr=${execStr}" -l INFO"

time ${execStr}
