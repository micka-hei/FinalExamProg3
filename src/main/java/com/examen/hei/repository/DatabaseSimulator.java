package com.examen.hei.repository;

import com.examen.hei.model.*;
import com.examen.hei.enums.*;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.*;

@Repository
public class DatabaseSimulator {

    private final DatabaseConnection dbConnection;

    public DatabaseSimulator(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void clearAllData() {
        String[] deleteStatements = {
                "DELETE FROM member_payments",
                "DELETE FROM transactions",
                "DELETE FROM member_referees",
                "DELETE FROM collectivity_members",
                "DELETE FROM collectivity_structure",
                "DELETE FROM membership_fees",
                "DELETE FROM financial_accounts",
                "DELETE FROM members",
                "DELETE FROM collectivities"
        };

        for (String sql : deleteStatements) {
            dbConnection.executeUpdate(sql);
        }

        String[] resetSequences = {
                "UPDATE sequences SET current_value = 1 WHERE name = 'collectivity'",
                "UPDATE sequences SET current_value = 1 WHERE name = 'member'",
                "UPDATE sequences SET current_value = 1 WHERE name = 'fee'",
                "UPDATE sequences SET current_value = 1 WHERE name = 'payment'",
                "UPDATE sequences SET current_value = 1 WHERE name = 'transaction'",
                "UPDATE sequences SET current_value = 1 WHERE name = 'account'",
                "UPDATE sequences SET current_value = 1 WHERE name = 'official_number'"
        };

        for (String sql : resetSequences) {
            dbConnection.executeUpdate(sql);
        }

        System.out.println("All data cleared and sequences reset");
    }

    public String generateCollectivityId() {
        return dbConnection.generateId("collectivity");
    }

    public String generateMemberId() {
        return dbConnection.generateId("member");
    }

    public String generateMembershipFeeId() {
        return dbConnection.generateId("fee");
    }

    public String generatePaymentId() {
        return dbConnection.generateId("payment");
    }

    public String generateTransactionId() {
        return dbConnection.generateId("transaction");
    }

    public String generateAccountId() {
        return dbConnection.generateId("account");
    }

    public String generateActivityId() {
        return dbConnection.generateId("activity");
    }

    public String generateAttendanceId() {
        return dbConnection.generateId("attendance");
    }

    public String generateNextOfficialNumber() {
        String seqValue = dbConnection.generateId("official_number");
        if (seqValue != null) {
            String num = seqValue.replace("OFF-", "");
            return String.format("F-2026-%03d", Integer.parseInt(num));
        }
        return "F-2026-001";
    }

    public String generateUniqueNameFromLocation(String collectivityId) {
        Collectivity c = findCollectivityById(collectivityId).orElse(null);
        if (c == null) return null;

        String baseName = c.getLocation().toUpperCase().replaceAll("[^A-Za-z0-9]", "_");
        String candidate = baseName;
        int counter = 1;

        while (existsByOfficialName(candidate)) {
            candidate = baseName + "_" + counter;
            counter++;
        }
        return candidate;
    }

    public Optional<Member> findMemberById(String id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM members WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, id);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> row = results.get(0);
        Member member = new Member();
        member.setId(new MemberIdentifier((String) row.get("id")));
        member.setFirstName((String) row.get("first_name"));
        member.setLastName((String) row.get("last_name"));

        Object birthDateObj = row.get("birth_date");
        if (birthDateObj instanceof LocalDate) {
            member.setBirthDate((LocalDate) birthDateObj);
        }

        String genderStr = (String) row.get("gender");
        if (genderStr != null) {
            member.setGender(Gender.valueOf(genderStr));
        }

        member.setAddress((String) row.get("address"));
        member.setProfession((String) row.get("profession"));
        member.setPhoneNumber((String) row.get("phone_number"));
        member.setEmail((String) row.get("email"));

        String occupationStr = (String) row.get("occupation");
        if (occupationStr != null) {
            member.setOccupation(MemberOccupation.valueOf(occupationStr));
        }

        // Charger les référents sans récursion
        String refSql = "SELECT referee_id FROM member_referees WHERE member_id = ?";
        List<Map<String, Object>> refResults = dbConnection.executeQuery(refSql, member.getId().getId());
        List<Member> referees = new ArrayList<>();
        for (Map<String, Object> refRow : refResults) {
            String refereeId = (String) refRow.get("referee_id");
            findMemberWithoutReferees(refereeId).ifPresent(referees::add);
        }
        member.setReferees(referees);

        return Optional.of(member);
    }

    private void loadReferees(Member member) {
        String sql = "SELECT referee_id FROM member_referees WHERE member_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, member.getId().getId());

        List<Member> referees = new ArrayList<>();
        for (Map<String, Object> row : results) {
            String refereeId = (String) row.get("referee_id");
            // Utiliser findMemberWithoutReferees pour éviter la récursion
            findMemberWithoutReferees(refereeId).ifPresent(referees::add);
        }
        member.setReferees(referees);
    }

    private Optional<Member> findMemberWithoutReferees(String id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM members WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, id);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> row = results.get(0);
        Member member = new Member();
        member.setId(new MemberIdentifier((String) row.get("id")));
        member.setFirstName((String) row.get("first_name"));
        member.setLastName((String) row.get("last_name"));

        Object birthDateObj = row.get("birth_date");
        if (birthDateObj instanceof LocalDate) {
            member.setBirthDate((LocalDate) birthDateObj);
        }

        String genderStr = (String) row.get("gender");
        if (genderStr != null) {
            member.setGender(Gender.valueOf(genderStr));
        }

        member.setAddress((String) row.get("address"));
        member.setProfession((String) row.get("profession"));
        member.setPhoneNumber((String) row.get("phone_number"));
        member.setEmail((String) row.get("email"));

        String occupationStr = (String) row.get("occupation");
        if (occupationStr != null) {
            member.setOccupation(MemberOccupation.valueOf(occupationStr));
        }

        // Ne pas charger les référents
        member.setReferees(new ArrayList<>());

        return Optional.of(member);
    }

    public void saveMember(Member member) {
        String sql = """
                    INSERT INTO members (id, first_name, last_name, birth_date, gender, 
                                        address, profession, phone_number, email, occupation, creation_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        dbConnection.executeUpdate(sql,
                member.getId().getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getBirthDate(),
                member.getGender() != null ? member.getGender().name() : null,
                member.getAddress(),
                member.getProfession(),
                member.getPhoneNumber(),
                member.getEmail(),
                member.getOccupation() != null ? member.getOccupation().name() : null,
                LocalDate.now()
        );

        if (member.getReferees() != null && !member.getReferees().isEmpty()) {
            String refereeSql = "INSERT INTO member_referees (member_id, referee_id) VALUES (?, ?)";
            for (Member referee : member.getReferees()) {
                dbConnection.executeUpdate(refereeSql, member.getId().getId(), referee.getId().getId());
            }
        }
    }

    public Optional<Collectivity> findCollectivityById(String id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM collectivities WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, id);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> row = results.get(0);
        Collectivity collectivity = new Collectivity();
        collectivity.setId((String) row.get("id"));
        collectivity.setLocation((String) row.get("location"));
        collectivity.setOfficialNumber((String) row.get("official_number"));
        collectivity.setOfficialName((String) row.get("official_name"));

        loadMembers(collectivity);
        loadStructure(collectivity);

        return Optional.of(collectivity);
    }

    private void loadMembers(Collectivity collectivity) {
        String sql = "SELECT member_id FROM collectivity_members WHERE collectivity_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivity.getId());

        List<Member> members = new ArrayList<>();
        for (Map<String, Object> row : results) {
            String memberId = (String) row.get("member_id");
            findMemberById(memberId).ifPresent(members::add);
        }
        collectivity.setMembers(members);
    }

    private void loadStructure(Collectivity collectivity) {
        String sql = "SELECT * FROM collectivity_structure WHERE collectivity_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivity.getId());

        if (!results.isEmpty()) {
            Map<String, Object> row = results.get(0);
            CollectivityStructure structure = new CollectivityStructure();

            String presidentId = (String) row.get("president_id");
            if (presidentId != null) {
                findMemberById(presidentId).ifPresent(structure::setPresident);
            }

            String vicePresidentId = (String) row.get("vice_president_id");
            if (vicePresidentId != null) {
                findMemberById(vicePresidentId).ifPresent(structure::setVicePresident);
            }

            String treasurerId = (String) row.get("treasurer_id");
            if (treasurerId != null) {
                findMemberById(treasurerId).ifPresent(structure::setTreasurer);
            }

            String secretaryId = (String) row.get("secretary_id");
            if (secretaryId != null) {
                findMemberById(secretaryId).ifPresent(structure::setSecretary);
            }

            collectivity.setStructure(structure);
        }
    }

    public void saveCollectivity(Collectivity collectivity) {
        String sql = "INSERT INTO collectivities (id, location, official_number, official_name) VALUES (?, ?, ?, ?)";
        dbConnection.executeUpdate(sql,
                collectivity.getId(),
                collectivity.getLocation(),
                collectivity.getOfficialNumber(),
                collectivity.getOfficialName()
        );

        if (collectivity.getMembers() != null && !collectivity.getMembers().isEmpty()) {
            String memberSql = "INSERT INTO collectivity_members (collectivity_id, member_id) VALUES (?, ?)";
            for (Member member : collectivity.getMembers()) {
                dbConnection.executeUpdate(memberSql, collectivity.getId(), member.getId().getId());
            }
        }

        if (collectivity.getStructure() != null) {
            String structureSql = """
                        INSERT INTO collectivity_structure (collectivity_id, president_id, vice_president_id, treasurer_id, secretary_id)
                        VALUES (?, ?, ?, ?, ?)
                    """;
            dbConnection.executeUpdate(structureSql,
                    collectivity.getId(),
                    collectivity.getStructure().getPresident() != null ? collectivity.getStructure().getPresident().getId().getId() : null,
                    collectivity.getStructure().getVicePresident() != null ? collectivity.getStructure().getVicePresident().getId().getId() : null,
                    collectivity.getStructure().getTreasurer() != null ? collectivity.getStructure().getTreasurer().getId().getId() : null,
                    collectivity.getStructure().getSecretary() != null ? collectivity.getStructure().getSecretary().getId().getId() : null
            );
        }
    }

    public void createCashAccountForCollectivity(String collectivityId) {
        String checkSql = "SELECT COUNT(*) FROM financial_accounts WHERE type = 'CASH' AND id LIKE ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(checkSql, collectivityId + "-%");

        if (results == null || results.isEmpty()) {
            String accountId = collectivityId + "-CASH";
            String insertSql = "INSERT INTO financial_accounts (id, type, amount, collectivity_id) VALUES (?, 'CASH', ?, ?)";
            dbConnection.executeUpdate(insertSql, accountId, 0.0, collectivityId);
            System.out.println("Compte CASH créé pour collectivité: " + accountId);
            return;
        }

        Map<String, Object> row = results.get(0);
        Object countObj = row.get("COUNT(*)");

        if (countObj == null) {
            String accountId = collectivityId + "-CASH";
            String insertSql = "INSERT INTO financial_accounts (id, type, amount, collectivity_id) VALUES (?, 'CASH', ?, ?)";
            dbConnection.executeUpdate(insertSql, accountId, 0.0, collectivityId);
            System.out.println("Compte CASH créé pour collectivité: " + accountId);
        } else {
            Long count = ((Number) countObj).longValue();
            if (count == 0) {
                String accountId = collectivityId + "-CASH";
                String insertSql = "INSERT INTO financial_accounts (id, type, amount, collectivity_id) VALUES (?, 'CASH', ?, ?)";
                dbConnection.executeUpdate(insertSql, accountId, 0.0, collectivityId);
                System.out.println("Compte CASH créé pour collectivité: " + accountId);
            }
        }
    }

    public boolean existsByOfficialNumber(String number) {
        if (number == null) return false;
        String sql = "SELECT COUNT(*) as total FROM collectivities WHERE official_number = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, number);

        if (results == null || results.isEmpty()) {
            return false;
        }
        System.out.println("existsByOfficialNumber results: " + results);

        Object countObj = results.get(0).get("total");
        if (countObj == null) {
            countObj = results.get(0).get("count");
        }

        if (countObj == null) {
            return false;
        }

        long count = ((Number) countObj).longValue();
        return count > 0;
    }

    public boolean existsByOfficialName(String name) {
        if (name == null) return false;
        String sql = "SELECT COUNT(*) as total FROM collectivities WHERE official_name = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, name);

        if (results == null || results.isEmpty()) {
            return false;
        }

        Object countObj = results.get(0).get("total");
        if (countObj == null) {
            countObj = results.get(0).get("count");
        }

        if (countObj == null) {
            return false;
        }

        long count = ((Number) countObj).longValue();
        return count > 0;
    }

    public boolean hasOfficialNumber(String collectivityId) {
        String sql = "SELECT official_number FROM collectivities WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivityId);
        if (results.isEmpty()) return false;
        return results.get(0).get("official_number") != null;
    }

    public boolean hasOfficialName(String collectivityId) {
        String sql = "SELECT official_name FROM collectivities WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivityId);
        if (results.isEmpty()) return false;
        return results.get(0).get("official_name") != null;
    }

    public void updateOfficialIdentifiers(String collectivityId, String officialNumber, String officialName) {
        String sql = "UPDATE collectivities SET official_number = ?, official_name = ? WHERE id = ?";
        dbConnection.executeUpdate(sql, officialNumber, officialName, collectivityId);
    }

    public Optional<FinancialAccount> findFinancialAccountById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM financial_accounts WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, id);

        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> row = results.get(0);
        String type = (String) row.get("type");
        if (type == null) {
            return Optional.empty();
        }

        FinancialAccount account;

        switch (type) {
            case "CASH":
                account = new CashAccount();
                break;
            case "BANK_ACCOUNT":
                account = new BankAccount();
                String holderName = (String) row.get("holder_name");
                if (holderName != null) {
                    ((BankAccount) account).setHolderName(holderName);
                }
                String bankName = (String) row.get("bank_name");
                if (bankName != null) {
                    ((BankAccount) account).setBankName(Bank.valueOf(bankName));
                }
                Object bankCode = row.get("bank_code");
                if (bankCode instanceof Number) {
                    ((BankAccount) account).setBankCode(((Number) bankCode).intValue());
                }
                Object bankBranchCode = row.get("bank_branch_code");
                if (bankBranchCode instanceof Number) {
                    ((BankAccount) account).setBankBranchCode(((Number) bankBranchCode).intValue());
                }
                ((BankAccount) account).setBankAccountNumber((String) row.get("bank_account_number"));
                Object bankAccountKey = row.get("bank_account_key");
                if (bankAccountKey instanceof Number) {
                    ((BankAccount) account).setBankAccountKey(((Number) bankAccountKey).intValue());
                }
                break;
            case "MOBILE_BANKING":
                account = new MobileBankingAccount();
                String mobileHolderName = (String) row.get("holder_name");
                if (mobileHolderName != null) {
                    ((MobileBankingAccount) account).setHolderName(mobileHolderName);
                }
                String mobileService = (String) row.get("mobile_service");
                if (mobileService != null) {
                    ((MobileBankingAccount) account).setMobileBankingService(MobileBankingService.valueOf(mobileService));
                }
                ((MobileBankingAccount) account).setMobileNumber((String) row.get("mobile_number"));
                break;
            default:
                return Optional.empty();
        }

        account.setId((String) row.get("id"));
        account.setCollectivityId((String) row.get("collectivity_id"));
        Object amount = row.get("amount");
        if (amount instanceof Number) {
            account.setAmount(((Number) amount).doubleValue());
        }

        return Optional.of(account);
    }

    public void saveFinancialAccount(FinancialAccount account) {
        String sql = """
                    INSERT INTO financial_accounts (id, type, amount, holder_name, bank_name, 
                                                   bank_code, bank_branch_code, bank_account_number, 
                                                   bank_account_key, mobile_service, mobile_number, collectivity_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String type = account instanceof CashAccount ? "CASH" :
                account instanceof BankAccount ? "BANK_ACCOUNT" : "MOBILE_BANKING";

        String holderName = null;
        String bankName = null;
        Integer bankCode = null;
        Integer bankBranchCode = null;
        String bankAccountNumber = null;
        Integer bankAccountKey = null;
        String mobileService = null;
        String mobileNumber = null;

        if (account instanceof BankAccount ba) {
            holderName = ba.getHolderName();
            bankName = ba.getBankName() != null ? ba.getBankName().name() : null;
            bankCode = ba.getBankCode();
            bankBranchCode = ba.getBankBranchCode();
            bankAccountNumber = ba.getBankAccountNumber();
            bankAccountKey = ba.getBankAccountKey();
        } else if (account instanceof MobileBankingAccount mba) {
            holderName = mba.getHolderName();
            mobileService = mba.getMobileBankingService() != null ? mba.getMobileBankingService().name() : null;
            mobileNumber = mba.getMobileNumber();
        }

        int result = dbConnection.executeUpdate(sql,
                account.getId(), type, account.getAmount(), holderName, bankName,
                bankCode, bankBranchCode, bankAccountNumber, bankAccountKey,
                mobileService, mobileNumber, account.getCollectivityId()
        );

        if (result > 0) {
            System.out.println("Financial account saved successfully: " + account.getId());
        } else {
            System.err.println("Failed to save financial account: " + account.getId());
        }
    }

    public List<Member> findMembersByCollectivityId(String collectivityId) {
        String sql = "SELECT member_id FROM collectivity_members WHERE collectivity_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivityId);

        List<Member> members = new ArrayList<>();
        for (Map<String, Object> row : results) {
            String memberId = (String) row.get("member_id");
            findMemberById(memberId).ifPresent(members::add);
        }
        return members;
    }

    public List<Member> findAllMembers() {
        String sql = "SELECT * FROM members";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);
        List<Member> members = new ArrayList<>();
        for (Map<String, Object> row : results) {
            findMemberById((String) row.get("id")).ifPresent(members::add);
        }
        return members;
    }

    public List<Collectivity> findAllCollectivities() {
        String sql = "SELECT * FROM collectivities";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);
        List<Collectivity> collectivities = new ArrayList<>();
        for (Map<String, Object> row : results) {
            findCollectivityById((String) row.get("id")).ifPresent(collectivities::add);
        }
        return collectivities;
    }

    public Optional<MembershipFee> findMembershipFeeById(String id) {
        if (id == null) {
            return Optional.empty();
        }

        String sql = "SELECT * FROM membership_fees WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, id);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> row = results.get(0);
        MembershipFee fee = new MembershipFee();
        fee.setId((String) row.get("id"));

        Object eligibleFrom = row.get("eligible_from");
        if (eligibleFrom instanceof LocalDate) {
            fee.setEligibleFrom((LocalDate) eligibleFrom);
        }

        String frequency = (String) row.get("frequency");
        if (frequency != null) {
            fee.setFrequency(Frequency.valueOf(frequency));
        }

        Object amount = row.get("amount");
        if (amount instanceof Number) {
            fee.setAmount(((Number) amount).doubleValue());
        }

        fee.setLabel((String) row.get("label"));

        String status = (String) row.get("status");
        if (status != null) {
            fee.setStatus(ActivityStatus.valueOf(status));
        }

        return Optional.of(fee);
    }

    public List<MembershipFee> findMembershipFeesByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM membership_fees";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);

        List<MembershipFee> fees = new ArrayList<>();
        for (Map<String, Object> row : results) {
            findMembershipFeeById((String) row.get("id")).ifPresent(fees::add);
        }
        return fees;
    }

    public MembershipFee saveMembershipFee(MembershipFee fee) {
        String sql = """
                    INSERT INTO membership_fees (id, eligible_from, frequency, amount, label, status)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        dbConnection.executeUpdate(sql,
                fee.getId(),
                fee.getEligibleFrom(),
                fee.getFrequency() != null ? fee.getFrequency().name() : null,
                fee.getAmount(),
                fee.getLabel(),
                fee.getStatus() != null ? fee.getStatus().name() : null
        );

        return fee;
    }

    public MemberPayment saveMemberPayment(MemberPayment payment) {
        String sql = """
                    INSERT INTO member_payments (id, member_id, membership_fee_id, amount, payment_mode, account_credited_id, creation_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        dbConnection.executeUpdate(sql,
                payment.getId(),
                payment.getMemberId(),
                payment.getMembershipFeeId(),
                payment.getAmount(),
                payment.getPaymentMode() != null ? payment.getPaymentMode().name() : null,
                payment.getAccountCredited() != null ? payment.getAccountCredited().getId() : null,
                payment.getCreationDate()
        );

        return payment;
    }

    public CollectivityTransaction saveTransaction(CollectivityTransaction transaction) {
        String sql = """
                    INSERT INTO transactions (id, creation_date, amount, payment_mode, account_credited_id, member_id, collectivity_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        dbConnection.executeUpdate(sql,
                transaction.getId(),
                transaction.getCreationDate(),
                transaction.getAmount(),
                transaction.getPaymentMode() != null ? transaction.getPaymentMode().name() : null,
                transaction.getAccountCredited() != null ? transaction.getAccountCredited().getId() : null,
                transaction.getMemberDebited() != null ? transaction.getMemberDebited().getId().getId() : null,
                transaction.getCollectivityId()
        );

        return transaction;
    }

    public List<CollectivityTransaction> findTransactionsByCollectivityIdAndDateRange(
            String collectivityId, LocalDate from, LocalDate to) {
        String sql = "SELECT * FROM transactions WHERE creation_date BETWEEN ? AND ? AND collectivity_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, from, to, collectivityId);

        List<CollectivityTransaction> transactions = new ArrayList<>();
        for (Map<String, Object> row : results) {
            CollectivityTransaction transaction = new CollectivityTransaction();
            transaction.setId((String) row.get("id"));

            Object creationDate = row.get("creation_date");
            if (creationDate instanceof LocalDate) {
                transaction.setCreationDate((LocalDate) creationDate);
            }

            Object amount = row.get("amount");
            if (amount instanceof Number) {
                transaction.setAmount(((Number) amount).doubleValue());
            }

            String paymentMode = (String) row.get("payment_mode");
            if (paymentMode != null) {
                transaction.setPaymentMode(PaymentMode.valueOf(paymentMode));
            }

            String accountId = (String) row.get("account_credited_id");
            if (accountId != null) {
                findFinancialAccountById(accountId).ifPresent(transaction::setAccountCredited);
            }

            String memberId = (String) row.get("member_id");
            if (memberId != null) {
                findMemberById(memberId).ifPresent(transaction::setMemberDebited);
            }

            transaction.setCollectivityId((String) row.get("collectivity_id"));

            transactions.add(transaction);
        }
        return transactions;
    }

    public List<FinancialAccount> findFinancialAccountsByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM financial_accounts WHERE collectivity_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivityId);

        List<FinancialAccount> accounts = new ArrayList<>();
        for (Map<String, Object> row : results) {
            findFinancialAccountById((String) row.get("id")).ifPresent(accounts::add);
        }
        return accounts;
    }

    public Collection<FinancialAccount> findAllFinancialAccounts() {
        String sql = "SELECT * FROM financial_accounts";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);
        List<FinancialAccount> accounts = new ArrayList<>();
        for (Map<String, Object> row : results) {
            findFinancialAccountById((String) row.get("id")).ifPresent(accounts::add);
        }
        return accounts;
    }

    public void listAllFinancialAccounts() {
        String sql = "SELECT * FROM financial_accounts";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);
        System.out.println("=== LISTE DES COMPTES FINANCIERS ===");
        for (Map<String, Object> row : results) {
            System.out.println("  id: " + row.get("id") + ", type: " + row.get("type") + ", amount: " + row.get("amount"));
        }
        System.out.println("===================================");
    }

    // METHODES POUR LES STATISTIQUES

    public Map<String, Double> getMemberPaymentsByPeriod(String collectivityId, LocalDate from, LocalDate to) {
        String sql = """
                    SELECT t.member_id, SUM(t.amount) as total_paid
                    FROM transactions t
                    WHERE t.collectivity_id = ? 
                    AND t.creation_date BETWEEN ? AND ?
                    GROUP BY t.member_id
                """;
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivityId, from, to);

        Map<String, Double> paymentsByMember = new HashMap<>();
        for (Map<String, Object> row : results) {
            String memberId = (String) row.get("member_id");
            Object totalPaid = row.get("total_paid");
            double amount = totalPaid instanceof Number ? ((Number) totalPaid).doubleValue() : 0.0;
            paymentsByMember.put(memberId, amount);
        }
        return paymentsByMember;
    }

    public List<MembershipFee> getActiveMembershipFees() {
        String sql = "SELECT * FROM membership_fees WHERE status = 'ACTIVE'";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);

        List<MembershipFee> activeFees = new ArrayList<>();
        for (Map<String, Object> row : results) {
            MembershipFee fee = new MembershipFee();
            fee.setId((String) row.get("id"));

            Object eligibleFrom = row.get("eligible_from");
            if (eligibleFrom instanceof LocalDate) {
                fee.setEligibleFrom((LocalDate) eligibleFrom);
            }

            String frequency = (String) row.get("frequency");
            if (frequency != null) {
                fee.setFrequency(Frequency.valueOf(frequency));
            }

            Object amount = row.get("amount");
            if (amount instanceof Number) {
                fee.setAmount(((Number) amount).doubleValue());
            }

            fee.setLabel((String) row.get("label"));

            String status = (String) row.get("status");
            if (status != null) {
                fee.setStatus(ActivityStatus.valueOf(status));
            }

            activeFees.add(fee);
        }
        return activeFees;
    }

    public int getMemberPaymentsCountForFee(String memberId, String feeId, LocalDate from, LocalDate to) {
        String sql = """
                    SELECT COUNT(*) as count FROM member_payments 
                    WHERE member_id = ? AND membership_fee_id = ? 
                    AND creation_date BETWEEN ? AND ?
                """;
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, memberId, feeId, from, to);

        if (results == null || results.isEmpty()) {
            return 0;
        }

        Map<String, Object> row = results.get(0);
        Object countObj = row.get("count");

        if (countObj == null) {
            countObj = row.get("COUNT");
            if (countObj == null) {
                // Essayer de prendre la première valeur numérique trouvée
                for (Object value : row.values()) {
                    if (value instanceof Number) {
                        countObj = value;
                        break;
                    }
                }
            }
        }

        if (countObj == null) {
            return 0;
        }

        return ((Number) countObj).intValue();
    }

    public int getNewMembersCount(String collectivityId, LocalDate from, LocalDate to) {
        String sql = """
                    SELECT COUNT(DISTINCT m.id) as new_members
                    FROM members m
                    JOIN collectivity_members cm ON cm.member_id = m.id
                    WHERE cm.collectivity_id = ? AND m.creation_date BETWEEN ? AND ?
                """;
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivityId, from, to);

        if (results == null || results.isEmpty()) {
            return 0;
        }

        Map<String, Object> row = results.get(0);
        Object countObj = row.get("new_members");

        if (countObj == null) {
            countObj = row.get("count");
            if (countObj == null) {
                for (Object value : row.values()) {
                    if (value instanceof Number) {
                        countObj = value;
                        break;
                    }
                }
            }
        }

        if (countObj == null) {
            return 0;
        }

        return ((Number) countObj).intValue();
    }

    // METHODES POUR LES ACTIVITES

    public void saveActivity(CollectivityActivity activity) {
        String sql = """
                    INSERT INTO activities (id, collectivity_id, label, activity_type, 
                                           week_ordinal, day_of_week, executive_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        Integer weekOrdinal = null;
        String dayOfWeek = null;
        if (activity.getRecurrenceRule() != null) {
            weekOrdinal = activity.getRecurrenceRule().getWeekOrdinal();
            dayOfWeek = activity.getRecurrenceRule().getDayOfWeek();
        }

        // Convertir l'enum en String pour la base de données
        String activityType = activity.getActivityType() != null ? activity.getActivityType().name() : null;

        dbConnection.executeUpdate(sql,
                activity.getId(),
                activity.getCollectivityId(),
                activity.getLabel(),
                activityType,
                weekOrdinal,
                dayOfWeek,
                activity.getExecutiveDate()
        );

        if (activity.getMemberOccupationConcerned() != null && !activity.getMemberOccupationConcerned().isEmpty()) {
            String occupationSql = "INSERT INTO activity_occupations (activity_id, occupation) VALUES (?, ?)";
            for (MemberOccupation occupation : activity.getMemberOccupationConcerned()) {
                dbConnection.executeUpdate(occupationSql, activity.getId(), occupation.name());
            }
        }
    }

    public Optional<CollectivityActivity> findActivityById(String id) {
        if (id == null) return Optional.empty();

        String sql = "SELECT * FROM activities WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, id);

        if (results.isEmpty()) return Optional.empty();

        Map<String, Object> row = results.get(0);
        CollectivityActivity activity = new CollectivityActivity();
        activity.setId((String) row.get("id"));
        activity.setCollectivityId((String) row.get("collectivity_id"));
        activity.setLabel((String) row.get("label"));

        // Convertir la String en enum
        String activityTypeStr = (String) row.get("activity_type");
        if (activityTypeStr != null) {
            activity.setActivityType(ActivityType.valueOf(activityTypeStr));
        }

        Object executiveDate = row.get("executive_date");
        if (executiveDate instanceof LocalDate) {
            activity.setExecutiveDate((LocalDate) executiveDate);
        }

        Object weekOrdinal = row.get("week_ordinal");
        Object dayOfWeek = row.get("day_of_week");
        if (weekOrdinal != null && dayOfWeek != null) {
            MonthlyRecurrenceRule rule = new MonthlyRecurrenceRule();
            rule.setWeekOrdinal(((Number) weekOrdinal).intValue());
            rule.setDayOfWeek((String) dayOfWeek);
            activity.setRecurrenceRule(rule);
        }

        String occSql = "SELECT occupation FROM activity_occupations WHERE activity_id = ?";
        List<Map<String, Object>> occResults = dbConnection.executeQuery(occSql, id);
        List<MemberOccupation> occupations = new ArrayList<>();
        for (Map<String, Object> occRow : occResults) {
            String occName = (String) occRow.get("occupation");
            if (occName != null) {
                occupations.add(MemberOccupation.valueOf(occName));
            }
        }
        activity.setMemberOccupationConcerned(occupations);

        return Optional.of(activity);
    }

    public List<CollectivityActivity> findActivitiesByCollectivityId(String collectivityId) {
        String sql = "SELECT * FROM activities WHERE collectivity_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivityId);

        List<CollectivityActivity> activities = new ArrayList<>();
        for (Map<String, Object> row : results) {
            findActivityById((String) row.get("id")).ifPresent(activities::add);
        }
        return activities;
    }

    public void saveAttendance(ActivityMemberAttendance attendance) {
        String sql = """
                    INSERT INTO attendances (id, activity_id, member_id, attendance_status)
                    VALUES (?, ?, ?, ?)
                """;

        dbConnection.executeUpdate(sql,
                attendance.getId(),
                attendance.getActivityId(),
                attendance.getMemberId(),
                attendance.getAttendanceStatus()
        );
    }

    public ActivityMemberAttendance findAttendanceByActivityIdAndMemberId(String activityId, String memberId) {
        String sql = "SELECT * FROM attendances WHERE activity_id = ? AND member_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, activityId, memberId);

        if (results.isEmpty()) return null;

        Map<String, Object> row = results.get(0);
        ActivityMemberAttendance attendance = new ActivityMemberAttendance();
        attendance.setId((String) row.get("id"));
        attendance.setActivityId((String) row.get("activity_id"));
        attendance.setMemberId((String) row.get("member_id"));
        attendance.setAttendanceStatus((String) row.get("attendance_status"));

        findMemberById(attendance.getMemberId()).ifPresent(member -> {
            MemberDescription desc = new MemberDescription();
            desc.setId(member.getId().getId());
            desc.setFirstName(member.getFirstName());
            desc.setLastName(member.getLastName());
            desc.setEmail(member.getEmail());
            desc.setOccupation(member.getOccupation() != null ? member.getOccupation().name() : null);
            attendance.setMemberDescription(desc);
        });

        return attendance;
    }

    public List<ActivityMemberAttendance> findAttendanceByActivityId(String activityId) {
        String sql = "SELECT * FROM attendances WHERE activity_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, activityId);

        List<ActivityMemberAttendance> attendances = new ArrayList<>();
        for (Map<String, Object> row : results) {
            ActivityMemberAttendance attendance = new ActivityMemberAttendance();
            attendance.setId((String) row.get("id"));
            attendance.setActivityId((String) row.get("activity_id"));
            attendance.setMemberId((String) row.get("member_id"));
            attendance.setAttendanceStatus((String) row.get("attendance_status"));

            findMemberById(attendance.getMemberId()).ifPresent(member -> {
                MemberDescription desc = new MemberDescription();
                desc.setId(member.getId().getId());
                desc.setFirstName(member.getFirstName());
                desc.setLastName(member.getLastName());
                desc.setEmail(member.getEmail());
                desc.setOccupation(member.getOccupation() != null ? member.getOccupation().name() : null);
                attendance.setMemberDescription(desc);
            });

            attendances.add(attendance);
        }
        return attendances;
    }

    public double getMemberAssiduityPercentage(String memberId, LocalDate from, LocalDate to) {
        try {
            // Compter les activités où le membre était concerné
            String concernedSql = """
                SELECT COUNT(DISTINCT a.id) as total_concerned
                FROM activities a
                JOIN activity_occupations ao ON ao.activity_id = a.id
                JOIN collectivity_members cm ON cm.collectivity_id = a.collectivity_id
                WHERE cm.member_id = ?
                AND (a.executive_date BETWEEN ? AND ? OR (a.week_ordinal IS NOT NULL AND a.day_of_week IS NOT NULL))
                AND ao.occupation = (SELECT occupation FROM members WHERE id = ?)
            """;

            List<Map<String, Object>> concernedResults = dbConnection.executeQuery(concernedSql, memberId, from, to, memberId);
            long totalConcerned = 0;
            if (!concernedResults.isEmpty()) {
                Object countObj = concernedResults.get(0).get("total_concerned");
                if (countObj instanceof Number) {
                    totalConcerned = ((Number) countObj).longValue();
                }
            }

            if (totalConcerned == 0) {
                return 0.0;
            }

            // Compter les activités où le membre a été présent
            String presentSql = """
                SELECT COUNT(*) as total_present
                FROM attendances att
                WHERE att.member_id = ?
                AND att.attendance_status = 'ATTENDED'
                AND att.activity_id IN (
                SELECT a.id FROM activities a
                JOIN activity_occupations ao ON ao.activity_id = a.id
                WHERE (a.executive_date BETWEEN ? AND ? OR (a.week_ordinal IS NOT NULL AND a.day_of_week IS NOT NULL))
                AND ao.occupation = (SELECT occupation FROM members WHERE id = ?)
            )
        """;

            List<Map<String, Object>> presentResults = dbConnection.executeQuery(presentSql, memberId, from, to, memberId);
            long totalPresent = 0;
            if (!presentResults.isEmpty()) {
                Object countObj = presentResults.get(0).get("total_present");
                if (countObj instanceof Number) {
                    totalPresent = ((Number) countObj).longValue();
                }
            }

            return (double) totalPresent / totalConcerned * 100.0;

        } catch (Exception e) {
            System.err.println("Error calculating assiduity for member " + memberId + ": " + e.getMessage());
            return 0.0;
        }
    }

    public List<ActivityMemberAttendance> findAttendanceByMemberId(String memberId) {
        String sql = "SELECT * FROM attendances WHERE member_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, memberId);

        List<ActivityMemberAttendance> attendances = new ArrayList<>();
        for (Map<String, Object> row : results) {
            ActivityMemberAttendance attendance = new ActivityMemberAttendance();
            attendance.setId((String) row.get("id"));
            attendance.setActivityId((String) row.get("activity_id"));
            attendance.setMemberId((String) row.get("member_id"));
            attendance.setAttendanceStatus((String) row.get("attendance_status"));

            findMemberById(attendance.getMemberId()).ifPresent(member -> {
                MemberDescription desc = new MemberDescription();
                desc.setId(member.getId().getId());
                desc.setFirstName(member.getFirstName());
                desc.setLastName(member.getLastName());
                desc.setEmail(member.getEmail());
                desc.setOccupation(member.getOccupation() != null ? member.getOccupation().name() : null);
                attendance.setMemberDescription(desc);
            });

            attendances.add(attendance);
        }
        return attendances;
    }

}