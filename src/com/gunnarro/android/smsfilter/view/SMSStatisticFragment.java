package com.gunnarro.android.smsfilter.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Fragment;
import android.os.Bundle;
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
import com.gunnarro.android.smsfilter.custom.CustomLog;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;

public class SMSStatisticFragment extends Fragment {

    protected FilterService filterService;
    private String viewBy = "Year";
    private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.US);

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
        viewBySpinner.setOnItemSelectedListener(new ViewByOnItemSelectedListener(view));

        this.filterService = new FilterServiceImpl(view.getContext());
        setupEventHandlers(view);
        return view;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupEventHandlers(final View statView) {
        ImageButton refreshButton = (ImageButton) statView.findViewById(R.id.refresh_statistic_btn);
        refreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                updateSMSStatistic(statView);
            }
        });

        ImageButton deleteButton = (ImageButton) statView.findViewById(R.id.delete_statistic_btn);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View btnView) {
                clearBlockedSMSlog();
                // the update the view
                updateSMSStatistic(statView);
            }
        });
    }

    private void clearStatistic(View statView) {
        TableLayout table = (TableLayout) statView.findViewById(R.id.tableLayout);
        if (table == null) {
            return;
        }
        CustomLog.i(this.getClass(), "...child: " + table.getChildCount());
        if (table.getChildCount() > 3) {
            table.removeViews(3, table.getChildCount() - 3);
            CustomLog.i(this.getClass(), "Removed rows from view: " + (table.getChildCount() - 3));
        }
    }

    private void clearBlockedSMSlog() {
        filterService.removeAllLog();
        CustomLog.i(this.getClass(), "Removed all from DB");
    }

    private void updateSMSStatistic(View statView) {
        TableLayout table = (TableLayout) statView.findViewById(R.id.tableLayout);
        if (table == null) {
            return;
        }
        // Remove all rows before updating the table, except for the table
        // header rows.
        clearStatistic(statView);
        List<SMSLog> blockedSMSmsLogs = filterService.getLogs(viewBy);
        List<SMSLog> logsStartDateAndEndDate = filterService.getLogsStartDateAndEndDate();
        Date startDate = logsStartDateAndEndDate != null ? new Date(logsStartDateAndEndDate.get(0).getReceivedTime()) : Calendar.getInstance().getTime();
        Date endDate = logsStartDateAndEndDate != null ? new Date(logsStartDateAndEndDate.get(1).getReceivedTime()) : Calendar.getInstance().getTime();
        int totalCount = 0;
        for (SMSLog sms : blockedSMSmsLogs) {
            if (viewBy.equalsIgnoreCase("number")) {
                // try to look up number in the contact in order to find the
                // name
                String contactName = filterService.lookUpContacts(sms.getKey());
                if (contactName != null) {
                    sms.setKey(contactName);
                }
            }
            totalCount += sms.getCount();
            table.addView(createTableRow(statView, sms.getKey(), sms.getCount(), table.getChildCount()));
        }
        formatter.applyPattern("dd.MM.yyyy");
        String periode = formatter.format(startDate) + " - " + formatter.format(endDate);
        TextView tableHeaderTxt = (TextView) statView.findViewById(R.id.tableHeaderPeriod);
        tableHeaderTxt.setText(getResources().getString(R.string.tbl_blocked_periode) + ": " + periode);
        // tableHeaderTxt.setTextColor(getResources().getColor(R.color.white));
        // Add row with totals at the end of the table
        TableRow row = new TableRow(statView.getContext());
        table.addView(row);
        table.addView(createTableRow(statView, getResources().getString(R.string.tbl_blocked_total), totalCount, -1));
    }

    private TableRow createTableRow(View statView, String value, int count, int rowNumber) {
        TableRow row = new TableRow(statView.getContext());
        int rowBgColor = getResources().getColor(R.color.tbl_row_even);
        int txtColor = getResources().getColor(R.color.tbl_txt);
        int numberColor = getResources().getColor(R.color.tbl_number);
        if (rowNumber % 2 != 0) {
            rowBgColor = getResources().getColor(R.color.tbl_row_odd);
        }

        if (rowNumber == -1) {
            // use different colors for the summary row
            rowBgColor = getResources().getColor(R.color.tbl_head_background);
            txtColor = getResources().getColor(R.color.white);
            numberColor = getResources().getColor(R.color.white);
        }
        row.addView(createTextView(statView, value, rowBgColor, txtColor, Gravity.CENTER));
        row.addView(createTextView(statView, Integer.toString(count), rowBgColor, numberColor, Gravity.RIGHT));
        // Just in order to make the table symmetric with 3 colons all over.
        // TableRow.LayoutParams params = (TableRow.LayoutParams)
        // row.getLayoutParams();
        // params.span = 3;
        // row.setLayoutParams(params);
        row.setBackgroundColor(rowBgColor);
        row.setPadding(1, 1, 1, 1);
        return row;
    }

    private TextView createTextView(View statView, String value, int bgColor, int txtColor, int gravity) {
        TextView txtView = new TextView(statView.getContext());
        txtView.setText(value);
        txtView.setGravity(gravity);
        txtView.setBackgroundColor(bgColor);
        txtView.setTextColor(txtColor);
        return txtView;
    }

    // for testing only
    @Deprecated
    private void addTestData() {
        this.filterService.createLog(new SMSLog(System.currentTimeMillis(), "11223344", SMSLog.STATUS_SMS_RECEIVED, "none"));
    }

    /**
     * 
     * @author gunnarro
     * 
     */
    public class ViewByOnItemSelectedListener implements OnItemSelectedListener {
        final View statView;

        public ViewByOnItemSelectedListener(final View statView) {
            this.statView = statView;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            viewBy = parent.getItemAtPosition(pos).toString();
            // update the table upon item selection
            updateSMSStatistic(statView);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }
}
