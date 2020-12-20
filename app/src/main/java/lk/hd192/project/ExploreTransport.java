package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePolicy;
import com.otaliastudios.autocomplete.AutocompletePresenter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.VolleyJsonCallback;
import lk.hd192.project.pojo.NearestTowns;

public class ExploreTransport extends GetSafeBase {

    RecyclerView recyclerDriver;
    EditText pickDropDown, dropDropDown;
    String searchPick = "";
    String searchDrop = "";
    String pickUpText = "";
    String dropText = "";

    Button pickupMap, dropMap;

    Double pickLat = 0.0, pickLng = 0.0;
    Double dropLat = 0.0, dropLng = 0.0;

    private Autocomplete userAutocomplete;
    GetSafeServices getSafeServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_transport);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerDriver = findViewById(R.id.recycler_driver);
        pickDropDown = findViewById(R.id.pick_up_dropdwn);
        dropDropDown = findViewById(R.id.drop_dropdwn);
        dropMap = findViewById(R.id.drop_map);
        pickupMap = findViewById(R.id.pickup_map);
        pickDropDown.setText(pickAddress);
        getSafeServices = new GetSafeServices();

        pickUpText = pickAddress;
        pickLat = GetSafeBase.pickLat;
        pickLng = GetSafeBase.pickLng;

        //call
        setupUserAutocompleteDrop();
        setupUserAutocompletePick();
        pickLatLong();

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
                Intent intent=new Intent(getApplicationContext(), Map.class);
                intent.putExtra("clicked","pickup");
                startActivity(intent);

                dropNPickAddressDistinguish=1;
            }
        });
        dropMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Map.class);
                intent.putExtra("clicked","drop");
                startActivity(intent);
                dropNPickAddressDistinguish=2;
            }
        });


    }

    //    ------------------ Auto Complete Pick-------------------------------------------------

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

        userAutocomplete = Autocomplete.<NearestTowns>on(pickDropDown)
                .with(elevation)
                .with(new SimplePolicy())
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MAP_SELECTED & dropNPickAddressDistinguish==1) {
            pickDropDown.setText(LOC_ADDRESS);
            dropNPickAddressDistinguish=0;
        }
        else if(MAP_SELECTED & dropNPickAddressDistinguish==2){
            dropNPickAddressDistinguish=0;
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


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.AUTO_COMPLETE) + "?input=" + code + "&key=" + getString(R.string.API_KEY) + "&sessiontoken=1234567890&components=country:lk", 1, new VolleyJsonCallback() {
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

        userAutocomplete = Autocomplete.<NearestTowns>on(dropDropDown)
                .with(elevation)
                .with(new SimplePolicyDrop())
                .with(backgroundDrawable)
                .with(presenter)
                .with(callback)
                .build();


    }

    // auto-search nearest town
    public void autoCompleteDrop(String code) {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("input", code);
        tempParam.put("key", "AIzaSyAPrTPADT_tYmMJYjg6nZZ4jUHLJILoWpM");
        tempParam.put("sessiontoken", "1234567890");
        tempParam.put("components", "country:lk");

//        AIzaSyAa4hHCIoe2ENqjwTljcJkNnYluHlvRwYY


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.AUTO_COMPLETE) + "?input=" + code + "&key=" + getString(R.string.API_KEY) + "&sessiontoken=1234567890&components=country:lk", 1, new VolleyJsonCallback() {
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


    // ----------------------------------- GET PICk LAT & LNG -----------------------------------------------------------------------//


    public void pickLatLong() {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("", "");


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.REVERSE_GEO) + "?address=" + pickUpText + "&key=" + getString(R.string.API_KEY), 1, new VolleyJsonCallback() {
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


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.REVERSE_GEO) + "?address=" + dropText + "&key=" + getString(R.string.API_KEY), 1, new VolleyJsonCallback() {
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

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            oneDriver = itemView.findViewById(R.id.one_driver);
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
        public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {

            holder.oneDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), DriverProfile.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}