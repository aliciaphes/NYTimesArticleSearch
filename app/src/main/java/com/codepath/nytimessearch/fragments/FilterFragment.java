package com.codepath.nytimessearch.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.listeners.FilterSearchDialogListener;
import com.codepath.nytimessearch.models.Query;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by Alicia P on 22-Oct-16.
 */


public class FilterFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    private View filterFragmentView;

    private DatePickerDialog datePicker;

    TextView tvSelectedDate;
    Spinner tvSelectedOrder;
    //faltan las checkboxes

    Button btnApplyFilter;

    Query query = new Query();


    public FilterFragment() {
        //empty constructor is required
    }


    public void createCalendar() {
        Calendar now = Calendar.getInstance();

        //datePicker = DatePickerDialog.newInstance((DatePickerDialog.OnDateSetListener) getActivity(),
        datePicker = DatePickerDialog.newInstance(this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
    }


    public static FilterFragment newInstance(String title) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();

        args.putString("title", title);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        filterFragmentView = inflater.inflate(R.layout.fragment_filter_articles, container);
        return filterFragmentView;

    }

    //todo: 'prettify' the filter dialog...

    //todo: give dialog a bar


    private void setupElements() {
        tvSelectedDate = (TextView) filterFragmentView.findViewById(R.id.selected_date);
        tvSelectedOrder = (Spinner) filterFragmentView.findViewById(R.id.sort_spinner);

        btnApplyFilter = (Button) filterFragmentView.findViewById(R.id.btn_apply_filter);
    }


    private void setButtonListener() {

        //btnCreateFilter.setOnEditorActionListener(this);

        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make sure date is not blank
                if (query.getBeginDate().isEmpty()) {
                    Toast.makeText(getActivity(), "Please select a valid date", Toast.LENGTH_SHORT).show();
                } else {

                    getSelectedValues();

                    dismiss();

                    //pass values to the parent
                    FilterSearchDialogListener listener = (FilterSearchDialogListener) getActivity();
                    listener.onFinishChoosingOptions(query);
                }

            }
        });
    }

    private void getSelectedValues() {
        //retrieve order that was selected
        String selectedOrder = tvSelectedOrder.getSelectedItem().toString();
        query.setSortOrder(selectedOrder);

        //retrieve checkboxes that were checked
        LinearLayout llCategories = (LinearLayout) filterFragmentView.findViewById(R.id.categories_container);
        int count = llCategories.getChildCount();

        for (int i = 0; i < count; i++) {
            CheckBox cbChild = (CheckBox) llCategories.getChildAt(i);
            if (cbChild.isChecked()) {
                String id = cbChild.getResources().getResourceEntryName(cbChild.getId());
                //all checkboxes id's are 'checkbox_[category]' so we subtract the category
                int index = id.indexOf('_') + 1;
                String category = id.substring(index);
                query.addCategory("\"" + category + "\"");
            }
        }
    }


    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createCalendar();

        setupElements();

        setButtonListener();

        tvSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        //GregorianCalendar dateRetrieved = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        Calendar c = Calendar.getInstance();

        //int month = monthOfYear-1;

        //REMEMBER Month value is 0-based. e.g., 0 for January.
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);


        //save as yyyymmdd in query as it needs to have 8 characters
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        query.setBeginDate(formatter.format(c.getTime()));

        //format textview 'prettily' (hey, 'bigly' seems to exist) with the date that was picked
        formatter = new SimpleDateFormat("dd MMMM yyyy");
        tvSelectedDate.setText(formatter.format(c.getTime()));


        //String selectedDate = year + "/" + monthOfYear + "/" + dayOfMonth;
        //query.setBeginDate(Integer.toString(year) + Integer.toString(monthOfYear) + Integer.toString(dayOfMonth));
        //tvSelectedDate.setText(selectedDate);
    }

}
