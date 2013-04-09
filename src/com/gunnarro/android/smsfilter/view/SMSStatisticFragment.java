package com.gunnarro.android.smsfilter.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gunnarro.android.smsfilter.R;
import com.gunnarro.android.smsfilter.domain.SMS;
import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;

public class SMSStatisticFragment extends Fragment {

    protected FilterService filterService;
    private String viewBy = "Year";
    private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.sms_statistic_layout, container, false);
        Spinner viewBySpinner = (Spinner) view.findViewById(R.id.view_by_spinner);
        ArrayAdapter<CharSequence> viewByAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.sms_view_by_options,
                android.R.layout.simple_spinner_item);
        viewByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewBySpinner.setAdapter(viewByAdapter);
        viewBySpinner.setOnItemSelectedListener(new ViewByOnItemSelectedListener());

        this.filterService = new FilterServiceImpl(view.getContext());
        setupEventHandlers(view);
        return view;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        this.filterService.close();
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        // onResume happens after onStart and onActivityCreate
        this.filterService.open();
        super.onResume();
    }

    private void setupEventHandlers(final View view) {
        ImageButton refreshButton = (ImageButton) view.findViewById(R.id.refresh_statistic_btn);
        refreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                updateSMSStatistic(view);
            }
        });

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_statistic_btn);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                clearBlockedSMSlog();
            }
        });
    }

    private void clearStatistic(View view) {
        TableLayout table = (TableLayout) view.findViewById(R.id.tableLayout);
        if (table == null) {
            return;
        }
        Log.i("clearStatistic", "...child: " + table.getChildCount());
        if (table.getChildCount() > 3) {
            table.removeViews(3, table.getChildCount() - 3);
            Log.i("clearStatistic", "Removed rows: " + (table.getChildCount() - 3));
        }
    }

    private void clearBlockedSMSlog() {
        filterService.removeAllList("SMS_BLOCKED_LOG");
    }

    private void updateSMSStatistic(View view) {
        TableLayout table = (TableLayout) view.findViewById(R.id.tableLayout);
        // Remove all rows before updating the table, except for the table
        // header rows.
        clearStatistic(view);
        List<SMS> blockedSMSList = filterService.getSMSList(viewBy);
        Collections.sort(blockedSMSList, new Comparator<SMS>() {
            public int compare(SMS sms1, SMS sms2) {
                return sms2.getKey().compareTo(sms1.getKey());
            };
        });

        Date startDate = Calendar.getInstance().getTime();
        Date endDate = startDate;

        SMS summarySMS = new SMS(getResources().getString(R.string.tbl_blocked_total), 0);
        summarySMS.setKey(getResources().getString(R.string.tbl_blocked_total));
        for (SMS sms : blockedSMSList) {
            startDate = startDate.before(new Date(sms.getTimeMilliSecound())) ? startDate : new Date(sms.getTimeMilliSecound());
            endDate = endDate.after(new Date(sms.getTimeMilliSecound())) ? endDate : new Date(sms.getTimeMilliSecound());
            summarySMS.increaseNumberOfBlocked(sms.getNumberOfBlocked());
            table.addView(createTableRow(view, sms, table.getChildCount()));
        }

        formatter.applyPattern("dd.MM.yyyy");
        String periode = formatter.format(startDate) + " - " + formatter.format(endDate);
        TextView tableHeaderTxt = (TextView) view.findViewById(R.id.tableHeaderPeriod);
        tableHeaderTxt.setText(getResources().getString(R.string.tbl_blocked_periode) + ": " + periode);
        // tableHeaderTxt.setTextColor(getResources().getColor(R.color.white));
        // Add row with totals at the end of the table
        TableRow row = new TableRow(view.getContext());
        table.addView(row);
        table.addView(createTableRow(view, summarySMS, 3));
    }

    private TableRow createTableRow(View view, SMS sms, int rowNumber) {
        TableRow row = new TableRow(view.getContext());
        int rowBgColor = getResources().getColor(R.color.tbl_row_even);
        if (rowNumber % 2 != 0) {
            rowBgColor = getResources().getColor(R.color.tbl_row_odd);
        }
        row.addView(createTextView(view, sms.getKey(), rowBgColor, getResources().getColor(R.color.tbl_txt), Gravity.CENTER));
        row.addView(createTextView(view, Integer.toString(sms.getNumberOfBlocked()), rowBgColor, getResources().getColor(R.color.tbl_number), Gravity.RIGHT));
        // Just in order to make the table symmetric with 3 colons all over.
        // TableRow.LayoutParams params = (TableRow.LayoutParams)
        // row.getLayoutParams();
        // params.span = 3;
        // row.setLayoutParams(params);
        row.setBackgroundColor(rowBgColor);
        row.setPadding(1, 1, 1, 1);
        return row;
    }

    private TextView createTextView(View view, String value, int bgColor, int txtColor, int gravity) {
        TextView txtView = new TextView(view.getContext());
        txtView.setText(value);
        txtView.setGravity(gravity);
        txtView.setBackgroundColor(bgColor);
        txtView.setTextColor(txtColor);
        return txtView;
    }

    public class ViewByOnItemSelectedListener implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            viewBy = parent.getItemAtPosition(pos).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }
}
