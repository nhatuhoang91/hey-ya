package com.socialapp.heyya.gcm;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.socialapp.heyya.service.QBServiceConsts;
import com.socialapp.heyya.ui.notifications.HandleLocationChanges;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GPSHelper implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

	static final String TAG = GPSHelper.class.getName();
	static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
	static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
	
	//LocationManager locationManager;
	GoogleApiClient googleApiClient;
	LocationRequest locationRequest;
	String provider;
	Location location;
	Context context;
	boolean isUseLocationUpdate;
	HandleLocationChanges locationChangesInterface;
	public GPSHelper(Context context){
		this.context = context;
		googleApiClient = new GoogleApiClient.Builder(context)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
		this.isUseLocationUpdate = false;
		this.locationChangesInterface = null;
		googleApiClient.connect();
		Log.d(TAG, "connect to google service");
	}
	
	public GPSHelper(Context context, boolean isUseLocationUpdate, HandleLocationChanges handleLocationChanges){
		this.context = context;
		googleApiClient = new GoogleApiClient.Builder(context)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
		this.isUseLocationUpdate = isUseLocationUpdate;
		this.locationChangesInterface = handleLocationChanges;
		if(isUseLocationUpdate)
			this.locationRequest = createLocationRequest();
		googleApiClient.connect();
		Log.d(TAG, "connect to google service");
	}
	
	private LocationRequest createLocationRequest(){
		LocationRequest locationRequest = new LocationRequest();
		locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
		locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		return locationRequest;
	}
	public Location getCurrentLocation(){
		if(location == null){
			LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(QBServiceConsts.OPEN_GPS_SETTING));
			return null;
		}else
			return location;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		 location = LocationServices.FusedLocationApi.getLastLocation(
	                googleApiClient);
		 if(location != null)
			 Log.d("GSP_HELPER", "location not null ");
		 else{
			 Log.d("GSP_HELPER", "location null ");
			// LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(QBServiceConsts.OPEN_GPS_SETTING));
		 }
		 
		 if(isUseLocationUpdate){
			 startLocationUpdates();
		 }
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		 Log.d("GSP_HELPER", "connection suspended ");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		 Log.d("GSP_HELPER", "connection fail ");
	}
	public void disconnectToGoogleServer(){
		googleApiClient.disconnect();
		googleApiClient=null;
		Log.d("GSP_HELPER", "disconnect to google service");
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		this.locationChangesInterface.handleLocationChanges(location);
	}

	private void startLocationUpdates(){
		if(googleApiClient.isConnected()){
			LocationServices.FusedLocationApi.requestLocationUpdates(
					googleApiClient, locationRequest, this);
			Log.d("GSP_HELPER", "start location update");
		}
	}
	public void stopLocationUpdates() {
		if(googleApiClient.isConnected())
			LocationServices.FusedLocationApi.removeLocationUpdates(
					googleApiClient, this);
	}
}
