
-- Count total events, S3 ORC
SELECT COUNT(*)
FROM spectrum_schema.eventsorc;

-- Find the number of events per year
SELECT year,
       COUNT(globaleventid) AS nb_events
FROM spectrum_schema.eventsorc
GROUP BY year
ORDER BY year ASC;

-- Find the top 10 countries with the most events
SELECT day,
       COUNT(globaleventid) AS nb_events
FROM spectrum_schema.eventsorc
GROUP BY day
ORDER BY nb_events DESC limit 10;

-- Find the top 10 days with the most events
SELECT day,
       COUNT(globaleventid) AS nb_events
FROM spectrum_schema.eventsorc
GROUP BY day
ORDER BY nb_events DESC limit 10;

-- Show top 10 event categories
SELECT eventcode,
       spectrum_schema.eventcodes.description,
       nb_events
FROM (SELECT spectrum_schema.eventsorc.eventcode,
             COUNT(spectrum_schema.eventsorc.globaleventid) AS nb_events
      FROM spectrum_schema.eventsorc
      GROUP BY spectrum_schema.eventsorc.eventcode
      ORDER BY nb_events DESC LIMIT 10)
  JOIN spectrum_schema.eventcodes ON eventcode = spectrum_schema.eventcodes.code
ORDER BY nb_events DESC;

-- Count Obama events per year
SELECT year,
       COUNT(globaleventid) AS nb_events
FROM spectrum_schema.eventsorc
WHERE actor1name='BARACK OBAMA'
GROUP BY year
ORDER BY year ASC;

-- Count Obama/Putin and Putin/Obama events per category
WITH tmp as (SELECT spectrum_schema.eventsorc.eventcode,
         COUNT(spectrum_schema.eventsorc.globaleventid) AS nb_events
    FROM spectrum_schema.eventsorc
    WHERE ((actor1name LIKE '%OBAMA' and actor2name LIKE '%PUTIN')
            OR (actor2name LIKE '%OBAMA' and actor1name LIKE '%PUTIN'))
    GROUP BY  spectrum_schema.eventsorc.eventcode
    ORDER BY  nb_events DESC)
SELECT eventcode,
         spectrum_schema.eventcodes.description,
         nb_events
FROM tmp
JOIN spectrum_schema.eventcodes
    ON eventcode = spectrum_schema.eventcodes.code
ORDER BY  nb_events DESC;


