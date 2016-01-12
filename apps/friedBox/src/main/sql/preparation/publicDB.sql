CREATE TABLE "cases" 
  ( "date_scheduled" DATETIME NOT NULL 
  , "date_inserted" DATETIME NOT NULL 
  , "judge" VARCHAR NOT NULL 
  , "number" VARCHAR NOT NULL 
  , "involved" VARCHAR
  , "description" VARCHAR
  , "type" VARCHAR NOT NULL
  , "court_id" VARCHAR
  , "court_address" VARCHAR
  );