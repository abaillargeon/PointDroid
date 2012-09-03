package com.al.gpsFinder;

import java.util.LinkedList;

import android.location.Location;
import android.util.Log;

public class GpsHandler {
	private LinkedList<Location> mPoints = new LinkedList<Location>();//Point list
	private Location mAnchor;
	
	public GpsHandler(){
		//Constructor
		
		
	}
	
	public void setInitialPoint(Location location){
		mAnchor = location;
		Log.d("GPS","Set initial: "+location.getLatitude() +" "+ location.getLongitude());
	}
	
	public void setInitialPoint(double lat, double lon){
		//Make new location with coords
	}
	
	public void addPoint(Location location){
		mPoints.addFirst(location);
		if(mPoints.size() > 10){
			mPoints.removeLast();
		}
	}
	
	public String returnPointInfo(){
		return "Point: "+mPoints.get(0).getLatitude()+" "+mPoints.get(0).getLongitude();
	}
	
	public double returnAnchorDistance(){
		//Use the first point, 
		double lat1 = Math.toRadians(mAnchor.getLatitude());
		double lon1 = Math.toRadians(mAnchor.getLongitude());
		double lat2 = Math.toRadians(mPoints.get(0).getLatitude());
		double lon2 = Math.toRadians(mPoints.get(0).getLongitude());
		
		double dist = Math.toDegrees(Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon1-lon2)));
		return dist*60*1852;//Meters
	}
	
	public double returnAnchorDirection(){
		//Use the first point, assume non-great circle math
		double lat1 = Math.toRadians(mAnchor.getLatitude());
		double lon1 = Math.toRadians(mAnchor.getLongitude());
		double lat2 = Math.toRadians(mPoints.get(0).getLatitude());
		double lon2 = Math.toRadians(mPoints.get(0).getLongitude());
		
		Double dir = Math.toDegrees(Math.atan2(lat2-lat1,lon2-lon1));
		if (dir < 0){
			dir = dir+360;
		}
		
		return dir;
	}
}
