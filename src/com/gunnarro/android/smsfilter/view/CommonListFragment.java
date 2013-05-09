package com.gunnarro.android.smsfilter.view;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.service.FilterService;

public class CommonListFragment extends ListFragment {

    protected FilterService filterService;

    /** Declaring an ArrayAdapter to set items to ListView */
    protected ArrayAdapter<Item> adapter;

    /** Items entered by the user is stored in this ArrayList variable */
    private List<Item> localList = new ArrayList<Item>();

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
    public void onResume() {
        // onResume happens after onStart and onActivityCreate
        initListView();
        super.onResume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // initListView();
        this.localList = filterService.getItemList(type);
        // Defining the ArrayAdapter to set items to ListView
        // this.adapter = new ArrayAdapter(getActivity(),
        // R.layout.checkbox_list_layout, this.localList);
        this.adapter = new ArrayAdapter<Item>(getActivity(), android.R.layout.simple_list_item_multiple_choice, localList);
        // Setting the adapter to the ListView
        super.setListAdapter(adapter);
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
            CustomLog.i(this.getClass(), " value=" + item.toValuePair() + " checked=" + checked);
        }
        item.setEnabled(checked);
        filterService.updateItem(item);
        CustomLog.d(this.getClass(), "onclick local:" + item.toValuePair());
        reloadDataSet();
        // Toast.makeText(l.getContext(), "saved list: " + item.toString(),
        // Toast.LENGTH_LONG).show();
    }

    private void initListView() {
        // Have to init. the check boxes upon loading
        for (int position = 0; position < adapter.getCount(); position++) {
            super.getListView().setItemChecked(position, adapter.getItem(position).isEnabled());
        }
    }

    protected void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    protected void setupEventHandlers(final View view) {
        view.findViewById(addBtnId).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputField = (EditText) view.findViewById(inputFieldId);
                String value = inputField.getText().toString();
                if (!value.isEmpty()) {
                    Item newItem = new Item(value, false);
                    if (addLocalList(newItem)) {
                        // Save the newly added item
                        filterService.createItem(type, newItem);
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
                        filterService.deleteItem(item);
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

    // /**
    // * Custom array adapter class
    // *
    // * @author gunnarro
    // *
    // */
    // private class ItemArrayAdapter extends ArrayAdapter<Item> {
    //
    // private Context context;
    // private ArrayList<Item> itemList;
    //
    // public ItemArrayAdapter(Context context, int textViewResourceId,
    // List<Item> itemList) {
    // super(context, textViewResourceId, itemList);
    // this.context = context;
    // this.itemList = new ArrayList<Item>();
    // this.itemList.addAll(itemList);
    // }
    //
    // private class ViewHolder {
    // TextView text;
    // CheckBox checkBox;
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // ViewHolder holder = null;
    // CustomLog.d(this.getClass(), String.valueOf(position));
    //
    // if (convertView == null) {
    // LayoutInflater inflater = ((Activity) context).getLayoutInflater();
    // convertView = inflater.inflate(R.layout.checkbox_list_layout, null);
    //
    // holder = new ViewHolder();
    // holder.text = (TextView) convertView.findViewById(R.id.code);
    // holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
    // convertView.setTag(holder);
    //
    // holder.checkBox.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    // CheckBox cb = (CheckBox) v;
    // Item item = (Item) cb.getTag();
    // item.setEnabled(cb.isChecked());
    // // Toast.makeText(getContext(), "Clicked on chbox: " +
    // // item.toValuePair(), Toast.LENGTH_LONG).show();
    // }
    // });
    // } else {
    // holder = (ViewHolder) convertView.getTag();
    // }
    //
    // Item item = itemList.get(position);
    // holder.text.setText(item.getValue());
    // holder.checkBox.setChecked(item.isEnabled());
    // holder.checkBox.setTag(item);
    //
    // return convertView;
    //
    // }
    // }// end ItemArrayAdapter
}