package com.prescriptionpad.app.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

/**
 * Created by sharana.b on 4/30/2017.
 */
public class BackupRestoreMasterClass {
    private static final File EXPORT_REALM_PATH = Environment.getExternalStorageDirectory();
    private static final String EXPORT_REALM_FILE_NAME = Constants.KEY_REALM_DB_NAME;
    private static final String IMPORT_REALM_FILE_NAME = Constants.KEY_REALM_DB_NAME; // Eventually replace activity if you're using a custom db name

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static void checkStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static String exportRealmDB(Realm realm, Context context, Activity activity) {
        checkStoragePermissions(activity);
        File exportRealmFile = null;

        Log.d("DBPATH", "Realm DB Path = " + realm.getPath());

        try {
            File file = new File(EXPORT_REALM_PATH.getAbsolutePath() + "/" + Constants.KEY_PP_FOLDER_NAME);
            if (!file.exists()) {
                file.mkdir();
            }

            // create a backup file
            exportRealmFile = new File(file, EXPORT_REALM_FILE_NAME);

            // if backup file already exists, delete it
            exportRealmFile.delete();

            // copy current realm to backup file
            if (!realm.isClosed()) {
                realm.writeCopyTo(exportRealmFile);
            } else {
                RealmMasterClass.initializeRealm(context).writeCopyTo(exportRealmFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exportRealmFile.toString();
    }

    public static Boolean importRealDB(Realm realm, Context context, Activity activity) {
        Boolean isImportSuccess = true;
        checkStoragePermissions(activity);
        String filePath = EXPORT_REALM_PATH.getAbsolutePath() + "/" + Constants.KEY_PP_FOLDER_NAME + "/" + IMPORT_REALM_FILE_NAME;
        RealmMasterClass.deleteRealM(realm);
        isImportSuccess = copyBundledRealmFile(context, filePath, IMPORT_REALM_FILE_NAME);
        return isImportSuccess;
    }

    private static Boolean copyBundledRealmFile(Context context, String oldFilePath, String outFileName) {
        try {
            File file = new File(context.getFilesDir(), outFileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            Toast.makeText(context, Constants.SuccessMessages.KEY_IMPORT_SUCCESS, Toast.LENGTH_LONG).show();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, Constants.Errors.KEY_FILE_NOT_FOUND, Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
