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
        testDirectQuery();
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

        loadReferees(member);

        return Optional.of(member);
    }

    private void loadReferees(Member member) {
        String sql = "SELECT referee_id FROM member_referees WHERE member_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, member.getId().getId());

        List<Member> referees = new ArrayList<>();
        for (Map<String, Object> row : results) {
            String refereeId = (String) row.get("referee_id");
            findMemberById(refereeId).ifPresent(referees::add);
        }
        member.setReferees(referees);
    }

    public void saveMember(Member member) {
        String sql = """
            INSERT INTO members (id, first_name, last_name, birth_date, gender, 
                                address, profession, phone_number, email, occupation)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        dbConnection.executeUpdate(sql,
                member.getId().getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getBirthDate(),
                member.getGender().name(),
                member.getAddress(),
                member.getProfession(),
                member.getPhoneNumber(),
                member.getEmail(),
                member.getOccupation() != null ? member.getOccupation().name() : null
        );

        if (member.getReferees() != null && !member.getReferees().isEmpty()) {
            String refereeSql = "INSERT INTO member_referees (member_id, referee_id) VALUES (?, ?)";
            for (Member referee : member.getReferees()) {
                dbConnection.executeUpdate(refereeSql, member.getId().getId(), referee.getId().getId());
            }
        }
    }

    public Optional<Collectivity> findCollectivityById(String id) {
        String sql = "SELECT * FROM collectivities WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, id);

        if (results.isEmpty()) {
            System.out.println("Collectivity not found with id: " + id);
            return Optional.empty();
        }

        Map<String, Object> row = results.get(0);
        Collectivity collectivity = new Collectivity();
        collectivity.setId((String) row.get("id"));
        collectivity.setLocation((String) row.get("location"));
        collectivity.setOfficialNumber((String) row.get("official_number"));
        collectivity.setOfficialName((String) row.get("official_name"));

        System.out.println("Found collectivity: " + collectivity.getId() + " - " + collectivity.getLocation());

        return Optional.of(collectivity);
    }

    public void testDirectQuery() {
        String sql = "SELECT * FROM collectivities WHERE id = 'COL-2'";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);
        System.out.println("Direct query result size: " + results.size());
        for (Map<String, Object> row : results) {
            System.out.println("Row: " + row);
        }
    }
    public void debugCollectivities() {
        String sql = "SELECT * FROM collectivities";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);
        System.out.println("=== Collectivities in database ===");
        for (Map<String, Object> row : results) {
            System.out.println("ID: " + row.get("id") +
                    ", Location: " + row.get("location") +
                    ", OfficialNumber: " + row.get("official_number") +
                    ", OfficialName: " + row.get("official_name"));
        }
        System.out.println("=== End ===");
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

    public boolean existsByOfficialNumber(String number) {
        if (number == null) return false;
        String sql = "SELECT COUNT(*) FROM collectivities WHERE official_number = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, number);
        if (results.isEmpty()) return false;
        Number count = (Number) results.get(0).get("COUNT(*)");
        return count.longValue() > 0;
    }

    public boolean existsByOfficialName(String name) {
        if (name == null) return false;
        String sql = "SELECT COUNT(*) FROM collectivities WHERE official_name = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, name);
        if (results.isEmpty()) return false;
        Number count = (Number) results.get(0).get("COUNT(*)");
        return count.longValue() > 0;
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
    public Collection<FinancialAccount> findAllFinancialAccounts() {
        String sql = "SELECT * FROM financial_accounts";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);
        List<FinancialAccount> accounts = new ArrayList<>();
        for (Map<String, Object> row : results) {
            findFinancialAccountById((String) row.get("id")).ifPresent(accounts::add);
        }
        return accounts;
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
    public void updateOfficialIdentifiers(String collectivityId, String officialNumber, String officialName) {
        String sql = "UPDATE collectivities SET official_number = ?, official_name = ? WHERE id = ?";
        dbConnection.executeUpdate(sql, officialNumber, officialName, collectivityId);
    }

    public Optional<FinancialAccount> findFinancialAccountById(String id) {
        String sql = "SELECT * FROM financial_accounts WHERE id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, id);

        if (results.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Object> row = results.get(0);
        String type = (String) row.get("type");
        FinancialAccount account;

        switch (type) {
            case "CASH":
                account = new CashAccount();
                break;
            case "BANK_ACCOUNT":
                account = new BankAccount();
                ((BankAccount) account).setHolderName((String) row.get("holder_name"));
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
                ((MobileBankingAccount) account).setHolderName((String) row.get("holder_name"));
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
        Object amount = row.get("amount");
        if (amount instanceof Number) {
            account.setAmount(((Number) amount).doubleValue());
        }

        return Optional.of(account);
    }
    public void debugMembers() {
        String sql = "SELECT id, first_name, last_name, gender FROM members";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql);
        for (Map<String, Object> row : results) {
            System.out.println("ID: " + row.get("id") +
                    ", Name: " + row.get("first_name") + " " + row.get("last_name") +
                    ", Gender: " + row.get("gender"));
        }
    }

    public void saveFinancialAccount(FinancialAccount account) {
        String sql = """
            INSERT INTO financial_accounts (id, type, amount, holder_name, bank_name, 
                                           bank_code, bank_branch_code, bank_account_number, 
                                           bank_account_key, mobile_service, mobile_number)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

        dbConnection.executeUpdate(sql,
                account.getId(), type, account.getAmount(), holderName, bankName,
                bankCode, bankBranchCode, bankAccountNumber, bankAccountKey,
                mobileService, mobileNumber
        );
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

    public Optional<MembershipFee> findMembershipFeeById(String id) {
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
        String sql = "SELECT * FROM membership_fees WHERE collectivity_id = ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, collectivityId);

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
            INSERT INTO member_payments (id, amount, payment_mode, account_credited_id, creation_date)
            VALUES (?, ?, ?, ?, ?)
        """;

        dbConnection.executeUpdate(sql,
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMode() != null ? payment.getPaymentMode().name() : null,
                payment.getAccountCredited() != null ? payment.getAccountCredited().getId() : null,
                payment.getCreationDate()
        );

        return payment;
    }

    public CollectivityTransaction saveTransaction(CollectivityTransaction transaction) {
        String sql = """
            INSERT INTO transactions (id, creation_date, amount, payment_mode, account_credited_id, member_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        dbConnection.executeUpdate(sql,
                transaction.getId(),
                transaction.getCreationDate(),
                transaction.getAmount(),
                transaction.getPaymentMode() != null ? transaction.getPaymentMode().name() : null,
                transaction.getAccountCredited() != null ? transaction.getAccountCredited().getId() : null,
                transaction.getMemberDebited() != null ? transaction.getMemberDebited().getId().getId() : null
        );

        return transaction;
    }

    public List<CollectivityTransaction> findTransactionsByCollectivityIdAndDateRange(
            String collectivityId, LocalDate from, LocalDate to) {
        String sql = "SELECT * FROM transactions WHERE creation_date BETWEEN ? AND ?";
        List<Map<String, Object>> results = dbConnection.executeQuery(sql, from, to);

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

            transactions.add(transaction);
        }
        return transactions;
    }
}