package com.aksan.baris.accessibilityviolationsreporter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


/**
 * An activity representing a list of Violations. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ViolationDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ViolationListFragment} and the item details
 * (if present) is a {@link ViolationDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ViolationListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ViolationListActivity extends AppCompatActivity
        implements ViolationListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Context mContext;
    private View.OnClickListener newReportClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_app_bar);
        mContext = this.getApplicationContext();

        newReportClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fabDoor = (FloatingActionButton)findViewById(R.id.fabDoor);
                FloatingActionButton fabCarPark = (FloatingActionButton)findViewById(R.id.fabCarPark);
                FloatingActionButton fabRamp = (FloatingActionButton)findViewById(R.id.fabRamp);
                FloatingActionButton fabReport = (FloatingActionButton)findViewById(R.id.fabReport);
                View notFab = findViewById(R.id.notFab);

                fabDoor.setVisibility(View.VISIBLE);
                fabCarPark.setVisibility(View.VISIBLE);
                fabRamp.setVisibility(View.VISIBLE);
                notFab.setVisibility(View.VISIBLE);

                fabDoor.animate()
                        .translationYBy(-50)
                        .alpha(1.0f);
                fabCarPark.animate()
                        .translationYBy(-100)
                        .alpha(1.0f);
                fabRamp.animate()
                        .translationYBy(-150)
                        .alpha(1.0f);
                notFab.animate().alpha(0.8f);

                fabReport.setImageDrawable(
                        ContextCompat.getDrawable(mContext, R.drawable.ic_dots_horizontal_white_24dp));
                fabReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Report other violation", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final FloatingActionButton fabReport = (FloatingActionButton) findViewById(R.id.fabReport);
        fabReport.setOnClickListener(newReportClickListener);

        findViewById(R.id.notFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fabDoor = (FloatingActionButton)findViewById(R.id.fabDoor);
                FloatingActionButton fabCarPark = (FloatingActionButton)findViewById(R.id.fabCarPark);
                FloatingActionButton fabRamp = (FloatingActionButton)findViewById(R.id.fabRamp);
                FloatingActionButton fabReport = (FloatingActionButton)findViewById(R.id.fabReport);

                fabReport.setOnClickListener(newReportClickListener);
                fabReport.setImageDrawable(
                        ContextCompat.getDrawable(mContext, R.drawable.ic_add_white_24dp));
                fabCarPark.setVisibility(View.INVISIBLE);
                fabDoor.setVisibility(View.INVISIBLE);
                fabRamp.setVisibility(View.INVISIBLE);
                fabDoor.animate()
                        .translationYBy(50)
                        .alpha(0.0f);
                fabCarPark.animate()
                        .translationYBy(100)
                        .alpha(0.0f);
                fabRamp.animate()
                        .translationYBy(150)
                        .alpha(0.0f);

                findViewById(R.id.notFab).setVisibility(View.GONE);
            }
        });

        if (findViewById(R.id.violation_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ViolationListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.violation_list))
                    .setActivateOnItemClick(true);
        }

        int iColor = Color.parseColor("#FFFFFF");
        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;
        float[] matrix = {
                0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0 };
        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        FloatingActionButton fabCarPark = (FloatingActionButton) findViewById(R.id.fabCarPark);
        FloatingActionButton fabDoor = (FloatingActionButton) findViewById(R.id.fabDoor);
        FloatingActionButton fabRamp = (FloatingActionButton) findViewById(R.id.fabRamp);
        fabCarPark.setColorFilter(colorFilter);
        fabDoor.setColorFilter(colorFilter);
        fabRamp.setColorFilter(colorFilter);
        fabCarPark.setVisibility(View.INVISIBLE);
        fabDoor.setVisibility(View.INVISIBLE);
        fabRamp.setVisibility(View.INVISIBLE);
        fabDoor.animate()
                .translationYBy(50)
                .alpha(0.0f);
        fabCarPark.animate()
                .translationYBy(100)
                .alpha(0.0f);
        fabRamp.animate()
                .translationYBy(150)
                .alpha(0.0f);

        fabDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(mContext, AddViolationActivity.class);
                detailIntent.putExtra(ViolationDetailFragment.ARG_ITEM_ID, "door");
                startActivity(detailIntent);
//                Snackbar.make(view, "Report a new door related violation", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        fabCarPark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(mContext, AddViolationActivity.class);
                detailIntent.putExtra(ViolationDetailFragment.ARG_ITEM_ID, "car");
                startActivity(detailIntent);
//                Snackbar.make(view, "Report a new car park related violation", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        fabRamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(mContext, AddViolationActivity.class);
                detailIntent.putExtra(ViolationDetailFragment.ARG_ITEM_ID, "ramp");
                startActivity(detailIntent);
//                Snackbar.make(view, "Report a new ramp related violation", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.search_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Search violations", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link ViolationListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ViolationDetailFragment.ARG_ITEM_ID, id);
            ViolationDetailFragment fragment = new ViolationDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.violation_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ViolationDetailActivity.class);
            detailIntent.putExtra(ViolationDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
