package be.electrodoctor.electroman.database;

import android.provider.BaseColumns;

/**
 * Created by janjoris on 27/01/15.
 */
public class RepairContext {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public RepairContext() {}

    /* Inner classes that defines the table contents */

    /*private int id;
    private String city;
    private String street;
    private int number;
    private int postalCode;*/

    public static abstract class AddressEntry implements BaseColumns {
        public static final String TABLE_NAME = "address";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_STREET = "street";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_POSTALCODE = "postalCode";
    }

    /*private int id;
    private Address address;
    private String name;*/

    public static abstract class ClientEntry implements BaseColumns {
        public static final String TABLE_NAME = "client";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ADDRESS = "addressId";
        public static final String COLUMN_NAME_NAME = "name";
    }
}
