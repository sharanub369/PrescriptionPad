package com.prescriptionpad.app.android.util;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by sharana.b on 4/13/2017.
 */
public class RealmMasterClass {
    public static final int DB_SCHEME_VERSION = 1;
    public static final String DB_NAME = Constants.KEY_REALM_DB_NAME;
    static RealmConfiguration config = null;

    public static Realm initializeRealm(Context context) {
        config = new RealmConfiguration.Builder(context)
                .name(DB_NAME)
                .schemaVersion(DB_SCHEME_VERSION)
                .build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        return realm;
    }

    public static void deleteRealM(Realm realm) {
        try {
            if(!realm.isClosed())realm.close();
            if(!realm.isClosed())realm.close();
            if(!realm.isClosed())realm.close();
            if(!realm.isClosed())realm.close();
            if(!realm.isClosed())realm.close();
            if(!realm.isClosed())realm.close();
            realm.deleteRealm(realm.getConfiguration());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
