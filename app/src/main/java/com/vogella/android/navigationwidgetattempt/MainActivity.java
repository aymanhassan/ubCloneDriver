package com.vogella.android.navigationwidgetattempt;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private int menuState = 0; //the user is signed out
    private static final int LOGIN_REQUEST_CODE = 10;
    private static driver driver = new driver();
    private static final String DUMMY_REQUEST_ID = "1243";
    private static final String DUMMY_PICKUP = "15.5838046,32.5543825";
    private static final String DUMMY_DEST = "15.8838046, 32.6543825";
    private static final String DUMMY_PASSENGER_NAME = "John Green";
    private static final String DUMMY_PASSENGER_PHONE = "0123456789";
    private static final String DUMMY_STATUS = "on the way";
    private static final String DUMMY_NOTES = "Drive slowly";
    private static final String DUMMY_PRICE = "43";
    private static final String DUMMY_TIME = "06/11/2016 ; 15:45";
    private static request current_request = new request(DUMMY_REQUEST_ID, DUMMY_PICKUP, DUMMY_DEST,
            DUMMY_PASSENGER_NAME, DUMMY_PASSENGER_PHONE, DUMMY_TIME, DUMMY_PRICE, DUMMY_NOTES,
            DUMMY_STATUS);
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
                    nextState.setText(current_request.status);
            }
        });


        previous_requests = (RecyclerView) findViewById(R.id.past_requests);
        previous_requests.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        previous_requests.setLayoutManager(layoutManager);

        if (driver.username == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }

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
                    navigationView.getMenu().findItem(R.id.sign_in).setVisible(false);
                    navigationView.getMenu().findItem(R.id.sign_out).setVisible(true);
                }
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
            RequestAdapter ca = new RequestAdapter(createList(30));
            previous_requests.setAdapter(ca);
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.ongoing_request);
            relativeLayout.setVisibility(View.INVISIBLE);
            //TODO: ensure the current request reappears after the card view is over

        } else if (id == R.id.current_requests) {

        } else if (id == R.id.sign_in) {
//            callLoginDialog();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
            //if(LoginActivity.UserLoginTask.Status)
        } else if (id == R.id.sign_out) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().findItem(R.id.sign_in).setVisible(true);
            navigationView.getMenu().findItem(R.id.sign_out).setVisible(false);
            driver = null;
            TextView username = (TextView) findViewById(R.id.show_username);
            username.setText("not logged in");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng khartoum = new LatLng(15.5838046, 32.5543825);
        mMap.addMarker(new MarkerOptions().position(khartoum).title("Marker in Khartoum"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(khartoum, 12 ));
    }
    public void requestDetails(View view){
        if (R.id.ongoing_request == view.getId()){
            Toast.makeText(this, "Wait! You ACTUUALLY believed you have requests!!!",Toast.LENGTH_SHORT).show();
        }
    }


    private List<request> createList(int size) {

        List<request> result = new ArrayList<request>();
        for (int i=1; i <= size; i++) {
            request ci = new request();
            ci.passenger_name = DUMMY_PASSENGER_NAME + i;
            ci.passenger_phone = DUMMY_PASSENGER_PHONE + i;
            ci.status = DUMMY_STATUS + i;
            ci.time = DUMMY_TIME + i;
            ci.dest = DUMMY_DEST + i;
            ci.pickup = DUMMY_PICKUP + i;

            result.add(ci);

        }

        return result;
    }
    
}
