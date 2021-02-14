package lk.hd192.project;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Objects;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeBaseFragment;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;


public class AddKidFirstFragment extends GetSafeBaseFragment implements DatePickerDialog.OnDateSetListener {
    int year;
    int month;
    int day;
    Dialog dialog;
    TextView calenderBirthday, txtSchoolName, txtBottomSheetSearch;
    EditText txtFirstName, txtLastName;
    SimpleDateFormat simpleDateFormat;
    RecyclerView bottomSheetRecycler;
    SchoolBottomSheet schoolBottomSheet;
    GetSafeServices getSafeServices;
    RadioGroup rbnGrpGender;
    TinyDB tinyDB;
    AddNewKid addNewKid;
    JSONObject schools;
    public AddKidFirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_kid_first, container, false);
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        calenderBirthday = view.findViewById(R.id.calender_birthday);
        txtSchoolName = view.findViewById(R.id.txt_school_name);
        txtFirstName = view.findViewById(R.id.txt_first_name);
        txtLastName = view.findViewById(R.id.txt_last_name);
        rbnGrpGender = view.findViewById(R.id.rbn_grp_gender);
        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getActivity());

        try {
            schools = new JSONObject(readFile());

        } catch (Exception e) {

        }

        if (AddNewKid.isEditing) {


            txtFirstName.setText(AddNewKid.FirstName);
            txtLastName.setText(AddNewKid.LastName);
            txtSchoolName.setText(AddNewKid.SchoolName);
            calenderBirthday.setText(AddNewKid.Birthday);

        }
        addNewKid = new AddNewKid();
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");


        calenderBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(year, month, day, R.style.DatePickerSpinner);
            }
        });

        txtSchoolName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });

        rbnGrpGender.clearCheck();
        rbnGrpGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbn_gender_male:
                        AddNewKid.Gender = "male";
                        break;
                    case R.id.rbn_gender_female:
                        AddNewKid.Gender = "female";
                        break;
                }
            }
        });
        return view;

    }

    public void validateFields() {

        if (txtFirstName.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtFirstName);
            txtFirstName.setError("Please enter kid first name");
            AddNewKid.firstCompleted = false;

        } else if (txtLastName.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtLastName);
            txtLastName.setError("Please enter kid last name");
            AddNewKid.firstCompleted = false;

        } else if (txtSchoolName.getHint().toString().equals("School Name")) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtSchoolName);
            txtSchoolName.setError("Please select school name");
            AddNewKid.firstCompleted = false;

        } else if (AddNewKid.Gender.equals("null")) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(rbnGrpGender);
            showWarningToast(dialog, "Please select gender", 0);

            AddNewKid.firstCompleted = false;

        } else if (calenderBirthday.getHint().toString().equals("Birthday")) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(calenderBirthday);
            calenderBirthday.setError("Please select kid birthday");
            AddNewKid.firstCompleted = false;
        } else {
            AddNewKid.firstCompleted = true;


        }
    }
    public String readFile() throws IOException
    {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.schools), "UTF-8"));

        String content = "";
        String line;
        while ((line = reader.readLine()) != null)
        {
            content = content + line;
        }

        return content;

    }

    public void addKidBasicDetails() {
        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("name", txtFirstName.getText().toString() + " " + txtLastName.getText().toString());
        tempParam.put("school-name", txtSchoolName.getText().toString());
        tempParam.put("birthday", calenderBirthday.getHint().toString());
        tempParam.put("gender", AddNewKid.Gender);
        tempParam.put("guardian", "");

        ((AddNewKid) Objects.requireNonNull(getActivity())).showLoading();
        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.ADD_NEW_KID), 2, tinyDB.getString("token"),
                new VolleyJsonCallback() {

                    @Override
                    public void onSuccessResponse(JSONObject result) {
                        ((AddNewKid) Objects.requireNonNull(getActivity())).hideLoading();
                        try {
                            Log.e("loc response", result + "");

                            if (result.getBoolean("saved_status")) {

                                AddNewKid.kidId = result.getJSONObject("child").getString("id");
                                AddNewKid.FirstName = txtFirstName.getText().toString();
                                AddNewKid.LastName = txtLastName.getText().toString();
                                AddNewKid.SchoolName = txtSchoolName.getText().toString();
                                AddNewKid.Birthday = calenderBirthday.getHint().toString();


                            } else
                                showWarningToast(dialog, result.getString("validation_errors"), 0);


                        } catch (Exception e) {
                            addNewKid.hideLoading();
                            Log.e("ex loc", e.getMessage());

                            showWarningToast(dialog, "Something went wrong. Please try again", 0);

                        }

                    }
                });

    }

    @VisibleForTesting
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(getActivity())
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .showDaySpinner(true)
                .maxDate(year - 4, month, day)
                .minDate(year - 20, month, day)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        calenderBirthday.setHint(simpleDateFormat.format(calendar.getTime()));

    }

    private class SchoolBottomSheet extends BottomSheetDialog {


        public SchoolBottomSheet(Context context) {
            super(context);

        }

        @Override
        protected void onStart() {
            super.onStart();
            getBehavior().setPeekHeight((GetSafeBase.device_height / 3) * 2, false);
            getBehavior().setDraggable(false);


        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            bottomSheetRecycler = findViewById(R.id.bottom_sheet_recycle_view);
            txtBottomSheetSearch = findViewById(R.id.school_search);

            bottomSheetRecycler.setAdapter(new SchoolAdapter());

            bottomSheetRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            txtBottomSheetSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (txtBottomSheetSearch.getText().toString().equals("")) {
                        //  schoolsArr = originalResult;
                        bottomSheetRecycler.getAdapter().notifyDataSetChanged();
                    } else {

                        SchoolAdapter homeAdapter = new SchoolAdapter();
                        homeAdapter.getFilter().filter(s);
                    }

                }
            });


        }
    }

    public void openBottomSheet() {


        schoolBottomSheet = new SchoolBottomSheet(getActivity());
        schoolBottomSheet.setContentView(R.layout.schools_bottom_sheet);
        schoolBottomSheet.show();

    }

    public class SchoolNameViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rltSchool;
        TextView textSchoolName;

        public SchoolNameViewHolder(@NonNull View itemView) {
            super(itemView);
            rltSchool = itemView.findViewById(R.id.rlt_school);
            textSchoolName = itemView.findViewById(R.id.text_school_name);
        }
    }

    public class SchoolAdapter extends RecyclerView.Adapter<SchoolNameViewHolder> implements Filterable {

        @NonNull
        @Override
        public SchoolNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_school_name, parent, false);
            return new SchoolNameViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull SchoolNameViewHolder holder, int position) {

            try {
                holder.textSchoolName.setText(schools.getJSONArray("Schools").getJSONObject(position).getString("School Name"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.rltSchool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtSchoolName.setText("Mahanama College");
                    txtSchoolName.setHint("Mahanama College");
                    schoolBottomSheet.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return schools.length()-1;
        }

        @Override
        public Filter getFilter() {
            return null;
        }

        //Home screen search - filter stories by story name or category name  #Udeesha
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //   schoolsArr = new JSONArray();
                int put = 0;

                try {
//                    for (int i = 0; i < originalResult.length(); i++) {
//                        if (originalResult.getJSONObject(i).getString("name_en").toLowerCase().contains(constraint.toString().toLowerCase()) ||
//                                originalResult.getJSONObject(i).getString("category_name").toLowerCase().contains(constraint.toString().toLowerCase())) {
//
//                            schoolsArr.put(put++, originalResult.getJSONObject(i));
//                        }
//
//                    }

                } catch (Exception e) {

                }


                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                bottomSheetRecycler.getAdapter().notifyDataSetChanged();

            }
        };

    }


}
