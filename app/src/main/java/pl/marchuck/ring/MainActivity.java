package pl.marchuck.ring;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.CENTER_HORIZONTAL;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {


    private GLSurfaceView glSurfaceView;
    private ProgressBar progressBar;
    //private OpenGLHelper openGLHelper;

    //  private Swipe swipe = new Swipe();
    private Triangle mTriangle;
    private Square mSquare;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setRenderer(this);
        //   openGLHelper = new OpenGLHelper(this);
        //swipe.addListener(new SwipeCharacterListener(openGLHelper));
        RelativeLayout relativeLayout = new RelativeLayout(this);

        //  Button btn = new Button(this);
        //   btn.setText("switch");
        //   btn.setOnClickListener(openGLHelper);
        RelativeLayout.LayoutParams paramsForBtn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        paramsForBtn.addRule(CENTER_HORIZONTAL);

        progressBar = new ProgressBar(this);
        //    openGLHelper.setProgressIndicator(this);
        RelativeLayout.LayoutParams paramsForProgress = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        paramsForProgress.addRule(CENTER_IN_PARENT);
        progressBar.setLayoutParams(paramsForProgress);
        progressBar.setVisibility(GONE);
        //   btn.setLayoutParams(paramsForBtn);

        relativeLayout.addView(glSurfaceView);
        relativeLayout.addView(progressBar);
        //   relativeLayout.addView(btn);
        setContentView(relativeLayout);
    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mTriangle = new Triangle();
        mSquare = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    }
}
