package com.epicodus.knowyourcongressmen.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.epicodus.knowyourcongressmen.R;
import com.epicodus.knowyourcongressmen.adapters.RepAdapter;
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
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends ListActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private String mZipcode;
    private ArrayList<Representative> mRepresentatives;
    private RepAdapter mAdapter;

    @Bind(R.id.zipCodeInput) EditText mZipCodeInput;
    @Bind(R.id.submitButton) Button mSubmitButton;
    @Bind(R.id.newSearchButton) Button mNewSearchButton;
    private ListView mRepList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRepList = (ListView) findViewById(android.R.id.list);

        mRepresentatives = new ArrayList<Representative>();

        mAdapter = new RepAdapter(this, mRepresentatives);
        setListAdapter(mAdapter);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZipcode = mZipCodeInput.getText().toString();
                getRepresentatives(mZipcode);
                toggleViews();
                mZipCodeInput.setText("");
            }
        });

        mNewSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleViews();
            }
        });
    }

    private void toggleViews() {
        if (mZipCodeInput.getVisibility() == View.VISIBLE) {
            mZipCodeInput.setVisibility(View.INVISIBLE);
            mSubmitButton.setVisibility(View.INVISIBLE);
            mRepList.setVisibility(View.VISIBLE);
            mNewSearchButton.setVisibility(View.VISIBLE);

        } else {
            mZipCodeInput.setVisibility(View.VISIBLE);
            mSubmitButton.setVisibility(View.VISIBLE);
            mRepList.setVisibility(View.INVISIBLE);
            mNewSearchButton.setVisibility(View.INVISIBLE);
        }
    }

    private void getRepresentatives(String zipcode) {
        String apiKey = "b13ce9f96c064ebfaadc9ffe944b33a0";
        String url = "https://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zipcode + "&apikey=" + apiKey;

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
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "OH NO! IOException caught.");
                    } catch (JSONException e) {
                        Log.e(TAG, "OH NO! JSONException caught.");
                    }
                }
            });
        } else {
            alertUserAboutError();
        }
    }

    private void getLocalRepDetails(String jsonData) throws JSONException {
        mRepresentatives.clear();
        JSONObject data = new JSONObject(jsonData);
        JSONArray representatives = data.getJSONArray("results");
        for (int index = 0; index < representatives.length(); index++) {
            JSONObject representativeJSON = representatives.getJSONObject(index);
            String repName = representativeJSON.getString("first_name") + " "
                    + representativeJSON.getString("last_name");
            String repParty = representativeJSON.getString("party");
            String repGender = representativeJSON.getString("gender");
            String repBirthday = representativeJSON.getString("birthday");
            String repPhone = representativeJSON.getString("phone");
            String repOffice = representativeJSON.getString("office");
            Representative representative = new Representative();
            representative.setName(repName);
            representative.setParty(repParty);
            representative.setGender(repGender);
            representative.setBirthday(repBirthday);
            representative.setPhone(repPhone);
            representative.setOffice(repOffice);

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
