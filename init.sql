-------------------------------------------------------------------------------
--- UPA 2019
--- xmarko15 | xnanoa00 | xvasek06
-------------------------------------------------------------------------------

-- delete old tables
DROP TABLE estates;
DROP TABLE pictures;
DROP TABLE map_entities;

-- primary application spatial objects
CREATE TABLE estates(
    id NUMBER NOT NULL,
    name VARCHAR(30),
    description VARCHAR(200),
    geometry SDO_GEOMETRY,
    thumbnail NUMBER
);

-- other spatial objects such as roads/rivers/..
CREATE TABLE map_entities(
    id NUMBER NOT NULL,
    name VARCHAR(30),
    description VARCHAR(200),
    geometry SDO_GEOMETRY,
    type VARCHAR(30)
);

-- multimedia associated with estate and map entity data
CREATE TABLE pictures(
    id NUMBER NOT NULL,
    picture ORDSYS.ORDIMAGE,
    picture_si ORDSYS.SI_STILLIMAGE,
    picture_ac ORDSYS.SI_AVERAGECOLOR,
    picture_ch ORDSYS.SI_COLORHISTOGRAM,
    picture_pc ORDSYS.SI_POSITIONALCOLOR,
    picture_tx ORDSYS.SI_TEXTURE
);

-- delete old dimension grid metadata
DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME = 'estates' AND COLUMN_NAME = 'geometry';
DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME = 'map_entities' AND COLUMN_NAME = 'geometry';

-- insert 500x500 dimension grid for estates
INSERT INTO USER_SDO_GEOM_METADATA VALUES (
	'estates', 'geometry',
	SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 500, 0.01), SDO_DIM_ELEMENT('Y', 0, 500, 0.01)),
	NULL
);

-- insert 500x500 dimension grid for map entities, same as estates
INSERT INTO USER_SDO_GEOM_METADATA VALUES (
    'map_entities', 'geometry',
    SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 500, 0.01), SDO_DIM_ELEMENT('Y', 0, 500, 0.01)),
    NULL
);

-- create spatial indexes
CREATE INDEX index_estates_geometry ON estates ( geometry ) indextype is MDSYS.SPATIAL_INDEX ;
CREATE INDEX index_map_entities_geometry ON map_entities ( geometry ) indextype is MDSYS.SPATIAL_INDEX ;



COMMIT;