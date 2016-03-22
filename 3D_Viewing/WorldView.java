/* class WorldView
 * The OpenGL drawing component of the interface
 * for the "world" viewport
 *
 * CS 428   Doug DeCarlo
 */

import java.awt.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

public class WorldView extends SimpleGLCanvas
{
    ViewVolume viewVol;
    Cube cube;
    CameraView cameraView;

    public WorldView(Window parent, boolean debug)
    {
        super(parent);
        debugging = debug;

        viewVol = new ViewVolume();
        cube = new Cube();
        
        cameraView = new CameraView(parent, debug);
        
    }

    // Method for initializing OpenGL (called once at the beginning)
    public void init(GL gl)
    {
        // Set background color to black
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Turn on visibility test
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    // ------------------------------------------------------------

    // Method for handling window resizing
    public void projection(GL gl)
    {
        // Set drawing area
        gl.glViewport(0, 0, width, height);

        // ...
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the world projection transformation based on the
        // aspect ratio of the window
        double l, r, b, t;
        double aspectRatio = width/(double)height;
        if(aspectRatio > 1)
        {
        	l = -1;
	        r = 1;
	        b = -1/aspectRatio ; t = 1/ aspectRatio;
        }
        else{
        	l = -1 * aspectRatio;
	        r = 1 * aspectRatio;
	        b = -1 ; t = 1;
        }
        
        gl.glFrustum(l, r, b, t, 2, 100);

        // ...
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }

    // Method for drawing the contents of the window
    public void draw(GL gl)
    {
        // Clear the window
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        projection(gl);
        // Draw the cube (perhaps clipped) and the view volume
        
        //Disable the clipping plane if check box is unchecked
        if(!World.isClipping()){
        	enableDisablePlanes(gl, false);
        }
        
        gl.glLoadIdentity();
        transformation(gl);
        cameraView.inverseTransformation(gl);
        viewVol.draw(gl);         
        if(World.isClipping())
        {
        	viewVol.placeClipPlanes(gl, 0);
        	enableDisablePlanes(gl, true);        	
        }
        gl.glLoadIdentity();
        transformation(gl);
        cube.transform(gl);       
        cube.draw(gl, true);        
        enableDisablePlanes(gl, false);
        
        gl.glLoadIdentity();
        transformation(gl);
        cameraView.inverseTransformation(gl);        
        if(World.isClipping())
        {
        	viewVol.placeClipPlanes(gl, 1);
        	gl.glLoadIdentity();
            transformation(gl);
            cube.transform(gl);
        	gl.glEnable(GL.GL_CLIP_PLANE0);
        	cube.draw(gl, false);
        	gl.glDisable(GL.GL_CLIP_PLANE0);
        	gl.glEnable(GL.GL_CLIP_PLANE1);
        	cube.draw(gl, false);
        	gl.glDisable(GL.GL_CLIP_PLANE1);
        	gl.glEnable(GL.GL_CLIP_PLANE2);
        	cube.draw(gl, false);
        	gl.glDisable(GL.GL_CLIP_PLANE2);
        	gl.glEnable(GL.GL_CLIP_PLANE3);
        	cube.draw(gl, false);
        	gl.glDisable(GL.GL_CLIP_PLANE3);
        	gl.glEnable(GL.GL_CLIP_PLANE4);
        	cube.draw(gl, false);
        	gl.glDisable(GL.GL_CLIP_PLANE4);
        	gl.glEnable(GL.GL_CLIP_PLANE5);
        	cube.draw(gl, false);
        	gl.glDisable(GL.GL_CLIP_PLANE5);
        }
     	
    }

    // Apply the (world) viewing transformation (W)
    void transformation(GL gl)
    {
        // ...
    	gl.glTranslated(World.tx(), World.ty(), World.tz());
    	gl.glRotated(World.roll(), 0, 0, 1);
    	gl.glRotated(World.yaw(), 0, 1, 0);
    	gl.glRotated(World.pitch(), 1, 0, 0);
    }
    
    void inverseTransform(GL gl)
    {
    	gl.glRotated(-World.pitch(), 1, 0, 0);
    	gl.glRotated(-World.yaw(), 0, 1, 0);
    	gl.glRotated(-World.roll(), 0, 0, 1);
    	gl.glTranslated(-World.tx(), -World.ty(), -World.tz());    	
    }
    
    void enableDisablePlanes(GL gl,boolean enable)
    {
    	if(enable)
    	{
    		gl.glEnable(GL.GL_CLIP_PLANE0);
        	gl.glEnable(GL.GL_CLIP_PLANE1);
        	gl.glEnable(GL.GL_CLIP_PLANE2);
        	gl.glEnable(GL.GL_CLIP_PLANE3);
        	gl.glEnable(GL.GL_CLIP_PLANE4);
        	gl.glEnable(GL.GL_CLIP_PLANE5);
    	}
    	else
    	{
			gl.glDisable(GL.GL_CLIP_PLANE0);
			gl.glDisable(GL.GL_CLIP_PLANE1);
			gl.glDisable(GL.GL_CLIP_PLANE2);
			gl.glDisable(GL.GL_CLIP_PLANE3);
			gl.glDisable(GL.GL_CLIP_PLANE4);
			gl.glDisable(GL.GL_CLIP_PLANE5);
    	}
    }
    
    double getNormalizedX(){
    	double ndcX = ((World.tx() / width) * 2) - 1; 
    	return ndcX;
    }
    
    double getNormalizedY(){
    	double ndcY = ((World.ty() / height) * 2) - 1; 
    	return ndcY;
    }
    
    double getNormalizedZ(){
    	double ndcZ = ((World.tz() / 80) * 2) - 1; 
    	return ndcZ;
    }
    
}