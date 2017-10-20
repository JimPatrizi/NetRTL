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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import static jimpatrizi.com.netrtl.Parameters.VOLUME;

/**
 * MainActivity of the NetRTL Android Application.
 * @author Jim Patrizi
 * @version 1.0
 * @since 2017-10-02
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public AsyncConnection connection;
    public SeekBar volumeSeekBar;


    //handles logcat messages for socket debugging, will be used to implement UI callback
    public ConnectionHandle handler = new ConnectionHandle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //Obtained the previously set IP + Port Preferences
        Context context = getApplicationContext();
        SharedPreferences sharedPrefs = context.getSharedPreferences("pref_main", Context.MODE_PRIVATE);
        String ip_address = sharedPrefs.getString("key_ip_name", "0.0.0");
        int port_number = sharedPrefs.getInt("key_port_name", 2832);

        //Open socket with AsyncTask (Background Thread)
        connection = new AsyncConnection(ip_address, port_number, handler);
        connection.execute();
        //end of open socket routine

        //Init Execute button
        executeButtonInit();
        //In Modulation Mode Spinner
        spinnerInit();
        //Init Buttons
        buttonInit();

        //SeekBar Init
        volumeSeekBar=(SeekBar) findViewById(R.id.volume_seek); // initiate the Seekbar
        volumeSeekBar.setMax(100); // 100 maximum value for the Seek bar
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "System Volume =  " + progressChangedValue + "%",
                        Toast.LENGTH_SHORT).show();

                if (VOLUME.isIndexValid(0))
                {
                    VOLUME.replaceIndex(0, "" +  progressChangedValue);
                }
                else
                {
                    VOLUME.append("" + progressChangedValue);
                }
            }
        });


    }


    /**
     * This executeButtonInit method sets the click listener on the execute button
     * @return Nothing
     */
    public void executeButtonInit(){
        Button executeButton = (Button) findViewById(R.id.execute);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        executeButton.setOnClickListener(new ExecuteButtonOnClickListener(getApplicationContext(), connection, handler));
        //TODO Make Reconnect button to do connection.execute() if user needs to redefine IP ADDR and PORT. Settings persist after app is closed and reopened to invoke onCreate
    }

    /**
     * This spinnerInit method gets the spinner id from content_main.xml,
     * populates spinner with WBFM, AM, USB, LSB
     * @return Nothing
     */
    public void spinnerInit(){
        //init spinner from id
        Spinner spin = (Spinner) findViewById(R.id.modeSpinner);
        //spinner strings to populate spinner object
        String[] modeSpinnerStrings = new String[] {
                "WBFM", "AM", "USB", "LSB"
        };
        //set array adapter to set the strings inside spinner obect
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, modeSpinnerStrings);
        spin.setAdapter(adapter);
        //formats spinner to be nice clickable size
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     * Initializes buttons with click listeners for their respective values in the FREQUENCY Parameter,
     * Buttons include +/- 1k, 10k, 100k, 10MHz
     * @return Nothing.
     */
    public void buttonInit() {

        Button increment1KHZ = (Button) findViewById(R.id.p1khz);
        increment1KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(1000, getApplicationContext()));

        Button decrement1KHZ = (Button) findViewById(R.id.n1khz);
        decrement1KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(-1000, getApplicationContext()));

        Button increment10KHZ = (Button) findViewById(R.id.p10khz);
        increment10KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(10000, getApplicationContext()));

        Button decrement10KHZ = (Button) findViewById(R.id.n10khz);
        decrement10KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(-10000, getApplicationContext()));

        Button increment100KHZ = (Button) findViewById(R.id.p100khz);
        increment100KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(100000, getApplicationContext()));

        Button decrement100KHZ = (Button) findViewById(R.id.n100khz);
        decrement100KHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(-100000, getApplicationContext()));

        Button increment10MHZ = (Button) findViewById(R.id.p10mhz);
        increment10MHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(10000000, getApplicationContext()));

        Button decrement10MHZ = (Button) findViewById(R.id.n10mhz);
        decrement10MHZ.setOnClickListener(new FrequencyChangeButtonOnClickListener(-10000000, getApplicationContext()));
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
        }

        return super.onOptionsItemSelected(item);
    }

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
}
