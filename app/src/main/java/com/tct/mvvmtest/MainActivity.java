package com.tct.mvvmtest;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ScrollView scrollView = null;
    private Button sendButton = null;
    private LinearLayout linearLayout = null;
    private Handler mHandler = null;
    private EditText mSendText = null;
    private SensorManager mSensorManager = null;
    private static boolean mIsOpened = false;
    static private  int mCount = 0;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());
        mSendText = (EditText) findViewById(R.id.input_text);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        linearLayout = (LinearLayout)findViewById(R.id.messagelist);
        sendButton = (Button) findViewById(R.id.input_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCount = mCount + 1;
                //addTextView(mSendText.getText().toString() + mCount);
                if (mIsOpened)
                {
                    mIsOpened = false;
                    closeSensor();
                    sendButton.setText("Open Sensor");
                }
                else {
                    mIsOpened = true;
                    openSensor();
                    sendButton.setText("Close Sensor");
                }
            }
        });
        mHandler = new Handler();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //openSensor();
    }

    public void openSensor()
    {
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            if (sensor.getName().contains("Coarse Motion")) {
                addTextView(sensor.getName() + " Opened ");
                mSensorManager.registerListener(this,
                        sensor,//传感器TYPE类型
                        SensorManager.SENSOR_DELAY_UI);//采集频率
            }
        }
    }

    public void closeSensor()
    {
        addTextView("Coarse Motion Classifier Closed");
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        closeSensor();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    /**
     * 传感器数据变化时回调
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        int value = (int)event.values[0];
        String[] strArray = {"UNKNOWN","STATIONARY","MOVE","FIDDLE", "PEDESTRIAN","VEHICLE","WALK","RUN","BIKE","HIKE",
        "INACTIVE","WALK_STEP_RATE","JOG_STEP_RATE","RUN_STEP_RATE"};
        if(value <= 13) {
            addTextView("value = " + value + " " + strArray[value]);
        }
    }
    private void addTextView(String text)
    {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });

        linearLayout.addView(textView);
        //scrollView.fullScroll(View.FOCUS_DOWN);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
