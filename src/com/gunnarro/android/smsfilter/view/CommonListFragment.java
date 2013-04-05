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
import android.widget.Toast;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.service.FilterService;

public class CommonListFragment extends ListFragment {

    protected FilterService filterService;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListView();
    }

    // @Override
    // public void onResume() {
    // // onResume happens after onStart and onActivityCreate
    // initListView();
    // super.onResume();
    // }

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
        filterService.updateList(type, item);
        Log.d(this.getClass().getSimpleName(), "local:" + item.toValuePair());
        reloadDataSet();
    }

    private void initListView() {
        StringBuffer sb = new StringBuffer();
        // Have to init. the check boxes upon loading
        for (int position = 0; position < adapter.getCount(); position++) {
            super.getListView().setItemChecked(position, adapter.getItem(position).isEnabled());
            CustomLog.d(this.getClass(), "smsitem: " + adapter.getItem(position).toValuePair());
            sb.append(adapter.getItem(position).toValuePair()).append("\n");
        }
        Toast.makeText(MainActivity.appContext, sb.toString(), Toast.LENGTH_LONG).show();
    }

    protected void setAppPreferences(FilterService appPreferences) {
        this.filterService = appPreferences;
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
                        // Save the newly added item
                        filterService.updateList(type, newItem);
                        reloadDataSet();
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
                        filterService.removeList(type, item);
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
        // update adapter
        adapter.notifyDataSetChanged();
        // then update items in the list view
        initListView();
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