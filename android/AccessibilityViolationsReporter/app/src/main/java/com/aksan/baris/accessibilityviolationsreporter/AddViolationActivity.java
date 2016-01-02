package com.aksan.baris.accessibilityviolationsreporter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;

import com.aksan.baris.accessibilityviolationsreporter.Violation.Location;
import com.aksan.baris.accessibilityviolationsreporter.Violation.Violation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Param;
import com.ning.http.client.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AddViolationActivity extends AppCompatActivity {

    private Context mContext = null;
    private Activity mActivity = null;
    private ArrayList<Bitmap> mPhotos = new ArrayList<Bitmap>();

    int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_violation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this.getApplicationContext();
        mActivity = this;

        final FloatingActionButton addViolationFab = (FloatingActionButton) findViewById(R.id.add_violation_fab);
        addViolationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent detailIntent = new Intent(mContext, CameraActivity.class);
//                detailIntent.putExtra(Camera2BasicFragment.ARG_PHOTO, "photo");
//                startActivityForResult(detailIntent, 1);

                AddViolationTask addViolationTask = new AddViolationTask(mActivity);
                addViolationTask.execute();

                AddViolationPhotos addViolationPhotos = new AddViolationPhotos(mActivity);
                addViolationPhotos.execute();
            }
        });

        FloatingActionButton addPhotoFab = (FloatingActionButton) findViewById(R.id.add_photo_fab);
        addPhotoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        // Gets the MapView from the XML layout and creates it
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        GoogleMap map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            mPhotos.add(imageBitmap);

            CollapsingToolbarLayout c = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            c.setContentScrim(new BitmapDrawable(mContext.getResources(), imageBitmap));

            AppBarLayout a = (AppBarLayout) findViewById(R.id.app_bar);
            a.setBackground(new BitmapDrawable(mContext.getResources(), imageBitmap));
        }
    }

    class AddViolationTask extends AsyncTask<String, Void, JSONArray> {

        Activity activity;
        //String baseurl = "http://aksan.duckdns.org:8080/AccessibilityViolationReporter/rest";
        //String baseurl = "http://192.168.1.150:8080/AccessibilityViolationReporter/rest";
        String baseurl = "http://192.168.1.109:8080/AccessibilityViolationReporter/rest";

        public AddViolationTask(Activity activity) {
            this.activity = activity;
        }

        protected JSONArray doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            try {
                //Violation v = new Violation(new JSONObject("{\"type\" : \"ramp\" , \"location\" : { \"name\" : \"balikesir\" , \"coordinates\" : \"41.043042,29.006645\"} , \"description\" : \"pof\" , \"reporter\" : \"barisaksan\" }"));

                Violation v = new Violation();
                v.setDescription("new violation");
                v.setReporter("barisaksan");
                v.setType("ramp");
                Location l = new Location();
                l.setName("uzuncaova");
                l.setCoordinates("123456,576789");
                v.setLocation(l);

                ObjectMapper mapper = new ObjectMapper();

                AsyncHttpClient.BoundRequestBuilder builder = asyncHttpClient.preparePost(baseurl + "/violations");
                Future<Response> lFuture = builder
                        .setBody(mapper.writeValueAsString(v))
                        .setHeader("Content-Type", "application/json")
                        .execute();
                return new JSONArray(lFuture.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return new JSONArray();
        }
    }

    class AddViolationPhotos extends AsyncTask<String, Void, JSONArray> {

        Activity activity;
        //String baseurl = "http://aksan.duckdns.org:8080/AccessibilityViolationReporter/rest";
        //String baseurl = "http://192.168.1.150:8080/AccessibilityViolationReporter/rest";
        String baseurl = "http://192.168.1.109:8080/AccessibilityViolationReporter/rest";

        public AddViolationPhotos(Activity activity) {
            this.activity = activity;
        }

        protected JSONArray doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

            try {
                String violationId = "123456789";

                ArrayList<Param> params = new ArrayList<Param>();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mPhotos.get(0).compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                String encodedPhotoStr = Base64.encodeToString(data, Base64.DEFAULT);

                params.add(new Param("newPhoto", encodedPhotoStr));
                Future<Response> lFuture =
                        asyncHttpClient.preparePost(baseurl + "/violations/" + violationId + "/new_photo")
                                .setHeader("Content-Type", "multipart/form-data")
                                .setBody(data)
                                .execute();
/*
                AsyncHttpClient.BoundRequestBuilder builder = asyncHttpClient.preparePost(baseurl + "/violations");
                Future<Response> lFuture = builder
                        .setBody(encodedPhotoStr)
                        .setHeader("Content-Type", "multipart/form-data")
                        .execute();
*/
                return new JSONArray(lFuture.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new JSONArray();
        }
    }
}
