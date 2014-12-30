package de.pnpq.rotatingrays.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import de.pnpq.rotatingrays.interfaces.OnResetListener;

public class ResetPreference extends DialogPreference
{
    private OnResetListener resetListener;

    public ResetPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setOnResetListener(OnResetListener listener)
    {
        resetListener = listener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        super.onClick(dialog, which);

        if (which == DialogInterface.BUTTON_POSITIVE)
        {
            resetListener.onReset();
        }
    }
}
