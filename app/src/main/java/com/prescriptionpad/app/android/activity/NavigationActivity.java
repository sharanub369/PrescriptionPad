package com.prescriptionpad.app.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.model.DoctorModel;
import com.prescriptionpad.app.android.util.Constants;
import com.prescriptionpad.app.android.util.GoogleClientClass;
import com.prescriptionpad.app.android.util.RealmMasterClass;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;

/**
 * Created by sharana.b on 4/25/2017.
 */
public abstract class NavigationActivity extends AppCompatActivity {

    protected static final int NAV_DRAWER_ITEM_INVALID = -1;
    private DrawerLayout drawerLayout;
    private Toolbar actionBarToolbar;
    private NavigationView navigationView;
    private GoogleApiClient mGoogleApiClient;
    Realm realm;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        realm = RealmMasterClass.initializeRealm(this);
        setupNavDrawer();
        setNavHeaderValues();
    }

    public abstract boolean providesActivityToolbar();

    /**
     * Sets up the navigation drawer.
     */
    private void setupNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            return;
        }
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
            setupDrawerSelectListener(navigationView);
            setSelectedItem(navigationView);
        }
    }

    /**
     * Updated the checked item in the navigation drawer
     *
     * @param navigationView the navigation view
     */
    private void setSelectedItem(NavigationView navigationView) {
        // Which navigation item should be selected?
        int selectedItem = getSelfNavDrawerItem(); // subclass has to override this method
        navigationView.setCheckedItem(selectedItem);
    }

    /**
     * Creates the item click listener.
     *
     * @param navigationView the navigation view
     */
    private void setupDrawerSelectListener(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        drawerLayout.closeDrawers();
                        int menuId = 0;
                        menuId = menuItem.getItemId();
                        onNavigationItemClicked(menuId);
                        return true;
                    }
                });
    }

    /**
     * Handles the navigation item click.
     *
     * @param itemId the clicked item
     */
    private void onNavigationItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            // Already selected
            closeDrawer();
            return;
        }

        goToNavDrawerItem(itemId);
    }

    /**
     * Handles the navigation item click and starts the corresponding activity.
     *
     * @param item the selected navigation item
     */
    private void goToNavDrawerItem(int item) {
        Intent intent = null;
        switch (item) {
            case R.id.nav_home:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case R.id.nav_faq:
                //startActivity(new Intent(this, FAQActivity.class));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.KEY_FAQ_URL));
                startActivity(browserIntent);
                //finish();
                break;
            case R.id.nav_profile:
                DoctorModel model = realm.where(DoctorModel.class).findFirst();
                if (model != null) {
                    intent = new Intent(this, DoctorDetailsViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.IS_DOCTOR_EXIST, true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, DoctorDetailsAddActivity.class));
                }
                finish();
                break;
            case R.id.nav_support:
                showSupportDialog();
                break;
            case R.id.nav_logout:
                logout();
                //   finish();
                break;
        }
    }

    private void showSupportDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_dialog_support_window, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView supportTxt = (TextView) dialogView.findViewById(R.id.supportTxt);
        TextView sendEmailTxtBtn = (TextView) dialogView.findViewById(R.id.sendEmailTxtBtn);
        String supportText = getResources().getString(R.string.supportText);
//        String supportEmail = getResources().getString(R.string.supportEmail);
//        String next = "<font color='#EE0000'>" + supportEmail + "</font>";
        supportTxt.setText(Html.fromHtml(supportText));
        TextView cancelTxtBtn = (TextView) dialogView.findViewById(R.id.cancelTxtBtn);
        sendEmailTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
                dialog.dismiss();
            }
        });
        cancelTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void sendEmail() {
        String[] TO = {getString(R.string.supportEmail)};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"+TO));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message here");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail by using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NavigationActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Provides the action bar instance.
     *
     * @return the action bar.
     */

    protected ActionBar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }


    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * have to override this method.
     */
    protected int getSelfNavDrawerItem() {
        return NAV_DRAWER_ITEM_INVALID;
    }

    protected void openDrawer() {
        if (drawerLayout == null)
            return;

        drawerLayout.openDrawer(GravityCompat.START);
    }

    protected void closeDrawer() {
        if (drawerLayout == null)
            return;

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void logout() {
//        GoogleApiClient mGoogleApiClient = GoogleClientClass.getGoogleClient(this);
//        mGoogleApiClient.connect();
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
//                        intent.putExtra(Constants.Errors.ERROR_MESSAGE, Constants.Errors.KEY_LOGOUT_MESSAGE);
//                        intent.putExtra(Constants.IS_LOGOUT, true);
//                        startActivity(intent);
//                    }
//                });

        mGoogleApiClient = GoogleClientClass.getGoogleClient(this);
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
                                intent.putExtra(Constants.Errors.ERROR_MESSAGE, Constants.Errors.KEY_LOGOUT_MESSAGE);
                                intent.putExtra(Constants.IS_LOGOUT, true);
                                startActivity(intent);
                                Toast.makeText(NavigationActivity.this, Constants.Errors.KEY_LOGOUT_MESSAGE, Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }

    public void setNavHeaderValues() {
        DoctorModel model = realm.where(DoctorModel.class).findFirst();
        View headerView = navigationView.inflateHeaderView(R.layout.include_navigation_header);
        ImageView navHeaderImg = (ImageView) headerView.findViewById(R.id.navHeaderImg);
        TextView navHeaderTxt = (TextView) headerView.findViewById(R.id.navHeaderTxt);
        if (model != null) {
            if (model.getHospitalLogoBytes() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(model.getHospitalLogoBytes(), 0, model.getHospitalLogoBytes().length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int ih = (int) (bitmap.getHeight() * (82.0 / bitmap.getWidth()));
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 82, ih, true);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                navHeaderImg.setImageBitmap(scaledBitmap);
            }

            navHeaderTxt.setText(model.getHospitalName());
        }

    }
}
