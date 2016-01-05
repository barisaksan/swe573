package com.aksan.baris.accessibilityviolationsreporter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aksan.baris.accessibilityviolationsreporter.Violation.Comment;
import com.aksan.baris.accessibilityviolationsreporter.Violation.GoogleMapsRef;
import com.aksan.baris.accessibilityviolationsreporter.Violation.ServerRef;
import com.aksan.baris.accessibilityviolationsreporter.Violation.UserRef;
import com.aksan.baris.accessibilityviolationsreporter.Violation.Violation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    private ViolationDetailActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        mActivity = this;
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Edit violation", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button addComment = (Button) findViewById(R.id.add_comment_button);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText commentTextView = (EditText)findViewById(R.id.add_comment);

                Comment c = new Comment();
                c.setComment(commentTextView.getText().toString());
                c.setUser(UserRef.user);
                c.setTime(new Date().toString());
                c.setViolationId(getIntent().getStringExtra(ViolationDetailFragment.ARG_ITEM_ID));

                AddViolationCommentTask commentTask = new AddViolationCommentTask(mActivity, getIntent().getStringExtra(ViolationDetailFragment.ARG_ITEM_ID), c);
                commentTask.execute();
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
        }

        SupportMapFragment mapFragment = (SupportMapFragment)this.getSupportFragmentManager()
                .findFragmentById(R.id.view_location);
        mapFragment.getMapAsync(this);

        GetViolationTask r = new GetViolationTask(this, getIntent().getStringExtra(ViolationDetailFragment.ARG_ITEM_ID));
        r.execute();

        GetViolationImagesTask i = new GetViolationImagesTask(this, getIntent().getStringExtra(ViolationDetailFragment.ARG_ITEM_ID));
        i.execute();

        GetViolationCommentsTask c = new GetViolationCommentsTask(this, getIntent().getStringExtra(ViolationDetailFragment.ARG_ITEM_ID));
        c.execute();
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
        String url = ServerRef.server + "AccessibilityViolationReporter/rest/violations/";

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
        String url = ServerRef.server + "AccessibilityViolationReporter/rest/violations/";

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

    class GetViolationCommentsTask extends AsyncTask<String, Void, JSONArray> {

        Activity activity;
        String id;
        String url = ServerRef.server + "AccessibilityViolationReporter/rest/violations/";

        public GetViolationCommentsTask(Activity activity, String id) {
            this.activity = activity;
            this.id = id;
        }

        protected JSONArray doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String getViolations = url + id + "/comments";
            try {
                Response r = asyncHttpClient.prepareGet(getViolations).execute().get();
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

        private View getSeperator(int width, int height) {
            View separator = new View(activity);
            separator.setLayoutParams(new Toolbar.LayoutParams(width, height));
            separator.setBackgroundColor(Color.DKGRAY);
            return separator;
        }

        protected void onPostExecute(JSONArray result) {

            for (int x = 0; x < result.length(); x++) {
                try {
                    TextView user = new TextView(activity);
                    user.setText(result.getJSONObject(x).getString("user"));
                    TextView comment = new TextView(activity);
                    comment.setText(result.getJSONObject(x).getString("comment"));
                    TextView time = new TextView(activity);
                    time.setText(result.getJSONObject(x).getString("time"));
                    LinearLayout nsv = (LinearLayout)activity.findViewById(R.id.view_comments);
                    nsv.addView(user);
                    nsv.addView(getSeperator(nsv.getWidth(), 2));
                    nsv.addView(comment);
                    nsv.addView(getSeperator(nsv.getWidth(), 2));
                    nsv.addView(time);
                    nsv.addView(getSeperator(nsv.getWidth(), 2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class AddViolationCommentTask extends AsyncTask<String, Void, Comment> {

        Activity activity;
        String id;
        Comment comment;
        String baseurl = ServerRef.server + "AccessibilityViolationReporter/rest/comments/";

        public AddViolationCommentTask(Activity activity, String id, Comment c) {
            this.activity = activity;
            this.id = id;
            this.comment = c;
        }

        protected Comment doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            try {
                AsyncHttpClient.BoundRequestBuilder builder = asyncHttpClient.preparePost(baseurl);
                Future<Response> lFuture = builder
                        .setBody(mapper.writeValueAsString(comment))
                        .setHeader("Content-Type", "application/json")
                        .execute();

                String violationId = lFuture.get().getResponseBody().toString();
                return comment;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private View getSeperator(int width, int height) {
            View separator = new View(activity);
            separator.setLayoutParams(new Toolbar.LayoutParams(width, height));
            separator.setBackgroundColor(Color.DKGRAY);
            return separator;
        }

        protected void onPostExecute(Comment result) {
            TextView user = new TextView(activity);
            user.setText(result.getUser());
            TextView comment = new TextView(activity);
            comment.setText(result.getComment());
            TextView time = new TextView(activity);
            time.setText(result.getTime());
            LinearLayout nsv = (LinearLayout)activity.findViewById(R.id.view_comments);
            nsv.addView(user);
            nsv.addView(getSeperator(nsv.getWidth(), 2));
            nsv.addView(comment);
            nsv.addView(getSeperator(nsv.getWidth(), 2));
            nsv.addView(time);
            nsv.addView(getSeperator(nsv.getWidth(), 2));

            EditText newComment = (EditText)activity.findViewById(R.id.add_comment);
            newComment.setText("");
        }
    }
}
