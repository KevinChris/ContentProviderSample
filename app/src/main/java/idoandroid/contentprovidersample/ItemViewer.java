package idoandroid.contentprovidersample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import idoandroid.adapter.RecyclerViewAdapter;
import idoandroid.database.DatabaseHandler;
import idoandroid.model.Contact;

public class ItemViewer extends AppCompatActivity {

    private final static int DISPLAY_RETRIEVED_CONTACT = 0;
    private final static int NO_CONTACTS_TO_DISPLAY = 1;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    String receivedIntent;
    /**
     * Callback for the Handler to run in UI
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DISPLAY_RETRIEVED_CONTACT:
                    List<Contact> contactList = setItems(msg.obj);
                    recyclerViewAdapter = new RecyclerViewAdapter(ItemViewer.this, contactList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    break;

                case NO_CONTACTS_TO_DISPLAY:
                    Toast.makeText(ItemViewer.this, "No Records to Display", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_viewer);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Contact> contactList = new DatabaseHandler(this).retrieveContact();
        if (contactList != null && contactList.size() > 0) {
            handler.sendMessage(Message.obtain(handler, DISPLAY_RETRIEVED_CONTACT, contactList));
        } else
            handler.sendMessage(Message.obtain(handler, NO_CONTACTS_TO_DISPLAY));
    }

    public List<Contact> setItems(Object var) {
        List<Contact> result = new ArrayList<Contact>();
        if (var instanceof List) {
            for (int i = 0; i < ((List<?>) var).size(); i++) {
                Object item = ((List<?>) var).get(i);
                if (item instanceof Contact) {
                    result.add((Contact) item);
                }
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_viewer, menu);
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
            new AlertDialog.Builder(ItemViewer.this)
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
}
