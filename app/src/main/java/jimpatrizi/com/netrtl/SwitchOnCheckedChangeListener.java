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
 * Last modified: 11/11/17 11:11 AM
 */

package jimpatrizi.com.netrtl;

import android.util.Log;
import android.widget.CompoundButton;

import static jimpatrizi.com.netrtl.Parameters.ENABLE_OPTION;

/**
 * Created by Jim Patrizi on 11/4/2017.
 */

public class SwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener
{
    /**
     * For Logcat debugging
     */
    private String TAG = getClass().getName();

    /**
     * What switch we are changing
     */
    private String type;

    /**
     * OnChecked Listener for ENABLE OPTION switches
     * @param type - the enable option we are switching
     */
    public SwitchOnCheckedChangeListener(final String type)
    {
        this.type = type;
    }

    /**
     * After a switch is toggled, can see if the switch is checked or not to
     * add appopriate enable option parameter
     * @param buttonView - not used
     * @param isChecked - is this button checked or not
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // if the switch is checked do
        if(isChecked)
        {
            Log.v(TAG, type + " option enabled");
            ENABLE_OPTION.append(type);
        }
        //else remove this option from the enable option parameter
        else
        {
            ENABLE_OPTION.remove(type);
            Log.v(TAG, type + " option disabled");
        }
    }
}
