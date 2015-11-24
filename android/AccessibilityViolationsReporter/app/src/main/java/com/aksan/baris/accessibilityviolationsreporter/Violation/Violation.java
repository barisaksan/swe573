package com.aksan.baris.accessibilityviolationsreporter.Violation;

import com.aksan.baris.accessibilityviolationsreporter.User.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by baris on 17.11.2015.
 */
public class Violation {

    public Violation(JSONObject jsonViolation) throws JSONException {
        id = jsonViolation.getJSONObject("_id").getString("$oid");
        type = jsonViolation.getString("type");
        location = jsonViolation.getString("location");
        description = jsonViolation.getString("description");
        reporter = jsonViolation.getString("reporter");
    }

    public String toString() {
        String str = description;
        try {
            JSONObject l = new JSONObject(location);
            str = "[" + type + "]" + " @" + l.getString("name") + ": " + description;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String getId() {
        return id;
    }

    String id;
    //ViolationType type;
    String type;
    //Location location;
    String location;
    String description;
    //Rating rating;
    //ViolationProperty[] properties;
    //Photo[] Photos;
    String[] Photos;
    String[] comments;
    //User reporter;
    String reporter;

    //TODO: implement classes
    class Location {}
    class Rating {}
    class ViolationProperty {

    }

    class Photo {
        String alt;
        String name;
        String url;
        String violationId;
    }

    public static enum ViolationType {
        RAMP,
        SIDEWALK,
        LIGHT,
        OTHER
    }
}









