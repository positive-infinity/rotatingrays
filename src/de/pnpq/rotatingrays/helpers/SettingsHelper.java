package de.pnpq.rotatingrays.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Pair;
import de.pnpq.rotatingrays.R;
import de.pnpq.rotatingrays.preferences.ColorPickerPreference;
import de.pnpq.rotatingrays.preferences.SliderPreference;
import de.pnpq.rotatingrays.structs.Ray;

public class SettingsHelper
{
    public static Pair<Float, Float> getRotationCenter(Context context, SharedPreferences prefs)
    {
        float centerX = prefs.getFloat(context.getString(R.string.rotation_center_horizontal_key), 0.5f);
        float centerY = prefs.getFloat(context.getString(R.string.rotation_center_vertical_key), 0.5f);
        return new Pair<Float, Float>(centerX, centerY);
    }

    public static float getRotationIncrementPerMs(Context context, SharedPreferences prefs)
    {
        float rotationSpeedFactor = prefs.getFloat(context.getString(R.string.rotation_speed_key), 0.3f);
        int rotationDirectionFactor = prefs.getString(context.getString(R.string.rotation_direction_key), context.getString(R.string.clockwise_value)).equals(context.getString(R.string.clockwise_value)) ? 1 : -1;

        return ((rotationSpeedFactor + 0.1f) / 60.0f) * rotationDirectionFactor;
    }

    public static ArrayList<Ray> getRays(Context context, SharedPreferences prefs)
    {
        ArrayList<Ray> rays = new ArrayList<Ray>();

        int currentRayIndex = 1;
        while (currentRayIndex <= 7)
        {
            if (prefs.getBoolean("ray" + currentRayIndex + "_status", false))
            {
                int color = prefs.getInt("ray" + currentRayIndex + "_color", 0);
                float widthFactor = prefs.getFloat("ray" + currentRayIndex + "_width", 0.1f);
                rays.add(new Ray(context, color, (int) (widthFactor * 40) + 5));
            }
            currentRayIndex++;
        }

        // set next colors
        for (int i = 0; i < rays.size(); i++)
        {
            rays.get(i).setNextColor(rays.get((i + 1) % rays.size()).getColor());
        }

        // set final transition colors
        if (!rays.isEmpty())
        {
            int angle = 0;
            int i = 0;
            while (angle < 360)
            {
                angle += rays.get(i).getAngle();
                i = (i + 1) % rays.size();
            }

            // remember color of the last ray
            int colorOfLastRay = rays.get((i - 1 + rays.size()) % rays.size()).getColor();

            // remember angle offset
            int angleDiff = angle - 360;

            angle = 0;
            i = 0;
            while (angle <= angleDiff)
            {
                angle += rays.get(i).getAngle();
                i = (i + 1) % rays.size();
            }
            
            // remember color of first (visible) ray 
            int colorOfFirstRay = rays.get((i - 1 + rays.size()) % rays.size()).getColor();
            
            Ray.setFinalTransitionColors(colorOfLastRay, colorOfFirstRay);

        }
        else
        {
            Ray.setFinalTransitionColors(0xff000000, 0xff000000);
        }

        return rays;
    }

    public static void transferPreferences(Context context, PreferenceManager prefManager, String oldTheme, String newTheme)
    {
        String packageName = PackageHelper.getUniquePackageName(context);
        Resources resources = context.getResources();

        // build list of relevant keys
        ArrayList<String> keys = new ArrayList<String>();
        keys.add(resources.getString(R.string.rotation_speed_key));
        keys.add(resources.getString(R.string.rotation_direction_key));
        keys.add(resources.getString(R.string.rotation_center_horizontal_key));
        keys.add(resources.getString(R.string.rotation_center_vertical_key));
        keys.add(resources.getString(R.string.ray1_status_key));
        keys.add(resources.getString(R.string.ray2_status_key));
        keys.add(resources.getString(R.string.ray3_status_key));
        keys.add(resources.getString(R.string.ray4_status_key));
        keys.add(resources.getString(R.string.ray5_status_key));
        keys.add(resources.getString(R.string.ray6_status_key));
        keys.add(resources.getString(R.string.ray7_status_key));
        keys.add(resources.getString(R.string.ray1_width_key));
        keys.add(resources.getString(R.string.ray2_width_key));
        keys.add(resources.getString(R.string.ray3_width_key));
        keys.add(resources.getString(R.string.ray4_width_key));
        keys.add(resources.getString(R.string.ray5_width_key));
        keys.add(resources.getString(R.string.ray6_width_key));
        keys.add(resources.getString(R.string.ray7_width_key));
        keys.add(resources.getString(R.string.ray1_color_key));
        keys.add(resources.getString(R.string.ray2_color_key));
        keys.add(resources.getString(R.string.ray3_color_key));
        keys.add(resources.getString(R.string.ray4_color_key));
        keys.add(resources.getString(R.string.ray5_color_key));
        keys.add(resources.getString(R.string.ray6_color_key));
        keys.add(resources.getString(R.string.ray7_color_key));

        // save current preferences and load theme preferences
        Editor editor = context.getSharedPreferences(oldTheme, Context.MODE_PRIVATE).edit();
        SharedPreferences themePrefs = context.getSharedPreferences(newTheme, Context.MODE_PRIVATE);
        for (String currentKey : keys)
        {
            String themeKey = newTheme + "_" + currentKey + "_default";
            Preference pref = prefManager.findPreference(currentKey);

            if (pref instanceof SliderPreference)
            {
                editor.putFloat(currentKey, ((SliderPreference) pref).getValue());
                String themeDefaultValueString = resources.getString(resources.getIdentifier(themeKey, "string", packageName));
                ((SliderPreference) pref).setValue(themePrefs.getFloat(currentKey, Float.parseFloat(themeDefaultValueString)));
            }
            else if (pref instanceof ListPreference)
            {
                editor.putString(currentKey, ((ListPreference) pref).getValue());
                String themeDefaultValueString = resources.getString(resources.getIdentifier(themeKey, "string", packageName));
                ((ListPreference) pref).setValue(themePrefs.getString(currentKey, themeDefaultValueString));
            }
            else if (pref instanceof CheckBoxPreference)
            {
                editor.putBoolean(currentKey, ((CheckBoxPreference) pref).isChecked());
                boolean themeDefaultValueBoolean = resources.getBoolean(resources.getIdentifier(themeKey, "bool", packageName));
                ((CheckBoxPreference) pref).setChecked(themePrefs.getBoolean(currentKey, themeDefaultValueBoolean));
            }
            else if (pref instanceof ColorPickerPreference)
            {
                editor.putInt(currentKey, ((ColorPickerPreference) pref).getValue());
                int themeDefaultValueInt = resources.getInteger(resources.getIdentifier(themeKey, "integer", packageName));
                ((ColorPickerPreference) pref).setValue(themePrefs.getInt(currentKey, themeDefaultValueInt));
            }
        }
        editor.commit();
    }

    public static void resetPreferences(Context context, PreferenceManager prefManager, String currentTheme)
    {
        Editor editor = context.getSharedPreferences(currentTheme, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();

        transferPreferences(context, prefManager, "dummy", currentTheme);
    }

}
