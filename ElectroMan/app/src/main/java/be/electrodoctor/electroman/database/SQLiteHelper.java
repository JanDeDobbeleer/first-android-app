package be.electrodoctor.electroman.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import be.electrodoctor.electroman.model.Client;

/**
 * Created by janjoris on 27/01/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RepairDb";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // SQL statement to create address table
        String CREATE_ADDRESS_TABLE = "CREATE TABLE " + RepairContext.AddressEntry.TABLE_NAME + " ( " +
                RepairContext.AddressEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RepairContext.AddressEntry.COLUMN_NAME_CITY + "TEXT, " +
                RepairContext.AddressEntry.COLUMN_NAME_STREET + " TEXT, " +
                RepairContext.AddressEntry.COLUMN_NAME_NUMBER + " INTEGER, " +
                RepairContext.AddressEntry.COLUMN_NAME_POSTALCODE + " INTEGER";

        // SQL statement to create client table
        String CREATE_CLIENT_TABLE = "CREATE TABLE " + RepairContext.ClientEntry.TABLE_NAME + " ( " +
                RepairContext.ClientEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RepairContext.ClientEntry.COLUMN_NAME_NAME + " TEXT, "+
                RepairContext.ClientEntry.COLUMN_NAME_ADDRESS + " INTEGER" +
                "FOREIGN KEY (" + RepairContext.ClientEntry.COLUMN_NAME_ADDRESS + ") REFERENCES " + RepairContext.AddressEntry.TABLE_NAME + " (" + RepairContext.AddressEntry.COLUMN_NAME_ID + "));";

        // create books table
        db.execSQL(CREATE_ADDRESS_TABLE);
        db.execSQL(CREATE_CLIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + RepairContext.ClientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RepairContext.AddressEntry.TABLE_NAME);

        // create fresh tables
        this.onCreate(db);
    }

    public void addClient(Client client){
        Log.d("addClient", client.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        long addressId = -1;

        if(client.getAddress() != null) {
            ContentValues addressValues = new ContentValues();
            addressValues.put(RepairContext.AddressEntry.COLUMN_NAME_STREET, client.getAddress().getStreet());
            addressValues.put(RepairContext.AddressEntry.COLUMN_NAME_NUMBER, client.getAddress().getNumber());
            addressValues.put(RepairContext.AddressEntry.COLUMN_NAME_POSTALCODE, client.getAddress().getPostalCode());
            addressValues.put(RepairContext.AddressEntry.COLUMN_NAME_CITY, client.getAddress().getCity());
            addressId = db.insert(RepairContext.AddressEntry.TABLE_NAME, null, addressValues);
        }

        // 2. create ContentValues to add key "column"/value
        ContentValues clientValues = new ContentValues();
        clientValues.put(RepairContext.ClientEntry.COLUMN_NAME_NAME, client.getName());
        if(addressId > 0) //only add if needed
            clientValues.put(RepairContext.ClientEntry.COLUMN_NAME_ADDRESS, addressId);

        // 3. insert
        db.insert(RepairContext.ClientEntry.TABLE_NAME, null, clientValues);

        // 4. close
        db.close();
    }
}
