package com.bishnu.android.taskmanager;

import com.bishal.android.taskmanager.adapters.TaskListAdapter;
import com.bishal.android.taskmanager.tasks.Task;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

	public class ViewTasksActivity extends ListActivity implements LocationListener {

	private static final long LOCATION_FILTER_DISTANCE = 2;
	//private Button addButton;
	private TaskListAdapter adapter;
	private TaskManagerApplication app;
	//private Button removeButton;
	private TextView locationText;
	//private TextView timeText;
	private ToggleButton localTasksToggle;
	private LocationManager locationManager;
	private Location latestLocation;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedules);
        
        setUpViews();
        app = (TaskManagerApplication)getApplication();
        //if(getIntent().getExtras().getString("email"));
        
        adapter = new TaskListAdapter(this, app.getCurrentTasks());
        setListAdapter(adapter);
        setUpLocation();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.forceReload();
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		adapter.toggleTaskCompleteAtPosition(position);
		Task t = adapter.getItem(position);
		app.saveTask(t);
	}
	
	public void onLocationChanged(Location location) {
		latestLocation = location;
		String locationString = String.format(
				"@ %f, %f +/- %fm",
				location.getLatitude(),
				location.getLongitude(),
				location.getAccuracy());
		locationText.setText(locationString);
	}

	public void onProviderDisabled(String provider) { }

	public void onProviderEnabled(String provider) { }

	public void onStatusChanged(String provider, int status, Bundle extras) { }

	protected void removeCompletedTasks() {
		Long[] ids = adapter.removeCompletedTasks();
		app.deleteTasks(ids);
	}
	
	protected void showLocalTasks(boolean checked) {
		if (checked) {
			adapter.filterTasksByLocation(latestLocation, LOCATION_FILTER_DISTANCE);
		} else {
			adapter.removeLocationFilter();
		}
	}

	private void setUpViews() {
		ImageButton addButton = (ImageButton)findViewById(R.id.add_button);
		ImageButton removeButton = (ImageButton)findViewById(R.id.remove_button);
		localTasksToggle = (ToggleButton)findViewById(R.id.show_local_tasks_toggle);
		locationText = (TextView)findViewById(R.id.location_text);
		
//		locationText.setBackgroundResource(R.color.white);
//		locationText.setTextColor(getResources().getColor(R.color.black));
		// timeText = (TextView)findViewById(R.id.time_text);
		
		addButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ViewTasksActivity.this, AddTaskActivity.class);
				startActivity(intent);
			}
		});
		removeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				removeCompletedTasks();
			}
		});
		localTasksToggle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showLocalTasks(localTasksToggle.isChecked());
			}
		});

	}
	
	private void setUpLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                60,
                5,
                this);
	}

}