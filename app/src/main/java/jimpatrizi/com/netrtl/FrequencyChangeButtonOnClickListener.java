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
 * Last modified: 11/8/17 10:47 AM
 */

package jimpatrizi.com.netrtl;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static jimpatrizi.com.netrtl.Parameters.FREQUENCY;

/**
 * Created by jamespatrizi on 9/30/17.
 *
 * Class that makes frequency buttons change listener and makes sure the new frequency is valid from parameters class
 * @author Jim Patrizi
 */

public class FrequencyChangeButtonOnClickListener implements View.OnClickListener {

    private final int changeValue;
    private Context context;
    EditText hzInput;

    /**
     * Constructor that gets the new value
     * @param changeValue
     * @param context
     */
    public FrequencyChangeButtonOnClickListener(final int changeValue, Context context, EditText hzInput){
        this.changeValue = changeValue;
        this.context = context;
        this.hzInput = hzInput;
    }

    /**
     * Increments/ decrements frequency by changeValue on click, if nothing is present, start the list
     * @param v default
     */
    @Override
    public void onClick(View v) {
        if (FREQUENCY.isIndexValid(0))
        {
            try {
                FREQUENCY.replaceIndex(0, "" + (Integer.parseInt(FREQUENCY.getByIndex(0)) + changeValue));
            }
            catch (NumberFormatException exception)
            {
                Toast.makeText(context, "Enter a frequency first: " + exception, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            FREQUENCY.append("" + changeValue);
        }
        String newFrequency = FREQUENCY.getByIndex(0);
        hzInput.setText(newFrequency);
    }




}
