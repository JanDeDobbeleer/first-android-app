package be.electrodoctor.electroman;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import be.electrodoctor.electroman.database.RepairContext;
import be.electrodoctor.electroman.database.SQLiteHelper;

/**
* Created by janjoris on 01/02/15.
*/
public class MainListViewFragment extends Fragment {

    private SQLiteHelper dbHelper;

    public MainListViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        dbHelper = new SQLiteHelper(getActivity());
        displayListView(rootView);
        return rootView;
    }



    private void displayListView(View rootView) {
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
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
                this.getActivity(), R.layout.client_info,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
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
                Toast.makeText(getActivity(), countryCode, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
