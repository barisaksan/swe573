package com.aksan.baris.accessibilityviolationsreporter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aksan.baris.accessibilityviolationsreporter.Violation.GoogleMapsRef;
import com.aksan.baris.accessibilityviolationsreporter.Violation.Location;
import com.aksan.baris.accessibilityviolationsreporter.Violation.UserRef;
import com.aksan.baris.accessibilityviolationsreporter.Violation.Violation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AddViolationActivity extends FragmentActivity
implements OnMapReadyCallback, LocationFragment.OnFragmentInteractionListener {

    private Context mContext = null;
    private Activity mActivity = null;
    private ArrayList<Bitmap> mPhotos = new ArrayList<Bitmap>();
    private double selectedLongitude = 0;
    private double selectedLatitude = 0;
    private String selectedLocationName = "";
    private String selectedLocationAddress = "";

    int REQUEST_IMAGE_CAPTURE = 1;
    int PLACE_PICKER_REQUEST = 2;

    @Override
    public void onMapReady(GoogleMap map) {

        GoogleMapsRef.map = map;

        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        if (ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplication(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        GoogleMapsRef.map.setMyLocationEnabled(true);
        GoogleMapsRef.map.getUiSettings().setMyLocationButtonEnabled(false);
        GoogleMapsRef.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapsInitializer.initialize(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_violation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        mContext = this.getApplicationContext();
        mActivity = this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_location);
        mapFragment.getMapAsync(this);

        final FloatingActionButton addViolationFab = (FloatingActionButton) findViewById(R.id.add_violation_fab);
        addViolationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String desc = ((TextView) findViewById(R.id.add_description)).getText().toString();
                String tags = ((TextView) findViewById(R.id.add_tags)).getText().toString();

                Violation v = new Violation();
                v.setDescription(desc);

                v.setReporter(UserRef.user);

                //TODO:
                v.setType("ramp");

                Location l = new Location();
                l.setName(selectedLocationName);
                l.setCoordinates(selectedLatitude+","+selectedLongitude);
                v.setLocation(l);

                AddViolationTask addViolationTask = new AddViolationTask(mActivity, v);
                addViolationTask.execute();
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

        FloatingActionButton pickLocationFab = (FloatingActionButton) findViewById(R.id.pick_location);
        pickLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(mActivity), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        else if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(mContext, data);
            String toastMsg = String.format("Place: %s", place.getName());
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

            selectedLatitude = place.getLatLng().latitude;
            selectedLongitude = place.getLatLng().longitude;
            selectedLocationName = String.format("%s", place.getName());
            selectedLocationAddress = String.format("%s", place.getAddress());

            LatLng selected = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            GoogleMapsRef.map.addMarker(new MarkerOptions().position(selected).title("Violation"));
            GoogleMapsRef.map.moveCamera(CameraUpdateFactory.newLatLngZoom(selected, 15));

            TextView locName = (TextView) findViewById(R.id.add_location_name);
            locName.setText(selectedLocationName);
            TextView locCoordinates = (TextView) findViewById(R.id.add_location_coordinates);
            locCoordinates.setText(Double.toString(selectedLatitude)+","+Double.toString(selectedLongitude));
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }

    class AddViolationTask extends AsyncTask<String, Void, String> {

        Activity activity;
        Violation violation;
        String baseurl = getString(R.string.accessible_city_server) + "AccessibilityViolationReporter/rest";

        public AddViolationTask(Activity activity, Violation v) {
            this.activity = activity;
            this.violation = v;
        }

        protected String doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            try {
                AsyncHttpClient.BoundRequestBuilder builder = asyncHttpClient.preparePost(baseurl + "/violations");
                Future<Response> lFuture = builder
                        .setBody(mapper.writeValueAsString(violation))
                        .setHeader("Content-Type", "application/json")
                        .execute();

                String violationId = lFuture.get().getResponseBody().toString();

                AddViolationPhotos addViolationPhotos = new AddViolationPhotos(activity, violationId);
                addViolationPhotos.doInBackground();

                return violationId;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            NavUtils.navigateUpFromSameTask(activity);
        }
    }

    class AddViolationPhotos extends AsyncTask<String, Void, String> {

        String violationId;
        Activity activity;
        String baseurl = getString(R.string.accessible_city_server) + "AccessibilityViolationReporter/rest";

        public AddViolationPhotos(Activity activity, String violationId) {
            this.activity = activity;
            this.violationId = violationId;
        }

        protected String doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mPhotos.get(0).compress(Bitmap.CompressFormat.JPEG, 75, bos);
                byte[] data = bos.toByteArray();
                String encodedPhotoStr = Base64.encodeToString(data, Base64.DEFAULT);

                Future<Response> lFuture =
                        asyncHttpClient.preparePost(baseurl + "/violations/" + violationId + "/new_photo")
                                .setHeader("Content-Type", "multipart/form-data")
                                .setBody(encodedPhotoStr)
                                .execute();
                return lFuture.get().toString();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
}
