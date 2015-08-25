package idoandroid.database;

/**
 * Created by KevinChris on 17-Aug-15.
 */
public class DatabaseHelper {

    public final static String DATABASE_NAME = "ContactsManager";
    public final static int DATABASE_VERSION = 1;

    /**
     * TableName
     */
    public final static String TABLE_CONTACTS = "contacts";

    /**
     * ColumnsName
     */
    public final static String ID = "id";
    public final static String NAME = "name";
    public final static String PHONE_NUMBER = "phone_number";
    public final static String CLOUD_SYNCED = "cloud_synced";

    public static final String[] CONTACT_COLUMNS = {
            ID, NAME, PHONE_NUMBER, CLOUD_SYNCED
    };

    public final static String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_CONTACTS + "("
            + ID + " INTEGER PRIMARY KEY," + NAME + " TEXT,"
            + PHONE_NUMBER + " TEXT," + CLOUD_SYNCED + " BOOLEAN " + ")";
}
