
package pl.marchuck.ring;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class PizzaCircle {
    public static final String TAG = PizzaCircle.class.getSimpleName();
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    static double coeff = 360;
    private double radius = 0.3f;
    public static final double DEFAULT_RADIUS = 0.3f;
    public static final int DEFAULT_PIECES_COUNT = 25;
    public static float[] DEFAULT_COLOR = new float[]{0.2f, 0.709803922f, 0.898039216f, 1.0f};

    float sin(float angle) {
        return (float) (radius * Math.sin(Math.toRadians(angle)));
    }

    float cos(float angle) {
        return (float) (radius * Math.cos(Math.toRadians(angle)));
    }

    private float color[] = {0.2f, 0.709803922f, 0.898039216f, 1.0f};
    private final float circleCoords[];

    private final short[] drawOrder;


    float[] circleCoords(int n) {
        float[] arr = new float[3 * n + 3];
        float k = 0;
        double dk = coeff / n;
        for (int j = 0; j < arr.length; j += 3) {
            k += dk;
            arr[j] = cos(k);
            arr[j + 1] = sin(k);
            arr[j + 2] = 0;
            Log.d(TAG, "circleCoords: " + k);
        }
        return arr;
    }


    private static short[] drawOrder(int n) {
        short[] arr = new short[3 * n + 3];
        short j = 0;
        for (int i = 0; i < arr.length; i += 3) {
            arr[i] = 0;
            arr[i + 1] = (short) (j + 1);
            arr[i + 2] = (short) (j + 2);
            if (j > n) arr[i + 2] = 1;
            ++j;
        }
        return arr;
    }

    public PizzaCircle() {
        this(DEFAULT_RADIUS, DEFAULT_COLOR);
    }

    public PizzaCircle(double radius, float[] color) {
        this(radius, DEFAULT_PIECES_COUNT, color);
    }

    public PizzaCircle(double radius, int pieces, float[] color) {
        this.color = color;
        this.radius = radius;
        this.circleCoords = circleCoords(pieces);
        this.drawOrder = drawOrder(pieces);

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                circleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(circleCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     *                  this shape.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}