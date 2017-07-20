package com.prescriptionpad.app.android.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.prescriptionpad.app.android.R;
import com.prescriptionpad.app.android.util.Constants;

/**
 * Created by sharana.b on 4/22/2017.
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.action_home:
                intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.action_Profile:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_Logout:
                logout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.Errors.ERROR_MESSAGE, Constants.Errors.KEY_LOGOUT_MESSAGE);
        intent.putExtra(Constants.IS_LOGOUT, true);
        startActivity(intent);
    }
}
