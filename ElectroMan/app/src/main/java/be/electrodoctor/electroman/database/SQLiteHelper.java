package be.electrodoctor.electroman.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import be.electrodoctor.electroman.model.Address;
import be.electrodoctor.electroman.model.Client;
import be.electrodoctor.electroman.model.RepairJob;

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
                RepairContext.AddressEntry.COLUMN_NAME_CITY + " TEXT, " +
                RepairContext.AddressEntry.COLUMN_NAME_STREET + " TEXT, " +
                RepairContext.AddressEntry.COLUMN_NAME_NUMBER + " INTEGER, " +
                RepairContext.AddressEntry.COLUMN_NAME_POSTALCODE + " INTEGER)";

        // SQL statement to create client table
        String CREATE_CLIENT_TABLE = "CREATE TABLE " + RepairContext.ClientEntry.TABLE_NAME + " ( " +
                RepairContext.ClientEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RepairContext.ClientEntry.COLUMN_NAME_NAME + " TEXT, "+
                RepairContext.ClientEntry.COLUMN_NAME_ADDRESS + " INTEGER, " +
                "FOREIGN KEY (" + RepairContext.ClientEntry.COLUMN_NAME_ADDRESS + ") REFERENCES " + RepairContext.AddressEntry.TABLE_NAME + " (" + RepairContext.AddressEntry.COLUMN_NAME_ID + "));";

        String CREATE_REPAIRJOB_TABLE = "CREATE TABLE " + RepairContext.RepairJobEntry.TABLE_NAME + " ( " +
                RepairContext.RepairJobEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RepairContext.RepairJobEntry.COLUMN_NAME_CODE + " INTEGER, "+
                RepairContext.RepairJobEntry.COLUMN_NAME_CLIENT_ID + " INTEGER, " +
                RepairContext.RepairJobEntry.COLUMN_NAME_DESCRIPTION + " TEXT, " +
                RepairContext.RepairJobEntry.COLUMN_NAME_COMMENT + " TEXT, " +
                RepairContext.RepairJobEntry.COLUMN_NAME_DEVICE + " TEXT, " +
                RepairContext.RepairJobEntry.COLUMN_NAME_PROCESSED + " INTEGER, " +
                "FOREIGN KEY (" + RepairContext.RepairJobEntry.COLUMN_NAME_CLIENT_ID + ") REFERENCES " + RepairContext.ClientEntry.TABLE_NAME + " (" + RepairContext.ClientEntry.COLUMN_NAME_ID + "));";

        // create books table
        db.execSQL(CREATE_ADDRESS_TABLE);
        db.execSQL(CREATE_CLIENT_TABLE);
        db.execSQL(CREATE_REPAIRJOB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + RepairContext.ClientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RepairContext.AddressEntry.TABLE_NAME);

        // create fresh tables
        this.onCreate(db);
    }

    public long addClient(Client client){
        Log.d("addClient", client.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        long addressId = -1;

        if(client.getAddress() != null){
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
        long id = db.insert(RepairContext.ClientEntry.TABLE_NAME, null, clientValues);

        // 4. close
        db.close();
        return id;
    }

    public long addRepairJob(RepairJob repairJob){
        Log.d("addRepairJob", repairJob.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        if(repairJob.getClient() == null)
            return -1;

        ContentValues jobValues = new ContentValues();
        jobValues.put(RepairContext.RepairJobEntry.COLUMN_NAME_CODE, repairJob.getProblemCode());
        jobValues.put(RepairContext.RepairJobEntry.COLUMN_NAME_CLIENT_ID, repairJob.getClient().getId());
        jobValues.put(RepairContext.RepairJobEntry.COLUMN_NAME_DESCRIPTION, repairJob.getDescription());
        jobValues.put(RepairContext.RepairJobEntry.COLUMN_NAME_DEVICE, repairJob.getDevice());
        jobValues.put(RepairContext.RepairJobEntry.COLUMN_NAME_PROCESSED, repairJob.isProcessed());

        // 3. insert
        long id = db.insert(RepairContext.RepairJobEntry.TABLE_NAME, null, jobValues);

        // 4. close
        db.close();
        return id;
    }

    public Client getClient(long id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        final String MY_QUERY = "SELECT * FROM " + RepairContext.ClientEntry.TABLE_NAME + " a INNER JOIN " + RepairContext.AddressEntry.TABLE_NAME + " b " +
                                "ON a." + RepairContext.ClientEntry.COLUMN_NAME_ADDRESS + "=b." + RepairContext.AddressEntry.COLUMN_NAME_ID +
                                " WHERE a." + RepairContext.ClientEntry.COLUMN_NAME_ID + "=?";
        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{String.valueOf(id)});

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        // 4. build client entity
        Client client = buildEntityFromCursor(cursor);

        //log
        Log.d("getClient(" + id + ")", client.toString());

        // 5. return entity
        return client;
    }

    // Get All Clients
    public List<Client> getAllClients() {
        List<Client> clients = new LinkedList<Client>();

        Cursor cursor = getAllClientsCursor();

        // 3. go over each row, build book and add it to list
        if (cursor.moveToFirst()) {
            do {
                clients.add(buildEntityFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        Log.d("getAllClients()", Integer.toString(clients.size()) + " clients");

        // return books
        return clients;
    }

    public Cursor getAllClientsCursor() {
        // 1. build the query
        String query = "SELECT  a." + RepairContext.ClientEntry.COLUMN_NAME_ID + " _id, * FROM " + RepairContext.ClientEntry.TABLE_NAME + " a INNER JOIN " + RepairContext.AddressEntry.TABLE_NAME + " b " +
                       "ON a." + RepairContext.ClientEntry.COLUMN_NAME_ADDRESS + "=b." + RepairContext.AddressEntry.COLUMN_NAME_ID + " INNER JOIN " +
                       RepairContext.RepairJobEntry.TABLE_NAME + " c ON c." + RepairContext.RepairJobEntry.COLUMN_NAME_CLIENT_ID + " = a." + RepairContext.ClientEntry.COLUMN_NAME_ID;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        return cursor;
    }

    public Cursor getAllClientsCursor(String criteria) {
        // 1. build the query
        String query = "SELECT  a." + RepairContext.ClientEntry.COLUMN_NAME_ID + " _id, * FROM " + RepairContext.ClientEntry.TABLE_NAME + " a INNER JOIN " + RepairContext.AddressEntry.TABLE_NAME + " b " +
                       "ON a." + RepairContext.ClientEntry.COLUMN_NAME_ADDRESS + "=b." + RepairContext.AddressEntry.COLUMN_NAME_ID + " INNER JOIN " +
                        RepairContext.RepairJobEntry.TABLE_NAME + " c ON c." + RepairContext.RepairJobEntry.COLUMN_NAME_CLIENT_ID + " = a." + RepairContext.ClientEntry.COLUMN_NAME_ID +
                        " WHERE a." + RepairContext.ClientEntry.COLUMN_NAME_NAME + " like '%" + criteria + "%' OR b." + RepairContext.AddressEntry.COLUMN_NAME_CITY + " like '%" + criteria + "%'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        return cursor;
    }

    private Client buildEntityFromCursor(Cursor cursor){
        Client client = new Client();
        client.setId(cursor.getInt(0));
        client.setName(cursor.getString(1));
        //get address
        Address address = new Address();
        address.setId(cursor.getInt(2));
        address.setCity(cursor.getString(2));
        address.setStreet(cursor.getString(2));
        address.setNumber(cursor.getInt(2));
        address.setPostalCode(cursor.getInt(2));
        client.setAddress(address);
        return client;
    }

    public void Feed(){

        //delete all records and recreate the thing
        final String dropTable = "DELETE FROM %s;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(String.format(dropTable, RepairContext.AddressEntry.TABLE_NAME), null);
        cursor.moveToNext();
        cursor = db.rawQuery(String.format(dropTable, RepairContext.ClientEntry.TABLE_NAME), null);
        cursor.moveToNext();

        //client1
        Client client = new Client();
        client.setName("Jan");
        Address address = new Address();
        address.setStreet("Fabiolalaan");
        address.setNumber(27);
        address.setPostalCode(3290);
        address.setCity("Diest");
        client.setAddress(address);
        client.setId(this.addClient(client));
        RepairJob job = new RepairJob();
        job.setClient(client);
        job.setDescription("The damn thing blew up and there was a curious case of green creatures with one of them wearing a white mohawk");
        job.setDevice("Samsung Microwave");
        job.setProblemCode(12);
        job.setProcessed(false);
        this.addRepairJob(job);


        //client2
        client = new Client();
        client.setName("Katrien");
        address = new Address();
        address.setStreet("Fabiolalaan");
        address.setNumber(27);
        address.setPostalCode(3290);
        address.setCity("Diest");
        client.setAddress(address);
        client.setId(this.addClient(client));
        job = new RepairJob();
        job.setClient(client);
        job.setDescription("Smoke, black smoke. That's how I know it was still burning on the inside. Very disappointed with this device.");
        job.setDevice("Nova hairdryer");
        job.setProblemCode(24);
        job.setProcessed(false);
        this.addRepairJob(job);

        //client 3
        client = new Client();
        client.setName("Tom");
        address = new Address();
        address.setStreet("Geldenaaksebaan");
        address.setNumber(214);
        address.setPostalCode(3000);
        address.setCity("Leuven");
        client.setAddress(address);
        client.setId(this.addClient(client));
        job = new RepairJob();
        job.setClient(client);
        job.setDescription("It stopped working after our son played with it. This should be kid resistant, right? We hope it can still be fixed under warranty. Otherwise I might have to tweet my experience.");
        job.setDevice("Philips coffee machine");
        job.setProblemCode(66);
        job.setProcessed(false);
        this.addRepairJob(job);


        client = new Client();
        client.setName("Monique");
        address = new Address();
        address.setStreet("Rodenemweg");
        address.setNumber(134);
        address.setPostalCode(1500);
        address.setCity("Halle");
        client.setAddress(address);
        client.setId(this.addClient(client));
        job = new RepairJob();
        job.setClient(client);
        job.setDescription("It used to work like a charm until last saturday. Grandma was visiting us and we could not get it to work. The device I mean, grandma is fine.");
        job.setDevice("Samsung kitchen robot");
        job.setProblemCode(24);
        job.setProcessed(false);
        this.addRepairJob(job);

        client = new Client();
        client.setName("Joris");
        address = new Address();
        address.setStreet("Lindenhof");
        address.setNumber(19);
        address.setPostalCode(3290);
        address.setCity("Diest");
        client.setAddress(address);
        client.setId(this.addClient(client));
        job = new RepairJob();
        job.setClient(client);
        job.setDescription("I'm lonely, I just wanted someone to visit me so I broke it. Please add me on Facebook.");
        job.setDevice("Beko freezer");
        job.setProblemCode(24);
        job.setProcessed(false);
        this.addRepairJob(job);

        client = new Client();
        client.setName("Fred");
        address = new Address();
        address.setStreet("Prins de Lignestraat");
        address.setNumber(20);
        address.setPostalCode(3000);
        address.setCity("Leuven");
        client.setAddress(address);
        client.setId(this.addClient(client));
        job = new RepairJob();
        job.setClient(client);
        job.setDescription("Please help me, I have no idea how to use this. I mean, it should just work, right?");
        job.setDevice("Siemens fridge");
        job.setProblemCode(24);
        job.setProcessed(false);
        this.addRepairJob(job);

        client = new Client();
        client.setName("Arnout");
        address = new Address();
        address.setStreet("Elisabethlaan");
        address.setNumber(56);
        address.setPostalCode(3200);
        address.setCity("Aarschot");
        client.setAddress(address);
        client.setId(this.addClient(client));
        job = new RepairJob();
        job.setClient(client);
        job.setDescription("Now look, it's not because I live in Aarschot that you people have to go on and eel me stuff that does not work. This is unacceptable!");
        job.setDevice("Wireless mouse");
        job.setProblemCode(24);
        job.setProcessed(false);
        this.addRepairJob(job);

        db.close();
    }
}
