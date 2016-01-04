package com.aksan.baris.accessibilityviolationsreporter;

import android.app.Application;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.test.ActivityUnitTestCase;
import android.test.ApplicationTestCase;
import android.widget.Button;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AddViolationActivityTest extends ActivityUnitTestCase<AddViolationActivity> {
    public AddViolationActivityTest() {
        super(AddViolationActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent mLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), AddViolationActivity.class);
        startActivity(mLaunchIntent, null, null);
    }
}