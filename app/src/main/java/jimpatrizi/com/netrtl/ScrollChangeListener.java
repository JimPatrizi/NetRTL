package jimpatrizi.com.netrtl;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Jim Patrizi on 11/4/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class ScrollChangeListener extends ScrollView implements View.OnScrollChangeListener{

    public ScrollChangeListener(Context context) {
        super(context);
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
        View view = (View) getChildAt(getChildCount()-1);

        // Calculate the scrolldiff
        int diff = (view.getBottom()-(getHeight()+getScrollY()));

        // if diff is zero, then the bottom has been reached
        if( diff == 0 )
        {
            // notify that we have reached the bottom
            System.out.println("ScrollView: Bottom has been reached");
        }

        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
    }
}
