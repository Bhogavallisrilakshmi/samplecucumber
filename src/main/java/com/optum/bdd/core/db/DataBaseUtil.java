package com.optum.bdd.core.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class DataBaseUtil {

    JdbcTemplate jdbcTemplate;
    SimpleDriverDataSource dataSource;
    SqlRowSet results;
    String username;
    String password;
    String url;
    String dbClient;
    int UpdateResult;
    private static Logger log = Logger.getLogger(DataBaseUtil.class.getName());

    public DataBaseUtil(String userName, String password, String url, String dbclient) {

        this.username = userName;
        this.password = password;
        this.url = url;
        this.dbClient = dbclient.toUpperCase().trim();

        switch (dbClient) {

            case "ORACLE":
                dataSource = new SimpleDriverDataSource();
                dataSource.setDriver(new oracle.jdbc.driver.OracleDriver());
                break;

            case "MARIA":
                dataSource = new SimpleDriverDataSource();
                dataSource.setDriver(new org.mariadb.jdbc.Driver());
                break;

            default:
                log.error(
                        "DataBase Bean INITIALIZATION FAILED as DBCLIENT value is null in projec.properties file.");
                throw new NullPointerException("DBClient value is null");
        }

        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        jdbcTemplate = new JdbcTemplate(dataSource);

        log.debug(
                "********************** DataBase Bean INITIALIZED *******************************************");

    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }


    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }


    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }


    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * @return the dbClient
     */
    public String getDbClient() {
        return dbClient;
    }


    /**
     * @param dbClient the dbClient to set
     */
    public void setDbClient(String dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * @return the datasource
     */
    public DataSource getDataSource() {
        return jdbcTemplate.getDataSource();
    }

    /**
     * Updates the datsource with the new values
     */
    public void setDataSource() {

        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        jdbcTemplate.setDataSource(dataSource);
    }

    /**
     * This method executes the database query and returns the result in SqlRowSet datatype
     * 
     * @param query - String variable containing the query to be executed
     * @return - SqlRowSet similar to ResultSet of java.sql
     */
    public SqlRowSet executeQuery(String query) {

        log.debug("Executing Query : " + query);
        results = jdbcTemplate.queryForRowSet(query);

        if (log.isDebugEnabled()) {
            log.debug("Sql Response" + results);
        }
        return results;
    }

    /**
     * This method is used to update/delete contents in DB table
     * 
     * @param query - String variable containing the query to be executed
     * @return - a string "Success" or "Failure"
     */
    public String UpdateQuery(String query) {

        log.debug("Executing Update Query : " + query);

        UpdateResult = jdbcTemplate.update(query);

        if (UpdateResult == 1) {
            return "Success";
        } else {
            return "Failure";
        }

    }

    public static List<LinkedHashMap<String, Object>> toList(SqlRowSet rowSet) {

        List<LinkedHashMap<String, Object>> rows = new ArrayList<>();

        String[] columnNames = rowSet.getMetaData().getColumnNames();

        rowSet.beforeFirst();

        while (rowSet.next()) {

            try {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();

                for (String s : columnNames) {
                    map.put(s, rowSet.getObject(s));
                }

                rows.add(map);
            } catch (Exception ex) {
                log.debug("Unable to serialize row for SqlRowSet");
            }

        }

        rowSet.beforeFirst();

        return rows;

    }

}
