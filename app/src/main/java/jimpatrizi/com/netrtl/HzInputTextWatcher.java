package jimpatrizi.com.netrtl;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by jamespatrizi on 11/1/17.
 */

public class HzInputTextWatcher implements TextWatcher{

    private EditText hzInput;

    public HzInputTextWatcher(EditText hzInput)
    {
        this.hzInput = hzInput;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String hzString = hzInput.getText().toString();
        Parameters.FREQUENCY.replaceIndex(0, hzString);
    }
}
