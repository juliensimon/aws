CREATE EXTERNAL TABLE salescsv (
  lastname string,
  firstname string,
  gender string,
  state string,
  age int,
  day int,
  hour int,
  minutes int,
  items int,
  basket int
)
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe'
WITH SERDEPROPERTIES (
  'serialization.format' = ',',
  'field.delim' = ','
) LOCATION 's3://jsimon-redshift-demo-us/data/';

msck repair table salescsv;

CREATE EXTERNAL TABLE salesparquet (
lastname string,
firstname string,
gender string,
state string,
age int,
day int,
hour int,
minutes int,
items int,
basket int
)
STORED AS PARQUET
LOCATION 's3://jsimon-redshift-demo-us/dataparquet/';

INSERT OVERWRITE TABLE salesparquet
SELECT lastname,firstname,gender,state,age,day,hour,minutes,items,basket
FROM salescsv;

CREATE EXTERNAL TABLE salesorc (
lastname string,
firstname string,
gender string,
state string,
age int,
day int,
hour int,
minutes int,
items int,
basket int
)
STORED AS ORC
LOCATION 's3://jsimon-redshift-demo-us/dataorc/';

INSERT OVERWRITE TABLE salesorc
SELECT lastname,firstname,gender,state,age,day,hour,minutes,items,basket
FROM salescsv;

