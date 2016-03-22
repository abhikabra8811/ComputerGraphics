/* class TreePart
 * Class for representing a subtree, describing the size of the part at
 * the transformation to get to this subtree from the parent, the
 * current tree node (length and width) and whether this is a leaf node
 *
 * Doug DeCarlo
 */

import java.nio.DoubleBuffer;
import java.util.*;

import javax.media.opengl.GL;
import javax.vecmath.*;

class TreePart
{
    // Transformation for this branch/leaf (relative to its parent)

    // ...
    // ... specify angles and translations to say how parts of the
    // ... tree are positions with respect to each other
    // ...
	
	// Tz, Rx, Ry, Rz
	double Tz, Rx, Ry, Rz;

	Random rgen;
	
    // Leaf or trunk
    boolean leaf;

    // Size of part
    double length, width;

    // Children
    TreePart[] parts;

    // ---------------------------------------------------------------

    // Constructor: recursively construct a treepart of a particular depth,
    // with specified branching factor, dimensions and transformation
    public TreePart(Random rgen,
		    int depth, int numBranch,
		    double partLen, double partWid,
                    double tz, double rz, double ry, double rx
                    )
    {
        // **** placeholder -- remove this when you start working on the tree
    	this.rgen = rgen;
        this.leaf = false;
        this.length = partLen;
        this.width = partWid;

        // **** end of placeholder
    	this.Tz = tz; this.Rx = rx; this.Ry = ry; this.Rz = rz;
        if(depth == 0){
        	this.leaf = true;
        }
        else{
        	parts = new TreePart[numBranch];
        	for(int iBranch = 0; iBranch < numBranch; iBranch++){
        		if(depth <= 0){
        			return;
        		}
        		else{
        			double rotX = getRandomAngle(0, 30);
        			double rotY = getRandomAngle(0, 70);
        			double rotZ = getRandomAngle(0, 70);
        			if(iBranch % 2 == 0){
        				rotX = -rotX; rotY = -rotY;
        			}
            		parts[iBranch] = new TreePart(rgen, depth-1, getRandomNumOfBranhes(), getRandomLength(), getRandomWidth(),getRandomZTranslation(), rotZ,rotY,rotX);
        		}
        	}
        }
        

        // ... Create branch or leaf (based on depth) and create children
        // ... branches/leaves recursively
    }

    // Recursively draw a tree component
    //  - place the component using transformation for this subtree
    //  - draw leaf (if this is a leaf node)
    //  - draw subtree (if this is an interior node)
    //    (draw this component, recursively draw children)
    public void draw(GL gl)
    {
	gl.glPushMatrix();

	// Place this component
        // ...
        // ... (apply transformation for this component)
	gl.glTranslated(0.0, 0.0, Tz);
	gl.glRotated(Rz, 0, 0, 1);
	gl.glRotated(Ry,0, 1, 0);
	gl.glRotated(Rx, 1, 0, 0);

	if (leaf) {
			double[] currentColor = new double[4];
			gl.glGetDoublev(GL.GL_CURRENT_COLOR, currentColor, 0);
			gl.glScaled(0.15, 0.15, 0.15);
            // Draw leaf
			gl.glColor3d(0.13, 0.75, 0.259);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3d(0.0, 0.0,0.0);
			gl.glVertex3d(0.0, 0.33, -0.33);
			gl.glVertex3d(0.0, 0.66,-0.33);
			gl.glVertex3d(0.0, 1.0,0.0);
			gl.glVertex3d(0.0, 0.66,0.33);
			gl.glVertex3d(0.0, 0.33,0.33);
			gl.glEnd();
			gl.glScaled(1/0.15, 1/0.15, 1/0.15);
			gl.glColor3d(currentColor[0], currentColor[1], currentColor[2]);
            // ...
	} else {
            // Draw branch
			//
            // ... (transformation for cylinder)
			
			gl.glScaled(this.width, this.width,length);
			Objs.cylinder(gl);			
			gl.glScaled(1/width, 1/width,1/length);
			// Recursively draw children
            // ...
            if(parts != null){
            	for(int iChild = 0; iChild < parts.length; iChild++){
                	parts[iChild].draw(gl);
                }
            }
            
	}
	
	gl.glPopMatrix();
    }
    
    double getRandomAngle(double rmin, double rmax){
    	return (rmin + rmax * rgen.nextDouble());
    }
    
    double getRandomZTranslation(){
    	return ((0.7 * length) + (0.5 * rgen.nextDouble() * (0.3 * length))); 	
    }
    
    int getRandomNumOfBranhes(){
    	return (4 + rgen.nextInt(3));
    }
    
    double getRandomLength(){
    	return ((0.6 * length) + (0.5 * rgen.nextGaussian() * (0.1 * length)));
    }
    
    double getRandomWidth(){
    	return ((0.4 * width) + (0.5 * rgen.nextGaussian() * (0.1 * width)));
    }
}
