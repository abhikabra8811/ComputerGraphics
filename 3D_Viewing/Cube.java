import javax.media.opengl.*;

import jdk.nashorn.internal.objects.Global;

class Cube
{
    // Constructor
    public Cube()
    {
    }

    // ----------------------------------------------------------------------

    // Apply the modeling transformation
    public void transform(GL gl)
    {
        // Multiply M onto stack
    	//gl.glLoadIdentity();
    	gl.glTranslated(Obj.tx(), Obj.ty(), Obj.tz());
    	gl.glRotated(Obj.rz(), 0, 0, 1);
    	gl.glRotated(Obj.ry(), 0, 1, 0);
    	gl.glRotated(Obj.rx(), 1, 0, 0);
    	gl.glScaled(Obj.scale(), Obj.scale(), Obj.scale());
        // ...
    }

    // ----------------------------------------------------------------------

    // Draw a cube with side-length 1, centered at the origin,
    // as either polygons or wireframe
    public void draw(GL gl, boolean polygon)
    {
        int mode = polygon ? GL.GL_POLYGON : GL.GL_LINE_LOOP;
    	//int mode = GL.GL_POLYGON;
    
        /* Cube vertices */
        double[] v0 = {-0.5f, -0.5f, -0.5f };
        double[] v1 = {-0.5f, -0.5f,  0.5f };
        double[] v2 = {-0.5f,  0.5f,  0.5f };
        double[] v3 = {-0.5f,  0.5f, -0.5f };
        double[] v4 = { 0.5f, -0.5f, -0.5f };
        double[] v5 = { 0.5f, -0.5f,  0.5f };
        double[] v6 = { 0.5f,  0.5f,  0.5f };
        double[] v7 = { 0.5f,  0.5f, -0.5f };
    
        /* Cube face colors */
        double[] cnx = { 0.0f, 1.0f, 2.0f };
        double[] cpx = { 1.0f, 0.0f, 0.0f };
        double[] cny = { 0.4f, 0.4f, 0.4f };
        double[] cpy = { 0.9f, 0.9f, 0.9f };
        double[] cnz = { 1.0f, 0.0f, 1.0f };
        double[] cpz = { 0.0f, 1.0f, 0.0f };
    
        if (!polygon) gl.glColor3d(0.5, 0.5, 0.5);
        
//        if(!polygon) 
//        {
//        	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
//        }
//        else 
//        {
//        	gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
//        }
        
        gl.glBegin(mode);
        if (polygon) gl.glColor3dv(cnx, 0); 
        gl.glVertex3dv(v0, 0);
        gl.glVertex3dv(v1, 0);
        gl.glVertex3dv(v2, 0);
        gl.glVertex3dv(v3, 0);
        gl.glEnd();

        gl.glBegin(mode);
        if (polygon) gl.glColor3dv(cpy, 0);
        gl.glVertex3dv(v3, 0);
        gl.glVertex3dv(v2, 0);
        gl.glVertex3dv(v6, 0);
        gl.glVertex3dv(v7, 0);
        gl.glEnd();

        gl.glBegin(mode);
        if (polygon) gl.glColor3dv(cpx, 0);
        gl.glVertex3dv(v7, 0);
        gl.glVertex3dv(v6, 0);
        gl.glVertex3dv(v5, 0);
        gl.glVertex3dv(v4, 0);
        gl.glEnd();

        gl.glBegin(mode);
        if (polygon) gl.glColor3dv(cny, 0);
        gl.glVertex3dv(v4, 0);
        gl.glVertex3dv(v5, 0);
        gl.glVertex3dv(v1, 0);
        gl.glVertex3dv(v0, 0);
        gl.glEnd();

        gl.glBegin(mode);
        if (polygon) gl.glColor3dv(cnz, 0);
        gl.glVertex3dv(v0, 0);
        gl.glVertex3dv(v3, 0);
        gl.glVertex3dv(v7, 0);
        gl.glVertex3dv(v4, 0);
        gl.glEnd();

        gl.glBegin(mode);
        if (polygon) gl.glColor3dv(cpz, 0);
        gl.glVertex3dv(v1, 0);
        gl.glVertex3dv(v5, 0);
        gl.glVertex3dv(v6, 0);
        gl.glVertex3dv(v2, 0);
        gl.glEnd();
    }
}
