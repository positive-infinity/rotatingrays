package de.pnpq.rotatingrays.preferences;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

// custom extension of built-in ListPreference 
// * due to a bug regarding summary updates
public class ListPreferenceWithSummary extends ListPreference
{
    public ListPreferenceWithSummary(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ListPreferenceWithSummary(Context context)
    {
        super(context);
    }

    @Override
    public void setValue(String value)
    {
        super.setValue(value);
        setSummary(value);
    }

    @Override
    public void setSummary(CharSequence summary)
    {
        super.setSummary(getEntry());
    }
}
