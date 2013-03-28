package com.gunnarro.android.smsfilter.view;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.gunnarro.android.smsfilter.AppPreferences;

public class ListConfigurationActivity extends ListActivity {

    protected AppPreferences appPreferences;
    private String type;

    private int addBtnId;
    private int delBtnId;
    private int refreshBtnId;
    private int inputFieldId;

    /** Items entered by the user is stored in this ArrayList variable */
    private List<Item> localList;

    /** Declaring an ArrayAdapter to set items to ListView */
    protected ArrayAdapter<Item> adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    protected void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        Item item = localList.get(pos);
        SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
        if (checkedItemPositions.get(pos)) {
            item.setEnabled(true);
        } else {
            item.setEnabled(false);
        }

        // Log.d(this.getClass().getSimpleName(), pos + " - " + id);
        // Object o = getListView().getItemAtPosition(pos);
        // Log.d(this.getClass().getSimpleName(), "obj.    " + o.toString());
        // Log.d(this.getClass().getSimpleName(), "local:" +
        // item.toValuePair());
        // // getListView().setItemChecked(pos, item.isEnabled());
        // Item item2 = adapter.getItem(pos);
        // Log.d(this.getClass().getSimpleName(), "adapter: " +
        // item.toValuePair());
        Log.d(this.getClass().getSimpleName(), "local:" + item.toValuePair());
        adapter.notifyDataSetChanged();
    }

    protected void setAppPreferences(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
        this.localList = appPreferences.getList(type);
    }

    protected void setupEventHandlers() {
        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_multiple_choice, localList);
        // adapter.setNotifyOnChange(true);
        /** Setting the adapter to the ListView */
        setListAdapter(adapter);

        findViewById(addBtnId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputField = (EditText) findViewById(inputFieldId);
                String value = inputField.getText().toString();
                if (!value.isEmpty()) {
                    Item newItem = new Item(value, false);
                    if (addLocalList(newItem)) {
                        adapter.notifyDataSetChanged();
                        appPreferences.updateList(type, newItem);
                    }
                    inputField.setText("");
                }
            }
        });

        findViewById(delBtnId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting the checked items from the list view
                SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
                int itemCount = getListView().getCount();
                for (int i = itemCount - 1; i >= 0; i--) {
                    if (checkedItemPositions.get(i)) {
                        Item item = localList.get(i);
                        adapter.remove(item);
                        appPreferences.removeList(type, item);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        findViewById(refreshBtnId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });
    }

    private void reload() {
        adapter.notifyDataSetChanged();
    }

    private boolean addLocalList(Item newItem) {
        for (Item item : localList) {
            if (item.getValue().equalsIgnoreCase(newItem.getValue())) {
                return false;
            }
        }
        return localList.add(newItem);
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAddBtnId(int addBtnId) {
        this.addBtnId = addBtnId;
    }

    public void setDelBtnId(int delBtnId) {
        this.delBtnId = delBtnId;
    }

    public void setRefreshBtnId(int refreshBtnId) {
        this.refreshBtnId = refreshBtnId;
    }

    public void setInputFieldId(int inputFieldId) {
        this.inputFieldId = inputFieldId;
    }
}