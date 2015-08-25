package idoandroid.contentprovidersample;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;

import idoandroid.database.DatabaseHandler;
import idoandroid.database.DatabaseHelper;

/**
 * Created by KevinChris on 17-Aug-15.
 */
public class MyContentProvider extends ContentProvider {

    /**
     * Authority name can be of any constant String.
     */
    private final static String AUTHORITY_NAME = "ContentProviderSample.Contacts";

    /**
     * Base name can be of any constant String
     */
    private final static String BASE_NAME = "iDoAndroid";

    /**
     * Constants
     */
    private static final int CONTACTS = 1;
    private static final int CONTACTS_GROUP_BY = 2;

    /**
     * Utility class uses to match the URI of ContentProviders
     */
    private static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY_NAME, BASE_NAME + "contacts", CONTACTS);
        URI_MATCHER.addURI(AUTHORITY_NAME, BASE_NAME + "contacts/sync", CONTACTS_GROUP_BY);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    /**
     * Query method is used to query the database of an application and the result will be returned
     * to the requested application.
     *
     * @param uri           uri to access the application
     * @param projection    list of column in the given table
     * @param selection     selection string
     * @param selectionArgs selection arguments
     * @param sortOrder     sorting order of the results
     * @return              cursor
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get the instance of SQLiteDatabase
        SQLiteDatabase db = new DatabaseHandler(getContext()).getInstance();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor = null;

        // Match the uri
        int uriType = URI_MATCHER.match(uri);

        // Check whether requested columns are present in database. If not then throw exception
        checkColumns(projection, uriType);

        switch (uriType) {
            case CONTACTS :
                /**
                 * setTables is to set the list of tables to be queried.
                 */
                queryBuilder.setTables(DatabaseHelper.TABLE_CONTACTS);

                /**
                 * Now query it accordingly as per the requirement. In CONTACTS I will retrieve all
                 * the contacts from table. So selection, selectionArgs, having and groupBy are null
                 */
                cursor = queryBuilder.query(db, projection, null, null, null, null, sortOrder);

                /**
                 * This is register to watch the content uri changes.
                 */
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case CONTACTS_GROUP_BY:
                /**
                 * setTables is to set the list of tables to be queried.
                 */
                queryBuilder.setTables(DatabaseHelper.TABLE_CONTACTS);

                /**
                 * Now query it accordingly as per the requirement. Unless like CONTACTS, in
                 * CONTACTS_GROUP_BY I will retrieve all the contacts from table. So the query
                 * result will be grouped based on CLOUD_SYNCED data
                 */
                cursor = queryBuilder.query(db, projection, selection, selectionArgs,
                        DatabaseHelper.CLOUD_SYNCED, null, sortOrder);

                /**
                 * This is register to watch the content uri changes.
                 */
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * Insert method is used to insert row into database from an other application
     *
     * @param uri       uri reference in an application
     * @param values    data to be inserted as content values
     * @return          uri
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase db = new DatabaseHandler(getContext()).getInstance();
        long rowId;
        switch (uriType) {
            case CONTACTS:
                    rowId = db.insert(DatabaseHelper.TABLE_CONTACTS, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_NAME + "CONTACTS/" + rowId);
    }

    /**
     * Delete the row's from the database
     *
     * @param uri           uri of an reference
     * @param selection     selection statement to delete query
     * @param selectionArgs selection arguments to delete query
     * @return              integer, no of rows affected
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase db = new DatabaseHandler(getContext()).getInstance();
        int rowsDeleted;
        switch (uriType) {
            case CONTACTS:
                rowsDeleted = db.delete(DatabaseHelper.TABLE_CONTACTS, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    /**
     * Update the row's in the database
     * @param uri           uri reference to the process
     * @param values        content values to be updated
     * @param selection     where clause condition
     * @param selectionArgs arguments for the selection clause
     * @return              integer, number of rows affected (updated)
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase db = new DatabaseHandler(getContext()).getInstance();
        int rowsUpdated;

        switch (uriType) {
            case CONTACTS:
                rowsUpdated = db.update(DatabaseHelper.TABLE_CONTACTS, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    /**
     * Reads the internal directory and folders of an application
     * @param uri   uri reference to an application
     * @param mode  access mode of the file
     * @return      ParseFileDescriptor of a file
     * @throws FileNotFoundException
     */
    public ParcelFileDescriptor fileDescriptor(Uri uri, String mode) throws FileNotFoundException {

        /**
         * Get the files directory of an application
         */
        File filesDir = getContext().getFilesDir();

        /**
         * Path to any of the folder or files can be sent along with uri
         * This happens in case of dynamic or runtime path or file name
         */
        String path = uri.getLastPathSegment();

        /**
         * File with the internal storage directory along with path name
         */
        File file = new File(filesDir, path);

        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    /**
     * Check whether the request Uri contains all requested columns. If the column which is not
     * available is requested then throw exception
     *
     * @param projection columnList from content resolver application.
     * @param uriType    type of Uri which is requested.
     */
    private void checkColumns(String[] projection, int uriType) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns;
            switch (uriType) {
                case CONTACTS:
                case CONTACTS_GROUP_BY:
                    availableColumns = new HashSet<>(Arrays.asList(DatabaseHelper.CONTACT_COLUMNS));
                    break;

                default:
                    throw new IllegalArgumentException("Unknown columns in projection");

            }

            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
