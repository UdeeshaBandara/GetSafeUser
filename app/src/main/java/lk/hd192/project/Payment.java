package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class Payment extends GetSafeBase {

    //    LinearLayout lnr_year, lnr_month;
    final static int PAYHERE_REQUEST = 11010;
    ImageView imgBanner;
    //    CardView card_month, card_year;
    Double selectedAmount = 100.0;
    View view;
    LottieAnimationView loading;
    GetSafeServices getSafeServices;
    TinyDB tinyDB;
    JSONObject payment;
    TextView txt_month, txt_driver_amount, txt_service, txt_late, txt_net, txt_deadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getApplicationContext());
        payment = new JSONObject();


        imgBanner = findViewById(R.id.imgBanner);
        txt_month = findViewById(R.id.txt_month);
        txt_driver_amount = findViewById(R.id.txt_driver_amount);
        txt_service = findViewById(R.id.txt_service);
        txt_late = findViewById(R.id.txt_late);
        txt_net = findViewById(R.id.txt_net);
        txt_deadline = findViewById(R.id.txt_deadline);
//        card_year = findViewById(R.id.card_year);
//        card_month = findViewById(R.id.card_month);
//        lnr_year = findViewById(R.id.lnr_year);
//        lnr_month = findViewById(R.id.lnr_month);
        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);

        findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePayment();
            }
        });
        findViewById(R.id.btn_payment_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Picasso.get()
                .load("https://www.payhere.lk/downloads/images/payhere_square_banner_dark.png").placeholder(R.drawable.payhere).fit().centerInside()
                .into(imgBanner);


        if (tinyDB.getBoolean("isStaffAccount"))
            getPaymentDetails();
        else
            getPaymentDetailsChild();

//        card_month.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                lnr_year.setBackgroundResource(0);
//                lnr_month.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_payment_selected));
//                selectedAmount = 100.0;
//            }
//        });
//        card_year.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                lnr_month.setBackgroundResource(0);
//                lnr_year.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_payment_selected));
//                selectedAmount = 1100.0;
//            }
//        });


    }

    private void handlePayment() {
        InitRequest req = new InitRequest();
        req.setMerchantId("1216746");       // Your Merchant PayHere ID
        req.setMerchantSecret("4Txk4P68X2p4JLCdKRUsgk4eSlFpFjHqT8VyZUuyRHbJ"); // Your Merchant secret (Add your app at Settings > Domains & Credentials, to get this))
        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
        req.setAmount(selectedAmount);             // Final Amount to be charged
        req.setOrderId("230000123");        // Unique Reference ID
        req.setItemsDescription("Get Safe Payments");  // Item description title

        req.getCustomer().setFirstName("Saman");
        req.getCustomer().setLastName("Perera");
        req.getCustomer().setEmail("samanp@gmail.com");
        req.getCustomer().setPhone("+94771234567");
        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

//Optional Params
        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
        req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
        req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        startActivityForResult(intent, PAYHERE_REQUEST); //unique request ID like private final static int PAYHERE_REQUEST = 11010;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null)
                    if (response.isSuccess()) {
                        msg = "Activity result:" + response.getData().toString();

                        if (tinyDB.getBoolean("isStaffAccount"))
                            makePayment();
                        else
                            makePaymentChild();

                    } else
                        msg = "Result:" + response.toString();
                else
                    msg = "Result: no response";
                Log.e("Payhere", msg);
//                textView.setText(msg);
            } else if (resultCode == Activity.RESULT_CANCELED) {
//                if (response != null)
//                    textView.setText(response.toString());
//                else
//                    textView.setText("User canceled the request");
            }
        }
    }

    private void getPaymentDetails() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.PAYMENT), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("res payment", result + "");

                    if (result.getBoolean("status")) {


                        payment = result;

                        setValues();

                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });


    }

    private void getPaymentDetailsChild() {

        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.PAYMENT_CHILD) + "?child_id=" + tinyDB.getString("selectedChildId"), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("res payment", result + "");

                    if (result.getBoolean("status")) {


                        payment = result;

                        setValues();

                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

    }

    private void makePayment() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.MAKE_PAYMENT), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("res payment", result + "");

                    if (result.getBoolean("status")) {


                        payment = result;

                        setValues();

                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

    }

    private void makePaymentChild() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.MAKE_PAYMENT_CHILD) + "?child_id=" + tinyDB.getString("selectedChildId"), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("res payment", result + "");

                    if (result.getBoolean("status")) {


                        payment = result;

                        setValues();

                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });
    }

    private void setValues() {
        try {
            txt_month.setText(payment.getJSONObject("model").getString("payment_for_month"));
            txt_driver_amount.setText(payment.getJSONObject("model").getString("driver_fee"));
            txt_service.setText(payment.getJSONObject("model").getString("service_fee"));
            txt_late.setText(payment.getJSONObject("model").getString("late_payment_fee"));
            txt_net.setText(payment.getJSONObject("model").getString("net_monthly_total"));
            txt_deadline.setText(payment.getJSONObject("model").getString("payment_deadline").substring(0, 10));
            selectedAmount = payment.getJSONObject("model").getDouble("net_monthly_total");
        } catch (Exception e) {
            e.printStackTrace();
        }
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