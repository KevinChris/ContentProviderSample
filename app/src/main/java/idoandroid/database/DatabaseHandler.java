package idoandroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import idoandroid.model.Contact;

/**
 * Created by Kevin Chris on 17-Aug-15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    SQLiteDatabase db;

    /**
     * Constructor
     * @param context context
     */
    public DatabaseHandler(Context context) {
        super(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
    }

    /**
     * Create an instance of an application
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getInstance() {
        if (db != null)
            return db;
        else
            return db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseHelper.CREATE_TABLE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_CONTACTS);

        onCreate(db);
    }

    /**
     * Add the new contact to the database
     * @param contentValues values to be added in database row
     * @return rowId
     */
    public long addContact(ContentValues contentValues) {
        getInstance();
        long value = db.insert(DatabaseHelper.TABLE_CONTACTS, null, contentValues);
        db.close();
        return value;
    }

    /**
     * Retrieve all the contact from table
     * @return List of Contact
     */
    public List<Contact> retrieveContact() {
        List<Contact> contactList = new ArrayList<>();
        getInstance();
        Cursor cursor = db.query(DatabaseHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        if (cursor != null)
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                /**
                 * Iterate the cursor and prepare the list
                 */
                for (int i = 0 ; i < cursor.getCount() ; i++) {
                    Contact contact = new Contact();
                    contact.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.ID)));
                    contact.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.NAME)));
                    contact.setPhoneNo(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PHONE_NUMBER))));
                    contact.setIsSynced(Boolean.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CLOUD_SYNCED))));
                    contactList.add(contact);
                    cursor.moveToNext();
                }
            }
        if (cursor != null) cursor.close();

        return contactList;
    }
}
