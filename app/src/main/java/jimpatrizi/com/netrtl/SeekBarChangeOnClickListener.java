/*
 * Created by James Donald Patrizi on 11/18/17 10:59 AM
 * Copyright (c) 2017. All rights reserved.
 * This application is distributed under the terms of the GNU General Public License
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Last modified: 11/13/17 8:36 AM
 */

package jimpatrizi.com.netrtl;

import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static jimpatrizi.com.netrtl.Parameters.SQUELCH_LEVEL;
import static jimpatrizi.com.netrtl.Parameters.TUNER_GAIN;
import static jimpatrizi.com.netrtl.Parameters.VOLUME;

/**
 * Created by jamespatrizi on 10/21/17.
 * Seek Bar Change Listener
 */

public class SeekBarChangeOnClickListener implements SeekBar.OnSeekBarChangeListener
{
    /**
     * Application Context
     */
    private Context context;

    /**
     *  Current Progress Changed Value
     */
    private int progressChangedValue = 0;

    /**
     * Type of SeekBar
     */
    private  String type;

    /**
     * The textView
     */
    private TextView textView;

    /**
     * Automatic Gain Control is defined as -100 according to rtl-sdr, rtl_fm
     */
    private int AGC = -100;


    /**
     * Takes seekbar context with associated text view object(to be implemented later, textview dynamic
     * updates), and the type of seek bar, to then change the respective PARAMETER with associated UI element
     * @param context - application context
     * @param textView - currently, not used
     * @param type - Parameter Type, gain, squelch, volume
     */
    public SeekBarChangeOnClickListener(Context context, TextView textView, String type){
        this.context = context;
        this.type = type;
        this.textView = textView;
    }

    /**
     * On Progress Changed, get the current progresschanged value
     * @param seekBar - this seekbar
     * @param progress - on progress changed, that progress value
     * @param fromUser - NA
     */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progressChangedValue = progress;
    }

    /**
     * Not used
     * @param seekBar
     */
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Once the user stops tracking the seekbar, replace the parameter's value with the current
     * progressedChangedValue. Shows Toast popup of this value.
     * @param seekBar - this seekbar
     */
    public void onStopTrackingTouch(SeekBar seekBar) {
        /**
         * Uses type to determine which seekbar we are using to change the correct Parameter
         */
        if(type.equalsIgnoreCase("volume"))
        {
            if (VOLUME.isIndexValid(0)) {
                VOLUME.replaceIndex(0, "" + progressChangedValue);
            } else {
                VOLUME.append("" + progressChangedValue);
            }

            Toast.makeText(context, "System Volume =  " + progressChangedValue + "%",
                    Toast.LENGTH_SHORT).show();

            // Send the volume to the server
            MainActivity.getTcpClient().sendToServer(Parameters.VOLUME.getDameonCallableStrings().get(0));

            //textView.append(Integer.toString(progressChangedValue), 7, 10);
        }
        else if(type.equalsIgnoreCase("gain"))
        {
            if(progressChangedValue != 0)
            {
                if (TUNER_GAIN.isIndexValid(0)) {
                    TUNER_GAIN.replaceIndex(0, "" + progressChangedValue);
                } else {
                    TUNER_GAIN.append("" + progressChangedValue);
                }
                Toast.makeText(context, "Gain (dB) =  " + progressChangedValue + "dB",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (TUNER_GAIN.isIndexValid(0)) {
                    TUNER_GAIN.replaceIndex(0, "" + AGC);
                } else {
                    TUNER_GAIN.append("" + AGC);
                }
                Toast.makeText(context, "Automatic Gain Control Enabled", Toast.LENGTH_SHORT).show();
            }
        }
        else //if(type.equalsIgnoreCase("squelch")
        {
            if (SQUELCH_LEVEL.isIndexValid(0)) {
                SQUELCH_LEVEL.replaceIndex(0, "" + progressChangedValue);
            } else {
                SQUELCH_LEVEL.append("" + progressChangedValue);
            }
            Toast.makeText(context, "Squelch Level =  " + progressChangedValue + "%",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
