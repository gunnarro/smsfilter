package com.gunnarro.android.smsfilter.view;

import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.gunnarro.android.smsfilter.AppPreferences;
import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Item;

public class CommonListFragment extends ListFragment {

    protected AppPreferences appPreferences;

    /** Declaring an ArrayAdapter to set items to ListView */
    protected ArrayAdapter<Item> adapter;

    /** Items entered by the user is stored in this ArrayList variable */
    private List<Item> localList;

    private String type;

    private int addBtnId;
    private int delBtnId;
    private int refreshBtnId;
    private int inputFieldId;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        Item item = localList.get(pos);
        SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
        boolean checked = false;
        if (checkedItemPositions.get(pos)) {
            checked = true;
        }
        item.setEnabled(checked);
        appPreferences.updateList(type, item);
        Log.d(this.getClass().getSimpleName(), "local:" + item.toValuePair());
        reloadDataSet();
    }

    protected void initListView() {
        // Have to init. the check boxes upon loading
        for (int position = 0; position < adapter.getCount(); position++) {
            super.getListView().setItemChecked(position, adapter.getItem(position).isEnabled());
            CustomLog.d(this.getClass(), "smsitem: " + adapter.getItem(position).toValuePair());
        }
    }

    protected void setAppPreferences(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
        // Fist of all, we must load the saved local list.
        this.localList = appPreferences.getList(type);
    }

    protected void setupEventHandlers(final View view) {
        // Defining the ArrayAdapter to set items to ListView
        adapter = new ArrayAdapter<Item>(getActivity(), android.R.layout.simple_list_item_multiple_choice, localList);
        // Setting the adapter to the ListView
        super.setListAdapter(adapter);

        view.findViewById(addBtnId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputField = (EditText) view.findViewById(inputFieldId);
                String value = inputField.getText().toString();
                if (!value.isEmpty()) {
                    Item newItem = new Item(value, false);
                    if (addLocalList(newItem)) {
                        reloadDataSet();
                        // Save the newly added item
                        appPreferences.updateList(type, newItem);
                    }
                    // Clear the input text field after every list insertion
                    inputField.setText("");
                }
            }
        });

        view.findViewById(delBtnId).setOnClickListener(new OnClickListener() {
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
                reloadDataSet();
            }
        });

        view.findViewById(refreshBtnId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadDataSet();
            }
        });
    }

    private void reloadDataSet() {
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
