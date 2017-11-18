/*
 * Created by James Donald Patrizi on 11/18/17 10:57 AM
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
 * Last modified: 11/13/17 9:49 AM
 */

package jimpatrizi.com.netrtl;

/**
 * Created by jamespatrizi on 11/13/17.
 */

/**
 * Class that can generate defaults to be used internally to set GUI with parseCmdsInUseResponse in ResponseListener
 */
public class DefaultBuilder {
    private int frequency;
    private String modulationMode;

    DefaultBuilder() { }

    // Multiple Constructors for each member variable
    public DefaultBuilder setFrequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    public DefaultBuilder setModulationMode(String modulationMode) {
        this.modulationMode = modulationMode;
        return this;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getModulationMode() {
        return modulationMode;
    }

    /**
     * toString to make a formatted string to send to parseCmdsInUseResponse
     * @return - returns daemon-esk string to update GUI to the set default parameters
     */
    @Override
    public String toString() {
        return String.format(
                "FREQUENCY=%d\n\nMODULATION_MODE=%s:\n\n"
                , getFrequency(), getModulationMode());
    }

}