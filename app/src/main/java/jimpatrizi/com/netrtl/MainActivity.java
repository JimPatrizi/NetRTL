package jimpatrizi.com.netrtl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bensherman.rtlsdrdjava.tcpcli.TcpClient;

import java.io.IOException;

import static jimpatrizi.com.netrtl.Parameters.FREQUENCY;
import static jimpatrizi.com.netrtl.Parameters.MODULATION_MODE;
import static jimpatrizi.com.netrtl.Parameters.RESAMPLE_RATE;
import static jimpatrizi.com.netrtl.Parameters.SAMPLE_RATE;
import static jimpatrizi.com.netrtl.Parameters.SQUELCH_LEVEL;
import static jimpatrizi.com.netrtl.Parameters.TUNER_GAIN;
import static jimpatrizi.com.netrtl.Parameters.VOLUME;

/**
 * MainActivity of the NetRTL Android Application.
 * @author Jim Patrizi
 * @version 1.0
 * @since 2017-10-02
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Default Parameter Inits
     */

    private static String modulationMode = "wbfm";
    private static String sampleRate = "2400000";
    private static String resampleRate = "48000";
    private static String volume = "100";
    private static String squelch = "0";
    private static String gain = "-100";
    private static String frequency = "0";

    /**
     * Private Variables
     */


    private static String overSampling;



    /**
     * Public Variables
     */
    public AsyncConnection connection; //needs replaced for Ben's networking code.
    public SeekBar volumeSeekBar;
    public SeekBar squelchSeekBar;
    public SeekBar gainSeekBar;
    public Spinner modulationModeSpinner;
    public EditText hzInput;
    private static TcpClient tcpClient;
    private static Thread tcpClientThread;


    /**
     * For Logcat debugging
     */
    private String TAG = getClass().getName();

    /**
     * Text Objects
     */

    public TextView volumeTextView;
    public TextView gainTextView;
    public TextView squelchTextView;

    /**
     * Networking Connection Parameters
     */
    public int port_number;
    public String ip_address;
    public SharedPreferences sharedPrefs;

    /**
     * MainActivity Context
     */
    public Context context;

    /**
     * Public Object creation
     */
    //handles logcat messages for socket debugging, will be used to implement UI callback
    public ConnectionHandle handler = new ConnectionHandle();

    /**
     * Functions
     */

    public static TcpClient getTcpClient() {
        return tcpClient;
    }

    /**
     * OnCreate method, app starts here at launch to initialize all fields
     *
     * @param savedInstanceState - saves previous instance data for when the user comes back to the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Bring back apps last known state and set activity_main view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        //Reset Parameter Enums at startup
        //Parameters.resetValues();

        //included with project creation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //end included with project creation


        //connect socket
        threadClientInit();
        //Init Execute button
        executeButtonInit();

        hzInputInit();
        //In Modulation Mode Spinner
        spinnerInit();
        //Init Buttons
        buttonInit();
        //Volume SeekBar Init
        volumeSeekBarInit();

        gainSeekBarInit();

        squelchSeekBarInit();
        defaultParamInits();





    }

    public void defaultParamInits()
    {
        //Base Default Parameter Inits
        FREQUENCY.append(frequency);
        MODULATION_MODE.append(modulationMode);
        RESAMPLE_RATE.append(resampleRate);
        SAMPLE_RATE.append(sampleRate);
    }

    public void hzInputInit() {
        hzInput = (EditText) findViewById(R.id.hz_input);
        hzInput.addTextChangedListener(new HzInputTextWatcher(hzInput));

        //TODO use later for scannable frequencies?
        hzInput.setOnClickListener(new View.OnClickListener() {
            @Override
            //this can be used for scannable frequencies if user hits enter and saves to parameter
            public void onClick(View view) {
                String hzString = hzInput.getText().toString();
                //Toast.makeText(context, hzString + "Hz", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Connect to socket with user given IP and socket
     */
    public void threadClientInit() {
        //Obtained the previously set IP + Port Preferences

        sharedPrefs = context.getSharedPreferences("pref_main", Context.MODE_PRIVATE);
        ip_address = sharedPrefs.getString("key_ip_name", "0.0.0");
        port_number = sharedPrefs.getInt("key_port_name", 2832);

        if (tcpClientThread != null && tcpClientThread.isAlive()) {
            try {
                tcpClient.terminate();
                // Potential resource leak since the tcpClientThread won't be dead yet
                // Zombie thread?
            } catch (Exception exception) {
                Log.e(TAG, "UNABLE TO TERMINATE TCP CLIENT");
            }
        }
        try {
            tcpClient = new TcpClient(ip_address, port_number);
            tcpClientThread = new Thread(tcpClient, TcpClient.getDefaultThreadName());
            tcpClientThread.start();
        } catch (IOException exception) {
            Log.e(TAG, "UNABLE TO CREATE SOCKET TO CLIENT");
        }
        //end of open socket routine
    }

    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void gainSeekBarInit() {
        int defaultGainInt = 0;
        int maxGainInt = 58;

        //Init Volume Parameter Default
        TUNER_GAIN.resetValues();
        TUNER_GAIN.append(gain);

        gainSeekBar = (SeekBar) findViewById(R.id.gain_seek); // initiate the Seekbar
        gainTextView = (TextView) findViewById(R.id.gain_text);

        gainSeekBar.setMax(maxGainInt); // 0 maximum value for the Seek bar
        gainSeekBar.setProgress(Integer.parseInt(gain));
        gainSeekBar.setOnSeekBarChangeListener(new SeekBarChangeOnClickListener(context, gainTextView, "gain"));
    }


    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void squelchSeekBarInit() {
        int maxSquelchInt = 100;

        //Init Volume Parameter Default
        SQUELCH_LEVEL.resetValues();
        SQUELCH_LEVEL.append(squelch);

        squelchSeekBar = (SeekBar) findViewById(R.id.squelch_seek); // initiate the Seekbar
        squelchTextView = (TextView) findViewById(R.id.squelch_text);

        squelchSeekBar.setMax(maxSquelchInt); // 0 maximum value for the Seek bar
        squelchSeekBar.setProgress(Integer.parseInt(squelch));
        squelchSeekBar.setOnSeekBarChangeListener(new SeekBarChangeOnClickListener(context, squelchTextView, "squelch"));
    }

    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void volumeSeekBarInit() {
        int defaultVolumeInt = 100;

        //Init Volume Parameter Default
        //BEN, weird stuff happens if i dont reset at startup
        VOLUME.resetValues();
        VOLUME.append(volume);

        volumeSeekBar = (SeekBar) findViewById(R.id.volume_seek); // initiate the Seekbar
        volumeTextView = (TextView) findViewById(R.id.volume_text);

        volumeSeekBar.setMax(Integer.parseInt(volume)); // 100 maximum value for the Seek bar
        //volumeSeekBar.setProgress(defaultVolumeInt);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBarChangeOnClickListener(context, volumeTextView, "volume"));
    }

    /**
     * This executeButtonInit method sets the click listener on the execute button
     */
    public void executeButtonInit() {
        Button executeButton = (Button) findViewById(R.id.execute);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        executeButton.setOnClickListener(new ExecuteButtonOnClickListener(getApplicationContext(), connection, handler));
    }

    /**
     * This spinnerInit method gets the spinner id from content_main.xml,
     * populates spinner with WBFM, AM, USB, LSB
     *
     * @return Nothing
     */
    public void spinnerInit() {
        //init spinner from id
        modulationModeSpinner = (Spinner) findViewById(R.id.modeSpinner);
        //spinner strings to populate spinner object
        String[] modeSpinnerStrings = new String[]{
                "wbfm", "fm", "am", "usb", "lsb", "raw"
        };
        //set array adapter to set the strings inside spinner obect
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, modeSpinnerStrings);
        modulationModeSpinner.setAdapter(adapter);
        //formats spinner to be nice clickable size
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        modulationModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String newModulationMode = getModulationModeSpinner(modulationModeSpinner);
                MODULATION_MODE.replaceIndex(0, newModulationMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do Nothing?
            }
        });
    }

    public String getModulationModeSpinner(Spinner modulationModeSpinner) {
      return modulationModeSpinner.getSelectedItem().toString();
    }

    public void setModulationModeSpinner(Spinner modulationModeSpinner, String newMode)
    {
        int index = 0;
        for(int i = 0; i < modulationModeSpinner.getCount(); i++)
        {
            if(modulationModeSpinner.getItemAtPosition(i).toString().equals(newMode))
            {
                modulationModeSpinner.setSelection(i);
                break;
            }
            else
                Log.e(TAG, "Invalid mode");
        }
    }

    /**
     * Initializes buttons with click listeners for their respective values in the FREQUENCY Parameter,
     * Buttons include +/- 1k, 10k, 100k, 10MHz
     *
     * @return Nothing.
     */
    public void buttonInit() {

        Button increment1KHZ = (Button) findViewById(R.id.p1khz);
        increment1KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(1000, getApplicationContext(), hzInput));

        Button decrement1KHZ = (Button) findViewById(R.id.n1khz);
        decrement1KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(-1000, getApplicationContext(), hzInput));

        Button increment10KHZ = (Button) findViewById(R.id.p10khz);
        increment10KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(10000, getApplicationContext(), hzInput));

        Button decrement10KHZ = (Button) findViewById(R.id.n10khz);
        decrement10KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(-10000, getApplicationContext(), hzInput));

        Button increment100KHZ = (Button) findViewById(R.id.p100khz);
        increment100KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(100000, getApplicationContext(), hzInput));

        Button decrement100KHZ = (Button) findViewById(R.id.n100khz);
        decrement100KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(-100000, getApplicationContext(), hzInput));

        Button increment10MHZ = (Button) findViewById(R.id.p10mhz);
        increment10MHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(10000000, getApplicationContext(), hzInput));

        Button decrement10MHZ = (Button) findViewById(R.id.n10mhz);
        decrement10MHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(-10000000, getApplicationContext(), hzInput));
    }

    //included with project creation
    //included code below came with Navigation Pullout Activity
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
            // launch settings activity
            startActivity(new Intent(MainActivity.this, SettingsPrefActivity.class));
            return true;
        } else if (id == R.id.reconnect) {
            //connect/reconnect to server and reinit client
            threadClientInit();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSocketConnection(AsyncConnection connection) {
        this.connection = connection;
    }

    //TODO This is where the Advanced option needs to go, to start an intent that can set advanced settings
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static String getModulationMode() {
        return modulationMode;
    }

    public void setModulationMode(String modulationMode) {
        MainActivity.modulationMode = modulationMode;
    }

    public static String getOverSampling() {
        return overSampling;
    }

    public  void setOverSampling(String overSampling) {
        MainActivity.overSampling = overSampling;
    }

    public static String getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(String sampleRate) {
        MainActivity.sampleRate = sampleRate;
    }

    public static String getSquelch() {
        return squelch;
    }

    public  void setSquelch(String squelch) {
        MainActivity.squelch = squelch;
    }

    public static String getResampleRate() {
        return resampleRate;
    }

    public void setResampleRate(String resampleRate) {
        MainActivity.resampleRate = resampleRate;
    }

    public static String getGain() {
        return gain;
    }

    public void setGain(String gain) {
        MainActivity.gain = gain;
    }

    public static String getVolume() {
        return volume;
    }

    public  void setVolume(String volume) {
        MainActivity.volume = volume;
    }

    public static String getFrequency() {
        return frequency;
    }

    public  void setFrequency(String frequency) {
        MainActivity.frequency = frequency;
    }
}