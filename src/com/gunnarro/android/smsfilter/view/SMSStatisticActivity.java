package com.gunnarro.android.smsfilter.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gunnarro.android.smsfilter.AppConstants;
import com.gunnarro.android.smsfilter.AppPreferences;
import com.gunnarro.android.smsfilter.ListAppPreferencesImpl;
import com.gunnarro.android.smsfilter.R;
import com.gunnarro.android.smsfilter.sms.SMS;

/**
 * Call which reads and holds statistic for blocked sms's.
 * 
 * @author gunnarro
 * 
 */
public class SMSStatisticActivity extends Activity implements OnClickListener {

    protected AppPreferences appPreferences;
    private String viewBy = "Year";
    private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the main.xml layout file.
        setContentView(R.layout.sms_statistic_layout);
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_statistic_btn);
        refreshButton.setOnClickListener(this);

        Spinner viewBySpinner = (Spinner) findViewById(R.id.view_by_spinner);
        ArrayAdapter<CharSequence> viewByAdapter = ArrayAdapter.createFromResource(this, R.array.sms_view_by_options, android.R.layout.simple_spinner_item);
        viewByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewBySpinner.setAdapter(viewByAdapter);
        viewBySpinner.setOnItemSelectedListener(new ViewByOnItemSelectedListener());

        this.appPreferences = new ListAppPreferencesImpl(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.refresh_statistic_btn:
            updateSMSStatistic();
            break;
        case R.id.delete_statistic_btn:
            clearBlockedSMSlog();
            break;
        }
    }

    private void clearStatistic() {
        TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
        Log.i("clearStatistic", "...child: " + table.getChildCount());
        if (table.getChildCount() > 3) {
            table.removeViews(3, table.getChildCount() - 3);
            Log.i("clearStatistic", "Removed rows: " + (table.getChildCount() - 3));
        }
    }

    private void clearBlockedSMSlog() {
        appPreferences.removeAllList(AppPreferences.SMS_BLOCKED_LOG);
    }

    private void updateSMSStatistic() {
        TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
        // Remove all rows before updating the table, except for the table
        // header rows.
        clearStatistic();
        List<SMS> blockedSMSList = appPreferences.getSMSList(viewBy);
        Collections.sort(blockedSMSList, new Comparator<SMS>() {
            public int compare(SMS sms1, SMS sms2) {
                return sms2.getKey().compareTo(sms1.getKey());
            };
        });

        Date startDate = Calendar.getInstance().getTime();
        Date endDate = startDate;

        SMS summarySMS = new SMS("Total", 0);
        summarySMS.setKey("Total");
        for (SMS sms : blockedSMSList) {
            startDate = startDate.before(new Date(sms.getTimeMilliSecound())) ? startDate : new Date(sms.getTimeMilliSecound());
            endDate = endDate.after(new Date(sms.getTimeMilliSecound())) ? endDate : new Date(sms.getTimeMilliSecound());
            summarySMS.increaseNumberOfBlocked(sms.getNumberOfBlocked());
            table.addView(createTableRow(sms, table.getChildCount()));
        }

        formatter.applyPattern("dd.MM.yyyy");
        String periode = formatter.format(startDate) + " - " + formatter.format(endDate);
        TextView tableHeaderTxt = (TextView) findViewById(R.id.tableHeaderTxt);
        tableHeaderTxt.setText("Periode: " + periode);
        tableHeaderTxt.setTextColor(Color.WHITE);
        // Add row with totals at the end of the table
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.BLACK);
        row.setPadding(1, 1, 1, 1);
        row.setMinimumHeight(2);
        table.addView(row);
        table.addView(createTableRow(summarySMS, 1));
    }

    private TableRow createTableRow(SMS sms, int rowNumber) {
        TableRow row = new TableRow(this);
        int bgColor = Color.WHITE;
        if (rowNumber % 2 == 0) {
            bgColor = Color.LTGRAY;
        }
        row.addView(createTextView(sms.getKey(), bgColor, Color.BLACK, Gravity.CENTER));
        row.addView(createTextView(Integer.toString(sms.getNumberOfBlocked()), bgColor, Color.parseColor(AppConstants.NAVY_HEX), Gravity.RIGHT));
        row.setBackgroundColor(bgColor);
        row.setPadding(1, 1, 1, 1);
        return row;
    }

    private TextView createTextView(String value, int bgColor, int txtColor, int gravity) {
        TextView txtView = new TextView(this);
        txtView.setText(value);
        txtView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        txtView.setTextSize(12);
        txtView.setLineSpacing(1, 1);
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