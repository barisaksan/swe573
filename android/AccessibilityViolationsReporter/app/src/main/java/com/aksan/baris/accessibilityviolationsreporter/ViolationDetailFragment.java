package com.aksan.baris.accessibilityviolationsreporter;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aksan.baris.accessibilityviolationsreporter.Violation.GoogleMapsRef;
import com.aksan.baris.accessibilityviolationsreporter.dummy.DummyContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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
import java.util.concurrent.ExecutionException;

/**
 * A fragment representing a single Violation detail screen.
 * This fragment is either contained in a {@link ViolationListActivity}
 * in two-pane mode (on tablets) or a {@link ViolationDetailActivity}
 * on handsets.
 */
public class ViolationDetailFragment extends Fragment implements OnMapReadyCallback {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private JSONObject mViolation;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViolationDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.view_location);
        mapFragment.getMapAsync(this);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            Activity activity = this.getActivity();
            GetViolationTask r = new GetViolationTask(activity, getArguments().getString(ARG_ITEM_ID));
            r.execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_violation_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.view_description)).setText(mItem.details);
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMapsRef.map = googleMap;

        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        if (ActivityCompat.checkSelfPermission(getActivity().getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplication(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        GoogleMapsRef.map.setMyLocationEnabled(true);
        GoogleMapsRef.map.getUiSettings().setMyLocationButtonEnabled(false);
        GoogleMapsRef.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapsInitializer.initialize(getActivity());
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

                TextView locName = (TextView) getActivity().findViewById(R.id.view_location_name);
                locName.setText(locationName);

                TextView locCoordinates = (TextView) getActivity().findViewById(R.id.view_location_coordinates);
                locCoordinates.setText(coordinatesStr);

                TextView reporter = (TextView)activity.findViewById(R.id.view_reporter);
                reporter.setText(reporterStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

