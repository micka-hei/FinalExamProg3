package com.examen.hei.repository;

import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

@Component
public class DatabaseConnection {

    private final DataSource dataSource;

    public DatabaseConnection(DataSource dataSource) {
        this.dataSource = dataSource;
        initializeDatabase();
    }

    private void initializeDatabase() {
        createTables();
        initializeDefaultData();
    }

    private void createTables() {
        String[] createTableStatements = {
                """
            CREATE TABLE IF NOT EXISTS collectivities (
                id VARCHAR(50) PRIMARY KEY,
                location VARCHAR(255),
                official_number VARCHAR(50),
                official_name VARCHAR(255)
            )
            """,

                """
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
                occupation VARCHAR(50)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS member_referees (
                member_id VARCHAR(50),
                referee_id VARCHAR(50),
                FOREIGN KEY (member_id) REFERENCES members(id),
                FOREIGN KEY (referee_id) REFERENCES members(id)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS collectivity_members (
                collectivity_id VARCHAR(50),
                member_id VARCHAR(50),
                FOREIGN KEY (collectivity_id) REFERENCES collectivities(id),
                FOREIGN KEY (member_id) REFERENCES members(id)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS collectivity_structure (
                collectivity_id VARCHAR(50) PRIMARY KEY,
                president_id VARCHAR(50),
                vice_president_id VARCHAR(50),
                treasurer_id VARCHAR(50),
                secretary_id VARCHAR(50),
                FOREIGN KEY (collectivity_id) REFERENCES collectivities(id),
                FOREIGN KEY (president_id) REFERENCES members(id),
                FOREIGN KEY (vice_president_id) REFERENCES members(id),
                FOREIGN KEY (treasurer_id) REFERENCES members(id),
                FOREIGN KEY (secretary_id) REFERENCES members(id)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS membership_fees (
                id VARCHAR(50) PRIMARY KEY,
                collectivity_id VARCHAR(50),
                eligible_from DATE,
                frequency VARCHAR(20),
                amount DOUBLE,
                label VARCHAR(255),
                status VARCHAR(20),
                FOREIGN KEY (collectivity_id) REFERENCES collectivities(id)
            )
            """,

                """
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
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS member_payments (
                id VARCHAR(50) PRIMARY KEY,
                member_id VARCHAR(50),
                membership_fee_id VARCHAR(50),
                amount INTEGER,
                payment_mode VARCHAR(20),
                account_credited_id VARCHAR(50),
                creation_date DATE,
                FOREIGN KEY (member_id) REFERENCES members(id),
                FOREIGN KEY (membership_fee_id) REFERENCES membership_fees(id),
                FOREIGN KEY (account_credited_id) REFERENCES financial_accounts(id)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS transactions (
                id VARCHAR(50) PRIMARY KEY,
                collectivity_id VARCHAR(50),
                member_id VARCHAR(50),
                creation_date DATE,
                amount DOUBLE,
                payment_mode VARCHAR(20),
                account_credited_id VARCHAR(50),
                FOREIGN KEY (collectivity_id) REFERENCES collectivities(id),
                FOREIGN KEY (member_id) REFERENCES members(id),
                FOREIGN KEY (account_credited_id) REFERENCES financial_accounts(id)
            )
            """,

                """
            CREATE TABLE IF NOT EXISTS sequences (
                name VARCHAR(50) PRIMARY KEY,
                current_value INTEGER DEFAULT 1
            )
            """
        };

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : createTableStatements) {
                stmt.execute(sql);
            }
            initializeSequences(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeSequences(Connection conn) {
        String[] sequences = {"collectivity", "member", "fee", "payment", "transaction", "account", "official_number"};
        String sql = "MERGE INTO sequences (name, current_value) KEY(name) VALUES (?, 1)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String seq : sequences) {
                pstmt.setString(1, seq);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeDefaultData() {
        String checkSql = "SELECT COUNT(*) FROM financial_accounts WHERE type = 'CASH'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next() && rs.getInt(1) == 0) {
                String accountId = generateId("account");
                String insertSql = "INSERT INTO financial_accounts (id, type, amount) VALUES (?, 'CASH', ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, accountId);
                    insertStmt.setDouble(2, 1000000.0);
                    insertStmt.executeUpdate();
                    System.out.println("Compte caisse créé avec ID: " + accountId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int executeUpdate(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public String generateId(String sequenceName) {
        String updateSql = "UPDATE sequences SET current_value = current_value + 1 WHERE name = ?";
        String selectSql = "SELECT current_value FROM sequences WHERE name = ?";

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, sequenceName);
                updateStmt.executeUpdate();
            }

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setString(1, sequenceName);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int value = rs.getInt(1);
                        String prefix = switch (sequenceName) {
                            case "collectivity" -> "COL";
                            case "member" -> "MEM";
                            case "fee" -> "FEE";
                            case "payment" -> "PAY";
                            case "transaction" -> "TXN";
                            case "account" -> "ACC";
                            case "official_number" -> "OFF";
                            default -> "SEQ";
                        };
                        return prefix + "-" + value;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}