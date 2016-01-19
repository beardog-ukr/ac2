insert into files(filename, date_processed) values ("not.real.foobar.json", "2016-01-16 14:10:00");
insert into files(filename, date_processed) values ("not.real.another.foobar.json", datetime("now"));

insert into court_addresses(court_id, court_address) values ("pc.ki", "khreschatyyk fourfour");

insert into cases (date_scheduled, judge, number, involved, description, type, court_address_id, file_id)
values ("2016-01-18 19:00:00", "Zvarich", "33/444/55-ф", "Colyada", "Making money with X", "АС", 1,2);

insert into cases (date_scheduled, judge, number, involved, description, type, court_address_id, file_id)
values ("2016-01-18 19:30:00", "Zvarich", "33/444/55-ф", "Colyada", "Making money with X", "АС", 1,2);


