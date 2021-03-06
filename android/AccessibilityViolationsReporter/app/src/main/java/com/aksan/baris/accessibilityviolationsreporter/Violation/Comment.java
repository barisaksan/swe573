package com.aksan.baris.accessibilityviolationsreporter.Violation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by baris on 24.11.2015.
 */
public class Comment {

    MongodbId _id;
    String violationId;
    String user;
    String comment;
    String time;

    public String toString() {
        return "[" + time + "]" + ": " + user + " : " + comment;
    }

    public MongodbId get_id() {
        return _id;
    }

    public void set_id(MongodbId _id) {
        this._id = _id;
    }

    public String getViolationId() {
        return violationId;
    }

    public void setViolationId(String violationId) {
        this.violationId = violationId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
