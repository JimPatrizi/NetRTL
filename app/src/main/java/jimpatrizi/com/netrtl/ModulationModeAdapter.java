package jimpatrizi.com.netrtl;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Jim Patrizi on 10/2/2017.
 */

public class ModulationModeAdapter extends BaseAdapter {
        Context context;
        String modulationModes[];

        public ModulationModeAdapter(Context applicationContext){
            this.context = applicationContext;
            this.modulationModes = new String[]{
                    "WBFM", "AM", "USB", "LSB"
            };
        }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}

