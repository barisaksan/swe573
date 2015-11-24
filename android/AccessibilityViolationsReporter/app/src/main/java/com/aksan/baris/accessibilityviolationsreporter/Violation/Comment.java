package com.aksan.baris.accessibilityviolationsreporter.Violation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by baris on 24.11.2015.
 */
public class Comment {

    public Comment (JSONObject jsonComment) throws JSONException {
        user = jsonComment.getString("user");
        comment = jsonComment.getString("comment");
        time = jsonComment.getString("time");
        violationId = jsonComment.getString("violation_id");
    }

    public String toString() {
        return "[" + time + "]" + ": " + user + " : " + comment;
    }

    String user;
    String comment;
    String time;
    String violationId;
}
