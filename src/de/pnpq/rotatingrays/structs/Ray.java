package de.pnpq.rotatingrays.structs;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import de.pnpq.rotatingrays.helpers.OpenGLHelper;

public class Ray
{
    private float mRed;
    private float mGreen;
    private float mBlue;

    private int mAngle;

    private FloatBuffer mRayVertexBuffer;
    private FloatBuffer mSmoothingTriangleVertexBuffer1;
    private FloatBuffer mSmoothingTriangleVertexBuffer2;
    private FloatBuffer mSmoothingTriangleColorBuffer1;
    private FloatBuffer mSmoothingTriangleColorBuffer2;
    
    private static FloatBuffer mSmoothingTriangleColorBufferFinal1;
    private static FloatBuffer mSmoothingTriangleColorBufferFinal2;

    public Ray(Context context, int color, int angle)
    {
        mRed = Color.red(color) / 255f;
        mGreen = Color.green(color) / 255f;
        mBlue = Color.blue(color) / 255f;

        mAngle = angle;
    }

    public int getColor()
    {
        return Color.rgb((int) (mRed * 255), (int) (mGreen * 255), (int) (mBlue * 255));
    }

    public int getAngle()
    {
        return mAngle;
    }

    public void setNextColor(int color)
    {
        float nextRed = Color.red(color) / 255f;
        float nextGreen = Color.green(color) / 255f;
        float nextBlue = Color.blue(color) / 255f;

        float[] smoothingTriangleColors1 = { nextRed, nextGreen, nextBlue, 1.0f, nextRed, nextGreen, nextBlue, 1.0f, mRed, mGreen, mBlue, 1.0f };
        mSmoothingTriangleColorBuffer1 = OpenGLHelper.getFloatBufferFromArray(smoothingTriangleColors1);

        float[] smoothingTriangleColors2 = { mRed, mGreen, mBlue, 1.0f, nextRed, nextGreen, nextBlue, 1.0f, mRed, mGreen, mBlue, 1.0f };
        mSmoothingTriangleColorBuffer2 = OpenGLHelper.getFloatBufferFromArray(smoothingTriangleColors2);
    }
    
    public static void setFinalTransitionColors(int colorFrom, int colorTo)
    {
        float fromRed = Color.red(colorFrom) / 255f;
        float fromGreen = Color.green(colorFrom) / 255f;
        float fromBlue = Color.blue(colorFrom) / 255f;
        
        float toRed = Color.red(colorTo) / 255f;
        float toGreen = Color.green(colorTo) / 255f;
        float toBlue = Color.blue(colorTo) / 255f;
        
        float[] smoothingTriangleColorsFinal1 = { toRed, toGreen, toBlue, 1.0f, toRed, toGreen, toBlue, 1.0f, fromRed, fromGreen, fromBlue, 1.0f };
        mSmoothingTriangleColorBufferFinal1 = OpenGLHelper.getFloatBufferFromArray(smoothingTriangleColorsFinal1);

        float[] smoothingTriangleColorsFinal2 = { fromRed, fromGreen, fromBlue, 1.0f, toRed, toGreen, toBlue, 1.0f, fromRed, fromGreen, fromBlue, 1.0f };
        mSmoothingTriangleColorBufferFinal2 = OpenGLHelper.getFloatBufferFromArray(smoothingTriangleColorsFinal2);        
    }

    public void onScreenDimensionChanged(int width, int height)
    {
        int diag = (int) Math.sqrt(width * width + height * height) + 1;
        int x = (int) (Math.tan(Math.toRadians(mAngle) / 2.0) * diag) + 1;

        float rayVertices[] = { 0, 0, 0, -x, diag, 0, x, diag, 0 };
        mRayVertexBuffer = OpenGLHelper.getFloatBufferFromArray(rayVertices);

        float smoothingTriangleVertices1[] = { -1, 0, 0, -x - 1, diag, 0, 1, 0, 0 };
        mSmoothingTriangleVertexBuffer1 = OpenGLHelper.getFloatBufferFromArray(smoothingTriangleVertices1);

        float smoothingTriangleVertices2[] = { 1, 0, 0, -x - 1, diag, 0, -x + 1, diag, 0 };
        mSmoothingTriangleVertexBuffer2 = OpenGLHelper.getFloatBufferFromArray(smoothingTriangleVertices2);
    }

    public void draw(GL10 gl)
    {
        // draw arc
        gl.glColor4f(mRed, mGreen, mBlue, 255);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mRayVertexBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void drawTransition(GL10 gl, TransitionType transitionType)
    {
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, transitionType == TransitionType.eRegular ? mSmoothingTriangleColorBuffer1 : mSmoothingTriangleColorBufferFinal1);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mSmoothingTriangleVertexBuffer1);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, transitionType == TransitionType.eRegular ? mSmoothingTriangleColorBuffer2 : mSmoothingTriangleColorBufferFinal2);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mSmoothingTriangleVertexBuffer2);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}
