package de.pnpq.rotatingrays.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import de.pnpq.rotatingrays.R;
import de.pnpq.rotatingrays.renderer.RotatingRaysRenderer;

public class RotatingRaysWallpaperService extends WallpaperService
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // android.os.Debug.waitForDebugger();

        // set preference default values if this is the first start ever
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
    }

    @Override
    public Engine onCreateEngine()
    {
        return new RotatingRaysEngine();
    }

    class RotatingRaysEngine extends Engine implements OnSharedPreferenceChangeListener
    {
        private WallpaperGLSurfaceView mGLSurfaceView;
        private RotatingRaysRenderer mRenderer;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder)
        {
            super.onCreate(surfaceHolder);
            
            mGLSurfaceView = new WallpaperGLSurfaceView(RotatingRaysWallpaperService.this);
            mRenderer = new RotatingRaysRenderer(RotatingRaysWallpaperService.this);
            mGLSurfaceView.setRenderer(mRenderer);
            
            PreferenceManager.getDefaultSharedPreferences(RotatingRaysWallpaperService.this).registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            super.onVisibilityChanged(visible);

            if (visible)
            {
                mGLSurfaceView.onResume();
                mRenderer.onResume();
            }
            else
            {
                mGLSurfaceView.onPause();
            }
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();

            mGLSurfaceView.onWallpaperDestroy();

            PreferenceManager.getDefaultSharedPreferences(RotatingRaysWallpaperService.this).unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            mGLSurfaceView.queueEvent(new Runnable()
            {
                public void run()
                {
                    mRenderer.init(RotatingRaysWallpaperService.this);
                }
            });
        }

        class WallpaperGLSurfaceView extends GLSurfaceView
        {
            WallpaperGLSurfaceView(Context context)
            {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder()
            {
                return getSurfaceHolder();
            }

            public void onWallpaperDestroy()
            {
                super.onDetachedFromWindow();
            }
        }

    }
}
