DROP TABLE estates;
DROP TABLE map_entities;
DROP TABLE pictures;

-- primary application spatial objects
CREATE TABLE estates(
    id NUMBER PRIMARY KEY,
    name VARCHAR(30),
    description VARCHAR(200),
    price NUMBER,
    type VARCHAR(50),
    transaction VARCHAR(50),
);

-- other spatial objects such as roads/rivers/..
CREATE TABLE map_entities(
    id NUMBER PRIMARY KEY,
    estateId NUMBER,
    name VARCHAR(30),
    description VARCHAR(200),
    shape SDO_GEOMETRY,
    type VARCHAR(30)
);

-- multimedia associated with estate and map entity data
CREATE TABLE pictures(
    id NUMBER PRIMARY KEY,
    estateId NUMBER,
    picture ORDSYS.ORDIMAGE,
    picture_si ORDSYS.SI_STILLIMAGE,
    picture_ac ORDSYS.SI_AVERAGECOLOR,
    picture_ch ORDSYS.SI_COLORHISTOGRAM,
    picture_pc ORDSYS.SI_POSITIONALCOLOR,
    picture_tx ORDSYS.SI_TEXTURE
);

DELETE user_sdo_geom_metadata WHERE TABLE_NAME = 'MAP_ENTITIES' AND COLUMN_NAME = 'SHAPE';

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
CREATE INDEX index_map_entities_geometry ON map_entities ( shape ) indextype is MDSYS.SPATIAL_INDEX ;

COMMIT;