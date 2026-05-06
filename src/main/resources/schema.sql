-- Supprimer les tables si elles existent
DROP TABLE IF EXISTS attendances CASCADE;
DROP TABLE IF EXISTS activity_occupations CASCADE;
DROP TABLE IF EXISTS activities CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS member_payments CASCADE;
DROP TABLE IF EXISTS financial_accounts CASCADE;
DROP TABLE IF EXISTS membership_fees CASCADE;
DROP TABLE IF EXISTS collectivity_structure CASCADE;
DROP TABLE IF EXISTS collectivity_members CASCADE;
DROP TABLE IF EXISTS member_referees CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS collectivities CASCADE;
DROP TABLE IF EXISTS sequences CASCADE;

-- Table sequences
CREATE TABLE sequences (
    name VARCHAR(50) PRIMARY KEY,
    current_value INTEGER DEFAULT 1
);

-- Table members
CREATE TABLE members (
    id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    birth_date DATE,
    gender VARCHAR(10),
    address VARCHAR(255),
    profession VARCHAR(100),
    phone_number VARCHAR(50),
    email VARCHAR(100),
    occupation VARCHAR(50),
    creation_date DATE DEFAULT CURRENT_DATE
);

-- Table collectivities
CREATE TABLE collectivities (
    id VARCHAR(50) PRIMARY KEY,
    location VARCHAR(255),
    official_number VARCHAR(50),
    official_name VARCHAR(255)
);

-- Table member_referees
CREATE TABLE member_referees (
    member_id VARCHAR(50),
    referee_id VARCHAR(50)
);

-- Table collectivity_members
CREATE TABLE collectivity_members (
    collectivity_id VARCHAR(50),
    member_id VARCHAR(50)
);

-- Table collectivity_structure
CREATE TABLE collectivity_structure (
    collectivity_id VARCHAR(50) PRIMARY KEY,
    president_id VARCHAR(50),
    vice_president_id VARCHAR(50),
    treasurer_id VARCHAR(50),
    secretary_id VARCHAR(50)
);

-- Table membership_fees (DOUBLE → DOUBLE PRECISION)
CREATE TABLE membership_fees (
    id VARCHAR(50) PRIMARY KEY,
    eligible_from DATE,
    frequency VARCHAR(20),
    amount DOUBLE PRECISION,
    label VARCHAR(255),
    status VARCHAR(20)
);

-- Table financial_accounts (DOUBLE → DOUBLE PRECISION)
CREATE TABLE financial_accounts (
    id VARCHAR(50) PRIMARY KEY,
    type VARCHAR(20),
    amount DOUBLE PRECISION,
    holder_name VARCHAR(255),
    bank_name VARCHAR(50),
    bank_code INTEGER,
    bank_branch_code INTEGER,
    bank_account_number VARCHAR(50),
    bank_account_key INTEGER,
    mobile_service VARCHAR(50),
    mobile_number VARCHAR(50),
    collectivity_id VARCHAR(50)
);

-- Table member_payments
CREATE TABLE member_payments (
    id VARCHAR(50) PRIMARY KEY,
    member_id VARCHAR(50),
    membership_fee_id VARCHAR(50),
    amount INTEGER,
    payment_mode VARCHAR(20),
    account_credited_id VARCHAR(50),
    creation_date DATE
);

-- Table transactions (DOUBLE → DOUBLE PRECISION)
CREATE TABLE transactions (
    id VARCHAR(50) PRIMARY KEY,
    collectivity_id VARCHAR(50),
    member_id VARCHAR(50),
    creation_date DATE,
    amount DOUBLE PRECISION,
    payment_mode VARCHAR(20),
    account_credited_id VARCHAR(50)
);

-- Table activities (Bonus)
CREATE TABLE activities (
    id VARCHAR(50) PRIMARY KEY,
    collectivity_id VARCHAR(50),
    label VARCHAR(255),
    activity_type VARCHAR(50),
    week_ordinal INTEGER,
    day_of_week VARCHAR(10),
    executive_date DATE
);

-- Table activity_occupations
CREATE TABLE activity_occupations (
    activity_id VARCHAR(50),
    occupation VARCHAR(50)
);

-- Table attendances
CREATE TABLE attendances (
    id VARCHAR(50) PRIMARY KEY,
    activity_id VARCHAR(50),
    member_id VARCHAR(50),
    attendance_status VARCHAR(20)
);