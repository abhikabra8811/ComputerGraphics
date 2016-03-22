/* class ViewVolume
 * OpenGL methods for drawing a view volume (given Camera specification)
 * using OpenGL clipping planes
 *
 * CS 428   Doug DeCarlo
 */

import javax.media.opengl.*;
import javax.vecmath.*;

class ViewVolume
{
	Point3d[] points = new Point3d[8];
    // Constructor
    public ViewVolume()
    {
    }

    //-----------------------------------------------------------------------

    // Draw view volume (given camera specification in Camera) in
    // its canonical coordinate system
    public void draw(GL gl)
    {
        // ...
    	double ratio = 1.0;
    	if(Camera.isPerspective())
    	{
    		ratio = Camera.far() / Camera.near(); 
    	}
    	
    	//Points on near plane
    	if(World.isNormalized()){
    		
    	}
    	points[0] = new Point3d(Camera.left(), Camera.bottom(), -Camera.near());
    	points[1] = new Point3d(Camera.right(), Camera.bottom(),-Camera.near());
    	points[2] = new Point3d(Camera.right(), Camera.top(), -Camera.near());
    	points[3] = new Point3d(Camera.left(), Camera.top(), -Camera.near());
    	
    	//Points on far plane
    	points[4] = new Point3d(Camera.left() * ratio, Camera.bottom() * ratio, -Camera.far());
    	points[5] = new Point3d(Camera.right()* ratio, Camera.bottom() * ratio,-Camera.far());
    	points[6] = new Point3d(Camera.right() * ratio, Camera.top() * ratio, -Camera.far());
    	points[7] = new Point3d(Camera.left() * ratio, Camera.top() * ratio, -Camera.far());
    	
    	//Drawing near plane
    	gl.glLineWidth(3);
    	gl.glBegin(GL.GL_LINE_LOOP);
    		gl.glColor3d(0, 0, 1);
    		gl.glVertex3d(points[0].x, points[0].y, points[0].z);
    		gl.glVertex3d(points[1].x, points[1].y, points[1].z);
    		gl.glVertex3d(points[2].x, points[2].y, points[2].z);
    		gl.glVertex3d(points[3].x, points[3].y, points[3].z);
    	gl.glEnd();
    	
    	//Drawing far plane
    	gl.glBegin(GL.GL_LINE_LOOP);
			gl.glColor3d(1, 1, 1);
			gl.glVertex3d(points[4].x, points[4].y, points[4].z);
    		gl.glVertex3d(points[5].x, points[5].y, points[5].z);
    		gl.glVertex3d(points[6].x, points[6].y, points[6].z);
    		gl.glVertex3d(points[7].x, points[7].y, points[7].z);
   		gl.glEnd();
    
		gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex3d(points[0].x, points[0].y, points[0].z);
			gl.glVertex3d(points[4].x, points[4].y, points[4].z);
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex3d(points[1].x, points[1].y, points[1].z);
			gl.glVertex3d(points[5].x, points[5].y, points[5].z);
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINE_STRIP);
			gl.glColor3d(1, 0, 0);
			gl.glVertex3d(points[2].x, points[2].y, points[2].z);
			gl.glVertex3d(points[6].x, points[6].y, points[6].z);
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex3d(points[3].x, points[3].y, points[3].z);
			gl.glVertex3d(points[7].x, points[7].y, points[7].z);
		gl.glEnd();
		
		gl.glLineWidth(1);
    }

    // Specify positions of all clipping planes
    //  - this is called from WorldView.draw()
    //  - it calls placeClipPlane() below 6 times -- once for each
    //    side of the view volume
    public void placeClipPlanes(GL gl, int side)
    {
        // ...
    	placeClipPlane(gl, GL.GL_CLIP_PLANE0, points[0], points[1], points[2], side);
    	placeClipPlane(gl, GL.GL_CLIP_PLANE1, points[1], points[5], points[6], side);
    	placeClipPlane(gl, GL.GL_CLIP_PLANE2, points[6], points[5], points[4], side);
    	placeClipPlane(gl, GL.GL_CLIP_PLANE3, points[0], points[3], points[7], side);
    	placeClipPlane(gl, GL.GL_CLIP_PLANE4, points[3], points[2], points[6], side);
    	placeClipPlane(gl, GL.GL_CLIP_PLANE5, points[0], points[4], points[5], side);
    	
    }

    // Specify position of one particular clipping plane given 3
    // points on the plane ordered counter-clockwise
    void placeClipPlane(GL gl, int plane, Point3d p1, Point3d p2, Point3d p3, int side)
    {
    	
        // Plane equation Ax + By + Cz + D stored as [A,B,C,D]
        double[] eqn = new double[4];
        
        //Creating vector P1P2
        double[] vec1 = new double[3];
        vec1[0] = p2.x - p1.x;
        vec1[1] = p2.y - p1.y;
        vec1[2] = p2.z - p1.z;
        
        Vector3d vec13D = new Vector3d(vec1);
         
        //Creating vector P1P3
        double[] vec2 = new double[3];
        vec2[0] = p3.x - p1.x;
        vec2[1] = p3.y - p1.y;
        vec2[2] = p3.z - p1.z;
        
        Vector3d vec23D = new Vector3d(vec2);
          
        //Taking cross product of P1P2 and P1P3 to compute normal
        Vector3d norm = new Vector3d();
        norm.cross(vec13D, vec23D);
        norm.normalize();
        //norm.negate();
        if(side == 0){
        	norm.negate();
        }        
        
        Vector3d vecP = new Vector3d(p1.x, p1.y, p1.z);

        // Compute the plane equation from the 3 points -- fill in eqn[]
        eqn[0] = norm.x;
        eqn[1] = norm.y;
        eqn[2] = norm.z;
        eqn[3] = -(norm.dot(vecP));

        // ...

        // *** Use the javax.vecmath library for this computation!

        // Specify the clipping plane
        gl.glClipPlane(plane, eqn,0 );
    }
}
