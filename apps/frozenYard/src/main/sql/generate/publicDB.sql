CREATE TABLE cases
  ( date_scheduled TEXT NOT NULL
  , judge TEXT
  , number TEXT NOT NULL
  , involved TEXT
  , description TEXT
  , type TEXT
  , court_address_id INTEGER
  , file_id INTEGER NOT NULL
  );

CREATE TABLE court_addresses
  ( court_address_id INTEGER NOT NULL PRIMARY KEY autoincrement
  , court_id TEXT NOT NULL
  , court_address TEXT NOT NULL UNIQUE
  );

CREATE TABLE files
  ( file_id INTEGER NOT NULL PRIMARY KEY autoincrement
  , filename TEXT NOT NULL UNIQUE
  , date_processed TEXT NOT NULL
  );


CREATE INDEX number_idx  ON cases(number);


