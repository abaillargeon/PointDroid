package com.al.gpsFinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GpsNavActivity extends Activity {
	private boolean mGpsFix = false;
	//Init managers
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	
	private SensorManager mSensorManager;
	private Sensor mCompassSensor;
	
	private GpsHandler mGPS = new GpsHandler();
	//Init current direction/distance to 0
	private Double mDirection = 0.0;
	private Double mDistance = 0.0;
	private CompassView mCompass;
	private float[] mCompassValues;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener(){
        	public void onLocationChanged(Location location){
        		if(!mGpsFix){
        			mGpsFix = true;
        			mGPS.setInitialPoint(location);
        			TextView fixStatus = (TextView) findViewById(R.id.fixStatus);
        			fixStatus.setText("Found a fix");
        		}
        		
        		mGPS.addPoint(location);
        		mDistance = mGPS.returnAnchorDistance();
        		mDirection = mGPS.returnAnchorDirection();
        		TextView currentCoords = (TextView) findViewById(R.id.currentCoord);
        		currentCoords.setText(mGPS.returnPointInfo());        		
        		TextView currentStat = (TextView) findViewById(R.id.currentStats);
        		currentStat.setText("Distance: "+ mDistance +" Dir:"+mDirection);
        		
        		
        	}
        	
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mCompassSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        
        
        
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        LinearLayout lLayout = (LinearLayout) findViewById(R.id.mainCont);
        mCompass = new CompassView(this);
        lLayout.addView(mCompass);
    }
    
    //Defines available menu buttons
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, R.string.set_location);
        menu.add(0, 1, 1, R.string.remove_location);
        return true;
    }
    
    //Handles user selecting menu option
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case 0://Set Anchor
                mGpsFix = false;
                return true;
            case 1://Remove Anchor
            	mGpsFix = false;
            	return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
    
    //Override app behavior when backgrounded
    protected void onResume(){
    	super.onResume();
    	mSensorManager.registerListener(mListener, mCompassSensor,SensorManager.SENSOR_DELAY_GAME);
    	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }
    
    //
    protected void onPause(){
    	super.onPause();
    	mSensorManager.unregisterListener(mListener);
    	mLocationManager.removeUpdates(mLocationListener);
    }
    
    protected void onStop(){
    	super.onStop();
    	mSensorManager.unregisterListener(mListener);
    	mLocationManager.removeUpdates(mLocationListener);
    }
    
    //Handles compass changes
    private final SensorEventListener mListener = new SensorEventListener(){
    	public void onSensorChanged(SensorEvent event){
    		mCompassValues = event.values;
    		if (mCompass != null){
    			mCompass.invalidate();
    		}
    	}
    	
    	public void onAccuracyChanged(Sensor sensor, int accuracy){
    		
    	}
    };
    
    //Custom view to display compass
    private class CompassView extends View{
    	//Draws the compass on the screen
    	private Paint mPaint = new Paint();
    	private Path mPath = new Path();
    	
    	public CompassView(Context context){
    		super(context);
    		
            mPath.moveTo(0, -50);
            mPath.lineTo(-20, 60);
            mPath.lineTo(0, 50);
            mPath.lineTo(20, 60);
            mPath.close();
    	}
    	
    	protected void onDraw(Canvas canvas){
    		canvas.drawColor(Color.WHITE);
    		
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;
            
            canvas.translate(cx, cy);
            if (mCompassValues != null) {
                canvas.rotate(-(mCompassValues[0]-mDirection.floatValue()));//Keeps it pointing in the right direction
            }

            canvas.drawPath(mPath, mPaint);
    	}
    	
    	protected void onAttachedToWindow(){
    		super.onAttachedToWindow();
    	}
    	
    	protected void onDetachedFromWindow(){
    		super.onDetachedFromWindow();
    	}
    }
}