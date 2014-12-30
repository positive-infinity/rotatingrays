package de.pnpq.rotatingrays.helpers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class OpenGLHelper
{
    public static FloatBuffer getFloatBufferFromArray(float[] array)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());        
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(array);
        floatBuffer.position(0);        
        return floatBuffer;
    }

}
