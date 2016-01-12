#!/bin/bash

dbFileName=$1

cat ./publicDB.sql | sqlite3 $dbFileName