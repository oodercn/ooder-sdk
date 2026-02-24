package net.ooder.sdk.drivers.db.impl;

import net.ooder.sdk.driver.annotation.DriverImplementation;
import net.ooder.sdk.drivers.db.DatabaseDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

@DriverImplementation(value = "DatabaseDriver", skillId = "skill-db-mysql")
public class MySqlDatabaseDriver implements DatabaseDriver {
    
    private static final Logger log = LoggerFactory.getLogger(MySqlDatabaseDriver.class);
    
    private DatabaseConfig config;
    private Connection connection;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean connected = false;
    
    @Override
    public void init(DatabaseConfig config) {
        this.config = config;
        
        try {
            String url = config.getUrl();
            if (url == null || url.isEmpty()) {
                url = String.format("jdbc:mysql://localhost:3306/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    config.getDatabase() != null ? config.getDatabase() : "ooder");
            }
            
            String username = config.getUsername();
            String password = config.getPassword();
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(config.isAutoCommit());
            
            connected = true;
            log.info("MySQL database initialized: {}", url);
            
        } catch (SQLException | ClassNotFoundException e) {
            log.error("Failed to initialize MySQL database", e);
            throw new RuntimeException("Failed to initialize MySQL database", e);
        }
    }
    
    @Override
    public CompletableFuture<Integer> executeUpdate(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                setParameters(stmt, params);
                return stmt.executeUpdate();
            } catch (SQLException e) {
                log.error("Execute update failed: {}", sql, e);
                throw new RuntimeException("Execute update failed", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Map<String, Object>> queryOne(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                setParameters(stmt, params);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return resultSetToMap(rs);
                }
                return null;
            } catch (SQLException e) {
                log.error("Query one failed: {}", sql, e);
                throw new RuntimeException("Query one failed", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Map<String, Object>>> queryList(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            List<Map<String, Object>> results = new ArrayList<>();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                setParameters(stmt, params);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    results.add(resultSetToMap(rs));
                }
                return results;
            } catch (SQLException e) {
                log.error("Query list failed: {}", sql, e);
                throw new RuntimeException("Query list failed", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Long> insert(String table, Map<String, Object> data) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder sql = new StringBuilder("INSERT INTO ");
            sql.append(table).append(" (");
            
            StringJoiner columns = new StringJoiner(", ");
            StringJoiner values = new StringJoiner(", ");
            List<Object> params = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                columns.add(entry.getKey());
                values.add("?");
                params.add(entry.getValue());
            }
            
            sql.append(columns).append(") VALUES (").append(values).append(")");
            
            try (PreparedStatement stmt = connection.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
                setParameters(stmt, params.toArray());
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return -1L;
            } catch (SQLException e) {
                log.error("Insert failed: {}", table, e);
                throw new RuntimeException("Insert failed", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Integer> update(String table, Map<String, Object> data, String whereClause, Object... whereParams) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder sql = new StringBuilder("UPDATE ");
            sql.append(table).append(" SET ");
            
            StringJoiner setClause = new StringJoiner(", ");
            List<Object> params = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                setClause.add(entry.getKey() + " = ?");
                params.add(entry.getValue());
            }
            
            sql.append(setClause);
            
            if (whereClause != null && !whereClause.isEmpty()) {
                sql.append(" WHERE ").append(whereClause);
                params.addAll(Arrays.asList(whereParams));
            }
            
            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                setParameters(stmt, params.toArray());
                return stmt.executeUpdate();
            } catch (SQLException e) {
                log.error("Update failed: {}", table, e);
                throw new RuntimeException("Update failed", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Integer> delete(String table, String whereClause, Object... whereParams) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(table);
            
            if (whereClause != null && !whereClause.isEmpty()) {
                sql.append(" WHERE ").append(whereClause);
            }
            
            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                if (whereParams != null && whereParams.length > 0) {
                    setParameters(stmt, whereParams);
                }
                return stmt.executeUpdate();
            } catch (SQLException e) {
                log.error("Delete failed: {}", table, e);
                throw new RuntimeException("Delete failed", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Long> count(String table, String whereClause, Object... whereParams) {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
            sql.append(table);
            
            if (whereClause != null && !whereClause.isEmpty()) {
                sql.append(" WHERE ").append(whereClause);
            }
            
            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                if (whereParams != null && whereParams.length > 0) {
                    setParameters(stmt, whereParams);
                }
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            } catch (SQLException e) {
                log.error("Count failed: {}", table, e);
                throw new RuntimeException("Count failed", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Boolean> tableExists(String tableName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DatabaseMetaData meta = connection.getMetaData();
                ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"});
                return rs.next();
            } catch (SQLException e) {
                log.error("Table exists check failed: {}", tableName, e);
                return false;
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> createTable(String tableName, Map<String, String> columns) {
        return CompletableFuture.runAsync(() -> {
            StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
            sql.append(tableName).append(" (");
            
            StringJoiner columnDefs = new StringJoiner(", ");
            for (Map.Entry<String, String> entry : columns.entrySet()) {
                columnDefs.add(entry.getKey() + " " + entry.getValue());
            }
            
            sql.append(columnDefs).append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql.toString());
                log.info("Table created: {}", tableName);
            } catch (SQLException e) {
                log.error("Create table failed: {}", tableName, e);
                throw new RuntimeException("Create table failed", e);
            }
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> executeScript(String script) {
        return CompletableFuture.runAsync(() -> {
            try (Statement stmt = connection.createStatement()) {
                String[] statements = script.split(";");
                for (String sql : statements) {
                    if (sql.trim().length() > 0) {
                        stmt.execute(sql.trim());
                    }
                }
            } catch (SQLException e) {
                log.error("Execute script failed", e);
                throw new RuntimeException("Execute script failed", e);
            }
        }, executor);
    }
    
    @Override
    public void beginTransaction() {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            log.error("Begin transaction failed", e);
        }
    }
    
    @Override
    public void commit() {
        try {
            connection.commit();
            connection.setAutoCommit(config.isAutoCommit());
        } catch (SQLException e) {
            log.error("Commit failed", e);
        }
    }
    
    @Override
    public void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(config.isAutoCommit());
        } catch (SQLException e) {
            log.error("Rollback failed", e);
        }
    }
    
    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            executor.shutdown();
            connected = false;
            log.info("MySQL database closed");
        } catch (SQLException e) {
            log.error("Close failed", e);
        }
    }
    
    @Override
    public boolean isConnected() {
        return connected;
    }
    
    @Override
    public String getDriverName() {
        return "MySQL";
    }
    
    @Override
    public String getDriverVersion() {
        return "1.0.0";
    }
    
    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
        }
    }
    
    private Map<String, Object> resultSetToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            map.put(meta.getColumnName(i), rs.getObject(i));
        }
        return map;
    }
}
