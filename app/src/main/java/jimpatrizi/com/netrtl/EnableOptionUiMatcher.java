/*
 * Created by James Donald Patrizi on 11/18/17 10:58 AM
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
 * Last modified: 11/11/17 11:59 AM
 */

package jimpatrizi.com.netrtl;

import android.app.Activity;
import android.widget.Switch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamespatrizi on 11/6/17.
 *
 * Package protected class that associates all enable option UI elements to the respective keys to be appended
 * to the enable options paraeter
 */

class EnableOptionUiMatcher {

    /**
     * Map that holds Enable Option and UI Switch
     */
    private final Map<String, Switch> mapper;

    EnableOptionUiMatcher()
    {
        mapper = new HashMap<>();
    }

    /**
     * Adds key and switch to map
     * @param key - enable option key
     * @param uiSwitch - ui switch element
     */
    void add(String key, Switch uiSwitch)
    {
        mapper.put(key, uiSwitch);
    }

    /**
     * Sets the appopriate UI element to true in UI thread, used in response listener thread
     * @param mainActivity - the main activity reference
     * @param value - Enable Option value to check true
     */
    void enableSwitchByString(Activity mainActivity, String value)
    {
        /**
         * For all keys in the map, if the key equals the provided value, set that ui element to true
         */
        for (final String key : mapper.keySet())
        {
            if (key.equals(value))
            {
                mainActivity.runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                       mapper.get(key).setChecked(true);
                   }
                                           }
                );

            }
        }
    }

    /**
     * Unchecks all switches in the map
     * @param mainActivity - main activity reference
     */
    void uncheckAll(Activity mainActivity)
    {
        for (final String key : mapper.keySet())
        {
            mainActivity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       mapper.get(key).setChecked(false);
                   }
               }
            );
        }
    }


}
