package de.pnpq.rotatingrays.activities;

import android.app.Activity;
import android.os.Bundle;
import de.pnpq.rotatingrays.fragments.SettingsFragment;

public class SettingsActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // display the fragment as the main content
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
