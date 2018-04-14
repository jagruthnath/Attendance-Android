package com.example.machinex.attendance;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Course extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private TextView tv;
    Double cgpa=0.0,total=33.0;
    String[] courseList;
    String cgpaVal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course);
        lv = (ListView) findViewById(R.id.list2);
        tv = (TextView) findViewById(R.id.cgpa);
        new getCourses().execute();

    }
    private class getCourses extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Course.this,"Json Data is downloading",Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            //String url = "http://msitis-iiith.appspot.com/api/profile/ag5ifm1zaXRpcy1paWl0aHIUCxIHU3R1ZGVudBiAgIDAyKqECQw";
            String url = "http://msitis-iiith.appspot.com/api/course/ag5ifm1zaXRpcy1paWl0aHIUCxIHU3R1ZGVudBiAgICA-O2dCgw";
            String jsonStr = sh.makeServiceCall(url);
//            http://msitis-iiith.appspot.com/api/profile/ag5ifm1zaXRpcy1paWl0aHIUCxIHU3R1ZGVudBiAgICAgIDACAw


            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray data = jsonObj.getJSONArray("data");
                    courseList = new String[data.length()+1];
                    courseList[0]="Course Id"+"\t"+"Course Name"+"\t\t\t\t"+"Grade"+"\t"+"Mentor Name";
                    HashMap<String, Double> gradePoints = new HashMap<>();
                    gradePoints.put("Ex",10.0);
                    gradePoints.put("A+",9.5);
                    gradePoints.put("A",9.0);
                    gradePoints.put("B+",8.5);
                    gradePoints.put("B",8.0);
                    gradePoints.put("C",7.0);
                    gradePoints.put("N/A",0.0);
                    gradePoints.put("Incomplete",0.0);
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String course_id = c.getString("course_id");
                        String course_name = c.getString("course_name");
                        String grade = c.getString("grade");
                        String mentor_name = c.getString("mentor_name");
                        String output = course_id+"\t"+course_name+"\t"+grade+"\t"+mentor_name;
                        courseList[i+1]=output;
                        switch(course_id)
                        {
                            case "IT171531":
                                cgpa+=(3.0*gradePoints.get(grade));
                                break;
                            case "IT171441":
                                cgpa+=(4.0*gradePoints.get(grade));
                                break;
                            case "IT171323":
                                cgpa+=(2.0*gradePoints.get(grade));
                                break;
                            case "IT171322":
                                cgpa+=(2.0*gradePoints.get(grade));
                                break;
                            case "IT171321":
                                cgpa+=(2.0*gradePoints.get(grade));
                                break;
                            case "IT171242":
                                cgpa+=(4.0*gradePoints.get(grade));
                                break;
                            case "IT171241":
                                cgpa+=(4.0*gradePoints.get(grade));
                                break;
                            case "IT171144":
                                cgpa+=(4.0*gradePoints.get(grade));
                                break;
                            case "IT171143":
                                cgpa+=(4.0*gradePoints.get(grade));
                                break;
                            case "IT171122":
                                cgpa+=(2.0*gradePoints.get(grade));
                                break;
                            case "IT171111":
                                cgpa+=(gradePoints.get(grade));
                                break;
                        }
                    }
                    cgpa/=total;
                    cgpaVal = tv.getText()+String.valueOf(cgpa);

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            ListAdapter adapter1 = new SimpleAdapter(Course.this, courseList,
//                    R.layout.activity_listview, new String[]{ "course_id","course_name","mobile","gender","f_name","roll_number"},
//                    new int[]{R.id.student_fullname,R.id.student_email, R.id.student_mobile1,R.id.gender,R.id.father_name,R.id.roll_number});
//            lv.setAdapter(adapter1);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(Course.this, R.layout.activity_listview2,R.id.course,courseList);
            lv.setAdapter(adapter1);
            tv.setText(cgpaVal);
        }

    }
}
