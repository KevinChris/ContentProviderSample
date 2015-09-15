package idoandroid.contentprovidersample;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import idoandroid.database.DatabaseHandler;
import idoandroid.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int CONTACT_SAVE_SUCCESSFUL = 1;
    private final static int CONTACT_SAVE_FAILED = 2;
    EditText editTextName, editTextContact;
    TextInputLayout layoutName, layoutContact;
    Button buttonSave;
    /**
     * Callback for the Handler to run in UI
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {

                case CONTACT_SAVE_SUCCESSFUL:
                    Toast.makeText(MainActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                    break;

                case CONTACT_SAVE_FAILED:
                    Toast.makeText(MainActivity.this, "Contact Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextContact = (EditText) findViewById(R.id.editTextContact);

        buttonSave = (Button) findViewById(R.id.button);
        buttonSave.setOnClickListener(this);

        layoutName = (TextInputLayout) findViewById(R.id.layoutName);
        layoutName.setErrorEnabled(true);
        layoutContact = (TextInputLayout) findViewById(R.id.layoutContact);
        layoutContact.setErrorEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getString(R.string.about_title))
                    .setMessage(getString(R.string.about_message))
                    .setPositiveButton(getString(R.string.about_dismiss),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the contact
     * @param view view of the Button
     */
    public void saveContact(View view) {

        /**
         * Validate the fields
         */
        if (validateFields()) {
            /**
             * Perform database operation in thread
             */
            new Thread(new Runnable() {
                @Override
                public void run() {

                    /**
                     * Save contact in database
                     */
                    DatabaseHandler dbHandler = new DatabaseHandler(MainActivity.this);

                    ContentValues values = new ContentValues();

                    values.put(DatabaseHelper.NAME, editTextName.getText().toString());
                    values.put(DatabaseHelper.PHONE_NUMBER, editTextContact.getText().toString());
                    values.put(DatabaseHelper.CLOUD_SYNCED, generateRandom());

                    /**
                     * Callback to display the message in UI
                     */
                    if (dbHandler.addContact(values) != -1)
                        handler.sendMessage(Message.obtain(handler, CONTACT_SAVE_SUCCESSFUL));
                    else
                        handler.sendMessage(Message.obtain(handler, CONTACT_SAVE_FAILED));
                }
            }).start();
        }
    }

    /**
     * Generate Random Number to set the sync state of contact
     *
     * @return 0 or 1
     */
    private int generateRandom() {
        return (Math.random() > 0.5) ? 1 : 0;
    }

    /**
     * Validates all the fields
     * @return boolean
     */
    private boolean validateFields() {
        boolean isValidated = true;

        if (editTextName.getText().toString().contentEquals("")) {
            layoutName.setError("Name can't be empty");
            isValidated = false;
        } else {
            layoutName.setError(null);
            layoutName.setErrorEnabled(false);
        }

        if (editTextContact.getText().toString().contentEquals("")) {
            layoutContact.setError("Contact can't be empty");
            isValidated = false;
        } else if (editTextContact.getText().toString().length() < 10) {
            layoutContact.setError("Contact Number must have 10 digits");
            isValidated = false;
        } else {
            layoutContact.setError(null);
            layoutName.setErrorEnabled(false);
        }

        return isValidated;
    }

    /**
     * Retrieve the all the data from the table
     * @param view view of the button
     */
    public void retrieveAll(View view) {

        Intent intent = new Intent(MainActivity.this, ItemViewer.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                saveContact(v);
                break;
        }
    }
}
