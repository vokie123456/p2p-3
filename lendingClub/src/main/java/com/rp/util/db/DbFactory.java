package com.rp.util.db;


import com.rp.util.ApplicationProperties;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbFactory
{
    private static final DbFactory DB_FACTORY = new DbFactory();
    private DbFactory()
    {}

    public static final DbFactory getInstance()
    {
        return DB_FACTORY;
    }

    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    public String getUrlWithoutCredentials(String db) throws IOException {
        // Read RDS connection information from the environment
        String dbName = ApplicationProperties.getInstance().getProperty(db+"."+"RDS_DB_NAME");
        String hostname = ApplicationProperties.getInstance().getProperty(db+"."+"RDS_HOSTNAME");
        String port = ApplicationProperties.getInstance().getProperty(db+"."+"RDS_PORT");
        //String driverClazz=ApplicationProperties.getInstance().getProperty(db+"."+"RDS_CLASS");
        return "jdbc:mysql://" + hostname + ":" + port + "/" + dbName ;
    }

    public Connection getConnection(String db)
    {
        String jdbcUrl=null;
        String driverClazz=null;
        try{
            // Read RDS connection information from the environment
            String dbName = ApplicationProperties.getInstance().getProperty(db+"."+"RDS_DB_NAME");
            String userName = ApplicationProperties.getInstance().getProperty(db+"."+"RDS_USERNAME");
            String password = ApplicationProperties.getInstance().getProperty(db+"."+"RDS_PASSWORD");
            String hostname = ApplicationProperties.getInstance().getProperty(db+"."+"RDS_HOSTNAME");
            String port = ApplicationProperties.getInstance().getProperty(db+"."+"RDS_PORT");
            driverClazz=ApplicationProperties.getInstance().getProperty(db+"."+"RDS_CLASS");
            jdbcUrl = "jdbc:mysql://" + hostname + ":" +
                    port + "/" + dbName + "?user=" + userName + "&password=" + password;
        } catch (IOException e) {
            throw new RuntimeException("Cannot generate jdbc url", e);
        }
        // Load the JDBC driver
        try {
            System.out.println("Loading driver...");
            Class.forName(driverClazz);
            System.out.println("Driver loaded!");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find the driver in the classpath!", e);
        }

        Connection conn=null;
        try {
            // Create connection to RDS DB instance
            conn = DriverManager.getConnection(jdbcUrl);

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return conn;
    }


    public static void main(String[] args) throws Exception
    {
        Connection conn =DbFactory.getInstance().getConnection("P2P");
        DbFactory.getInstance().closeConnection(conn);
    }
}
