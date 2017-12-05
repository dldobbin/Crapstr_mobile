package dics.crapstr;


import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by daniel on 11/22/2017.
 *
 */

public class ButtonWrapper extends android.support.v7.widget.AppCompatButton {
    private Outgoing outgoing;

    public ButtonWrapper(Context context) {
        super(context);
    }

    public ButtonWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Outgoing getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(Outgoing outgoing) {
        this.outgoing = outgoing;
    }
}
