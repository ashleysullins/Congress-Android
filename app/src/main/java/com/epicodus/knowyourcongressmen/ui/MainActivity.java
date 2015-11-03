package com.epicodus.knowyourcongressmen.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompatApi23;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.epicodus.knowyourcongressmen.R;
import com.epicodus.knowyourcongressmen.models.LocalRepresentation;
import com.epicodus.knowyourcongressmen.models.Representative;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private String mZipcode;
    private ArrayList<Representative> mRepresentatives;

    @Bind(R.id.zipCodeInput) EditText mZipcCodeInput;
    @Bind(R.id.submitButton) Button mSubmitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRepresentatives = new ArrayList<Representative>();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZipcode = mZipcCodeInput.getText().toString();
                getRepresentatives(mZipcode);
            }
        });
    }

    private void getRepresentatives(String zipcode) {
        String apiKey = "b13ce9f96c064ebfaadc9ffe944b33a0";
        String url = "congress.api.sunlightfoundation.com/legislators/locate?zip=" + zipcode + "&apikey=" + apiKey;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            getLocalRepDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "OH NO! IOException caught.");
                    } catch (JSONException e) {
                        Log.e(TAG, "OH NO! IOException caught.");
                    }
                }
            });
        } else {
            alertUserAboutError();
        }
    }

    private void getLocalRepDetails(String jsonData) throws JSONException {
        JSONArray representatives = new JSONArray(jsonData);
        for (int index = 0; index < representatives.length(); index++) {
            JSONObject representativeJSON = representatives.getJSONObject(index);
            String repName = representativeJSON.getString("first_name") + " "
                    + representativeJSON.getString("last_name");
            String repParty = representativeJSON.getString("party");
            String repGender = representativeJSON.getString("gender");
            String repBirthday = representativeJSON.getString("birthday");
            Representative representative = new Representative();
            representative.setName(repName);
            representative.setParty(repParty);
            representative.setGender(repGender);
            representative.setBirthday(repBirthday);

            mRepresentatives.add(representative);
        }
    }

    private void alertUserAboutError() {
        AlertDialog show = new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("Oops! Something went wrong!")
                .setNeutralButton("Ok", null)
                .show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}