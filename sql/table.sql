CREATE DATABASE _home_renovation;
\c _home_renovation;


CREATE TABLE _role (
  _id VARCHAR,
  _name VARCHAR NOT NULL,
  PRIMARY KEY (_id)
);
INSERT INTO _role
(_id, _name)
VALUES
('ROLE01', 'Administrateur'),
('ROLE02', 'Client');


CREATE TABLE _user (
  _id VARCHAR,
  _first_name VARCHAR NOT NULL,
  _last_name VARCHAR NOT NULL,
  _email VARCHAR NOT NULL,
  _password VARCHAR NOT NULL,
  _role VARCHAR NOT NULL,
  PRIMARY KEY (_id),
  UNIQUE (_email),
  FOREIGN KEY (_role) REFERENCES _role(_id)
);
CREATE SEQUENCE _user_sequence;
CREATE VIEW _v_main_user AS
  SELECT
    _user.*,
    _role._id AS _role_id,
    _role._name AS _role_name
  FROM
    _user
    LEFT JOIN
      _role ON _role._id = _user._role;

  
CREATE TABLE _unit (
  _id VARCHAR,
  _name VARCHAR NOT NULL,
  PRIMARY KEY (_id),
  UNIQUE (_name)
);
CREATE SEQUENCE _unit_sequence;


CREATE TABLE _client (
  _id VARCHAR,
  _name VARCHAR NOT NULL DEFAULT 'John Doe',
  _contact VARCHAR NOT NULL,
  PRIMARY KEY (_id)
);
CREATE SEQUENCE _client_sequence;


CREATE TABLE _location (
  _id VARCHAR,
  _name VARCHAR NOT NULL DEFAULT 'Hello world',
  PRIMARY KEY (_id),
  UNIQUE (_location)
);
CREATE SEQUENCE _location_sequence;


CREATE TABLE _house (
  _id VARCHAR,
  _name VARCHAR NOT NULL,
  _duration NUMERIC NOT NULL DEFAULT 0,
  _description VARCHAR NOT NULL,
  _area NUMERIC NOT NULL DEFAULT 0,
  PRIMARY KEY (_id)
);
CREATE SEQUENCE _house_sequence;


CREATE TABLE _finishing_type (
  _id VARCHAR,
  _name VARCHAR NOT NULL,
  _augmentation NUMERIC NOT NULL DEFAULT 0,
  PRIMARY KEY (_id)
);
CREATE SEQUENCE _finishing_type_sequence;
INSERT INTO _finishing_type
(_id, _name, _augmentation)
VALUES
('F01', 'Standard', 0),
('F02', 'VIP', 10),
('F03', 'Gold', 20),
('F04', 'Prenium', 40);


CREATE TABLE _work (
  _id VARCHAR,
  _name VARCHAR NOT NULL,
  _price NUMERIC NOT NULL DEFAULT 0,
  _unit VARCHAR NOT NULL,
  _parent VARCHAR,
  PRIMARY KEY (_id),
  FOREIGN KEY (_unit) REFERENCES _unit(_id),
  FOREIGN KEY (_parent) REFERENCES _work(_id)
);
CREATE SEQUENCE _work_sequence;
CREATE VIEW _v_main_work AS
  SELECT
    _work.*,
    _unit._id AS _unit_id,
    _unit._name AS _unit_name,
    _parent._id AS _parent_id,
    _parent._name AS _parent_name
  FROM
    _work
    LEFT JOIN
      _unit ON _unit._id = _work._unit
    LEFT JOIN
      _work AS _parent ON _parent._id = _work._parent;


CREATE TABLE _house_details (
  _id VARCHAR,
  _quantity NUMERIC NOT NULL DEFAULT 0,
  _house VARCHAR NOT NULL,
  _work VARCHAR NOT NULL,
  PRIMARY KEY (_id),
  FOREIGN KEY (_work) REFERENCES _work(_id),
  FOREIGN KEY (_house) REFERENCES _house(_id),
  UNIQUE (_house, _work)
);
CREATE SEQUENCE _house_details_sequence;
CREATE VIEW _v_main_house_details AS
  SELECT
    _house_details.*,
    _house._id AS _house_id,
    _house._name AS _house_name,
    _house._duration AS _house_duration,
    _work._id AS _work_id,
    _work._name AS _work_name,
    _work._price AS _work_price,
  FROM
    _house_details
    LEFT JOIN
      _house ON _house._id = _house_details._house
    LEFT JOIN
      _work ON _work._id = _house_details._work;


CREATE TABLE _client_contract (
  _id VARCHAR,
  _date DATE NOT NULL,
  _begin TIMESTAMP NOT NULL,
  _end TIMESTAMP NOT NULL,
  _client VARCHAR NOT NULL,
  _house VARCHAR NOT NULL,
  _finishing_type VARCHAR NOT NULL,
  _finishing_augmentation NUMERIC NOT NULL,
  _location VARCHAR NOT NULL,
  PRIMARY KEY (_id),
  FOREIGN KEY (_client) REFERENCES _client(_id),
  FOREIGN KEY (_house) REFERENCES _house(_id),
  FOREIGN KEY (_finishing_type) REFERENCES _finishing_type(_id),
  FOREIGN KEY (_location) REFERENCES _location(_id)
);
CREATE SEQUENCE _client_contract_sequence;


CREATE TABLE _client_payment (
  _id VARCHAR,
  _contract VARCHAR NOT NULL,
  _amount NUMERIC NOT NULL,
  _date DATE NOT NULL,
  PRIMARY KEY (_id),
  FOREIGN KEY (_contract) REFERENCES _client_contract(_id)
);
CREATE SEQUENCE _client_payment_sequence;
CREATE VIEW _v_main_client_payment AS
  SELECT
    _client_payment.*,
    _contract._id AS _contract_id,
    _contract._begin AS _contract_begin,
    _contract._end AS _contract_end
  FROM
    _client_payment
    LEFT JOIN
      _client_contract AS _contract ON _contract._id = _client_payment._contract;
CREATE VIEW _v_client_payment_total AS
  SELECT
    SUM(_amount) AS _amount
  FROM
    _client_payment;


CREATE OR REPLACE VIEW _v_main_house AS
  SELECT 
    _house._id,
    _house._name,
    _house._duration,
    SUM(_v_main_house_details._quantity * _v_main_house_details._work_price) AS _price,
    _house._description,
    _house._area
  FROM
    _house
    LEFT JOIN
      _v_main_house_details ON _v_main_house_details._house = _house._id
    GROUP BY
      _house._id,
      _house._name,
      _house._duration,
      _house._description,
      _house._area;


CREATE TABLE _contract_details (
  _id VARCHAR,
  _quantity NUMERIC NOT NULL,
  _unit_price NUMERIC NOT NULL,
  _contract VARCHAR NOT NULL,
  _work VARCHAR NOT NULL,
  PRIMARY KEY (_id),
  FOREIGN KEY (_contract) REFERENCES _client_contract(_id),
  FOREIGN KEY (_work) REFERENCES _work(_id)
);
CREATE SEQUENCE _contract_details_sequence;
CREATE VIEW _v_main_contract_details AS
  SELECT
    _contract_details.*,
    _work._id AS _work_id,
    _work._name AS _work_name,
    _unit._name AS _unit_name
  FROM
    _contract_details
    LEFT JOIN
      _work ON _work._id = _contract_details._work
    LEFT JOIN
      _unit ON _unit._id = _work._unit;


CREATE VIEW _v_client_contract_price AS
  SELECT
    _client_contract._id,
    SUM(_contract_details._quantity * _contract_details._unit_price) * (100 + _client_contract._finishing_augmentation) / 100 AS _price
  FROM
    _client_contract
    LEFT JOIN
      _contract_details ON _contract_details._contract = _client_contract._id
    GROUP BY
      _client_contract._id;
CREATE VIEW _v_client_contract_payed AS
  SELECT
    _client_contract._id,
    SUM(_client_payment._amount) AS _amount
  FROM
    _client_contract
    LEFT JOIN
      _client_payment ON _client_payment._contract = _client_contract._id
    GROUP BY
      _client_contract._id;
CREATE OR REPLACE VIEW _v_main_client_contract AS
  SELECT
    _client_contract._id,
    _client_contract._begin,
    _client_contract._end,
    _client_contract._client,
    _client_contract._house,
    _client_contract._finishing_type,
    _client_contract._date,
    _client_contract._finishing_augmentation,
    _client._id AS _client_id,
    _client._name AS _client_name,
    _client._contact AS _client_contact,
    _house._id AS _house_id,
    _house._name AS _house_name,
    _house._duration AS _house_duration,
    _finishing_type._id AS _finishing_type_id,
    _finishing_type._name AS _finishing_type_name,
    _finishing_type._augmentation AS _finishing_type_augmentation,
    _v_client_contract_price._price AS _price,
    _v_client_contract_payed._amount AS _payed,
    _location._id AS _location_id,
    _location._name AS _location_name
  FROM
    _client_contract
    LEFT JOIN
      _client ON _client._id = _client_contract._client
    LEFT JOIN
      _house ON _house._id = _client_contract._house
    LEFT JOIN 
      _location ON _location._id = _client_contract._location
    LEFT JOIN
      _finishing_type ON _finishing_type._id = _client_contract._finishing_type
    LEFT JOIN
      _v_client_contract_price ON _v_client_contract_price._id = _client_contract._id
    LEFT JOIN
      _v_client_contract_payed ON _v_client_contract_payed._id = _client_contract._id;


CREATE VIEW _v_current_client_contract AS
  SELECT
    *
  FROM
    _v_main_client_contract
    WHERE
      _begin <= CURRENT_TIMESTAMP AND _end >= CURRENT_TIMESTAMP;


CREATE VIEW _v_client_contract_total_price AS
  SELECT
    SUM(_v_main_client_contract._price) AS _price
  FROM
    _v_main_client_contract;


CREATE VIEW _v_client_contract_month_year AS
  SELECT
    DISTINCT TO_CHAR(_date, 'YYYY-MM') AS _month_year
  FROM
    _client_contract
    ORDER BY _month_year ASC;
CREATE VIEW _v_client_contract_year AS
  SELECT
    DISTINCT TO_CHAR(_date, 'YYYY') AS _year
  FROM
    _client_contract
    ORDER BY _year;


CREATE VIEW _v_client_contract_amount_month_year AS
  SELECT
    _v_client_contract_month_year._month_year,
    SUM(_v_main_client_contract._price) AS _price
  FROM
    _v_main_client_contract
    JOIN
      _v_client_contract_month_year ON _v_client_contract_month_year._month_year = TO_CHAR(_v_main_client_contract._date, 'YYYY-MM')
    GROUP BY 
      _v_client_contract_month_year._month_year
    ORDER BY
      _v_client_contract_month_year._month_year ASC;


CREATE TABLE _house_work_temp (
  _house VARCHAR,
  _description VARCHAR,
  _area NUMERIC,
  _work_id VARCHAR,
  _work_name VARCHAR,
  _unit_name VARCHAR,
  _unit_price NUMERIC,
  _quantity NUMERIC,
  _duration NUMERIC
);


CREATE VIEW _v_house_work_temp_unit AS
  SELECT
    DISTINCT(_unit_name) AS _unit
  FROM
    _house_work_temp;
CREATE VIEW _v_house_work_temp_unit_unsaved AS
  SELECT
    _v_house_work_temp_unit._unit
  FROM
    _v_house_work_temp_unit
    LEFT JOIN
      _unit ON _unit._name = _v_house_work_temp_unit._unit
    WHERE _unit._name IS NULL;


CREATE VIEW _v_house_work_temp_work AS
  SELECT
    DISTINCT(_work_id) AS _work
  FROM
    _house_work_temp;
CREATE VIEW _v_house_work_temp_unsaved AS
  SELECT
    _v_house_work_temp_work._work
  FROM
    _v_house_work_temp_work
    LEFT JOIN
      _work ON _work._id = _v_house_work_temp_work._work
    WHERE
      _work._id IS NULL;
CREATE VIEW _v_house_work_temp_work_to_save AS
  SELECT
    DISTINCT(_v_house_work_temp_unsaved._work) AS _id,
    _house_work_temp._work_name AS _name,
    _unit._id AS _unit,
    _house_work_temp._unit_price AS _unit_price
  FROM
    _v_house_work_temp_unsaved
    LEFT JOIN
      _house_work_temp ON _house_work_temp._work_id = _v_house_work_temp_unsaved._work
    LEFT JOIN
      _unit ON _unit._name = _house_work_temp._unit_name;


CREATE VIEW _v_house_work_temp_house_unsaved AS
  SELECT
    DISTINCT(_house) AS _house,
    _house_work_temp._description,
    _house_work_temp._area,
    _house_work_temp._duration,
    _house._id
  FROM
    _house_work_temp
    LEFT JOIN 
      _house ON _house._name = _house_work_temp._house
    WHERE
      _house._id IS NULL;


CREATE VIEW _v_house_work_temp_house_details_unsaved AS
  SELECT
    DISTINCT(_house._id) AS _house,
    _house_work_temp._quantity AS _quantity,
    _house_work_temp._work_id AS _work
  FROM
    _house_work_temp
    LEFT JOIN
      _house ON _house._name = _house_work_temp._house;


CREATE TABLE _contract_temp (
  _client VARCHAR,
  _id VARCHAR,
  _house VARCHAR,
  _finishing_name VARCHAR,
  _augmentation VARCHAR,
  _date VARCHAR,
  _begin VARCHAR,
  _location VARCHAR
);


CREATE VIEW _v_contract_temp_location AS
  SELECT
    DISTINCT(_location) AS _name
  FROM
    _contract_temp;
CREATE VIEW _v_contract_temp_location_unsaved AS
  SELECT
    _v_contract_temp_location._name
  FROM
    _v_contract_temp_location
    LEFT JOIN
      _location ON _location._name = _v_contract_temp_location._name
    WHERE _location._id IS NULL;


CREATE VIEW _v_contract_temp_contract_unsaved AS
  SELECT
    _contract_temp._id,
    _contract_temp._begin,
    _client._id AS _client,
    _house._id AS _house,
    _finishing_type._id AS _finishing_type,
    _contract_temp._date,
    _contract_temp._augmentation AS _augmentation,
    _location._id AS _location
  FROM
    _contract_temp
    LEFT JOIN
      _client ON _client._contact = _contract_temp._client
    LEFT JOIN
      _house ON _house._name = _contract_temp._house
    LEFT JOIN
      _finishing_type ON _finishing_type._name = _contract_temp._finishing_name
    LEFT JOIN
      _location ON _location._name = _contract_temp._location;


CREATE TABLE _payment_temp (
  _contract VARCHAR,
  _payment VARCHAR,
  _date VARCHAR,
  _amount VARCHAR
);
