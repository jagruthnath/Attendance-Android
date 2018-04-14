package com.example.machinex.attendance;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    DatabaseHelper dbhelper=new DatabaseHelper(MainActivity.this);
    ListView lv;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;
    double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView) findViewById(R.id.listAttendance);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver1.class); // AlarmReceiver1 = broadcast receiver
        PendingIntent [] pendingIntent = new PendingIntent[6];
        for (int i=0;i<6;i++){
            pendingIntent[i] = PendingIntent.getBroadcast(  this, i, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
            alarmManager.cancel(pendingIntent[i]);
        }


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setAlarmTime(10,55,0).getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent[0]);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setAlarmTime(10,57,0).getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent[1]);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setAlarmTime(11,0,0).getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent[2]);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setAlarmTime(11,5,0).getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent[3]);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setAlarmTime(13,55,0).getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent[4]);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setAlarmTime(14,5,0).getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent[5]);

        int alarm = Log.d("Alarm", "Alarms set for everyday 8 am.");

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



 }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.item1) {
            // add your action here that you want
            startActivity(new Intent(MainActivity.this,Details.class));
            return true;
        }
        if (item.getItemId() == R.id.item2) {
            // add your action here that you want
            startActivity(new Intent(MainActivity.this,Course.class));
            return true;
        }
        //DoSomething();
        return super.onOptionsItemSelected(item);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getCheckIn(View x)
    {
        Button bt=(Button) findViewById(R.id.checkIn);
        int id=0;
        float status=0;
        Date time=null,s1_start=null,s1_end=null,s2_start=null,s2_end=null,s3_start=null,s3_end=null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        try {
            time=sdf.parse(sdf.format(new Date()));
            s1_start = sdf.parse("08:50:00");
            s1_end = sdf.parse("09:10:00");
            s2_start= sdf.parse("10:50:00");
            s2_end = sdf.parse("11:10:00");
            s3_start= sdf.parse("13:50:00");
            s3_end = sdf.parse("14:10:00");
        }
        catch(ParseException e){
            e.printStackTrace();
        }
        assert time != null;
        if(time.after(s1_start) && time.before(s1_end)){
            id=1;
        }
        else if(time.after(s2_start) && time.before(s2_end)){
            id=2;
        }
        else if(time.after(s3_start) && time.before(s3_end)){
            id=3;
        }
        else if(time.after(s1_end) && time.before(s2_start))
        {
            bt.setEnabled(false);
            id=1;
            status=(float)0.25;
        }
        else if(time.after(s2_end) && time.before(s3_start))
        {
            bt.setEnabled(false);
            id=2;
            status=(float)0.25;
        }
        else if(time.after(s3_end) || time.before(s1_start))
        {
            bt.setEnabled(false);
            id=3;
            status=(float)0.50;

        }
        db = dbhelper.getWritableDatabase();
        // create class object
        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude= gps.getLatitude();
            longitude = gps.getLongitude();

            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                    + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
        addNewEntry(id,status,latitude,longitude);
    }

    public void viewAttendance(View x)
    {
        Cursor cr=dbhelper.getAllData();
        ArrayList<String> arrayList = new ArrayList<String>();
        String[] str=new String[10];
        int i=0;
        cr.moveToFirst();
        arrayList.add("Id\tPenalty\tTimeStamp\t\t\t\t\tLatitude\tLongitude");
        while(!cr.isLast()){

            String str1=cr.getString(0)+"\t\t"+cr.getFloat(2)+"\t\t\t\t"+cr.getString(3)+"\t"+cr.getString(4)+"\t\t\t\t\t"+cr.getString(5);
            Log.d("HH",str1);
            arrayList.add(str1);
            cr.moveToNext();
            i++;
        }
        Log.d("SIZE",arrayList.size()+"");
        String[] item = new String[arrayList.size()];
        item=arrayList.toArray(item);

        ArrayAdapter adapter1 = new ArrayAdapter<String>(MainActivity.this,
                R.layout.activity_listview2, R.id.course,item);
        lv.setAdapter(adapter1);
    }
    private void addNewEntry(int id, float st,double latitude,double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_SID, id);
        cv.put(DatabaseHelper.COLUMN_STATUS, st);
        cv.put(DatabaseHelper.COLUMN_LATITUDE, latitude);
        cv.put(DatabaseHelper.COLUMN_LONGITUDE, longitude);
        db.insert(DatabaseHelper.TABLE_NAME, null, cv);
        Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
    }
    public Cursor getData(){
        db=dbhelper.getReadableDatabase();
        String[] col={DatabaseHelper.COLUMN_SID,DatabaseHelper.COLUMN_STATUS,DatabaseHelper.COLUMN_TIMESTAMP};
        return db.query(DatabaseHelper.TABLE_NAME,col,null,null,null,null,null);
    }
    public Calendar setAlarmTime(int hour,int minute,int second)
    {
        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmStartTime.set(Calendar.MINUTE, minute);
        alarmStartTime.set(Calendar.SECOND, second);
        if (now.after(alarmStartTime))
            alarmStartTime.add(Calendar.DATE, 1);
        return alarmStartTime;
    }
}