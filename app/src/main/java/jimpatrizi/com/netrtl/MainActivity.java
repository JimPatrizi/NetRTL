package jimpatrizi.com.netrtl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bensherman.rtlsdrdjava.tcpcli.TcpClient;

import java.io.IOException;

import static jimpatrizi.com.netrtl.Parameters.ENABLE_OPTION;
import static jimpatrizi.com.netrtl.Parameters.FREQUENCY;
import static jimpatrizi.com.netrtl.Parameters.MODULATION_MODE;
import static jimpatrizi.com.netrtl.Parameters.OVERSAMPLING;
import static jimpatrizi.com.netrtl.Parameters.RESAMPLE_RATE;
import static jimpatrizi.com.netrtl.Parameters.SAMPLE_RATE;
import static jimpatrizi.com.netrtl.Parameters.SQUELCH_DELAY;
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
{

    /**
     * Default Parameter Inits
     */

    private static String modulationMode = "wbfm";
    private static String sampleRate = "2400000";
    private static String resampleRate = "48000";
    private static String overSampling = "-1";
    private static String volume = "0";
    private static String squelch = "0";
    private static String gain = "-100"; //ACG enables as per rtl_fm?
    private static String frequency = "0";

    private static String sqelchDelay = "10";
    private static String ppmError = "0";
    private static String deviceIndex = "0";
    private static String atanMath = "std"; //TODO ADD SCANNABLE FREQUENCY

    private static final int maxSquelchInt = 100; //TODO is this the max squelch?
    private static final int maxGainInt = 50;
    private static final int maxVolumeInt = 100;


    /**
     * Private Variables
     */

    private final String DIRECT = "direct";
    private final String EDGE = "edge";
    private final String DC = "dc";
    private final String DEEMP = "deemp";
    private final String OFFSET = "offset";
    static final String HZFIELD = "hz";
    static final String SAMPLE = "sample";
    static final String RESAMPLE = "resample";




    /**
     * Public Variables
     */
    SeekBar volumeSeekBar;
    SeekBar squelchSeekBar;
    SeekBar gainSeekBar;
    Spinner modulationModeSpinner;
    Spinner oversampleModeSpinner;
    EditText hzInput;
    EditText samplingRate;
    EditText resamplingRate;
    private static TcpClient tcpClient;
    private static Thread tcpClientThread;
    private ResponseListener responseListener;
    private Thread responseListenerThread;
    Switch directSwitch;
    Switch edgeSwitch;
    Switch dcSwitch;
    Switch deempSwitch;
    Switch offsetSwitch;
    private final EnableOptionUiMatcher enableOptionUiMatcher = new EnableOptionUiMatcher();


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
    @RequiresApi(api = Build.VERSION_CODES.M)
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
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
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

        switchInits();

        stopButtonInit();

//        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview);
//        scrollView.setOnScrollChangeListener(new ScrollChangeListener(getApplicationContext()));
    }

    public void switchInits()
    {
        ENABLE_OPTION.setUiMembers(enableOptionUiMatcher, enableOptionUiMatcher.getClass());

        //Enable Option switches
        directSwitch = (Switch) findViewById(R.id.direct);
        directSwitch.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener(DIRECT));
        enableOptionUiMatcher.add(DIRECT, directSwitch);

        edgeSwitch = (Switch) findViewById(R.id.edge);
        edgeSwitch.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener(EDGE));
        enableOptionUiMatcher.add(EDGE, edgeSwitch);

        dcSwitch = (Switch) findViewById(R.id.dc);
        dcSwitch.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener(DC));
        enableOptionUiMatcher.add(DC, dcSwitch);

        deempSwitch = (Switch) findViewById(R.id.deemp);
        deempSwitch.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener(DEEMP));
        enableOptionUiMatcher.add(DEEMP, deempSwitch);

        offsetSwitch = (Switch) findViewById(R.id.offset);
        offsetSwitch.setOnCheckedChangeListener(new SwitchOnCheckedChangeListener(OFFSET));
        enableOptionUiMatcher.add(OFFSET, offsetSwitch);
    }

    public void defaultParamInits()
    {
        //Base Default Parameter Inits
        FREQUENCY.append(frequency);
        MODULATION_MODE.append(modulationMode);
        RESAMPLE_RATE.append(resampleRate);
        SAMPLE_RATE.append(sampleRate);
        OVERSAMPLING.append(overSampling);
        SQUELCH_DELAY.append(sqelchDelay);

//        private static String ppmError = "0";
//        private static String deviceIndex = "0";
//        private static String atanMath = "std"; //TODO ADD SCANNABLE FREQUENCY


    }

    public void hzInputInit() {
        hzInput = (EditText) findViewById(R.id.hz_input);
        samplingRate = (EditText) findViewById(R.id.sample_rate);
        resamplingRate = (EditText) findViewById(R.id.resample_rate);


        // Associate this with the Frequency parameter
        Parameters.FREQUENCY.setUiMembers(hzInput, hzInput.getClass());
        Parameters.SAMPLE_RATE.setUiMembers(samplingRate, samplingRate.getClass());
        Parameters.RESAMPLE_RATE.setUiMembers(resamplingRate, resamplingRate.getClass());

        hzInput.addTextChangedListener(new InputTextWatcher(hzInput, HZFIELD));
        samplingRate.addTextChangedListener(new InputTextWatcher(samplingRate, SAMPLE));
        resamplingRate.addTextChangedListener(new InputTextWatcher(resamplingRate, RESAMPLE));

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
            responseListener = new ResponseListener(tcpClient, this);
            responseListenerThread = new Thread(responseListener);
            responseListenerThread.start();
            //TODO Kill old thread
        } catch (IOException exception) {
            Log.e(TAG, "UNABLE TO CREATE SOCKET TO CLIENT");
            Toast.makeText(context, "Unable to connect to: " + ip_address, Toast.LENGTH_SHORT).show();
        }
        //end of open socket routine
    }

    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void gainSeekBarInit() {
        //Init Volume Parameter Default
        TUNER_GAIN.append(gain);

        gainSeekBar = (SeekBar) findViewById(R.id.gain_seek); // initiate the Seekbar
        gainTextView = (TextView) findViewById(R.id.gain_text);
        Parameters.TUNER_GAIN.setUiMembers(gainSeekBar, gainSeekBar.getClass());

        gainSeekBar.setMax(maxGainInt); // 0 maximum value for the Seek bar
        gainSeekBar.setProgress(Integer.parseInt(gain));
        gainSeekBar.setOnSeekBarChangeListener(new SeekBarChangeOnClickListener(context, gainTextView, "gain"));
    }


    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void squelchSeekBarInit() {
        //Init Volume Parameter Default
        SQUELCH_LEVEL.append(squelch);

        squelchSeekBar = (SeekBar) findViewById(R.id.squelch_seek); // initiate the Seekbar
        squelchTextView = (TextView) findViewById(R.id.squelch_text);
        Parameters.SQUELCH_LEVEL.setUiMembers(squelchSeekBar, squelchSeekBar.getClass());

        squelchSeekBar.setMax(maxSquelchInt); // 0 maximum value for the Seek bar
        squelchSeekBar.setProgress(Integer.parseInt(squelch));
        squelchSeekBar.setOnSeekBarChangeListener(new SeekBarChangeOnClickListener(context, squelchTextView, "squelch"));
    }

    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void volumeSeekBarInit() {
        //Init Volume Parameter Default
        //BEN, weird stuff happens if i dont reset at startup
        VOLUME.append(volume);

        volumeSeekBar = (SeekBar) findViewById(R.id.volume_seek); // initiate the Seekbar
        volumeTextView = (TextView) findViewById(R.id.volume_text);
        Parameters.VOLUME.setUiMembers(volumeSeekBar, volumeSeekBar.getClass());

        volumeSeekBar.setMax(maxVolumeInt); // 100 maximum value for the Seek bar
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
        executeButton.setOnClickListener(new ExecuteButtonOnClickListener(getApplicationContext()));
    }

    public void stopButtonInit()
    {
        Button stopButton = (Button) findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tcpClient.sendToServer("STOP");
            }
        });
    }

    /**
     * This spinnerInit method gets the spinner id from content_main.xml,
     * populates spinner with WBFM, AM, USB, LSB
     *
     */
    public void spinnerInit() {
        //init spinner from id
        modulationModeSpinner = (Spinner) findViewById(R.id.modeSpinner);
        oversampleModeSpinner = (Spinner) findViewById(R.id.oversamp_spinner);
        Parameters.MODULATION_MODE.setUiMembers(modulationModeSpinner, modulationModeSpinner.getClass());
        Parameters.OVERSAMPLING.setUiMembers(oversampleModeSpinner, oversampleModeSpinner.getClass());
        //spinner strings to populate spinner object
        String[] modeSpinnerStrings = new String[]{
                "wbfm", "fm", "am", "usb", "lsb", "raw"
        };
        String[] overSampling = new String[]{
                "-1", "1", "2", "3", "4"
        };
        //set array adapter to set the strings inside spinner obect
        ArrayAdapter<String> adapterMod = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, modeSpinnerStrings);
        ArrayAdapter<String> adapterSample = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, overSampling);
        modulationModeSpinner.setAdapter(adapterMod);
        oversampleModeSpinner.setAdapter(adapterSample);
        oversampleModeSpinner.setSelection(0);
        //formats spinner to be nice clickable size
        adapterMod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSample.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

        oversampleModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newsamplingMode = getModulationModeSpinner(oversampleModeSpinner);
                OVERSAMPLING.replaceIndex(0, newsamplingMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
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
//    //TODO This is where the Advanced option needs to go, to start an intent that can set advanced settings
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
////        int id = item.getItemId();
////
////        if (id == R.id.nav_camera) {
////            // Handle the camera action
////        } else if (id == R.id.nav_gallery) {
////
////        } else if (id == R.id.nav_slideshow) {
////
////        } else if (id == R.id.nav_manage) {
////
////        } else if (id == R.id.nav_share) {
////
////        } else if (id == R.id.nav_send) {
////
////        }
////
////        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
////        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    public static String getModulationMode() {
        return modulationMode;
    }

    public void setModulationMode(String modulationMode) {
        MainActivity.modulationMode = modulationMode;
    }

    public static boolean isConnected()
    {
        return getTcpClient() != null && tcpClientThread != null && tcpClientThread.isAlive();
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