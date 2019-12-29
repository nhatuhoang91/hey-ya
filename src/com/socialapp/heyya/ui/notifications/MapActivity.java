package com.socialapp.heyya.ui.notifications;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.socialapp.heyya.R;
import com.socialapp.heyya.base.LoggedInActivity;
import com.socialapp.heyya.gcm.GPSHelper;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.utils.DialogUtils;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MapActivity extends LoggedInActivity implements OnMapReadyCallback, HandleLocationChanges{

	GPSHelper gpsHelper;
	String message;
	double lat;
	double lon;
	boolean isReceive;
	GoogleMap mGoogleMap;
	Marker mMarker;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		Intent intent = getIntent();
		this.message = intent.getStringExtra(QBServiceConsts.EXTRA_MESSAGE);
		this.lat = intent.getDoubleExtra(QBServiceConsts.EXTRA_LATITUDE, 0);
		this.lon = intent.getDoubleExtra(QBServiceConsts.EXTRA_LONGTITUDE, 0);
		this.isReceive = intent.getBooleanExtra(QBServiceConsts.EXTRA_IS_RECEIVE, false);
		MapFragment mapFragment = (MapFragment) getFragmentManager()
			    .findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		Log.d("MAP_ACTIVITY", "OnCreate");
	}
	
	@Override
	 protected void onStart() {
		 super.onStart();
		 Log.d("MAP_ACTIVITY", "onStart");
	 }
	@Override
	 protected void onResume() {
	     super.onResume();
	     if(this.isReceive)
				this.gpsHelper = new GPSHelper(this, true, this);
	     Log.d("MAP_ACTIVITY", "onResume");
	 }
	@Override
	 protected void onPause() {
		 super.onPause();
		 if(this.isReceive)
				this.gpsHelper.stopLocationUpdates();
		 Log.d("MAP_ACTIVITY", "onPaused");
	 }
	@Override
	 protected void onStop() {
		 super.onStop();
		 if(gpsHelper!=null)
			 gpsHelper.disconnectToGoogleServer();
		 Log.d("MAP_ACTIVITY", "onStop");
	 }
	@Override
	 protected void onDestroy() {
		 super.onDestroy();
		 Log.d("MAP_ACTIVITY", "onDestroy");
	 }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			navigateToParent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		// TODO Auto-generated method stub
		mGoogleMap = googleMap;
		Marker marker = googleMap.addMarker(new MarkerOptions()
		.position(new LatLng(lat,lon))
		.title(this.message)
		);
		marker.showInfoWindow();
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon),
				14f));
		//googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
		Log.d("MAP_ACTIVITY", "OnMapReady");
	}

	@Override
	public void handleLocationChanges(Location location) {
		// TODO Auto-generated method stub
		if(mMarker!=null){
			mMarker.remove();
		}
		if(mGoogleMap!=null){
			mMarker = mGoogleMap.addMarker(new MarkerOptions()
			.position(new LatLng(location.getLatitude(),location.getLongitude()))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));
			
		}
	}

	@Override
	public void handleLoginSuccessAction() {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleLoginFailAction() {
		// TODO Auto-generated method stub
	}

}
