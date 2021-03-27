package lk.hd192.project;

import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class DriverProfile extends GetSafeBase {

    CardView cardDriverDetails;
    RoundedImageView imgDriver;
    Button btn_driver_route;
    public static String driverId, phone;
    GetSafeServices getSafeServices;
    JSONObject driverDetails;
    Dialog dialog;
    View view;
    LottieAnimationView loading;
    Button btn_send_request;
    ImageView driver_photo, img_vehicle_one, img_vehicle_two, img_vehicle_three, img_vehicle_four;
    TextView txt_facilities, txt_driver_name, txt_transport_category, txt_vehicle_type, txt_vehicle_model, txt_vehicle_reg_no, txt_seating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        cardDriverDetails = findViewById(R.id.card_driver_details);
        imgDriver = findViewById(R.id.driver_photo);
        btn_driver_route = findViewById(R.id.btn_driver_route);
        txt_driver_name = findViewById(R.id.txt_driver_name);
        txt_transport_category = findViewById(R.id.txt_transport_category);
        txt_vehicle_type = findViewById(R.id.txt_vehicle_type);
        txt_vehicle_model = findViewById(R.id.txt_vehicle_model);
        txt_vehicle_reg_no = findViewById(R.id.txt_vehicle_reg_no);
        btn_send_request = findViewById(R.id.btn_send_request);
        txt_seating = findViewById(R.id.txt_seating);
        img_vehicle_one = findViewById(R.id.img_vehicle_one);
        driver_photo = findViewById(R.id.driver_photo);
        img_vehicle_two = findViewById(R.id.img_vehicle_two);
        img_vehicle_three = findViewById(R.id.img_vehicle_three);
        img_vehicle_four = findViewById(R.id.img_vehicle_four);
        txt_facilities = findViewById(R.id.txt_facilities);
        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);
        getSafeServices = new GetSafeServices();
        driverDetails = new JSONObject();

        try {
            driverId = getIntent().getStringExtra("driver_id");
            Log.e("driver id", driverId);
        } catch (Exception e) {
            e.printStackTrace();
        }


        findViewById(R.id.btn_driver_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        findViewById(R.id.btn_call_driver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);

            }
        });
        btn_driver_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriverRoute.class);
                intent.putExtra("driver_id", driverId);
                startActivity(intent);
            }
        });
        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tinyDB.getBoolean("isStaffAccount"))
                    sendStaffDriverRequest();
                else
                    sendStudentDriverRequest();
            }
        });

        getDriverDetails();

    }

    private void sendStaffDriverRequest() {
        HashMap<String, String> param = new HashMap<>();
        param.put("driver_id", driverId);

        showLoading();
        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.USER_REQUEST_DRIVER), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("res", result + "");

                    if (result.getBoolean("saved_status")) {
                        showToast(dialog, "Request sent to driver", 2);
                    }


                } catch (
                        Exception e) {
                    e.printStackTrace();
                    showToast(dialog, "Request failed", 0);
                    Log.e("ex from loc", e.getMessage());

                }
                hideLoading();
            }

        });

    }

    private void sendStudentDriverRequest() {
        HashMap<String, String> param = new HashMap<>();
        param.put("driver_id", driverId);
        param.put("child_id", tinyDB.getString("selectedChildId"));
        showLoading();
        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.CHILD_REQUEST_DRIVER), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("res", result + "");

                    if (result.getBoolean("saved_status")) {
                        showToast(dialog, "Request sent to driver", 2);

                    }


                } catch (
                        Exception e) {
                    e.printStackTrace();
                    showToast(dialog, "Request failed", 0);
                    Log.e("ex from loc", e.getMessage());

                }
                hideLoading();

            }
        });

    }

    private void getDriverDetails() {
        HashMap<String, String> param = new HashMap<>();

        showLoading();
        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.GET_DRIVER_DETAILS) + "?id=" + driverId, 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("driver", result + "");
                    if (result.getBoolean("status")) {

                        driverDetails = result.getJSONObject("data");
                        txt_driver_name.setText(driverDetails.getString("name"));
                        txt_vehicle_type.setText(driverDetails.getJSONArray("vehicle").getJSONObject(0).getString("type"));
                        phone = driverDetails.getString("phone_no");

                        txt_vehicle_model.setText(driverDetails.getJSONArray("vehicle").getJSONObject(0).getString("model"));
                        if (driverDetails.getJSONArray("vehicle").getJSONObject(0).getInt("AC") == 1)
                            txt_facilities.setText("A/C");
                        if (driverDetails.getJSONArray("vehicle").getJSONObject(0).getInt("camera") == 1)
                            txt_facilities.setText(" Camera");
                        txt_vehicle_reg_no.setText(driverDetails.getJSONArray("vehicle").getJSONObject(0).getString("registration_no"));
                        txt_seating.setText("Vacant Capacity " + driverDetails.getJSONArray("vehicle").getJSONObject(0).getString("seating_capacity"));
                        driver_photo.setImageBitmap(populateImage(driverDetails.getJSONObject("driver_image").getString("image").substring(21)));

                        if (driverDetails.getJSONArray("vehicle_images").length() == 1) {
                            img_vehicle_one.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(0).getString("image").substring(21)));
                            img_vehicle_two.setVisibility(View.INVISIBLE);
                            img_vehicle_three.setVisibility(View.INVISIBLE);
                            img_vehicle_four.setVisibility(View.INVISIBLE);

                        } else if (driverDetails.getJSONArray("vehicle_images").length() == 2) {
                            img_vehicle_one.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(0).getString("image").substring(21)));

                            img_vehicle_two.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(1).getString("image").substring(21)));
                            img_vehicle_three.setVisibility(View.INVISIBLE);
                            img_vehicle_four.setVisibility(View.INVISIBLE);

                        } else if (driverDetails.getJSONArray("vehicle_images").length() == 3) {
                            img_vehicle_one.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(0).getString("image").substring(21)));

                            img_vehicle_two.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(1).getString("image").substring(21)));
                            img_vehicle_three.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(2).getString("image").substring(21)));

                            img_vehicle_four.setVisibility(View.INVISIBLE);

                        } else if (driverDetails.getJSONArray("vehicle_images").length() != 0) {
                            img_vehicle_one.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(0).getString("image").substring(21)));
                            img_vehicle_four.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(3).getString("image").substring(21)));

                            img_vehicle_two.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(1).getString("image").substring(21)));
                            img_vehicle_three.setImageBitmap(populateImage(driverDetails.getJSONArray("vehicle_images").getJSONObject(2).getString("image").substring(21)));


                        }


                    }


                } catch (
                        Exception e) {
                    e.printStackTrace();
                    Log.e("ex from loc", e.getMessage());

                }
                hideLoading();
            }
        });

    }

    private Bitmap populateImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

    }

    void showLoading() {

        view.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        loading.playAnimation();


    }

    void hideLoading() {


        loading.setVisibility(View.GONE);
        view.setVisibility(View.GONE);

    }
}