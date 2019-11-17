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
    id NUMBER PRIMARY KEY,
    name VARCHAR(30),
    description VARCHAR(200),
    shape SDO_GEOMETRY,
    thumbnail NUMBER
);

-- other spatial objects such as roads/rivers/..
CREATE TABLE map_entities(
    id NUMBER PRIMARY KEY,
    name VARCHAR(30),
    description VARCHAR(200),
    shape SDO_GEOMETRY,
    type VARCHAR(30)
);

-- multimedia associated with estate and map entity data
CREATE TABLE pictures(
    id NUMBER PRIMARY KEY,
    picture ORDSYS.ORDIMAGE,
    picture_si ORDSYS.SI_STILLIMAGE,
    picture_ac ORDSYS.SI_AVERAGECOLOR,
    picture_ch ORDSYS.SI_COLORHISTOGRAM,
    picture_pc ORDSYS.SI_POSITIONALCOLOR,
    picture_tx ORDSYS.SI_TEXTURE
);

-- delete old dimension grid metadata
DELETE FROM user_sdo_geom_metadata WHERE TABLE_NAME = 'estates' AND COLUMN_NAME = 'shape';
DELETE FROM user_sdo_geom_metadata WHERE TABLE_NAME = 'map_entities' AND COLUMN_NAME = 'shape';

-- insert 500x500 dimension grid for estates
INSERT INTO user_sdo_geom_metadata
    (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
    VALUES (
	'estates',
	'shape',
	SDO_DIM_ARRAY( -- 500x500 grid
	    SDO_DIM_ELEMENT('X', 0, 500, 0.01),
	    SDO_DIM_ELEMENT('Y', 0, 500, 0.01)
	),
	NULL -- SRID
);

-- insert 500x500 dimension grid for map entities, same as estates
INSERT INTO user_sdo_geom_metadata
    (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
    VALUES (
	'map_entities',
	'shape',
	SDO_DIM_ARRAY( -- 500x500 grid
	    SDO_DIM_ELEMENT('X', 0, 500, 0.01),
	    SDO_DIM_ELEMENT('Y', 0, 500, 0.01)
	),
	NULL -- SRID
);

-- create spatial indexes
CREATE INDEX index_estates_geometry ON estates ( geometry ) indextype is MDSYS.SPATIAL_INDEX ;
CREATE INDEX index_map_entities_geometry ON map_entities ( geometry ) indextype is MDSYS.SPATIAL_INDEX ;

COMMIT;