package io.aiven.examples;

import io.aiven.examples.types.StockData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Properties;

class PGRepository {
    private static final Logger logger = LogManager.getLogger();
    private static Connection connection;

    static void connect() {
        Properties pgConfig = Configs.get(
            Configs.get(Configs.APP_CONFIG_LOCATION).getProperty("pg.configs")
        );

        logger.trace("Opening Connection to PG");
        try {
            connection = DriverManager.getConnection(pgConfig.getProperty("jdbc.url"), pgConfig);
            logger.trace("Connection Established to PG");
        } catch (Exception e) {
            logger.error("Exception in PG Connection");
            throw new RuntimeException(e);
        }
    }

    static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("Exception in Closing Connection");
        }
    }

    static void showData(String tableName) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName);
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int nCols = rsMetaData.getColumnCount();

            logger.info("Show Table Data");
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= nCols; i++) {
                    row.append(rs.getString(i)).append((i == nCols) ? "" : ", ");
                }
                System.out.println(row);
            }
        } catch (Exception e) {
            logger.error("Exception in Fetching Data");
            throw new RuntimeException(e);
        }
    }

    static void saveStockData(String tableName, List<StockData> dataList) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableName +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");

            for (StockData data : dataList) {
                statement.setString(1, data.getSymbol());
                statement.setString(2, data.getSeries());
                statement.setDouble(3, data.getOpen());
                statement.setDouble(4, data.getHigh());
                statement.setDouble(5, data.getLow());
                statement.setDouble(6, data.getClose());
                statement.setDouble(7, data.getLast());
                statement.setDouble(8, data.getPreviousClose());
                statement.setDouble(9, data.getTotalTradedQty());
                statement.setDouble(10, data.getTotalTradedVal());
                statement.setDate(11, new java.sql.Date(data.getTradeDate().getTime()));
                statement.setDouble(12, data.getTotalTrades());
                statement.setString(13, data.getIsinCode());
                statement.addBatch();
            }
            statement.executeBatch();
            statement.close();
            logger.info("Saved a batch of  " + dataList.size() + " Records");

        } catch (SQLException e) {
            logger.error("Exception in Saving Data");
            throw new RuntimeException(e);
        }

    }

}
