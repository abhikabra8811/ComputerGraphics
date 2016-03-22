/* class Rock
 * Represents a rock using a rectangular grid; given a particular level
 * of subdivision l, the rock will be a 2^l+1 X 2^l+1 height field
 * The rock is drawn a little below the surface to get a rough edge.
 *
 * Doug DeCarlo
 */

import java.util.*;

import javax.media.opengl.GL;
import javax.vecmath.*;


class Rock implements Obstacle
{
    // Location of rock
    double xpos, ypos, scale;

    // -- Rock mesh: a height-field of rsize X rsize vertices
    int rsize;
    // Height field: z values
    private double[][] height;
    // Whether height value has been set (locked) already
    private boolean[][] locked;

    // Random number generator
    Random rgen;

    // ---------------------------------------------------------------

    public Rock(Random randGen, int level, 
		double xPosition, double yPosition, double scaling)
    {
        // Grid size of (2^level + 1)
        rsize = (1 << level) + 1;

        // Height field -- initially all zeros
        height = new double[rsize][rsize];
        locked = new boolean[rsize][rsize];
 
        rgen = randGen;

	// Set rock position in the world
	xpos = xPosition;
	ypos = yPosition;
	scale = scaling;

	compute();
    }

    // ----------------------------------------------------------------
    // Obstacle methods

    // Get rock location (as a scene element)
    public Point3d getLocation()
    {
	return new Point3d(xpos, ypos, 0);
    }

    // Draw rock in scene
    public void draw(GL gl)
    {
	gl.glPushMatrix();

        // Translate rock down (so it has an interesting boundary)
	gl.glTranslated(xpos, ypos, -0.15);

	gl.glScaled(scale, scale, scale);

        gl.glColor3d(0.6, 0.6, 0.6);

        // Create these outside the loops, so objects persist and
        // unnecessary GC is avoided
        Point3d p = new Point3d();
        Vector3d n = new Vector3d();

        // Draw polygon grid of rock as quad-strips
        for (int i = 0; i < rsize-1; i++) {
            gl.glBegin(GL.GL_QUAD_STRIP);
            for (int j = 0; j < rsize; j++) {
                getRockPoint(i, j, p);
                getRockNormal(i, j, n);
                gl.glNormal3d(n.x, n.y, n.z);
                gl.glVertex3d(p.x, p.y, p.z);
                
                getRockPoint(i+1, j, p);
                getRockNormal(i+1, j, n);
                gl.glNormal3d(n.x, n.y, n.z);
                gl.glVertex3d(p.x, p.y, p.z);
            }
            gl.glEnd();
        }

        // Make GC easy
        p = null;
        n = null;
    
	gl.glPopMatrix();
    }
    
    // ---------------------------------------------------------------

    // Point (i,j) on the rock -- point p gets filled in
    public void getRockPoint(int i, int j, Point3d p)
    {
        // Rock (x,y) locations are on the grid [-0.5, 0.5] x [-0.5, 0.5]
        p.x = (double)i / (rsize-1) - 0.5;
        p.y = (double)j / (rsize-1) - 0.5;
        // Rock z comes from height field
        p.z = height[i][j];
    }

    // Normal vector (i,j) on the rock -- vector n gets filled in
    public void getRockNormal(int i, int j, Vector3d n)
    {
        // This is the formula for a normal vector of a height field with
        // regularly spaced x and y values (assuming rock is zero on
        // its borders and outside of it too)

        // X component is zleft - zright (respecting boundaries)
        n.x = height[(i == 0) ? i : i-1][j] - 
              height[(i == rsize-1) ? i : i+1][j];

        // Y component is zbottom - ztop (respecting boundaries)
        n.y = height[i][(j == 0) ? j : j-1] - 
              height[i][(j == rsize-1) ? j : j+1];

        // Z component is twice the separation
        n.z = 2 / (rsize-1);

        n.normalize();
    }

    // ---------------------------------------------------------------

    // Compute the geometry of the rock
    // (called when the rock is created)
    public void compute()
    {
	// Initialize mesh
	for (int i = 0; i < rsize; i++) {
            for (int j = 0; j < rsize; j++) {
                height[i][j] = 0;
            	
                // Lock sides...
                locked[i][j] = (i == 0 || i == rsize-1 ||
                                j == 0 || j == rsize-1);
            }
	}

        // Raise the middle point and lock it there
        height[rsize/2][rsize/2] = 0.6; //golden ratio
        locked[rsize/2][rsize/2] = true;
        
        // Recursively compute fractal structure
        computeFractal(rsize,0, rsize-1,0, rsize-1, 1);
    }

    // Recursively compute fractal rock geometry
    private void computeFractal(int currentSize, int lowIndexRow, int highIndexRow, int lowIndexCol, int highIndexCol, int n)
    {
        // ...   	
    	int midIndexRow = (highIndexRow + lowIndexRow)/2;
    	int midIndexCol = (highIndexCol + lowIndexCol)/2;
    	
    	n++;
    	performDiamondStep(lowIndexRow, midIndexRow,lowIndexCol, midIndexCol, n);
    	performDiamondStep(lowIndexRow,midIndexRow,midIndexCol,highIndexCol, n);
    	performDiamondStep(midIndexRow, highIndexRow,lowIndexCol, midIndexCol, n);
    	performDiamondStep(midIndexRow,highIndexRow, midIndexCol, highIndexCol, n);
    	
    	
    	performSquareStep(currentSize/2, (lowIndexRow + midIndexRow)/2, (lowIndexCol+midIndexCol)/2, n);
    	performSquareStep(currentSize/2, (lowIndexRow + midIndexRow)/2, (midIndexCol + highIndexCol)/2, n);
    	performSquareStep(currentSize/2, (midIndexRow + highIndexRow)/2, (lowIndexCol + midIndexCol)/2, n);
    	performSquareStep(currentSize/2, (midIndexRow + highIndexRow)/2, (midIndexCol + highIndexCol)/2, n);
       	
    	if(currentSize <= 1) return;
    	computeFractal(currentSize/2, lowIndexRow, midIndexRow,lowIndexCol, midIndexCol, n);
    	computeFractal(currentSize/2, lowIndexRow,midIndexRow,midIndexCol,highIndexCol, n);
    	computeFractal(currentSize/2, midIndexRow, highIndexRow,lowIndexCol, midIndexCol, n);
    	computeFractal(currentSize/2, midIndexRow,highIndexRow, midIndexCol, highIndexCol, n);
        // Write this entire method, adding arguments when necessary

        // ...
    }
    
    void performDiamondStep(int lowIndexRow, int highIndexRow, int lowIndexCol, int highIndexCol, int n){
    	int midIndexRow = (highIndexRow + lowIndexRow)/2;
    	int midIndexCol = (highIndexCol + lowIndexCol)/2;    	
    	
    	double variance = 0.5 * Math.pow(1.0 / 2.5, n);
    	double rand = rgen.nextGaussian() * variance;
    	//perform diamond step
    	if(!locked[midIndexRow][midIndexCol])
    	{
    		double topLeft = height[lowIndexRow][lowIndexCol];
    		double topRight = height[lowIndexRow][highIndexCol];
    		double bottomLeft = height[highIndexRow][lowIndexCol];
    		double bottomRight = height[highIndexRow][highIndexCol];
    		height[midIndexRow][midIndexCol] += (topLeft + topRight + bottomLeft + bottomRight) / 4;
    		
    		height[midIndexRow][midIndexCol] += rand;
    		locked[midIndexRow][midIndexCol] = true;
    	}
    	
    }
    
    void performSquareStep(int currentSize,int diamondPtRow, int diamondPtCol, int level){
    	//leftPt
    	computeHeightSquareStep(currentSize,diamondPtRow , diamondPtCol -(currentSize/2), level);
    	//top point
    	computeHeightSquareStep(currentSize,diamondPtRow -(currentSize/2), diamondPtCol, level);
    	//right point
    	computeHeightSquareStep(currentSize,diamondPtRow , diamondPtCol + (currentSize/2), level);
    	//bottom point
    	computeHeightSquareStep(currentSize,diamondPtRow + (currentSize/2), diamondPtCol, level);
    }
    
    //ptNumber = 1 --> Left point
    //ptNumber = 2 --> Top point
    //ptNumber = 3 --> Right
    //ptNumber = 4 --> Bottom
    void computeHeightSquareStep(int currentSize ,int ptRow, int ptCol, int level){
    	
    	
    	if(currentSize <= 1)
    		return;
    	

    	int n = 0;
    	double avg = 0.0;
    	int leftPtRow = ptRow;
    	int leftPtCol = ptCol - (currentSize/2);
    	int topPtRow =  ptRow - (currentSize/2);
    	int topPtCol = ptCol;
    	int rightPtRow = ptRow;
    	int rightPtCol = ptCol + (currentSize /2);
    	int bottomPtRow = ptRow + (currentSize / 2);
    	int bottmPtCol = ptCol;
    	
    	if(leftPtCol >= 0 && locked[leftPtRow][leftPtCol])
    	{
    		avg += height[leftPtRow][leftPtCol];
    		n++;
    	}
    	
    	if(topPtRow >= 0 && locked[topPtRow][topPtCol])
    	{
    		avg += height[topPtRow][topPtCol];
    		n++;
    	}
    	
    	if(rightPtCol <rsize && locked[rightPtRow][rightPtCol])
    	{
    		avg += height[rightPtRow][rightPtCol];
    		n++;
    	}
  
    	if(bottomPtRow < rsize && locked[bottomPtRow][bottmPtCol])
    	{
    		avg += height[bottomPtRow][bottmPtCol];
    		n++;
    	}
    	
    	double variance = 0.5* Math.pow(1.0 / 2.5, level);
    	double rand = rgen.nextGaussian() * variance;
    	avg /= n;
    	avg += rand;
    	if(!locked[ptRow][ptCol] && !Double.isNaN(avg))
    	{
    		height[ptRow][ptCol] = avg; 
    		locked[ptRow][ptCol]  = true;
    	}
    	   	   			
    }
}
