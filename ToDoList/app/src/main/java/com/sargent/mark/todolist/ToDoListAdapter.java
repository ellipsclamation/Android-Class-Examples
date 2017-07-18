package com.sargent.mark.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.ToDoItem;


/**
 * Created by mark on 7/4/17.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ItemHolder>{

    private CheckboxClickListener mCheckboxClickListener;

    private Cursor cursor;
    private ItemClickListener listener;
    private String TAG = "todolistadapter";

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public interface ItemClickListener {
        void onItemClick(int pos, String description, String duedate, String category, boolean isDone, long id);
    }

    //An interface to send the checkbox and id to the mainactivity
    public interface CheckboxClickListener {
        void onUpdateIsDone(boolean isDone, long id);
    }

    //added context from mainactivity to connect interface
    public ToDoListAdapter(Cursor cursor, ItemClickListener listener, Context context) {
        this.cursor = cursor;
        this.listener = listener;
        this.mCheckboxClickListener = (CheckboxClickListener) context;
    }

    public void swapCursor(Cursor newCursor){
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView descr;
        TextView due;
        TextView displayCategory;
        CheckBox displayIsDone;
        String duedate;
        String description;
        String category;
        Boolean isDone;
        long id;


        ItemHolder(View view) {
            //added textview for category and checkbox for isDone
            super(view);
            displayCategory = (TextView) view.findViewById(R.id.displayCategory);
            descr = (TextView) view.findViewById(R.id.description);
            due = (TextView) view.findViewById(R.id.dueDate);
            displayIsDone = (CheckBox) view.findViewById(R.id.isDone);
            view.setOnClickListener(this);
        }

        public void bind(ItemHolder holder, int pos) {
            cursor.moveToPosition(pos);
            id = cursor.getLong(cursor.getColumnIndex(Contract.TABLE_TODO._ID));
            Log.d(TAG, "deleting id: " + id);

            duedate = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE));
            description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION));
            category = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY));
            //convert int to boolean
            isDone = cursor.getInt(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_IS_DONE)) > 0;

            //displays category
            displayCategory.setText(category);
            descr.setText(description);
            due.setText(duedate);
            displayIsDone.setChecked(isDone);
            holder.itemView.setTag(id);

            //sends state of checkbox to interface when checkbox is clicked
            displayIsDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    mCheckboxClickListener.onUpdateIsDone(displayIsDone.isChecked(), id);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos, description, duedate, category, isDone, id);
        }
    }
}
