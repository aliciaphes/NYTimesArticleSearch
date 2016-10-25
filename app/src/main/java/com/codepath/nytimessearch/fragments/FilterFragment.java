package com.codepath.nytimessearch.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
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
    LinearLayout llCategories;

    Button btnApplyFilter;
    Button btnCancel;

    ProgressDialog pd;

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

        //filterFragmentView = inflater.inflate(R.layout.fragment_filter_fields, container);
        //filterFragmentView = inflater.inflate(R.layout.fragment_filter_layout, container);
        filterFragmentView = inflater.inflate(R.layout.fragment_filter_layout, container, false);

        return filterFragmentView;
    }


    private void setupElements() {
        tvSelectedDate = (TextView) filterFragmentView.findViewById(R.id.selected_date);
        tvSelectedOrder = (Spinner) filterFragmentView.findViewById(R.id.sort_spinner);

        btnApplyFilter = (Button) filterFragmentView.findViewById(R.id.btn_apply_filter);
        btnCancel = (Button) filterFragmentView.findViewById(R.id.btn_cancel);
        llCategories = (LinearLayout) filterFragmentView.findViewById(R.id.categories_container);
    }


    private void setButtonsListeners() {

        btnApplyFilter.setOnClickListener(v -> {

            //make sure date is not blank
            if (query.getBeginDate().isEmpty()) {
                Toast.makeText(getActivity(), "Please select a valid date", Toast.LENGTH_SHORT).show();
            } else {
                //pd = Utilities.createLoadingDialog(getContext());
                //pd.show();

                getSelectedValues();

                dismiss();

                //pass values to the parent
                FilterSearchDialogListener listener = (FilterSearchDialogListener) getActivity();
                listener.onFinishChoosingOptions(query);
            }

        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void getSelectedValues() {
        //retrieve order that was selected
        String selectedOrder = tvSelectedOrder.getSelectedItem().toString();
        query.setSortOrder(selectedOrder);

        //retrieve checkboxes that were checked
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

        String title = getArguments().getString("title");

        getDialog().setTitle(title);//todo: does not work and I don't know why

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        createCalendar();

        setupElements();

        setButtonsListeners();

        tvSelectedDate.setOnClickListener(v -> datePicker.show(getActivity().getFragmentManager(), "Datepickerdialog"));

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
    }

    public ProgressDialog returnDialog() {
        return pd;
    }


    // Fires when a configuration change occurs and fragment needs to save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
