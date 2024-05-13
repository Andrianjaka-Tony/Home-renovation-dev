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
  PRIMARY KEY (_id)
);
CREATE SEQUENCE _unit_sequence;


CREATE TABLE _client (
  _id VARCHAR,
  _name VARCHAR NOT NULL DEFAULT 'John Doe',
  _contact VARCHAR NOT NULL,
  PRIMARY KEY (_id)
);
CREATE SEQUENCE _client_sequence;


CREATE TABLE _house (
  _id VARCHAR,
  _name VARCHAR NOT NULL,
  _duration NUMERIC NOT NULL DEFAULT 0,
  PRIMARY KEY (_id)
);
CREATE SEQUENCE _house_sequence;


CREATE TABLE _finishing_type (
  _id VARCHAR,
  _name VARCHAR NOT NULL,
  _description VARCHAR NOT NULL,
  _augmentation NUMERIC NOT NULL DEFAULT 0,
  PRIMARY KEY (_id)
);
CREATE SEQUENCE _finishing_type_sequence;


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
  PRIMARY KEY (_id),
  FOREIGN KEY (_client) REFERENCES _client(_id),
  FOREIGN KEY (_house) REFERENCES _house(_id),
  FOREIGN KEY (_finishing_type) REFERENCES _finishing_type(_id)
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


CREATE OR REPLACE VIEW _v_main_house AS
  SELECT 
    _house._id,
    _house._name,
    _house._duration,
    SUM(_v_main_house_details._quantity * _v_main_house_details._work_price) AS _price,
    _house._description
  FROM
    _house
    LEFT JOIN
      _v_main_house_details ON _v_main_house_details._house = _house._id
    GROUP BY
      _house._id,
      _house._name,
      _house._duration,
      _house._description;


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
CREATE VIEW _v_main_client_contract AS
  SELECT
    _client_contract.*,
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
    _v_client_contract_payed._amount AS _payed
  FROM
    _client_contract
    LEFT JOIN
      _client ON _client._id = _client_contract._client
    LEFT JOIN
      _house ON _house._id = _client_contract._house
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


CREATE VIEW s AS
  SELECT
    DISTINCT TO_CHAR(_date, 'YYYY-MM') AS _month_year
  FROM
    _client_contract
    ORDER BY _month_year ASC;


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
