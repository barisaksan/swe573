package com.aksan.baris.accessibilityviolationsreporter.Violation;

/**
 * Created by baris on 02.01.2016.
 */
public class MongodbId {

    @Override
    public String toString() {
        return $oid;
    }

    public String get$oid() {
        return $oid;
    }

    public void set$oid(String $oid) {
        this.$oid = $oid;
    }

    String $oid;
}
