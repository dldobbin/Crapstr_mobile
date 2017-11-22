package dics.crapstr;


import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by daniel on 11/22/2017.
 *
 */

public class ButtonWrapper extends android.support.v7.widget.AppCompatButton {
    private String placeId;

    public ButtonWrapper(Context context) {
        super(context);
    }

    public ButtonWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
