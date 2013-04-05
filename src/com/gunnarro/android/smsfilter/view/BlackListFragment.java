package com.gunnarro.android.smsfilter.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gunnarro.android.smsfilter.R;
import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.FilterServiceImpl;

public class BlackListFragment extends CommonListFragment {

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.black_list_layout, container, false);
        super.setType(FilterService.SMS_BLACK_LIST);
        super.setAddBtnId(R.id.black_list_add_btn);
        super.setDelBtnId(R.id.black_list_del_btn);
        super.setRefreshBtnId(R.id.black_list_refresh_btn);
        super.setInputFieldId(R.id.black_list_input_field);
        super.setAppPreferences(new FilterServiceImpl(view.getContext()));
        super.setupEventHandlers(view);
        return view;
    }
}
