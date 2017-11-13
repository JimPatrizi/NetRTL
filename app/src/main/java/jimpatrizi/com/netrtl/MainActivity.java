package jimpatrizi.com.netrtl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import static jimpatrizi.com.netrtl.Parameters.ATAN_MATH;
import static jimpatrizi.com.netrtl.Parameters.ENABLE_OPTION;
import static jimpatrizi.com.netrtl.Parameters.FIR_SIZE;
import static jimpatrizi.com.netrtl.Parameters.FREQUENCY;
import static jimpatrizi.com.netrtl.Parameters.MODULATION_MODE;
import static jimpatrizi.com.netrtl.Parameters.OVERSAMPLING;
import static jimpatrizi.com.netrtl.Parameters.PPM_ERROR;
import static jimpatrizi.com.netrtl.Parameters.RESAMPLE_RATE;
import static jimpatrizi.com.netrtl.Parameters.SAMPLE_RATE;
import static jimpatrizi.com.netrtl.Parameters.SQUELCH_DELAY;
import static jimpatrizi.com.netrtl.Parameters.SQUELCH_LEVEL;
import static jimpatrizi.com.netrtl.Parameters.TUNER_GAIN;
import static jimpatrizi.com.netrtl.Parameters.VOLUME;

/**
 * MainActivity of the NetRTL Android Application.
 * Communicates with rtlsdrd daemon (https://github.com/bennettmsherman/rtlsdrd)
 * to build parameters that are sent to rtl sdr via rtl_fm from the rtl-sdr OS project
 * @author Jim Patrizi
 * @version 1.0
 * @since 2017-11-11-2017
 */
public class MainActivity extends AppCompatActivity
{

     /********************************************************************************
      *     Default Parameter Inits
      *     Defaults based on rtlsdrd
      *     Parameters based on what is sent to rtl_fm from rtl-sdr
     *********************************************************************************/

    /**
     *  Modulation Mode Default - fm, am, usb, lsb, raw, wbfm
     */
    private static final String modulationMode = "fm";

    /**
     *  Sample Rate Default
     *  static const uint32_t MIN_VALID = 0;
     *  static const uint32_t MAX_VALID = 3.2 MS/s
     *  static const uint32_t DEFAULT_VALUE = 24 kS/s
     */
    private static final String sampleRate = "default";

    /**
     * Resample Rate Default
     * static const uint32_t MIN_VALID = 0;
     * static const uint32_t MAX_VALID = 3.2 MS/s
     * static const uint32_t DEFAULT_VALUE = 24 kS/s
     */
    private static final String resampleRate = "default";

    /**
     * Oversampling Default
     * 1  Default 2, 3 , 4
     */
    private static final String overSampling = "default";

    /**
     * System Volume Default - 0 - 100%
     */
    private static final String volume = "0";

    /**
     * Adjust this parameter to adjust the max volume allowed on the volume seek bar
     */
    private static final int maxVolumeInt = 100;

    /**
     * Squelch Level
     * Min 0, Max UINT32 MAX
     */
    private static final String squelch = "0";

    /**
     * Adjust this parameter to change the max squelch allowed on the squelch seek bar
     */
    private static final int maxSquelchInt = 500; //TODO is this the max squelch?

    /**
     *    Tuner Gain
     *
     *    static const int32_t MIN_VALID = -100;
     *    static const int32_t MAX_VALID = INT32_MAX;
     *    static const int32_t DEFAULT_VALUE = -100;
     */
    private static String gain = "-100";

    /**
     * Adjust this parameter to change the max gain allowed on gain seek bar
     */
    private static final int maxGainInt = 50;

    /**
     *  Input Frequency
     *
     *  static const uint32_t MIN_VALID = 0;
     *  static const uint32_t MAX_VALID = 2000000000, 2GHz
     *  static const uint32_t DEFAULT_VALUE = 91100000, 91.1Mhz
     */
    private static String frequency = "91100000";

    /**
     *   Squelch Delay
     *
     *   static const int32_t MIN_VALID = INT32_MIN;
     *   static const int32_t MAX_VALID = INT32_MAX;
     *   static const int32_t DEFAULT_VALUE = 10;
     */
    private static String squelchDelay = "default";

    /**
     *  PPM Error
     *
     *  static const uint32_t MIN_VALID = 0;
     *  static const uint32_t MAX_VALID = 100000;
     *  static const uint32_t DEFAULT_VALUE = 0;
     */
    private static String ppmError = "default";

    /**
     *  ATAN Math
     *
     * const std::vector<std::string> AtanMath::VALID_VALUES {"std", "fast", "lut"};
     * const std::string AtanMath::DEFAULT_VALUE = "std";
     */
    private static String atanMath = "std";

    /**
     * FIR Size
     *
     * const std::vector<std::string> FirSize::VALID_VALUES {"-1", "0", "9"};
     * const std::string FirSize::DEFAULT_VALUE = "-1";
     */
    private static String firSize = "default"; //FIR_SIZE



    /********************************************************************************
     *     Private Variables
     *********************************************************************************/

    /**
     * For SwitchOnCheckedListener, to know what parameter to toggle when switch is pressed
     */
    private final String DIRECT = "direct";
    private final String EDGE = "edge";
    private final String DC = "dc";
    private final String DEEMP = "deemp";
    private final String OFFSET = "offset";

    /**
     * For InputTextWatcher, used for all Edit Text fields for equals() to use one class for all fields
     */
    static final String HZFIELD = "hz";
    static final String SAMPLE = "sample";
    static final String RESAMPLE = "resample";
    static final String PPM = "ppm";
    static final String DELAY = "delay";

    /**
     * Handles matching ENABLE OPTION switch matching between key and UI object
     */
    private final EnableOptionUiMatcher enableOptionUiMatcher = new EnableOptionUiMatcher();


    /********************************************************************************
     *    Package Private Variables
     *********************************************************************************/

    /**
     * Volume SeekBar
     */
    SeekBar volumeSeekBar;

    /**
     * Squelch SeekBar
     */
    SeekBar squelchSeekBar;

    /**
     * Tuner Gain SeekBar
     */
    SeekBar gainSeekBar;

    /**
     * Modulation Mode Spinner
     */
    Spinner modulationModeSpinner;

    /**
     * Oversampling Mode Spinner
     */
    Spinner oversampleModeSpinner;

    /**
     * Fir Size Spinner
     */
    Spinner firSizeSpinner;

    /**
     * ATAN Math Spinner
     */
    Spinner atanMathSpinner;

    /**
     * Hz Edit TextInput
     */
    EditText hzInput;

    /**
     * Sampling Rate EditText Input
     */
    EditText samplingRate;

    /**
     * Resampling Rate EditText Input
     */
    EditText resamplingRate;

    /**
     * PPM Error EditText Input
     */
    EditText ppmErrorText;

    /**
     * Squelch Delay EditText Input
     */
    EditText squelchDelayText;

    /**
     * Direct Enable Option Switch
     */
    Switch directSwitch;

    /**
     * Edge Enable Option Switch
     */
    Switch edgeSwitch;

    /**
     * dc Enable Option Switch
     */
    Switch dcSwitch;

    /**
     * deemp Enable Option Switch
     */
    Switch deempSwitch;

    /**
     * offset Enable Option Switch
     */
    Switch offsetSwitch;


    /********************************************************************************
     *     Logcat variables
     *********************************************************************************/

    /**
     * For logcat debugging, used for logcat filter, type in class name to find this classe's msgs
     */
    private String TAG = getClass().getName();


    /********************************************************************************
     *     Text View Objects
     * TODO These Text Views should have current value of seekbar appended after user releases touch
     *********************************************************************************/

    public TextView volumeTextView;
    public TextView gainTextView;
    public TextView squelchTextView;

    /********************************************************************************
     *     Network Connection Variables
     *********************************************************************************/

    /**
     * Port Number Setting - Default 2832
     */
    public int portNumber;

    /**
     * IP Address Setting - Default if none inputted, 0.0.0
     */
    public String ipAddress;

    /**
     * Port number and ip addr are stored as SharedPreferences, public for access anywhere
     */
    public SharedPreferences sharedPrefs;


    /**
     * TcpClient
     */
    private static TcpClient tcpClient;

    /**
     * TcpClient Thread
     */
    private static Thread tcpClientThread;

    /**
     * Listens for server callbacks in new thread
     */
    private ResponseListener responseListener;

    /**
     * New Thread that responseListener is run on
     */
    private Thread responseListenerThread;



    /********************************************************************************
     *     Main Activity and public context
     *********************************************************************************/

    /**
     * Context, usually needed for Toast msgs
     */
    public Context context;

    /**
     * Static Context Reference
     */
    private static Context sContext;


    /**
     * Main Activity Variable
     */
    private static MainActivity mainActivity;



    /********************************************************************************
     *     Getters and Setters
     *********************************************************************************/

    /**
     * TcpClient Getter
     * @return - Returns TcpClient object
     */
    public static TcpClient getTcpClient() {
        return tcpClient;
    }

    /**
     * Returns Application Context
     * @return - MainActivity Application Context
     */
    public static Context getAppContext()
    {
        return mainActivity.getApplicationContext();
    }


    /**
     * Returns the current value in spinner, used in
     * @param modulationModeSpinner spinner object to get param from
     * @return - returns the current value selected in spinner
     */
    public String getModulationModeSpinner(Spinner modulationModeSpinner) {
        return modulationModeSpinner.getSelectedItem().toString();
    }

    /**
     * Sets this spinner to the selection if its contained in that spinner
     * @param modulationModeSpinner spinner object to get param from
     * @param newMode the new mode to set to
     */
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


    /********************************************************************************
     *     Functions
     *********************************************************************************/

    /**
     * Prints toast msg to application context on the UI thread
     * @param msg - msg to be toasted
     */
    public static void printToast(final String msg)
    {
        mainActivity.runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           Toast.makeText(getAppContext(), msg, Toast.LENGTH_LONG).show();
                                       }
                                   }
        );

    }

    /**
     * Tells us if the tcpClient is connected
     * @return - True if TcpClient and tcpClientThread are not null nd the client thread is still alive
     */
    public static boolean isConnected()
    {
        return getTcpClient() != null && tcpClientThread != null && tcpClientThread.isAlive();
    }



    /********************************************************************************
     *     UI Methods
     *********************************************************************************/


    /**
     * OnCreate method
     * App starts here at launch to initialize all fields
     *
     * @param savedInstanceState - saves previous instance data for when the user comes back to the app
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Bring back apps last known state and set activity_main view
        super.onCreate(savedInstanceState);
        //sets view to the activity_main.xml layout file
        setContentView(R.layout.activity_main);

        //init context and mainActivity Variables
        context = getApplicationContext();
        sContext = getApplicationContext();
        mainActivity = this;

        //Init Toolbar for menu/settings hamburger menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Init Default Parameters
        defaultParamInits();

        //connect socket
        threadClientInit();

        //Init Execute button
        executeButtonInit();

        //Init Stop button
        stopButtonInit();

        //Init EditText inputs
        editTextInputInit();

        //Init Spinners
        spinnerInit();

        //Init Buttons
        buttonInit();

        //Volume SeekBar Init
        volumeSeekBarInit();

        //Gain SeekBar Init
        gainSeekBarInit();

        //Squelch SeekBar Init
        squelchSeekBarInit();

        //Enable Option Switch Init
        switchInits();
    }

    /**
     * Displays a dialog message with the specified title and message. One button is shown,
     * with the text "Got it!", which doesn't have an associated action.
     * @param title The title of the dialog box
     * @param message The message to be shown
     */
    public static void showGotItDialog(final String title, final String message, final boolean settingsonclick)
    {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder gotItDialog = new AlertDialog.Builder(mainActivity).
                        setMessage(message).setTitle(title).setCancelable(false);

                gotItDialog.setPositiveButton("Got it!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(settingsonclick)
                                {
                                    goToSettingsActivity();
                                }
                            }
                        });

                gotItDialog.create().show();
            }
        });
    }

    /**
     * Go to the Settings Activity
     */
    public static void goToSettingsActivity() {
        Intent settings = new Intent(mainActivity, SettingsPrefActivity.class);
        sContext.startActivity(settings);
    }

    /**
     * Inflate hamburger menu
     * @param menu - menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Has menu items in hamburger do something
     * @param item - for menu item, do defined action
     * @return
     */
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
        } else if (id == R.id.clear)
        {
            tcpClient.sendToServer("CLEAR");
        } else if (id == R.id.pull)
        {
            tcpClient.sendToServer("CMDS_IN_USE");
        } else if (id == R.id.default_settings)
        {
            DefaultBuilder defaultBuilder = new DefaultBuilder();
            defaultBuilder.setFrequency(91100000);
            defaultBuilder.setModulationMode("fm");
            String toDaemon = defaultBuilder.toString();
            responseListener.parseCmdsInUseResponse(toDaemon);
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Init Default Parameters in Parameter enum class that need initial values
     * SeekBar types init in their respective init methods (squelch, gain, volume)
     */
    public void defaultParamInits()
    {
        FREQUENCY.append(frequency);
        MODULATION_MODE.append(modulationMode);
        RESAMPLE_RATE.append(resampleRate);
        SAMPLE_RATE.append(sampleRate);
        OVERSAMPLING.append(overSampling);
        SQUELCH_DELAY.append(squelchDelay);
        PPM_ERROR.append(ppmError);
        ATAN_MATH.append(atanMath);
        FIR_SIZE.append(firSize);
    }

    /**
     * Init EditText Inputs
     */
    public void editTextInputInit() {

        /**
         * Grab all of the references from context_main.xml
         */
        hzInput = (EditText) findViewById(R.id.hz_input);
        samplingRate = (EditText) findViewById(R.id.sample_rate);
        resamplingRate = (EditText) findViewById(R.id.resample_rate);
        ppmErrorText = (EditText) findViewById(R.id.ppm_error);
        squelchDelayText = (EditText) findViewById(R.id.squelch_delay);

        /**
         * Associate this UI Members with their respective Parameter enums
         */
        Parameters.FREQUENCY.setUiMembers(hzInput, hzInput.getClass());
        Parameters.SCANNABLE_FREQUENCY.setUiMembers(hzInput, hzInput.getClass());
        Parameters.SAMPLE_RATE.setUiMembers(samplingRate, samplingRate.getClass());
        Parameters.RESAMPLE_RATE.setUiMembers(resamplingRate, resamplingRate.getClass());
        Parameters.PPM_ERROR.setUiMembers(ppmErrorText, ppmErrorText.getClass());
        Parameters.SQUELCH_DELAY.setUiMembers(squelchDelayText, squelchDelayText.getClass());


        /**
         * Add TextWatcher Listeners to the EditText fields
         * Adjusts Parameters on UI change
         */
        hzInput.addTextChangedListener(new InputTextWatcher(hzInput, HZFIELD));
        samplingRate.addTextChangedListener(new InputTextWatcher(samplingRate, SAMPLE));
        resamplingRate.addTextChangedListener(new InputTextWatcher(resamplingRate, RESAMPLE));
        ppmErrorText.addTextChangedListener(new InputTextWatcher(ppmErrorText, PPM));
        squelchDelayText.addTextChangedListener(new InputTextWatcher(squelchDelayText, DELAY));
    }

    /**
     * Connect to socket with user given IP and socket
     */
    public void threadClientInit() {

        /**
         * Obtain the shared preferences object and get the respective keys for user set ip addr and port
         */
        sharedPrefs = context.getSharedPreferences("pref_main", Context.MODE_PRIVATE);

        // Returns null if no value has been set to this shared preferences key
        ipAddress = sharedPrefs.getString("key_ip_name", null);

        //inits port to 2832 if field is empty
        portNumber = sharedPrefs.getInt("key_port_name", 2832);

        // If no value has been set for the IP, don't try to connect.
        if (ipAddress == null || ipAddress.length() == 0)
        {
            showGotItDialog("Connection information", "Visit the Settings page to set a " +
                    "valid IP address or hostname corresponding to the host running rtlsdrd. " +
                    "Then, select \"Connect\" from the dropdown menu", true);
            return;
        }

        /**
         * If Thread is already running and still running, on call to threadClientInit, terminates
         * original thread first before init a new thread, or resource leaks can happen
         */
        if (tcpClientThread != null && tcpClientThread.isAlive()) {
            try {
                tcpClient.terminate();
                // Potential resource leak since the tcpClientThread won't be dead yet
                // Zombie thread?
            } catch (Exception exception) {
                Log.e(TAG, "UNABLE TO TERMINATE TCP CLIENT");
            }
        }

        /**
         * If response listener thread is running on call to threadClientInit, terminates
         * original thread, by stopping infinite while loop
         */
        if (responseListenerThread != null && responseListenerThread.isAlive()) {
            try {
                //stops listening
                responseListener.terminate();
            } catch (Exception exception) {
                Log.e(TAG, "UNABLE TO TERMINATE ResponseListener");
            }
        }

        /**
         * Init the tcpClient and thread and response listener, response listener thread
         */
        try {
            // Let the user know of the connection process
            showGotItDialog("Connecting to rtlsdrd",
                    "Host: " + ipAddress + "\nPort (TCP): " + portNumber, false);

            //Init tcp client
            tcpClient = new TcpClient(ipAddress, portNumber);
            //Init tcpClient thread
            tcpClientThread = new Thread(tcpClient, TcpClient.getDefaultThreadName());
            //Start tcpClient in new thread, starts in run() because implements runnable
            tcpClientThread.start();
            //init response listener
            responseListener = new ResponseListener(tcpClient, this);
            //init response listener's new thread
            responseListenerThread = new Thread(responseListener);
            //start in response listener's run method, implements runnable
            responseListenerThread.start();
            //This sends CMDS_IN_USE to server, so that it pulls currently configured daemon settings from server to UI/Parameters
            tcpClient.sendToServer("CMDS_IN_USE");
            //TODO Kill old thread, did i take care of it with responselistener terminate?
        } catch (IOException exception) {
            Log.e(TAG, "UNABLE TO CREATE SOCKET TO CLIENT");
            Toast.makeText(context, "Unable to connect to: " + ipAddress, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void gainSeekBarInit() {
        //Init Volume Parameter Default
        TUNER_GAIN.append(gain);

        //Init SeekBar and text view with xml reference
        gainSeekBar = (SeekBar) findViewById(R.id.gain_seek);
        gainTextView = (TextView) findViewById(R.id.gain_text);//not currently used

        //Associate TUNER GAIN with gain seekbar
        Parameters.TUNER_GAIN.setUiMembers(gainSeekBar, gainSeekBar.getClass());

        //set gainseek bar max
        gainSeekBar.setMax(maxGainInt);
        //set the default progress of the seekbar
        gainSeekBar.setProgress(Integer.parseInt(gain));
        //set SeekBarOnChangeListener
        gainSeekBar.setOnSeekBarChangeListener(new SeekBarChangeOnClickListener(context, gainTextView, "gain"));
    }


    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void squelchSeekBarInit() {
        //Init Volume Parameter Default
        SQUELCH_LEVEL.append(squelch);

        //Init Squelch Bar from xml reference
        squelchSeekBar = (SeekBar) findViewById(R.id.squelch_seek);
        squelchTextView = (TextView) findViewById(R.id.squelch_text);//not currently used

        //Associate Squelch Parameter with this UI Member
        Parameters.SQUELCH_LEVEL.setUiMembers(squelchSeekBar, squelchSeekBar.getClass());

        //Set SeekBar Max and current Value
        squelchSeekBar.setMax(maxSquelchInt);
        squelchSeekBar.setProgress(Integer.parseInt(squelch));
        //set on change listener
        squelchSeekBar.setOnSeekBarChangeListener(new SeekBarChangeOnClickListener(context, squelchTextView, "squelch"));
    }

    /**
     * Sets volume bar's seekbar methods and listener
     */
    public void volumeSeekBarInit() {
        //Init Volume Parameter Default
        VOLUME.append(volume);

        //init volumeSeekBar with xml reference
        volumeSeekBar = (SeekBar) findViewById(R.id.volume_seek);
        volumeTextView = (TextView) findViewById(R.id.volume_text);//not currently used

        //associate ui member with parameter
        Parameters.VOLUME.setUiMembers(volumeSeekBar, volumeSeekBar.getClass());

        // 100 maximum value for the Seek bar, default volume is 0
        volumeSeekBar.setMax(maxVolumeInt);
        //set on change listener
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBarChangeOnClickListener(context, volumeTextView, "volume"));
    }

    /**
     * This executeButtonInit method sets the click listener on the execute button
     */
    public void executeButtonInit() {
        //associate button with xml reference
        Button executeButton = (Button) findViewById(R.id.execute);
        //init toast obj to be used in this application context, just needed once
        Toast toast = new Toast(getApplicationContext());
        //sets toast position on screen
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        //sets on click listener for execute button
        executeButton.setOnClickListener(new ExecuteButtonOnClickListener(getApplicationContext()));
    }

    /**
     * Inits the stop button and click listener
     */
    public void stopButtonInit()
    {
        //associate button with xml reference
        Button stopButton = (Button) findViewById(R.id.stop);
        //if clicked, send STOP to the server
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
        //init spinner from id, xml references
        modulationModeSpinner = (Spinner) findViewById(R.id.modeSpinner);
        oversampleModeSpinner = (Spinner) findViewById(R.id.oversamp_spinner);
        atanMathSpinner = (Spinner) findViewById(R.id.atan_spinner);
        firSizeSpinner = (Spinner) findViewById(R.id.fir_spinner);

        //associate ui members with parameters
        Parameters.MODULATION_MODE.setUiMembers(modulationModeSpinner, modulationModeSpinner.getClass());
        Parameters.OVERSAMPLING.setUiMembers(oversampleModeSpinner, oversampleModeSpinner.getClass());
        Parameters.ATAN_MATH.setUiMembers(atanMathSpinner, atanMathSpinner.getClass());
        Parameters.FIR_SIZE.setUiMembers(firSizeSpinner, firSizeSpinner.getClass());

        //modulation mode spinner strings to populate spinner object
        String[] modeSpinnerStrings = new String[]{
                "fm", "am", "usb", "lsb", "wbfm", "raw",
        };

        //oversampling mode spinner strings
        String[] overSampling = new String[]{
                "default", "1", "2", "3", "4"
        };

        //ATAN Math spinner options
        String[] atanMath = new String[]{
                "std", "lut", "fast"
        };

        //FIR Size Options
        String[] fir = new String[]{
                "default", "0", "9"
        };

        //set array adapter to set the strings inside spinner object
        ArrayAdapter<String> adapterMod = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, modeSpinnerStrings);
        ArrayAdapter<String> adapterSample = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, overSampling);
        ArrayAdapter<String> adapterATAN = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, atanMath);
        ArrayAdapter<String> adapterFIR = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, fir);

        //associate the adapeters and spinners together
        modulationModeSpinner.setAdapter(adapterMod);
        oversampleModeSpinner.setAdapter(adapterSample);
        atanMathSpinner.setAdapter(adapterATAN);
        firSizeSpinner.setAdapter(adapterFIR);

        //formats spinner to be nice clickable size for content, based off content wrapping
        adapterMod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSample.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterATAN.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterFIR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        modulationModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Once modulation mode spinner is set, change the Parameter to that value
             * @param adapterView -  not used
             * @param view -  not used
             * @param i - not used
             * @param l - not used
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String newModulationMode = getModulationModeSpinner(modulationModeSpinner);
                MODULATION_MODE.replaceIndex(0, newModulationMode);
            }

            /**
             * Does nothing
             * @param adapterView - not used
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do Nothing
            }
        });


        oversampleModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             * Once oversampling mode spinner is set, change the Parameter to that value
             * @param parent - not used
             * @param view - not used
             * @param position - not used
             * @param id - not used
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newsamplingMode = getModulationModeSpinner(oversampleModeSpinner);
                OVERSAMPLING.replaceIndex(0, newsamplingMode);
            }

            /**
             * Not used
             * @param parent - not used
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        atanMathSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Once atan math  spinner is set, change the Parameter to that value
             * @param adapterView -  not used
             * @param view -  not used
             * @param i -  not used
             * @param l -  not used
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String newsamplingMode = getModulationModeSpinner(atanMathSpinner);
                ATAN_MATH.replaceIndex(0, newsamplingMode);
            }

            /**
             * Not used
             * @param adapterView -  not used
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        firSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Once fir size  spinner is set, change the Parameter to that value
             * @param adapterView - not used
             * @param view - not used
             * @param i - not used
             * @param l - not used
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String newsamplingMode = getModulationModeSpinner(firSizeSpinner);
                FIR_SIZE.replaceIndex(0, newsamplingMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    /**
     * Init Switch Enable Optoons
     */
    public void switchInits()
    {
        //associate the enableoption UI mater class with all enable options
        ENABLE_OPTION.setUiMembers(enableOptionUiMatcher, enableOptionUiMatcher.getClass());

        /**
         * Enable Option switches, enableOptionUIMatcher adds to hash map the key and switch to watch
         * for server callbacks. Adds onchecked listener to each to add and remove that enable option
         * from the PARAMETER List
         */
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
}