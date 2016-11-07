package com.vogella.android.navigationwidgetattempt;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    private LatLng pickupPoint;
    private Marker pickupMarker;
    private LatLng destPoint;
    private Marker destMarker;

    private int menuState = 0; //the user is signed out
    private static final int LOGIN_REQUEST_CODE = 10;
    private static final int ONGOING_REQUESTS_CODE = 50;
    private static driver driver = new driver();
    private static final String DUMMY_REQUEST_ID = "1243";
    private static final double DUMMY_PICKUP[] = {15.6023428,32.5873593};
    private static final double DUMMY_DEST[] = {15.5551185, 32.5543017};
    private static final String DUMMY_PASSENGER_NAME = "John Green";
    private static final String DUMMY_PASSENGER_PHONE = "0123456789";
    private static final String DUMMY_STATUS = "on the way";
    private static final String DUMMY_NOTES = "Drive slowly";
    private static final String DUMMY_PRICE = "43";
    private static final String DUMMY_TIME = "06/11/2016 ; 15:45";
    private static request current_request = new request(DUMMY_REQUEST_ID, DUMMY_PICKUP, DUMMY_DEST,
            DUMMY_PASSENGER_NAME, DUMMY_PASSENGER_PHONE, DUMMY_TIME, DUMMY_PRICE, DUMMY_NOTES,
            DUMMY_STATUS);
//    private static request current_request = new request();
    private RecyclerView previous_requests;
    private RecyclerView.Adapter RVadapter;
    private RecyclerView.LayoutManager layoutManager;
//    private Dialog myDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button changeDriverStatus = (Button) findViewById(R.id.driver_status);
        changeDriverStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changeDriverStatus.getText().toString().equals("available"))
                    changeDriverStatus.setText("away");
                else if(changeDriverStatus.getText().toString().equals("away"))
                    changeDriverStatus.setText("available");
            }
        });

        Button cancelRequest = (Button) findViewById(R.id.cancel_request);
        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerBuilder = new AlertDialog.Builder(MainActivity.this);
                alerBuilder.setMessage("Are you sure you want to cancel this request?");
                alerBuilder.setPositiveButton("Yes, cancel the request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.ongoing_request);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "The request has been canceled",
                                Toast.LENGTH_LONG).show();
                        current_request = new request();
                    }
                });
                alerBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alerBuilder.show();
            }
        });
        final Button nextState = (Button) findViewById(R.id.next_state);
        nextState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView current = (TextView) findViewById(R.id.current_status);
                current.setText(nextState.getText().toString());
                current_request.nextStatus();
                if (current_request.status.equals("completed")) {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.ongoing_request);
                    relativeLayout.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Thank you for your efforts! The request is complete",
                            Toast.LENGTH_LONG).show();
                    current_request = new request();
                }
                else
                    nextState.setText(current_request.getNextStatus());
            }
        });


//        previous_requests = (RecyclerView) findViewById(R.id.past_requests);
//        previous_requests.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        previous_requests.setLayoutManager(layoutManager);

        if (driver.username == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
//            finish();
        }
        // ==================== To get location ================

        // Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Location Request
        mLocationRequest = new LocationRequest();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(5000);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(1000);


        // Check device location settings
        LocationSettingsRequest.Builder locationSettingsReqBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        locationSettingsReqBuilder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    0x1);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK){
            if(data.hasExtra("username")){
                String name = data.getExtras().getString("username");
                if(name != null && name.length() > 0){
                    menuState = 1;
                    invalidateOptionsMenu();
                    driver.username = name;
//                    TextView username = (TextView) findViewById(R.id.show_username);
                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    TextView username = (TextView)(navigationView.inflateHeaderView(R.layout.nav_header_main))
                            .findViewById(R.id.show_username);
                    username.setText(name);
                }
            }
        }
        if(requestCode == ONGOING_REQUESTS_CODE && resultCode == RESULT_OK){
//            Toast.makeText(this,data.getExtras().getString("passenger_name"), Toast.LENGTH_LONG).show();
            if(data.hasExtra("passenger_name")) {
                //set the data
                current_request.passenger_name = data.getExtras().getString("passenger_name");
                current_request.passenger_phone = data.getExtras().getString("passenger_phone");
                current_request.status = data.getExtras().getString("status");
                current_request.pickup[0] = data.getExtras().getDouble("pickup_longitude");
                current_request.pickup[1] = data.getExtras().getDouble("pickup_latitude");
                current_request.dest[0] = data.getExtras().getDouble("dest_longitude");
                current_request.dest[1] = data.getExtras().getDouble("dest_latitude");
                current_request.time = data.getExtras().getString("time");

                pickupPoint = new LatLng(current_request.pickup[0],current_request.pickup[1]);
                destPoint = new LatLng(current_request.dest[0],current_request.dest[1]);

                // Setting marker
                if (pickupMarker != null) {
                    pickupMarker.remove();
                }
                pickupMarker = mMap.addMarker(new MarkerOptions()
                        .position(pickupPoint)
                        .title("Pickup")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_loc_smaller))
                );


                // Setting marker
                if (destMarker != null) {
                    destMarker.remove();
                }

                destMarker = mMap.addMarker(new MarkerOptions()
                        .position(destPoint)
                        .title("Destination")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.stop_loc_smaller))
                );

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(pickupPoint).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.ongoing_request);
                Button nextState = (Button) findViewById(R.id.next_state);
                TextView current = (TextView) findViewById(R.id.current_status);
                current.setText(current_request.status);
                String temp = current_request.status;
                current_request.nextStatus();
                nextState.setText(current_request.status);
                current_request.status = temp;
                relativeLayout.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.current_requests) {
            Intent intent = new Intent(this, OngoingRequestsActivity.class);
            startActivityForResult(intent, ONGOING_REQUESTS_CODE);

        } else if (id == R.id.sign_out) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng khartoum = new LatLng(15.5838046, 32.5543825);
//        mMap.addMarker(new MarkerOptions().position(khartoum).title("Marker in Khartoum"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(khartoum, 12 ));
    }




    @Override
    public void onConnected(Bundle bundle) {


        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (LocationListener) this);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mCurrentLocation = mLastLocation;
            Toast.makeText(this, "Connected GPlServices "+mLastLocation.getLatitude()+" "+mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Sorry, it's null", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
