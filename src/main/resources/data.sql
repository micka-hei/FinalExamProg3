-- Initialiser les séquences
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('collectivity', 1);
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('member', 1);
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('fee', 1);
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('payment', 1);
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('transaction', 1);
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('account', 1);
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('official_number', 1);
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('activity', 1);
MERGE INTO sequences (name, current_value) KEY(name) VALUES ('attendance', 1);

-- Insérer les membres
INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, creation_date) VALUES
('MEM-1', 'Jean', 'Rakoto', '1980-02-01', 'MALE', 'Lot II V M Ambato', 'Riziculteur', '0341234501', 'jean.rakoto@test.com', 'PRESIDENT', CURRENT_DATE),
('MEM-2', 'Marie', 'Rabe', '1982-03-05', 'FEMALE', 'Lot II F Ambato', 'Agriculteur', '0341234502', 'marie.rabe@test.com', 'VICE_PRESIDENT', CURRENT_DATE),
('MEM-3', 'Paul', 'Andrian', '1992-03-10', 'MALE', 'Lot II J Ambato', 'Collecteur', '0341234503', 'paul.andrian@test.com', 'SECRETARY', CURRENT_DATE),
('MEM-4', 'Claire', 'Razafy', '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato', 'Distributeur', '0341234504', 'claire.razafy@test.com', 'TREASURER', CURRENT_DATE),
('MEM-5', 'Thomas', 'Nomenjanahary', '1999-08-21', 'MALE', 'Lot UV 80 Ambato', 'Riziculteur', '0341234505', 'thomas.nomen@test.com', 'SENIOR', CURRENT_DATE),
('MEM-6', 'Sophie', 'Rakotomalala', '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato', 'Riziculteur', '0341234506', 'sophie.rakoto@test.com', 'SENIOR', CURRENT_DATE),
('MEM-7', 'Lucas', 'Randria', '1998-01-31', 'MALE', 'Lot UV 7 Ambato', 'Riziculteur', '0341234507', 'lucas.randria@test.com', 'SENIOR', CURRENT_DATE),
('MEM-8', 'Julie', 'Ramaroson', '1975-08-20', 'FEMALE', 'Lot UV 8 Ambato', 'Riziculteur', '0341234508', 'julie.ramaroson@test.com', 'SENIOR', CURRENT_DATE),
('MEM-9', 'Kevin', 'Andriantsima', '1990-02-01', 'MALE', 'Lot II V M Ambato', 'Riziculteur', '0341234509', 'kevin.andriantsima@test.com', 'SENIOR', CURRENT_DATE),
('MEM-10', 'Laura', 'Rakotondrazaka', '1985-03-20', 'FEMALE', 'Lot II F Ambato', 'Agriculteur', '0341234510', 'laura.rakotondrazaka@test.com', 'SENIOR', CURRENT_DATE);

-- Insérer les relations membre_parrains
INSERT INTO member_referees (member_id, referee_id) VALUES
('MEM-3', 'MEM-1'),
('MEM-3', 'MEM-2'),
('MEM-4', 'MEM-1'),
('MEM-4', 'MEM-2'),
('MEM-5', 'MEM-1'),
('MEM-5', 'MEM-2'),
('MEM-6', 'MEM-1'),
('MEM-6', 'MEM-2'),
('MEM-7', 'MEM-1'),
('MEM-7', 'MEM-2'),
('MEM-8', 'MEM-5'),
('MEM-8', 'MEM-6');

-- Insérer les collectivités
INSERT INTO collectivities (id, location, official_number, official_name) VALUES
('COL-1', 'Antananarivo', NULL, NULL),
('COL-2', 'Antananarivo', NULL, NULL);

-- Insérer les membres des collectivités
INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
('COL-1', 'MEM-1'),
('COL-1', 'MEM-2'),
('COL-1', 'MEM-3'),
('COL-1', 'MEM-4'),
('COL-1', 'MEM-5'),
('COL-1', 'MEM-6'),
('COL-1', 'MEM-7'),
('COL-1', 'MEM-8'),
('COL-2', 'MEM-9'),
('COL-2', 'MEM-10');

-- Insérer les structures des collectivités
INSERT INTO collectivity_structure (collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id) VALUES
('COL-1', 'MEM-1', 'MEM-2', 'MEM-3', 'MEM-4'),
('COL-2', 'MEM-9', 'MEM-10', 'MEM-9', 'MEM-10');

-- Insérer les comptes financiers
INSERT INTO financial_accounts (id, type, amount, holder_name, mobile_service, mobile_number) VALUES
('COL-1-CASH', 'CASH', 0.0, NULL, NULL, NULL),
('COL-2-CASH', 'CASH', 0.0, NULL, NULL, NULL),
('COL-1-MOBILE-1', 'MOBILE_BANKING', 0.0, 'Mpanorina', 'ORANGE_MONEY', '0341234567');

-- Insérer la cotisation
INSERT INTO membership_fees (id, eligible_from, frequency, amount, label, status) VALUES
('FEE-1', '2026-01-01', 'ANNUALLY', 100000.0, 'Cotisation annuelle', 'ACTIVE');

-- Insérer un compte caisse par défaut
INSERT INTO financial_accounts (id, type, amount) VALUES ('ACC-2', 'CASH', 1000000.0);