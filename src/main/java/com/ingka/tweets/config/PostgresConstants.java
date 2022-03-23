package com.ingka.tweets.config;

public class PostgresConstants {

    public static final long BATCH_SIZE_DEFAULT = 500l;
    public static final String TIME_STAMP_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";
    /**
     * hiding default constructor
     */
    private PostgresConstants() {
        super();
    }
}
