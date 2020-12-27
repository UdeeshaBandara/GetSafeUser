package lk.hd192.project.Utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetSafeServices extends GetSafeBase {


    String requestResponse = "";


    String TAG = "Get Safe Service >> ";


    public GetSafeServices() {

    }

    // Network Service Class for String Requests
    public void networkStringRequest(Context context, final HashMap<String, String> parameters, String url, int method, final VolleyCallback callback) {

        int requestMethod = 0;

        if (method == 1) {

            requestMethod = Request.Method.GET;

        } else if (method == 2) {

            requestMethod = Request.Method.POST;

        } else {

            requestMethod = Request.Method.GET;

        }

        StringRequest request = new StringRequest(requestMethod, url, new Response.Listener<String>() { // request
            @Override
            public void onResponse(String response) {

                callback.onSuccessResponse(response); // send response back to the calling class
                requestResponse = response;

                Log.e(TAG + "RESPONSE String ", requestResponse);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.e(TAG, "volley error : " + jsonError); // volley error description

                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params;
                params = parameters;
                Log.e(TAG + "PARAMETERS ", params + "");
                return params;
            }

        };

        int MY_SOCKET_TIMEOUT_MS = 5000;
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        Volley.newRequestQueue(context).add(request);

    }


    // Network Service Class for Google API Requests
    public void googleAPIRequest(Context context, final HashMap<String, String> parameters, String url, int method, final VolleyJsonCallback callback) {


        int requestMethod = 0;

        if (method == 1) {

            requestMethod = Request.Method.GET;

        } else if (method == 2) {

            requestMethod = Request.Method.POST;

        } else {

            requestMethod = Request.Method.GET;

        }

        JsonObjectRequest req = new JsonObjectRequest(requestMethod, url, new JSONObject(parameters), new Response.Listener<JSONObject>() { //Json request
            @Override
            public void onResponse(JSONObject response) {


                callback.onSuccessResponse(response); // send response back to the calling class
                // Log.e(TAG + "JSON REQ ", response + "");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.e(TAG, "volley JSON error : " + jsonError); // volley error description

                }

                try {
                    callback.onSuccessResponse(new JSONObject(new String(networkResponse.data)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        int MY_SOCKET_TIMEOUT_MS = 20000;
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(req);


    }

    public void networkJsonRequest(Context context, final HashMap<String, String> parameters, String url, int method, final VolleyJsonCallback callback) {


        int requestMethod = 0;

        if (method == 1) {

            requestMethod = Request.Method.GET;

        } else if (method == 2) {

            requestMethod = Request.Method.POST;

        } else {

            requestMethod = Request.Method.GET;

        }

        JsonObjectRequest req = new JsonObjectRequest(requestMethod, url, new JSONObject(parameters), new Response.Listener<JSONObject>() { //Json request
            @Override
            public void onResponse(JSONObject response) {


                callback.onSuccessResponse(response); // send response back to the calling class
                // Log.e(TAG + "JSON REQ ", response + "");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.e(TAG, "volley JSON error : " + jsonError); // volley error description

                }

                try {
                    callback.onSuccessResponse(new JSONObject(new String(networkResponse.data)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Bearer", tinyDB.getString("token"));
                return headers;
            }
        };

        int MY_SOCKET_TIMEOUT_MS = 20000;
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(req);


    }
    public void networkJsonRequestWithoutHeader(Context context, final HashMap<String, String> parameters, String url, int method, final VolleyJsonCallback callback) {


        int requestMethod = 0;

        if (method == 1) {

            requestMethod = Request.Method.GET;

        } else if (method == 2) {

            requestMethod = Request.Method.POST;

        } else {

            requestMethod = Request.Method.GET;

        }

        JsonObjectRequest req = new JsonObjectRequest(requestMethod, url, new JSONObject(parameters), new Response.Listener<JSONObject>() { //Json request
            @Override
            public void onResponse(JSONObject response) {


                callback.onSuccessResponse(response); // send response back to the calling class
                // Log.e(TAG + "JSON REQ ", response + "");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.e(TAG, "volley JSON error : " + jsonError); // volley error description

                }

                try {
                    callback.onSuccessResponse(new JSONObject(new String(networkResponse.data)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        int MY_SOCKET_TIMEOUT_MS = 20000;
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(req);


    }


    // Network Service Class for JSONObject Requests with HashMap<String, Object>
    public void networkJsonRequestWithDifHashMap(Context context, final HashMap<String, Object> parameters, String url, int method, final VolleyJsonCallback callback) {

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(parameters), new Response.Listener<JSONObject>() { //Json request
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG + "JSON REQ DifHashMap", response + "");

                callback.onSuccessResponse(response); // send response back to the calling class


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String jsonError = new String(networkResponse.data);
                    Log.e(TAG, "volley JSON error : " + jsonError); // volley error description

                }

                try {
                    callback.onSuccessResponse(new JSONObject(new String(networkResponse.data)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();

//                headers.put("Authorization", tinyDB.getString("TOKEN"));

                Log.e("Headers ", headers + "");

                return headers;
            }
        };

        int MY_SOCKET_TIMEOUT_MS = 5000;
        req.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(req);
    }


//    // Network Request : JSON OBJ with HEADERS
//    public void networkJsonRequestWithHeaders(Context context, final HashMap<String, String> parameters, String url, int method, final VolleyJsonCallback callback) {
//
//
//        int requestMethod = 0;
//
//        if (method == 1) {
//
//            requestMethod = Request.Method.GET;
//
//        } else if (method == 2) {
//
//            requestMethod = Request.Method.POST;
//
//        } else {
//
//            requestMethod = Request.Method.PUT;
//
//        }
//
//        JsonObjectRequest req = new JsonObjectRequest(requestMethod, url, new JSONObject(parameters), new Response.Listener<JSONObject>() { //Json request
//            @Override
//            public void onResponse(JSONObject response) {
//
//
//                callback.onSuccessResponse(response); // send response back to the calling class
//                Log.e(TAG + "JSON REQ ", response + "");
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Log.e(TAG + "JSON REQ ERR > ", error + "");
//
//                NetworkResponse networkResponse = error.networkResponse;
//                if (networkResponse != null && networkResponse.data != null) {
//                    String jsonError = new String(networkResponse.data);
//                    Log.e(TAG, "volley JSON error : " + jsonError); // volley error description
//                }
//
//                try {
//                    callback.onSuccessResponse(new JSONObject(new String(networkResponse.data)));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", tinyDB.getString("TOKEN"));
//
////                headers.put("Authorization", "Bearer TThfab87wy438rf38vbqb3yf78qybfiqc387fcgnq387fg8cn3ncfg8k");
//
//                Log.e("Headers ", headers + "");
//
//                return headers;
//            }
//        };
//
//        int MY_SOCKET_TIMEOUT_MS = 20000;
//        req.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        Volley.newRequestQueue(context).add(req);
//
//
//    }


}
