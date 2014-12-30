package de.pnpq.rotatingrays.renderer;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;
import android.util.Pair;
import de.pnpq.rotatingrays.helpers.SettingsHelper;
import de.pnpq.rotatingrays.structs.Ray;
import de.pnpq.rotatingrays.structs.TransitionType;

public class RotatingRaysRenderer implements GLSurfaceView.Renderer
{
    private Pair<Float, Float> mRotationCenter;
    private ArrayList<Ray> mRays;
    private float mRotationIncrementPerMs;

    private float mRotation = 0.0f;
    private long mLastRotateTime = System.currentTimeMillis();
    
    private int mWidth = 0;
    private int mHeight = 0;

    public RotatingRaysRenderer(Context context)
    {
        init(context);
    }

    public void init(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        mRotationCenter = SettingsHelper.getRotationCenter(context, prefs);
        mRays = SettingsHelper.getRays(context, prefs);
        mRotationIncrementPerMs = SettingsHelper.getRotationIncrementPerMs(context, prefs);

        // set ray dimensions
        // remark: mWidth and mHeight are set if this is called after a prefs change,
        // they are still zero during call from constructor but then onSurfaceChanged will be called shortly after
        for (Ray r : mRays)
        {
            r.onScreenDimensionChanged(mWidth, mHeight);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        gl.glDisable(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // remember width and height
        mWidth = width;
        mHeight = height;

        // set viewport
        gl.glViewport(0, 0, width, height);

        // set projection to actual screen size
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, width, height, 0, -1f, 1f);

        // notify the rays
        for (Ray r : mRays)
        {
            r.onScreenDimensionChanged(mWidth, mHeight);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        // clear and set matrix mode
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // adapt rotation
        mRotation += mRotationIncrementPerMs * (System.currentTimeMillis() - mLastRotateTime);
        mRotation %= 360;
        mLastRotateTime = System.currentTimeMillis();

        // reset, translate to rotation center and rotate
        gl.glLoadIdentity();
        gl.glTranslatef(mWidth * mRotationCenter.first, mHeight * mRotationCenter.second, 0);
        gl.glRotatef(mRotation, 0, 0, 1);

        // draw the rays
        if (!mRays.isEmpty())
        {
            int angle = 0;
            int i = 0;
            int iNext = (i + 1) % mRays.size();
            while (angle < 360)
            {
                mRays.get(i).draw(gl);

                gl.glRotatef(mRays.get(i).getAngle() / 2.0f + mRays.get(iNext).getAngle() / 2.0f, 0, 0, 1);

                angle += mRays.get(i).getAngle();
                i = (i + 1) % mRays.size();
                iNext = (iNext + 1) % mRays.size();
            }
        }

        // reset, translate to rotation center and rotate
        gl.glLoadIdentity();
        gl.glTranslatef(mWidth * mRotationCenter.first, mHeight * mRotationCenter.second, 0);
        gl.glRotatef(mRotation, 0, 0, 1);

        // draw the fading
        if (!mRays.isEmpty())
        {
            int angle = 0;
            int i = 0;
            int iNext = (i + 1) % mRays.size();
            while (angle < 360)
            {                
                if (angle + mRays.get(i).getAngle() < 360)
                {
                    // this is a regular transition
                    mRays.get(i).drawTransition(gl, TransitionType.eRegular);
                }
                else
                {
                    // this is the final transition
                    mRays.get(i).drawTransition(gl, TransitionType.eFinal);
                }

                gl.glRotatef(mRays.get(i).getAngle() / 2.0f + mRays.get(iNext).getAngle() / 2.0f, 0, 0, 1);

                angle += mRays.get(i).getAngle();

                i = (i + 1) % mRays.size();
                iNext = (iNext + 1) % mRays.size();
            }
        }

    }

    public void onResume()
    {
        mLastRotateTime = System.currentTimeMillis();
    }
}
