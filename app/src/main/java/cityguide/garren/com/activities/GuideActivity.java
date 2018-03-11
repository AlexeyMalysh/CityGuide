package cityguide.garren.com.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import cityguide.garren.com.R;
import cityguide.garren.com.adapters.ResultsAdapter;
import cityguide.garren.com.models.Result;
import cityguide.garren.com.utils.NearbyPlacesData;
import cityguide.garren.com.utils.NearbyPlacesUtil;
import cityguide.garren.com.utils.PermissionUtil;

public class GuideActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private String TAG = "GuideActivity";

    private String TYPE_BAR = "bar";
    private String TYPE_BISTRO = "restaurant";
    private String TYPE_CAFE = "cafe";

    private Context mContext;
    private TextView mMenu1;
    private TextView mMenu2;
    private TextView mMenu3;
    private SeekBar mSeekBar;
    private ListView mResultsView;
    private View mPermissionLayout;
    private DrawerLayout mDrawer;
    private SwipeRefreshLayout mRefreshLayout;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private NearbyPlacesUtil mNearbyPlacesUtil;

    private int mRadiusPref;
    private Location mLastKnownLocation;
    private String mZip;
    private String selectedType;

    private NearbyPlacesData nearbyData;
    private ArrayList<Result> mCompiledResults;

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        mNearbyPlacesUtil = new NearbyPlacesUtil();

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        setContentView(R.layout.activity_guide);
        mPermissionLayout = findViewById(R.id.main_content);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMenu1 = findViewById(R.id.menuText1);
        mMenu2 = findViewById(R.id.menuText2);
        mMenu3 = findViewById(R.id.menuText3);

        mSeekBar = findViewById(R.id.menuSeekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < 18) {
                    seekBar.setProgress(18);
                    updateSelectedMenu(0);
                } else if (progress > 82) {
                    seekBar.setProgress(82);
                    updateSelectedMenu(2);
                } else {
                    if(progress < 40) {
                        updateSelectedMenu(0);
                    } else if(progress > 40 && progress < 60) {
                        updateSelectedMenu(1);
                    } else if(progress > 60) {
                        updateSelectedMenu(2);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = mSeekBar.getProgress();
                if(progress < 18) {
                    seekBar.setProgress(18);
                    updateSelectedMenu(0);
                    checkLocationPermission(TYPE_BAR);
                } else if (progress > 82) {
                    seekBar.setProgress(82);
                    updateSelectedMenu(2);
                    checkLocationPermission(TYPE_CAFE);
                } else {
                    if(progress < 40) {
                        seekBar.setProgress(18);
                        updateSelectedMenu(0);
                        checkLocationPermission(TYPE_BAR);
                    } else if(progress >= 40 && progress <= 60) {
                        seekBar.setProgress(50);
                        updateSelectedMenu(1);
                        checkLocationPermission(TYPE_BISTRO);
                    } else if(progress > 60) {
                        seekBar.setProgress(82);
                        updateSelectedMenu(2);
                        checkLocationPermission(TYPE_CAFE);
                    }
                }
            }
        });

        mRefreshLayout = findViewById(R.id.swipeRefresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(selectedType == null) {
                    selectedType = TYPE_BAR;
                }
                checkLocationPermission(selectedType);
            }
        });

        mResultsView = findViewById(R.id.resultsList);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mRadiusPref = Integer.valueOf(prefs.getString("location_radius", "10"));

        if(selectedType == null) {
            selectedType = TYPE_BAR;
        }

        checkLocationPermission(selectedType);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int newPref = Integer.valueOf(prefs.getString("location_radius", "10"));
        if(newPref != mRadiusPref) {
            mRadiusPref = newPref;

            if(selectedType == null) {
                selectedType = TYPE_BAR;
            }
            checkLocationPermission(selectedType);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guide, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(mContext, SettingsActivity.class);
            mContext.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_location) {
            final EditText edittext = new EditText(this);
            edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
            edittext.setImeOptions(EditorInfo.IME_ACTION_DONE);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getString(R.string.zip_dialog_title));
            dialog.setMessage(getString(R.string.zip_dialog_message));
            dialog.setView(edittext);

            dialog.setPositiveButton(getString(R.string.zip_dialog_search_btn), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //What ever you want to do with the value
                    mZip = edittext.getText().toString();
                    checkLocationPermission(selectedType);
                }
            });

            dialog.setNegativeButton(getString(R.string.zip_dialog_mine_btn), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    mZip = null;
                    checkLocationPermission(selectedType);
                }
            });

            dialog.show();
        } else if (id == R.id.nav_share) {
            //TODO: Share on Social Media where you are going
        } else if (id == R.id.nav_send) {
            //TODO: Share through text, where you are going/invite along
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getDeviceLocation(final String type) {
        selectedType = type;
        try {
            if(mZip != null) {
                Geocoder geocoder = new Geocoder(this);
                try{
                    List<Address> addresses = geocoder.getFromLocationName(mZip, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();

                        Location zipLocation = new Location(mZip);
                        zipLocation.setLatitude(latitude);
                        zipLocation.setLongitude(longitude);

                        mLastKnownLocation = zipLocation;
                        getNearbyPlaces(type);
                    } else {
                        // Display appropriate message when Geocoder services are not available
                        Toast.makeText(this, getString(R.string.invalid_zip), Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e) {
                    Toast.makeText(this, getString(R.string.zip_search_failed), Toast.LENGTH_LONG).show();
                }
            } else {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null) {
                                getNearbyPlaces(type);
                            } else {
                                Toast.makeText(mContext, getString(R.string.my_location_search_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch(SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getNearbyPlaces(String nearbyPlaceType) {
        setProgressVisibility(true);

        if(nearbyData != null && !nearbyData.isCancelled()) {
            nearbyData.cancel(true);
        }

        clearCompiledResults();

        int type = mNearbyPlacesUtil.getNearbyTypeAsInt(nearbyPlaceType);
        String url = mNearbyPlacesUtil.getNearbyPlacesRequestUrl(mLastKnownLocation, nearbyPlaceType, mRadiusPref, null, mContext);
        nearbyData = new NearbyPlacesData();
        Object[] args = new Object[4];
        args[0] = GuideActivity.this;
        args[1] = url;
        args[2] = type;
        args[3] = mLastKnownLocation;
        nearbyData.execute(args);
    }

    public void getNextNearbyPlaces(int type, String nextPage) {
        String nearbyType = mNearbyPlacesUtil.getNearbyTypeAsString(type);

        String url = mNearbyPlacesUtil.getNearbyPlacesRequestUrl(mLastKnownLocation, nearbyType, mRadiusPref, nextPage, mContext);
        final NearbyPlacesData data = new NearbyPlacesData();
        final Object[] args = new Object[4];
        args[0] = GuideActivity.this;
        args[1] = url;
        args[2] = type;
        args[3] = mLastKnownLocation;

        //There seems to be a short delay before next token works, so wait a little over a second before fetching next
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                data.execute(args);
            }
        }, 1100);
    }

    public void setupResults(ArrayList<Result> results) {
        Log.d("GARREN", "Size = " + results.size());
        ResultsAdapter resultsAdapter = new ResultsAdapter(mContext, R.layout.result_list_item, results);
        mResultsView.setAdapter(resultsAdapter);
        setProgressVisibility(false);
    }

    public void updateSelectedMenu(int position) {
        if(position == 0) {
            mMenu1.setTextAppearance(this, R.style.MenuSelectedText);
            mMenu2.setTextAppearance(this, R.style.MenuDefaultText);
            mMenu3.setTextAppearance(this, R.style.MenuDefaultText);
        } else if(position == 1) {
            mMenu1.setTextAppearance(this, R.style.MenuDefaultText);
            mMenu2.setTextAppearance(this, R.style.MenuSelectedText);
            mMenu3.setTextAppearance(this, R.style.MenuDefaultText);
        } else {
            mMenu1.setTextAppearance(this, R.style.MenuDefaultText);
            mMenu2.setTextAppearance(this, R.style.MenuDefaultText);
            mMenu3.setTextAppearance(this, R.style.MenuSelectedText);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void checkLocationPermission(String type) {
        Log.i(TAG, "Checking permission.");
        // Check if the Location permission is already available.
        if (mZip == null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Location permission has not been granted.
            requestLocationPermission();
        } else {
            // Location permissions is already available, get the user's location
            getDeviceLocation(type);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void requestLocationPermission() {
        Log.i(TAG, "Location permission has NOT been granted. Requesting permission.");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(mPermissionLayout, R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(GuideActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_ACCESS_FINE_LOCATION
                            );
                        }
                    }).show();
        } else {
            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(GuideActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                // We have requested multiple permissions for contacts, so all of them need to be checked.
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    // All required permissions have been granted, display contacts fragment.
                    Snackbar.make(mPermissionLayout, getResources().getString(R.string.permission_available_coarse_location), Snackbar.LENGTH_SHORT).show();
                    if(selectedType == null) {
                        selectedType = TYPE_BAR;
                    }
                    getDeviceLocation(selectedType);
                } else {
                    Log.i(TAG, "Location permissions were NOT granted. Try searching be Zip in the Menu.");
                    Snackbar.make(mPermissionLayout, getResources().getString(R.string.permissions_not_granted), Snackbar.LENGTH_SHORT).show();

                    //If Location permission not granted, kindly nudge them to the drawer to search by zip
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            mDrawer.openDrawer(Gravity.LEFT);
                        }
                    }, 2000);
                }
            }
        }
    }

    private void clearCompiledResults() {
        mCompiledResults = null;
    }

    public ArrayList<Result> getCompiledResults() {
        return mCompiledResults;
    }

    public void setCompiledResults(ArrayList<Result> compiledResults) {
        if(mCompiledResults == null) {
            mCompiledResults = compiledResults;
        } else {
            mCompiledResults.addAll(compiledResults);
        }
    }

    public void setProgressVisibility(Boolean isVisible) {
        if(isVisible) {
            mRefreshLayout.setRefreshing(true);
            mSeekBar.setEnabled(false);
        } else {
            mRefreshLayout.setRefreshing(false);
            mSeekBar.setEnabled(true);
        }
    }
}
