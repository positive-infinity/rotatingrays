package de.pnpq.rotatingrays.helpers;

import java.util.Locale;

import android.content.Context;

public class PackageHelper
{
    public static String getUniquePackageName(Context context)
    {
        return context.getPackageName().toLowerCase(Locale.US);
    }
}
