-- 1. COLLECTIVITÉS
INSERT INTO collectivities (id, location, official_number, official_name) VALUES
    ('col-1', 'Ambatondrazaka', NULL, NULL),
    ('col-2', 'Ambatondrazaka', NULL, NULL),
    ('col-3', 'Brickaville', NULL, NULL);

-- 2. MEMBRES COLLECTIVITÉ 1
INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, creation_date) VALUES
    ('C1-M1', 'Nom1', 'Prénom1', '1980-02-01', 'MALE', 'Lot II V M Ambato', 'Riziculteur', '0341234567', 'member.1@fed-agri.mg', 'PRESIDENT', '2026-01-01'),
    ('C1-M2', 'Nom2', 'Prénom2', '1982-03-05', 'MALE', 'Lot II F Ambato', 'Agriculteur', '0321234567', 'member.2@fed-agri.mg', 'VICE_PRESIDENT', '2026-01-01'),
    ('C1-M3', 'Nom3', 'Prénom3', '1992-03-10', 'MALE', 'Lot II J Ambato', 'Collecteur', '0331234567', 'member.3@fed-agri.mg', 'SECRETARY', '2026-01-01'),
    ('C1-M4', 'Nom4', 'Prénom4', '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato', 'Distributeur', '0381234567', 'member.4@fed-agri.mg', 'TREASURER', '2026-01-01'),
    ('C1-M5', 'Nom5', 'Prénom5', '1999-08-21', 'MALE', 'Lot UV 80 Ambato', 'Riziculteur', '0373434567', 'member.5@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C1-M6', 'Nom6', 'Prénom6', '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato', 'Riziculteur', '0372234567', 'member.6@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C1-M7', 'Nom7', 'Prénom7', '1998-01-31', 'MALE', 'Lot UV 7 Ambato', 'Riziculteur', '0374234567', 'member.7@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C1-M8', 'Nom8', 'Prénom8', '1975-08-20', 'MALE', 'Lot UV 8 Ambato', 'Riziculteur', '0370234567', 'member.8@fed-agri.mg', 'SENIOR', '2026-01-01');

-- 3. MEMBRES COLLECTIVITÉ 2
INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, creation_date) VALUES
    ('C2-M1', 'Nom1', 'Prénom1', '1980-02-01', 'MALE', 'Lot II V M Ambato', 'Riziculteur', '0341234567', 'member.1@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C2-M2', 'Nom2', 'Prénom2', '1982-03-05', 'MALE', 'Lot II F Ambato', 'Agriculteur', '0321234567', 'member.2@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C2-M3', 'Nom3', 'Prénom3', '1992-03-10', 'MALE', 'Lot II J Ambato', 'Collecteur', '0331234567', 'member.3@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C2-M4', 'Nom4', 'Prénom4', '1988-05-22', 'FEMALE', 'Lot A K 50 Ambato', 'Distributeur', '0381234567', 'member.4@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C2-M5', 'Nom5', 'Prénom5', '1999-08-21', 'MALE', 'Lot UV 80 Ambato', 'Riziculteur', '0373434567', 'member.5@fed-agri.mg', 'PRESIDENT', '2026-01-01'),
    ('C2-M6', 'Nom6', 'Prénom6', '1998-08-22', 'FEMALE', 'Lot UV 6 Ambato', 'Riziculteur', '0372234567', 'member.6@fed-agri.mg', 'VICE_PRESIDENT', '2026-01-01'),
    ('C2-M7', 'Nom7', 'Prénom7', '1998-01-31', 'MALE', 'Lot UV 7 Ambato', 'Riziculteur', '0374234567', 'member.7@fed-agri.mg', 'SECRETARY', '2026-01-01'),
    ('C2-M8', 'Nom8', 'Prénom8', '1975-08-20', 'MALE', 'Lot UV 8 Ambato', 'Riziculteur', '0370234567', 'member.8@fed-agri.mg', 'TREASURER', '2026-01-01');

-- 4. MEMBRES COLLECTIVITÉ 3
INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, creation_date) VALUES
    ('C3-M1', 'Nom9', 'Prénom9', '1988-01-02', 'MALE', 'Lot 33 J Antsirabe', 'Apiculteur', '034034567', 'member.9@fed-agri.mg', 'PRESIDENT', '2026-01-01'),
    ('C3-M2', 'Nom10', 'Prénom10', '1982-03-05', 'MALE', 'Lot 2 J Antsirabe', 'Agriculteur', '0338634567', 'member.10@fed-agri.mg', 'VICE_PRESIDENT', '2026-01-01'),
    ('C3-M3', 'Nom11', 'Prénom11', '1992-03-12', 'MALE', 'Lot 8 KM Antsirabe', 'Collecteur', '0338234567', 'member.11@fed-agri.mg', 'SECRETARY', '2026-01-01'),
    ('C3-M4', 'Nom12', 'Prénom12', '1988-05-10', 'FEMALE', 'Lot A K 50 Antsirabe', 'Distributeur', '0382334567', 'member.12@fed-agri.mg', 'TREASURER', '2026-01-01'),
    ('C3-M5', 'Nom13', 'Prénom13', '1999-08-11', 'MALE', 'Lot UV 80 Antsirabe', 'Apiculteur', '0373365567', 'member.13@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C3-M6', 'Nom14', 'Prénom14', '1998-08-09', 'FEMALE', 'Lot UV 6 Antsirabe', 'Apiculteur', '0378234567', 'member.14@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C3-M7', 'Nom15', 'Prénom15', '1998-01-13', 'MALE', 'Lot UV 7 Antsirabe', 'Apiculteur', '0374914567', 'member.15@fed-agri.mg', 'SENIOR', '2026-01-01'),
    ('C3-M8', 'Nom16', 'Prénom16', '1975-08-02', 'MALE', 'Lot UV 8 Antsirabe', 'Apiculteur', '0370634567', 'member.16@fed-agri.mg', 'SENIOR', '2026-01-01');

-- 5. RELATIONS PARRAINS
-- Collectivité 1
INSERT INTO member_referees (member_id, referee_id) VALUES
    ('C1-M3', 'C1-M1'), ('C1-M3', 'C1-M2'),
    ('C1-M4', 'C1-M1'), ('C1-M4', 'C1-M2'),
    ('C1-M5', 'C1-M1'), ('C1-M5', 'C1-M2'),
    ('C1-M6', 'C1-M1'), ('C1-M6', 'C1-M2'),
    ('C1-M7', 'C1-M1'), ('C1-M7', 'C1-M2'),
    ('C1-M8', 'C1-M6'), ('C1-M8', 'C1-M7');

-- Collectivité 2
INSERT INTO member_referees (member_id, referee_id) VALUES
    ('C2-M3', 'C1-M1'), ('C2-M3', 'C1-M2'),
    ('C2-M4', 'C1-M1'), ('C2-M4', 'C1-M2'),
    ('C2-M5', 'C1-M1'), ('C2-M5', 'C1-M2'),
    ('C2-M6', 'C1-M1'), ('C2-M6', 'C1-M2'),
    ('C2-M7', 'C1-M1'), ('C2-M7', 'C1-M2'),
    ('C2-M8', 'C1-M6'), ('C2-M8', 'C1-M7');

-- Collectivité 3
INSERT INTO member_referees (member_id, referee_id) VALUES
    ('C3-M1', 'C1-M1'), ('C3-M1', 'C1-M2'),
    ('C3-M2', 'C1-M1'), ('C3-M2', 'C1-M2'),
    ('C3-M3', 'C3-M1'), ('C3-M3', 'C3-M2'),
    ('C3-M4', 'C3-M1'), ('C3-M4', 'C3-M2'),
    ('C3-M5', 'C3-M1'), ('C3-M5', 'C3-M2'),
    ('C3-M6', 'C3-M1'), ('C3-M6', 'C3-M2'),
    ('C3-M7', 'C3-M1'), ('C3-M7', 'C3-M2'),
    ('C3-M8', 'C3-M1'), ('C3-M8', 'C3-M2');

-- 6. MEMBRES DES COLLECTIVITÉS
INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
    ('col-1', 'C1-M1'), ('col-1', 'C1-M2'), ('col-1', 'C1-M3'), ('col-1', 'C1-M4'),
    ('col-1', 'C1-M5'), ('col-1', 'C1-M6'), ('col-1', 'C1-M7'), ('col-1', 'C1-M8'),
    ('col-2', 'C2-M1'), ('col-2', 'C2-M2'), ('col-2', 'C2-M3'), ('col-2', 'C2-M4'),
    ('col-2', 'C2-M5'), ('col-2', 'C2-M6'), ('col-2', 'C2-M7'), ('col-2', 'C2-M8'),
    ('col-3', 'C3-M1'), ('col-3', 'C3-M2'), ('col-3', 'C3-M3'), ('col-3', 'C3-M4'),
    ('col-3', 'C3-M5'), ('col-3', 'C3-M6'), ('col-3', 'C3-M7'), ('col-3', 'C3-M8');

-- 7. STRUCTURES DES COLLECTIVITÉS
INSERT INTO collectivity_structure (collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id) VALUES
    ('col-1', 'C1-M1', 'C1-M2', 'C1-M3', 'C1-M4'),
    ('col-2', 'C2-M5', 'C2-M6', 'C2-M7', 'C2-M8'),
    ('col-3', 'C3-M1', 'C3-M2', 'C3-M3', 'C3-M4');

-- 8. COMPTES FINANCIERS DE BASE
INSERT INTO financial_accounts (id, type, amount, collectivity_id) VALUES
    ('C1-A-CASH', 'CASH', 0, 'col-1'),
    ('C2-A-CASH', 'CASH', 0, 'col-2'),
    ('C3-A-CASH', 'CASH', 0, 'col-3');

INSERT INTO financial_accounts (id, type, amount, holder_name, mobile_service, mobile_number, collectivity_id) VALUES
    ('C1-A-MOBILE-1', 'MOBILE_BANKING', 0, 'Mpanorina', 'ORANGE_MONEY', '0370489612', 'col-1'),
    ('C2-A-MOBILE-1', 'MOBILE_BANKING', 0, 'Dobo voalohany', 'ORANGE_MONEY', '0320489612', 'col-2');

-- 9. NOUVEAUX COMPTES POUR COLLECTIVITÉ 3
INSERT INTO financial_accounts (id, type, amount, holder_name, bank_name, bank_code, bank_branch_code, bank_account_number, bank_account_key, collectivity_id) VALUES
    ('C3-A-BANK-1', 'BANK_ACCOUNT', 0, 'Koto', 'BMOI', 4, 1, '12345678901', 23, 'col-3'),
    ('C3-A-BANK-2', 'BANK_ACCOUNT', 0, 'Naivo', 'BRED', 8, 3, '45678901235', 89, 'col-3');

INSERT INTO financial_accounts (id, type, amount, holder_name, mobile_service, mobile_number, collectivity_id) VALUES
    ('C3-A-MOBILE-1', 'MOBILE_BANKING', 0, 'Kolo', 'MVOLA', '0341889612', 'col-3');

-- 10. COTISATIONS (mise à jour et nouvelles)
UPDATE membership_fees SET amount = 200000 WHERE id = 'cot-1';
UPDATE membership_fees SET amount = 200000 WHERE id = 'cot-2';
UPDATE membership_fees SET amount = 50000 WHERE id = 'cot-3';

INSERT INTO membership_fees (id, eligible_from, frequency, amount, label, status) VALUES
    ('cot-4', '2026-04-30', 'PUNCTUALLY', 20000, 'Famangiana', 'ACTIVE'),
    ('cot-5', '2026-01-01', 'ANNUALLY', 200000, 'Cotisation annuelle', 'ACTIVE'),
    ('cot-6', '2025-01-01', 'ANNUALLY', 100000, 'Cotisation 2025', 'INACTIVE'),
    ('cot-7', '2026-04-01', 'MONTHLY', 25000, 'Cotisation mensuelle', 'ACTIVE');

-- Associer les cotisations aux collectivités
-- 11. PAIEMENTS COLLECTIVITÉ 1
INSERT INTO member_payments (id, member_id, membership_fee_id, amount, payment_mode, account_credited_id, creation_date) VALUES
    (gen_random_uuid()::text, 'C1-M1', 'cot-1', 200000, 'CASH', 'C1-A-CASH', '2026-01-01'),
    (gen_random_uuid()::text, 'C1-M2', 'cot-1', 200000, 'CASH', 'C1-A-CASH', '2026-01-01'),
    (gen_random_uuid()::text, 'C1-M3', 'cot-1', 200000, 'MOBILE_BANKING', 'C1-A-MOBILE-1', '2026-01-01'),
    (gen_random_uuid()::text, 'C1-M4', 'cot-1', 200000, 'MOBILE_BANKING', 'C1-A-MOBILE-1', '2026-01-01'),
    (gen_random_uuid()::text, 'C1-M5', 'cot-1', 150000, 'MOBILE_BANKING', 'C1-A-MOBILE-1', '2026-01-01'),
    (gen_random_uuid()::text, 'C1-M6', 'cot-1', 100000, 'CASH', 'C1-A-CASH', '2026-05-01'),
    (gen_random_uuid()::text, 'C1-M7', 'cot-1', 60000, 'CASH', 'C1-A-CASH', '2026-05-01'),
    (gen_random_uuid()::text, 'C1-M8', 'cot-1', 90000, 'CASH', 'C1-A-CASH', '2026-05-01');

-- 12. PAIEMENTS COLLECTIVITÉ 2
INSERT INTO member_payments (id, member_id, membership_fee_id, amount, payment_mode, account_credited_id, creation_date) VALUES
    (gen_random_uuid()::text, 'C2-M1', 'cot-2', 120000, 'CASH', 'C2-A-CASH', '2026-01-01'),
    (gen_random_uuid()::text, 'C2-M2', 'cot-2', 180000, 'CASH', 'C2-A-CASH', '2026-01-01'),
    (gen_random_uuid()::text, 'C2-M3', 'cot-2', 200000, 'CASH', 'C2-A-CASH', '2026-01-01'),
    (gen_random_uuid()::text, 'C2-M4', 'cot-2', 200000, 'CASH', 'C2-A-CASH', '2026-01-01'),
    (gen_random_uuid()::text, 'C2-M5', 'cot-2', 200000, 'CASH', 'C2-A-CASH', '2026-01-01'),
    (gen_random_uuid()::text, 'C2-M6', 'cot-2', 200000, 'CASH', 'C2-A-CASH', '2026-01-01'),
    (gen_random_uuid()::text, 'C2-M7', 'cot-2', 80000, 'MOBILE_BANKING', 'C2-A-MOBILE-1', '2026-01-01'),
    (gen_random_uuid()::text, 'C2-M8', 'cot-2', 120000, 'MOBILE_BANKING', 'C2-A-MOBILE-1', '2026-01-01');

-- 13. PAIEMENTS COLLECTIVITÉ 3
INSERT INTO member_payments (id, member_id, membership_fee_id, amount, payment_mode, account_credited_id, creation_date) VALUES
-- Avril 2026 (cot-7 mensuelle)
(gen_random_uuid()::text, 'C3-M1', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-1', '2026-04-01'),
(gen_random_uuid()::text, 'C3-M2', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-1', '2026-04-01'),
(gen_random_uuid()::text, 'C3-M3', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-1', '2026-04-01'),
(gen_random_uuid()::text, 'C3-M4', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-1', '2026-04-01'),
(gen_random_uuid()::text, 'C3-M5', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-2', '2026-04-01'),
(gen_random_uuid()::text, 'C3-M6', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-2', '2026-04-01'),
(gen_random_uuid()::text, 'C3-M7', 'cot-7', 25000, 'CASH', 'C3-A-CASH', '2026-04-01'),
(gen_random_uuid()::text, 'C3-M8', 'cot-7', 25000, 'CASH', 'C3-A-CASH', '2026-04-01'),
-- Mai 2026
(gen_random_uuid()::text, 'C3-M1', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-1', '2026-05-01'),
(gen_random_uuid()::text, 'C3-M2', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-1', '2026-05-01'),
(gen_random_uuid()::text, 'C3-M3', 'cot-7', 15000, 'BANK_TRANSFER', 'C3-A-MOBILE-1', '2026-05-01'),
(gen_random_uuid()::text, 'C3-M4', 'cot-7', 15000, 'BANK_TRANSFER', 'C3-A-MOBILE-1', '2026-05-01'),
(gen_random_uuid()::text, 'C3-M5', 'cot-7', 20000, 'BANK_TRANSFER', 'C3-A-BANK-2', '2026-05-01'),
(gen_random_uuid()::text, 'C3-M6', 'cot-7', 25000, 'BANK_TRANSFER', 'C3-A-BANK-2', '2026-05-01'),
(gen_random_uuid()::text, 'C3-M7', 'cot-7', 5000, 'CASH', 'C3-A-CASH', '2026-05-01'),
(gen_random_uuid()::text, 'C3-M8', 'cot-7', 5000, 'CASH', 'C3-A-CASH', '2026-05-01');

-- 14. TRANSACTIONS
INSERT INTO transactions (id, collectivity_id, member_id, creation_date, amount, payment_mode, account_credited_id)
SELECT gen_random_uuid()::text, 'col-1', member_id, creation_date, amount, payment_mode, account_credited_id
FROM member_payments WHERE member_id IN (SELECT id FROM members WHERE id LIKE 'C1-M%');

INSERT INTO transactions (id, collectivity_id, member_id, creation_date, amount, payment_mode, account_credited_id)
SELECT gen_random_uuid()::text, 'col-2', member_id, creation_date, amount, payment_mode, account_credited_id
FROM member_payments WHERE member_id IN (SELECT id FROM members WHERE id LIKE 'C2-M%');

INSERT INTO transactions (id, collectivity_id, member_id, creation_date, amount, payment_mode, account_credited_id)
SELECT gen_random_uuid()::text, 'col-3', member_id, creation_date, amount, payment_mode, account_credited_id
FROM member_payments WHERE member_id IN (SELECT id FROM members WHERE id LIKE 'C3-M%');

-- 15. NOUVEAUX MEMBRES ADHÉRENTS
-- Collectivité 1 (4 nouveaux juniors)
INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, creation_date) VALUES
    ('C1-NEW1', 'Random1', 'New1', '1995-01-01', 'MALE', 'Adresse random', 'Agriculteur', '0390000001', 'new1@test.com', 'JUNIOR', '2026-04-01'),
    ('C1-NEW2', 'Random2', 'New2', '1996-02-02', 'FEMALE', 'Adresse random', 'Agriculteur', '0390000002', 'new2@test.com', 'JUNIOR', '2026-04-01'),
    ('C1-NEW3', 'Random3', 'New3', '1997-03-03', 'MALE', 'Adresse random', 'Agriculteur', '0390000003', 'new3@test.com', 'JUNIOR', '2026-05-01'),
    ('C1-NEW4', 'Random4', 'New4', '1998-04-04', 'FEMALE', 'Adresse random', 'Agriculteur', '0390000004', 'new4@test.com', 'JUNIOR', '2026-06-01');

INSERT INTO member_referees (member_id, referee_id) VALUES
    ('C1-NEW1', 'C1-M1'), ('C1-NEW1', 'C1-M2'),
    ('C1-NEW2', 'C1-M1'), ('C1-NEW2', 'C1-M2'),
    ('C1-NEW3', 'C1-M1'), ('C1-NEW3', 'C1-M2'),
    ('C1-NEW4', 'C1-M1'), ('C1-NEW4', 'C1-M2');

INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
    ('col-1', 'C1-NEW1'), ('col-1', 'C1-NEW2'), ('col-1', 'C1-NEW3'), ('col-1', 'C1-NEW4');

-- Collectivité 2 (3 nouveaux juniors)
INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, creation_date) VALUES
    ('C2-NEW1', 'Random5', 'New5', '1995-05-05', 'MALE', 'Adresse random', 'Agriculteur', '0390000005', 'new5@test.com', 'JUNIOR', '2026-03-01'),
    ('C2-NEW2', 'Random6', 'New6', '1996-06-06', 'FEMALE', 'Adresse random', 'Agriculteur', '0390000006', 'new6@test.com', 'JUNIOR', '2026-03-01'),
    ('C2-NEW3', 'Random7', 'New7', '1997-07-07', 'MALE', 'Adresse random', 'Agriculteur', '0390000007', 'new7@test.com', 'JUNIOR', '2026-03-01');

INSERT INTO member_referees (member_id, referee_id) VALUES
    ('C2-NEW1', 'C1-M1'), ('C2-NEW1', 'C1-M2'),
    ('C2-NEW2', 'C1-M1'), ('C2-NEW2', 'C1-M2'),
    ('C2-NEW3', 'C1-M1'), ('C2-NEW3', 'C1-M2');

INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
    ('col-2', 'C2-NEW1'), ('col-2', 'C2-NEW2'), ('col-2', 'C2-NEW3');

-- Collectivité 3 (6 nouveaux juniors)
INSERT INTO members (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, creation_date) VALUES
    ('C3-NEW1', 'Random8', 'New8', '1995-01-01', 'MALE', 'Adresse random', 'Apiculteur', '0390000008', 'new8@test.com', 'JUNIOR', '2026-01-01'),
    ('C3-NEW2', 'Random9', 'New9', '1996-02-02', 'FEMALE', 'Adresse random', 'Apiculteur', '0390000009', 'new9@test.com', 'JUNIOR', '2026-02-01'),
    ('C3-NEW3', 'Random10', 'New10', '1997-03-03', 'MALE', 'Adresse random', 'Apiculteur', '0390000010', 'new10@test.com', 'JUNIOR', '2026-02-01'),
    ('C3-NEW4', 'Random11', 'New11', '1998-04-04', 'FEMALE', 'Adresse random', 'Apiculteur', '0390000011', 'new11@test.com', 'JUNIOR', '2026-03-01'),
    ('C3-NEW5', 'Random12', 'New12', '1999-05-05', 'MALE', 'Adresse random', 'Apiculteur', '0390000012', 'new12@test.com', 'JUNIOR', '2026-03-01'),
    ('C3-NEW6', 'Random13', 'New13', '2000-06-06', 'FEMALE', 'Adresse random', 'Apiculteur', '0390000013', 'new13@test.com', 'JUNIOR', '2026-03-01');

INSERT INTO member_referees (member_id, referee_id) VALUES
    ('C3-NEW1', 'C3-M1'), ('C3-NEW1', 'C3-M2'),
    ('C3-NEW2', 'C3-M1'), ('C3-NEW2', 'C3-M2'),
    ('C3-NEW3', 'C3-M1'), ('C3-NEW3', 'C3-M2'),
    ('C3-NEW4', 'C3-M1'), ('C3-NEW4', 'C3-M2'),
    ('C3-NEW5', 'C3-M1'), ('C3-NEW5', 'C3-M2'),
    ('C3-NEW6', 'C3-M1'), ('C3-NEW6', 'C3-M2');

INSERT INTO collectivity_members (collectivity_id, member_id) VALUES
    ('col-3', 'C3-NEW1'), ('col-3', 'C3-NEW2'), ('col-3', 'C3-NEW3'),
    ('col-3', 'C3-NEW4'), ('col-3', 'C3-NEW5'), ('col-3', 'C3-NEW6');