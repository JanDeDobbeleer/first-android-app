package be.electrodoctor.electroman;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.List;

import be.electrodoctor.electroman.database.RepairContext;
import be.electrodoctor.electroman.database.SQLiteHelper;
import be.electrodoctor.electroman.model.Address;
import be.electrodoctor.electroman.model.Client;


public class MainActivity extends ActionBarActivity {

    private SQLiteHelper dbHelper;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        //create new instance of helper
        dbHelper = new SQLiteHelper(this);
        //feed database
        dbHelper.Feed();
        // get all clients
        List<Client> clients = dbHelper.getAllClients();
        Log.d("Received clients", Integer.toString(clients.size()) + " number of clients retrieved");

        //show the clients in the listview
        //TODO: find where to put this in order to have access to the activity stuff
        //displayListView();
    }

    private void displayListView() {
        Cursor cursor = dbHelper.getAllClientsCursor();
        cursor.moveToNext();

        // The desired columns to be bound
        String[] columns = new String[]{
                RepairContext.ClientEntry.COLUMN_NAME_NAME,
                RepairContext.AddressEntry.COLUMN_NAME_CITY
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[]{
                R.id.listView_Name,
                R.id.ListView_City
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.client_info,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.listView);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                // Get the state's capital from this row in the database.
                String countryCode = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Toast.makeText(getApplicationContext(), countryCode, Toast.LENGTH_SHORT).show();

            }
        });


        //Handle fintering for id's
        EditText myFilter = (EditText) findViewById(R.id.filter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.getAllClientsCursor();
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
