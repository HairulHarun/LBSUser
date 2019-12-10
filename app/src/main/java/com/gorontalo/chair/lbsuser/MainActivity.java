package com.gorontalo.chair.lbsuser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gorontalo.chair.lbsuser.adapter.SessionAdapter;
import com.gorontalo.chair.lbsuser.adapter.URLAdapter;
import com.gorontalo.chair.lbsuser.adapter.VolleyAdapter;
import com.gorontalo.chair.lbsuser.model.LocationModel;
import com.gorontalo.chair.lbsuser.service.TrackingService;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private boolean doubleBackToExitPressedOnce = false;

    private SessionAdapter sessionAdapter;
    private ProgressDialog pDialog;

    String tag_json_obj = "json_obj_req";
    int success;

    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    private ChildEventListener mChildEventListener;
    private DatabaseReference mProfileRef;

    private Button btnUpdate;
    private TextView txtStatus;
    private String STATUS_TARGET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        txtStatus = (TextView) findViewById(R.id.txtStatusTarget);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        sessionAdapter.checkLoginMain();

        STATUS_TARGET = sessionAdapter.getStatusTarget();
        if (STATUS_TARGET.equals("No")){
            txtStatus.setText("Target tidak terpantau !");
            txtStatus.setBackgroundColor(getResources().getColor(R.color.colorRed));
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionAdapter.editStatusTarget("Yes");
                    txtStatus.setText("Target terpantau !");
                    txtStatus.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                }
            });
        }else{
            txtStatus.setText("Target terpantau !");
            txtStatus.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionAdapter.editStatusTarget("No");
                    txtStatus.setText("Target tidak terpantau !");
                    txtStatus.setBackgroundColor(getResources().getColor(R.color.colorRed));
                }
            });
        }

        startService(new Intent(MainActivity.this, TrackingService.class));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_logout:
                updateStatus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik lagi untuk keluar !", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void updateStatus() {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Tunggu ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().updateStatusLogin(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        sessionAdapter.logoutUser();
                        stopService(new Intent(MainActivity.this, TrackingService.class));
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Data Errorrrr: " + error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", sessionAdapter.getID());
                params.put("logout", "1");

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (!sessionAdapter.getID().equals("")) {
                gMap = googleMap;
                gMap.setMaxZoomPreference(16);
                gMap.setOnMarkerClickListener(this);
                LatLng  wollongong = new LatLng(0.57395177, 123.07756159);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(wollongong, 15));
                gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                loginToFirebase();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gMap.setMyLocationEnabled(true);
                loginToFirebase();
            }
        }catch (NullPointerException e){

        }
    }

    private void addMarkersToMap(final GoogleMap map){
        mProfileRef = FirebaseDatabase.getInstance().getReference("location_users/"+sessionAdapter.getIdGrup());
        mChildEventListener = mProfileRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LocationModel marker = dataSnapshot.getValue(LocationModel.class);
                String latitude = marker.getLatitude();
                String longitude = marker.getLongitude();
                LatLng location = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

                map.addMarker(new MarkerOptions().position(location).title(marker.getName()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    try {
                        addMarkersToMap(gMap);
                    }catch (IllegalStateException | NullPointerException e){
                        Log.d("Main Activity", "Error Fragment");
                    }
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    @Override
    public void onStop(){
        if(mChildEventListener != null) {
            mProfileRef.removeEventListener(mChildEventListener);
        }
        super.onStop();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }
}
