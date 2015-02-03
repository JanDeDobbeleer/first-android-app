package be.electrodoctor.electroman.common;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import be.electrodoctor.electroman.R;
import be.electrodoctor.electroman.database.RepairContext;
import be.electrodoctor.electroman.dialogs.ProcessDialog;

/**
 * Created by janjoris on 03/02/15.
 */
public class CustomAdapter extends SimpleCursorAdapter {

    public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.client_info, null);
        Button btn = (Button)view.findViewById(R.id.Button_Processed);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent().getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                // Get the state's capital from this row in the database.
                String repairId = cursor.getString(cursor.getColumnIndexOrThrow(RepairContext.RepairJobEntry.COLUMN_NAME_ID));
                //Toast.makeText(v.getContext(), repairId, Toast.LENGTH_SHORT).show();
                ProcessDialog dialog = new ProcessDialog();
                dialog.show(((Activity)context).getFragmentManager(), "ProcessedDialog");
            }
        });
        return view;
    }
}
