package com.codepath.nytimessearch.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alicia P on 22-Oct-16.
 */

//public class FilterFragment extends DialogFragment {
public class FilterFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    private View filterFragmentView;

    private DatePickerDialog datePicker;

    TextView tvSelectedDate;
    Spinner tvSelectedOrder;
    //falta uno

    Button btnCreateFilter;

    String selectedDate;


    public FilterFragment(){
        //empty constructor is required
    }


    public void createCalendar(){
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

    private void setupElements() {
        tvSelectedDate = (TextView) filterFragmentView.findViewById(R.id.selected_date);
        tvSelectedOrder = (Spinner) filterFragmentView.findViewById(R.id.sort_spinner);

        btnCreateFilter = (Button) filterFragmentView.findViewById(R.id.btn_apply_filter);
    }

    private void setButtonListener() {
        btnCreateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();

                //retrieve values
                String selectedOrder = tvSelectedOrder.getSelectedItem().toString();

                //retrieve checkboxes that are checked
                ArrayList<String> selectedCategories = new ArrayList<String>();
                LinearLayout llCategories = (LinearLayout) filterFragmentView.findViewById(R.id.categories_container);
                int count = llCategories.getChildCount();

                for(int i=0; i<count; i++) {
                    CheckBox cbChild = (CheckBox) llCategories.getChildAt(i);
                    if(cbChild.isChecked()){
                        String id = cbChild.getResources().getResourceEntryName(cbChild.getId());
                        //all checkboxes id's are 'checkbox_[category]' so we subtract the category
                        int index = id.indexOf('_')+1;
                        String category = id.substring(index);
                        Toast.makeText(getActivity(), category, Toast.LENGTH_SHORT).show();
                        selectedCategories.add(category);
                    }

                }
                //pass values to the parent
            }
        });
    }



//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        //getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
//        //android.R.style.Theme_Black_NoTitleBar_Fullscreen
//
//    }




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
        //populate the field with the date that was picked
        selectedDate = year + "/" + monthOfYear + "/" + dayOfMonth;
        tvSelectedDate.setText(selectedDate);
    }

}
