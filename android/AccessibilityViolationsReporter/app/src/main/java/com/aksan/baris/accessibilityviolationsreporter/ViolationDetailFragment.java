package com.aksan.baris.accessibilityviolationsreporter;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aksan.baris.accessibilityviolationsreporter.Violation.Violation;
import com.aksan.baris.accessibilityviolationsreporter.dummy.DummyContent;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.json.*;

/**
 * A fragment representing a single Violation detail screen.
 * This fragment is either contained in a {@link ViolationListActivity}
 * in two-pane mode (on tablets) or a {@link ViolationDetailActivity}
 * on handsets.
 */
public class ViolationDetailFragment extends Fragment {
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

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));


            Activity activity = this.getActivity();
            /*
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
            */

            //GetViolationTask r = new GetViolationTask(activity, "5648bac5f72251f221f496dc");
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
            ((TextView) rootView.findViewById(R.id.violation_detail)).setText(mItem.details);
        }

        return rootView;
    }

    class GetViolationTask extends AsyncTask<String, Void, JSONObject> {

        Activity activity;
        String id;
        String url = "http://192.168.1.106:8080/AccessibilityViolationReporter/rest/violations/";

        public GetViolationTask(Activity activity, String id) {
            this.activity = activity;
            this.id = id;
        }

        protected JSONObject doInBackground(String... urls) {
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            String getViolations = url + id;
            try {
                Response r = asyncHttpClient.prepareGet(getViolations).execute().get();
                Log.wtf("mViolation", r.getResponseBody());
                return new JSONObject(r.getResponseBody());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        protected void onPostExecute(JSONArray result) {
            TextView textView = (TextView) activity.findViewById(R.id.violation_detail);
            try {
                textView.setText(result.getJSONObject(0).getString("description"));
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(result.getJSONObject(0).getString("type"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

