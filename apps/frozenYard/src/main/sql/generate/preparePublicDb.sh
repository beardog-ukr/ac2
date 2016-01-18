#!/bin/bash

dbFileName=$1

if [ -f  "$dbFileName" ]
then
  echo "File $dbFileName already exists, do nothing"
  exit
fi

cat ./publicDB.sql | sqlite3 $dbFileName