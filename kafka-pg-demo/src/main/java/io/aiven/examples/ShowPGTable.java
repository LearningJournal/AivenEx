package io.aiven.examples;

public class ShowPGTable {
    public static void main(String[] args) {
        String tableName = Configs.get(Configs.APP_CONFIG_LOCATION).getProperty("pg.table");
        PGRepository.connect();
        PGRepository.showData(tableName);
        PGRepository.close();
    }
}
