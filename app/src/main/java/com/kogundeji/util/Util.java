package com.kogundeji.util;

public class Util {

    //Database related items
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "optionsDB";
    public static final String TABLE_NAME = "options";

    //Option table column names
    public static final String KEY_ID = "id";
    public static final String KEY_TICKER = "ticker";
    public static final String KEY_CURRENT = "current_price";
    public static final String KEY_STRIKE = "strike_price";
    public static final String KEY_VOL = "volatility";
    public static final String KEY_RFRATE = "rf_rate";
    public static final String KEY_EXPIRATION = "expiration";

}
