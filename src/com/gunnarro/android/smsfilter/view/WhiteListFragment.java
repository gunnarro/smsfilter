package com.gunnarro.android.smsfilter.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gunnarro.android.smsfilter.R;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public class WhiteListFragment extends CommonListFragment {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setType(FilterTypeEnum.SMS_WHITE_LIST.name());
        super.setAddBtnId(R.id.white_list_add_btn);
        super.setDelBtnId(R.id.white_list_del_btn);
        super.setRefreshBtnId(R.id.white_list_refresh_btn);
        super.setInputFieldId(R.id.white_list_input_field);
        super.setFilterService(new FilterServiceImpl(getActivity()));
    }

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
        View view = inflater.inflate(R.layout.white_list_layout, container, false);
        super.setupEventHandlers(view);
        return view;
    }
}
