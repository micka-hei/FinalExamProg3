-- Supprimer les tables si elles existent (optionnel, pour nettoyer)
DROP TABLE IF EXISTS attendances;
DROP TABLE IF EXISTS activity_occupations;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS member_payments;
DROP TABLE IF EXISTS financial_accounts;
DROP TABLE IF EXISTS membership_fees;
DROP TABLE IF EXISTS collectivity_structure;
DROP TABLE IF EXISTS collectivity_members;
DROP TABLE IF EXISTS member_referees;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS collectivities;
DROP TABLE IF EXISTS sequences;

-- Table sequences
CREATE TABLE IF NOT EXISTS sequences (
    name VARCHAR(50) PRIMARY KEY,
    current_value INTEGER DEFAULT 1
);

-- Table members
CREATE TABLE IF NOT EXISTS members (
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
CREATE TABLE IF NOT EXISTS collectivities (
    id VARCHAR(50) PRIMARY KEY,
    location VARCHAR(255),
    official_number VARCHAR(50),
    official_name VARCHAR(255)
);

-- Table member_referees
CREATE TABLE IF NOT EXISTS member_referees (
    member_id VARCHAR(50),
    referee_id VARCHAR(50)
);

-- Table collectivity_members
CREATE TABLE IF NOT EXISTS collectivity_members (
    collectivity_id VARCHAR(50),
    member_id VARCHAR(50)
);

-- Table collectivity_structure
CREATE TABLE IF NOT EXISTS collectivity_structure (
    collectivity_id VARCHAR(50) PRIMARY KEY,
    president_id VARCHAR(50),
    vice_president_id VARCHAR(50),
    treasurer_id VARCHAR(50),
    secretary_id VARCHAR(50)
);

-- Table membership_fees
CREATE TABLE IF NOT EXISTS membership_fees (
    id VARCHAR(50) PRIMARY KEY,
    eligible_from DATE,
    frequency VARCHAR(20),
    amount DOUBLE,
    label VARCHAR(255),
    status VARCHAR(20)
);

-- Table financial_accounts
CREATE TABLE IF NOT EXISTS financial_accounts (
    id VARCHAR(50) PRIMARY KEY,
    type VARCHAR(20),
    amount DOUBLE,
    holder_name VARCHAR(255),
    bank_name VARCHAR(50),
    bank_code INTEGER,
    bank_branch_code INTEGER,
    bank_account_number VARCHAR(50),
    bank_account_key INTEGER,
    mobile_service VARCHAR(50),
    mobile_number VARCHAR(50)
);

-- Table member_payments
CREATE TABLE IF NOT EXISTS member_payments (
    id VARCHAR(50) PRIMARY KEY,
    member_id VARCHAR(50),
    membership_fee_id VARCHAR(50),
    amount INTEGER,
    payment_mode VARCHAR(20),
    account_credited_id VARCHAR(50),
    creation_date DATE
);

-- Table transactions
CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(50) PRIMARY KEY,
    collectivity_id VARCHAR(50),
    member_id VARCHAR(50),
    creation_date DATE,
    amount DOUBLE,
    payment_mode VARCHAR(20),
    account_credited_id VARCHAR(50)
);

-- Table activities (Bonus)
CREATE TABLE IF NOT EXISTS activities (
    id VARCHAR(50) PRIMARY KEY,
    collectivity_id VARCHAR(50),
    label VARCHAR(255),
    activity_type VARCHAR(50),
    week_ordinal INTEGER,
    day_of_week VARCHAR(10),
    executive_date DATE
);

-- Table activity_occupations (Bonus)
CREATE TABLE IF NOT EXISTS activity_occupations (
    activity_id VARCHAR(50),
    occupation VARCHAR(50)
);

-- Table attendances (Bonus)
CREATE TABLE IF NOT EXISTS attendances (
    id VARCHAR(50) PRIMARY KEY,
    activity_id VARCHAR(50),
    member_id VARCHAR(50),
    attendance_status VARCHAR(20)
);