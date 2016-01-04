package com.aksan.baris.accessibilityviolationsreporter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aksan.baris.accessibilityviolationsreporter.Violation.GoogleMapsRef;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * An activity representing a single Violation detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ViolationListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ViolationDetailFragment}.
 */
public class ViolationDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            /*
            Bundle arguments = new Bundle();
            arguments.putString(ViolationDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ViolationDetailFragment.ARG_ITEM_ID));
            ViolationDetailFragment fragment = new ViolationDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.violation_detail_container, fragment)
                    .commit();

            */
/*
            CommentListFragment fragment2 = new CommentListFragment();
            fragment2.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.view_comments, fragment2)
                    .commit();
*/
        }

        SupportMapFragment mapFragment = (SupportMapFragment)this.getSupportFragmentManager()
                .findFragmentById(R.id.view_location);
        mapFragment.getMapAsync(this);

        GetViolationTask r = new GetViolationTask(this, getIntent().getStringExtra(ViolationDetailFragment.ARG_ITEM_ID));
        r.execute();

        GetViolationImagesTask i = new GetViolationImagesTask(this, getIntent().getStringExtra(ViolationDetailFragment.ARG_ITEM_ID));
        i.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMapsRef.map = googleMap;

        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        if (ActivityCompat.checkSelfPermission(this.getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getApplication(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        GoogleMapsRef.map.setMyLocationEnabled(true);
        GoogleMapsRef.map.getUiSettings().setMyLocationButtonEnabled(false);
        GoogleMapsRef.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapsInitializer.initialize(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ViolationListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class GetViolationTask extends AsyncTask<String, Void, JSONArray> {

        Activity activity;
        String id;
        String url = "http://192.168.1.109:8080/AccessibilityViolationReporter/rest/violations/";

        public GetViolationTask(Activity activity, String id) {
            this.activity = activity;
            this.id = id;
        }

        protected JSONArray doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String getViolations = url + id;
            try {
                Response r = asyncHttpClient.prepareGet(getViolations).execute().get();
                Log.wtf("mViolation", r.getResponseBody());
                return new JSONArray(r.getResponseBody());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new JSONArray();
        }

        protected void onPostExecute(JSONArray result) {
            try {
                String coordinatesStr = result.getJSONObject(0).getJSONObject("location").getString("coordinates");
                String[] coordinates = coordinatesStr.split(",");
                String locationName = result.getJSONObject(0).getJSONObject("location").getString("name");
                String reporterStr = result.getJSONObject(0).getString("reporter");
                String descriptionStr = result.getJSONObject(0).getString("description");

                TextView description = (TextView) activity.findViewById(R.id.view_description);
                description.setText(descriptionStr);

                double latitude = Double.parseDouble(coordinates[0]);
                double longitude = Double.parseDouble(coordinates[1]);
                LatLng loc = new LatLng(latitude, longitude);

                GoogleMapsRef.map.addMarker(new MarkerOptions().position(loc).title("Violation"));
                GoogleMapsRef.map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

                TextView locName = (TextView) findViewById(R.id.view_location_name);
                locName.setText(locationName);

                TextView locCoordinates = (TextView) findViewById(R.id.view_location_coordinates);
                locCoordinates.setText(coordinatesStr);

                TextView reporter = (TextView)activity.findViewById(R.id.view_reporter);
                reporter.setText(reporterStr);

                Toolbar toolbar = (Toolbar) activity.findViewById(R.id.detail_toolbar);
                toolbar.setTitle(result.getJSONObject(0).getString("type"));
                toolbar.setSubtitle(result.getJSONObject(0).getString("reporter"));
                ImageView detailImage = (ImageView) activity.findViewById(R.id.detail_image);
                String type = result.getJSONObject(0).getString("type");
                switch (type) {
                    case "bump":
                        detailImage.setImageDrawable(ContextCompat.getDrawable(
                                activity.getApplicationContext(),
                                R.drawable.bump_noun_37451_cc));
                        break;
                    case "ramp":
                        detailImage.setImageDrawable(ContextCompat.getDrawable(
                                activity.getApplicationContext(),
                                R.drawable.ramp_noun_169108_cc));
                        break;
                    case "bus_stop":
                        detailImage.setImageDrawable(ContextCompat.getDrawable(
                                activity.getApplicationContext(),
                                R.drawable.bus_stop_noun_19258_cc));
                        break;
                    case "car_park":
                        detailImage.setImageDrawable(ContextCompat.getDrawable(
                                activity.getApplicationContext(),
                                R.drawable.car_park_noun_157120_cc));
                        break;
                    case "pavement":
                        detailImage.setImageDrawable(ContextCompat.getDrawable(
                                activity.getApplicationContext(),
                                R.drawable.pavement_noun_145921_cc));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class GetViolationImagesTask extends AsyncTask<String, Void, String> {

        Activity activity;
        String id;
        String url = "http://192.168.1.109:8080/AccessibilityViolationReporter/rest/violations/";

        public GetViolationImagesTask(Activity activity, String id) {
            this.activity = activity;
            this.id = id;
        }

        protected String doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String getViolations = url + id + "/photos";
            try {
                Response r = asyncHttpClient.prepareGet(getViolations).execute().get();
                return r.getResponseBody();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            byte[] data = Base64.decode(result, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            ImageView detailImage = (ImageView) activity.findViewById(R.id.detail_image);
            detailImage.setImageBitmap(bmp);
        }
    }
}
