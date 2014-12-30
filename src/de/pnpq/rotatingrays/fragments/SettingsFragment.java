package de.pnpq.rotatingrays.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import de.pnpq.rotatingrays.R;
import de.pnpq.rotatingrays.helpers.SettingsHelper;
import de.pnpq.rotatingrays.interfaces.OnResetListener;
import de.pnpq.rotatingrays.preferences.ResetPreference;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener, OnResetListener
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // load the preferences from XML
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // register preference listener
        findPreference(getActivity().getString(R.string.theme_key)).setOnPreferenceChangeListener(this);

        ((ResetPreference) findPreference(getActivity().getString(R.string.theme_reset_key))).setOnResetListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // unregister preference listener
        findPreference(getActivity().getString(R.string.theme_key)).setOnPreferenceChangeListener(null);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String oldTheme = ((ListPreference) findPreference(getActivity().getString(R.string.theme_key))).getValue();
        String newTheme = (String) newValue;

        // save current and load new preferences
        if (!oldTheme.equals(newTheme))
        {
            SettingsHelper.transferPreferences(getActivity(), getPreferenceManager(), oldTheme, newTheme);
        }

        return true;
    }

    @Override
    public void onReset()
    {
        String currentTheme = ((ListPreference) findPreference(getActivity().getString(R.string.theme_key))).getValue();

        // reset the current theme
        SettingsHelper.resetPreferences(getActivity(), getPreferenceManager(), currentTheme);
    }

}
