package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePolicy;
import com.otaliastudios.autocomplete.AutocompletePresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.VolleyJsonCallback;
import lk.hd192.project.pojo.NearestTowns;

public class ExploreTransport extends GetSafeBase {

    RecyclerView recyclerDriver;
    TextView pickDropDown, dropDropDown;
    String searchPick = "";
    String searchDrop = "";
    String pickUpText = "";
    String dropText = "";
    Dialog dialog;
    Button pickupMap, dropMap;
    //    ImageView imgAc, imgNonAc;
    Double pickLat = 0.0, pickLng = 0.0;
    Double dropLat = 0.0, dropLng = 0.0;
    JSONArray driverList;

    private Autocomplete userAutocomplete;
    GetSafeServices getSafeServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_transport);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        recyclerDriver = findViewById(R.id.recycler_driver);
        pickDropDown = findViewById(R.id.pick_up_dropdwn);
        dropDropDown = findViewById(R.id.drop_dropdwn);
        dropMap = findViewById(R.id.drop_map);
        pickupMap = findViewById(R.id.pickup_map);
//        imgAc = findViewById(R.id.img_ac);
//        imgNonAc = findViewById(R.id.img_non_ac);
        pickDropDown.setText(pickAddress);
        getSafeServices = new GetSafeServices();
        driverList= new JSONArray();

        pickUpText = pickAddress;
        pickLat = GetSafeBase.pickLat;
        pickLng = GetSafeBase.pickLng;

        //call
        setupUserAutocompleteDrop();
        setupUserAutocompletePick();
        pickLatLong();
//
//        imgNonAc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imgNonAc.setVisibility(View.GONE);
//                imgAc.setVisibility(View.VISIBLE);
//            }
//        });
//        imgAc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imgAc.setVisibility(View.GONE);
//                imgNonAc.setVisibility(View.VISIBLE);
//
//            }
//        });
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tinyDB.getBoolean("isStaffAccount"))
          searchDriverForStaff();
                else
                    searchDriverForChild();
            }
        });

        recyclerDriver.setAdapter(new DriverAdapter());
        recyclerDriver.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        findViewById(R.id.btn_explore_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pickDropDown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchPick = pickDropDown.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //drop
        dropDropDown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchDrop = dropDropDown.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pickupMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Map.class);
//                intent.putExtra("clicked", "pickup");
//                startActivity(intent);
//
//                dropNPickAddressDistinguish = 1;
                EditProfile.needToEnableEditMode = true;
                if (tinyDB.getBoolean("isStaffAccount")) {

                    Intent intent = new Intent(getApplicationContext(), EditProfile.class);
//                intent.putExtra("kid_id", tinyDB.getString("selectedChildId"));
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getApplicationContext(), EditKidProfile.class);
                    intent.putExtra("kid_id", tinyDB.getString("selectedChildId"));
                    startActivity(intent);
                }

            }
        });
        dropMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Map.class);
//                intent.putExtra("clicked", "drop");
//                startActivity(intent);
//                dropNPickAddressDistinguish = 2;
                EditProfile.needToEnableEditMode = true;
                if (tinyDB.getBoolean("isStaffAccount")) {

                    Intent intent = new Intent(getApplicationContext(), EditProfile.class);
//                intent.putExtra("kid_id", tinyDB.getString("selectedChildId"));
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getApplicationContext(), EditKidProfile.class);
                    intent.putExtra("kid_id", tinyDB.getString("selectedChildId"));
                    startActivity(intent);
                }
            }
        });
        if (tinyDB.getBoolean("isStaffAccount"))
            getStaffUserLocationDetails();
        else
            getChildLocationDetails();
    }

    private void getChildLocationDetails() {
        HashMap<String, String> param = new HashMap<>();
        param.put("id", tinyDB.getString("selectedChildId"));


        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.GET_KID_LOCATION_BY_ID), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {


                    if (result.getBoolean("status")) {

                        pickDropDown.setText(result.getJSONObject("location").getString("pick_up_add1") + " " + result.getJSONObject("location").getString("pick_up_add2"));
                        dropDropDown.setText(result.getJSONObject("location").getString("drop_off_add1") + " " + result.getJSONObject("location").getString("drop_off_add2"));

                    }

                } catch (Exception e) {
                    Log.e("ex from loc", e.getMessage());

                }

            }
        });
    }

    private void getStaffUserLocationDetails() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.USER_LOCATION), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("status")) {

                        pickDropDown.setText(result.getJSONObject("location").getString("pick_up_add1") + " " + result.getJSONObject("location").getString("pick_up_add2"));
                        dropDropDown.setText(result.getJSONObject("location").getString("drop_off_add1") + " " + result.getJSONObject("location").getString("drop_off_add2"));

                    }

                } catch (Exception e) {


                }

            }
        });

    }
    private void searchDriverForStaff(){
        HashMap<String, String> param = new HashMap<>();
//        param.put("id", tinyDB.getString("selectedChildId"));


        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.SEARCH_DRIVER_FOR_STAFF), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("result",result+"");
                    driverList=result.getJSONArray("list_of_drivers");

                    if (driverList.length()==0) {

                        showToast(dialog, "No drivers found. Try again", 1);
                    }
                    else
                        recyclerDriver.getAdapter().notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e("ex from loc", e.getMessage());

                }

            }
        });

    }
    private void searchDriverForChild() {
Log.e("",tinyDB.getString("selectedChildId"));
        HashMap<String, String> param = new HashMap<>();
        param.put("id", tinyDB.getString("selectedChildId"));


        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.SEARCH_DRIVER_FOR_KID), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("result",result+"");
                    driverList=result.getJSONArray("list_of_drivers");

                    if (driverList.length()==0) {

                        showToast(dialog, "No drivers found. Try again", 1);
                    }
                    else
                        recyclerDriver.getAdapter().notifyDataSetChanged();

                } catch (Exception e) {
                    Log.e("ex from loc", e.getMessage());

                }

            }
        });
    }

    // set up nearest town auto complete
    private void setupUserAutocompletePick() {

        autoCompletePick(pickDropDown.getText().toString());
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
        AutocompletePresenter<NearestTowns> presenter = new TownsPresenter(this);
        AutocompleteCallback<NearestTowns> callback = new AutocompleteCallback<NearestTowns>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, NearestTowns item) {
                editable.clear();
                editable.append(item.getTownName());

                pickUpText = item.getTownName();
                pickLatLong();


                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
            }
        };

//        userAutocomplete = Autocomplete.<NearestTowns>on(pickDropDown)
//                .with(elevation)
//                .with(new SimplePolicy())
//                .with(backgroundDrawable)
//                .with(presenter)
//                .with(callback)
//                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MAP_SELECTED & dropNPickAddressDistinguish == 1) {
            pickDropDown.setText(LOC_ADDRESS);
            dropNPickAddressDistinguish = 0;
        } else if (MAP_SELECTED & dropNPickAddressDistinguish == 2) {
            dropNPickAddressDistinguish = 0;
            dropDropDown.setText(LOC_ADDRESS);
        }
    }

    // auto-search nearest town
    public void autoCompletePick(String code) {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("input", code);
        tempParam.put("key", "AIzaSyAPrTPADT_tYmMJYjg6nZZ4jUHLJILoWpM");
        tempParam.put("sessiontoken", "1234567890");
        tempParam.put("components", "country:lk");

//        AIzaSyAa4hHCIoe2ENqjwTljcJkNnYluHlvRwYY


        getSafeServices.googleAPIRequest(this, tempParam, getString(R.string.AUTO_COMPLETE) + "?input=" + code + "&key=" + getString(R.string.API_KEY) + "&sessiontoken=1234567890&components=country:lk", 1, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    NearestTowns.TOWNS = new ArrayList<>();

                    for (int i = 0; i < result.getJSONArray("predictions").length(); i++)

                        NearestTowns.TOWNS.add(new NearestTowns(result.getJSONArray("predictions").getJSONObject(i).getString("description")));


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public class SimplePolicy implements AutocompletePolicy {
        @Override
        public boolean shouldShowPopup(Spannable text, int cursorPos) {

            autoCompletePick(searchPick);
            return text.length() > 0;
        }

        @Override
        public boolean shouldDismissPopup(Spannable text, int cursorPos) {
            return text.length() == 0;
        }

        @Override
        public CharSequence getQuery(Spannable text) {

            autoCompletePick(searchPick);
            return text;
        }

        @Override
        public void onDismiss(Spannable text) {
        }
    }


    //    ------------------ Auto Complete Drop-------------------------------------------------

    // set up nearest town auto complete
    private void setupUserAutocompleteDrop() {

        autoCompleteDrop(dropDropDown.getText().toString());
        float elevation = 6f;
        Drawable backgroundDrawable = new ColorDrawable(Color.WHITE);
        AutocompletePresenter<NearestTowns> presenter = new TownsPresenter(this);
        AutocompleteCallback<NearestTowns> callback = new AutocompleteCallback<NearestTowns>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, NearestTowns item) {
                editable.clear();
                editable.append(item.getTownName());

                dropText = item.getTownName();
                dropLatLong();

                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
            }
        };

//        userAutocomplete = Autocomplete.<NearestTowns>on(dropDropDown)
//                .with(elevation)
//                .with(new SimplePolicyDrop())
//                .with(backgroundDrawable)
//                .with(presenter)
//                .with(callback)
//                .build();


    }

    // auto-search nearest town
    public void autoCompleteDrop(String code) {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("input", code);
        tempParam.put("key", "AIzaSyAPrTPADT_tYmMJYjg6nZZ4jUHLJILoWpM");
        tempParam.put("sessiontoken", "1234567890");
        tempParam.put("components", "country:lk");

//        AIzaSyAa4hHCIoe2ENqjwTljcJkNnYluHlvRwYY


        getSafeServices.googleAPIRequest(this, tempParam, getString(R.string.AUTO_COMPLETE) + "?input=" + code + "&key=" + getString(R.string.API_KEY) + "&sessiontoken=1234567890&components=country:lk", 1, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    NearestTowns.TOWNS = new ArrayList<>();

                    for (int i = 0; i < result.getJSONArray("predictions").length(); i++)

                        NearestTowns.TOWNS.add(new NearestTowns(result.getJSONArray("predictions").getJSONObject(i).getString("description")));


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public class SimplePolicyDrop implements AutocompletePolicy {
        @Override
        public boolean shouldShowPopup(Spannable text, int cursorPos) {

            autoCompleteDrop(searchDrop);
            return text.length() > 0;
        }

        @Override
        public boolean shouldDismissPopup(Spannable text, int cursorPos) {
            return text.length() == 0;
        }

        @Override
        public CharSequence getQuery(Spannable text) {

            autoCompleteDrop(searchDrop);
            return text;
        }

        @Override
        public void onDismiss(Spannable text) {
        }
    }


    public void pickLatLong() {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("", "");


        getSafeServices.googleAPIRequest(this, tempParam, getString(R.string.REVERSE_GEO) + "?address=" + pickUpText + "&key=" + getString(R.string.API_KEY), 1, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {


                    pickLat = result.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    pickLng = result.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public void dropLatLong() {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("", "");


        getSafeServices.googleAPIRequest(this, tempParam, getString(R.string.REVERSE_GEO) + "?address=" + dropText + "&key=" + getString(R.string.API_KEY), 1, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {


                    dropLat = result.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    dropLng = result.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }


    class DriverViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout oneDriver;
        TextView txt_driver_name,txt_start,txt_end,txt_rate;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            oneDriver = itemView.findViewById(R.id.one_driver);
            txt_driver_name = itemView.findViewById(R.id.txt_driver_name);
            txt_start = itemView.findViewById(R.id.txt_start);
            txt_end = itemView.findViewById(R.id.txt_end);
            txt_rate = itemView.findViewById(R.id.txt_rate);
        }
    }

    class DriverAdapter extends RecyclerView.Adapter<DriverViewHolder> {

        @NonNull
        @Override
        public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_drivers, parent, false);
            return new DriverViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DriverViewHolder holder, final int position) {

            try {
                holder.txt_driver_name.setText(" : "+driverList.getJSONObject(position).getString("name"));
                holder.txt_start.setText(" : "+driverList.getJSONObject(position).getString("phone_no"));
                holder.txt_end.setText(" : "+driverList.getJSONObject(position).getString("email"));

                holder.oneDriver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), DriverProfile.class));
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {
            return driverList.length();
        }
    }
}