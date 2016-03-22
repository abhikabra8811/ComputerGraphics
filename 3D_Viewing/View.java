/* class View
 * The main program -- construct the GUI and displays
 *
 * Doug DeCarlo
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class View extends JFrame
{
    // Swing OpenGL component classes
    static WorldView worldDraw;
    static CameraView cameraDraw;

    // Set to true to add the 'normalized coordinates' checkbox (extra credit)
    final boolean normalized_extension = false;

    // Flip this boolean value if the viewvolume isn't drawn correctly
    // when the program starts or after a resize
    final boolean camera_window_added_first = true;

    // Method to refresh entire display
    public static void refresh()
    {
        if (!SwingUtilities.isEventDispatchThread())
          return;

        cameraDraw.display();
        worldDraw.display();
    }

    // Construct GUI for a set of parameters
    public static void makeControls(Container controls,
                                    GridBagLayout controlLayout,
                                    GridBagConstraints controlCon,
                                    DoubleParameter[] param, 
                                    String title)
    {
        Container spec = new Container();
        controlLayout.setConstraints(spec, controlCon);
        controls.add(spec);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();

        spec.setLayout(layout);

        // Label column on bottom
        con.fill = GridBagConstraints.VERTICAL;
        con.anchor = GridBagConstraints.SOUTH;
        con.weightx = 0.0;
        con.weighty = 0.0;
        con.gridwidth = GridBagConstraints.REMAINDER;
        JLabel ltitle = new JLabel(title);
        layout.setConstraints(ltitle, con);
        spec.add(ltitle);

        for (int i = 0; i < param.length; i++) {
            // Make slider and text field
            JSlider val = new JSlider(JSlider.HORIZONTAL);
            JTextField tval = new JTextField(5);

            param[i].register(val);
            param[i].register(tval);

            // Lay out label, slider, text field
            con.fill = GridBagConstraints.NONE;
            con.weightx = 0.0;
            con.weighty = 0.0;
            con.gridwidth = 1;

            JLabel name = new JLabel(param[i].name);
            layout.setConstraints(name, con);
            spec.add(name);

            con.fill = GridBagConstraints.HORIZONTAL;
            con.weightx = 0.8;
            layout.setConstraints(val, con);
            spec.add(val);
            
            con.weightx = 0.2;
            con.gridwidth = GridBagConstraints.REMAINDER;
            layout.setConstraints(tval, con);
            spec.add(tval);
        }

        // Label column on bottom
        con.fill = GridBagConstraints.VERTICAL;
        con.anchor = GridBagConstraints.SOUTH;
        con.weightx = 0.0;
        con.weighty = 1.0;
        con.gridwidth = GridBagConstraints.REMAINDER;
	JLabel lspacing = new JLabel(" ");
        layout.setConstraints(lspacing, con);
        spec.add(lspacing);
    }

    // Main program -- create and start GUI
    public View(boolean debug, double paramd[])
    {
        // Create parameters
        World.init(paramd, 0);
        Camera.init(paramd, 8);
        Obj.init(paramd, 8+9);

        // Create drawing areas
        worldDraw = new WorldView(this, debug);
        cameraDraw = new CameraView(this, debug);
        worldDraw.setSize(400, 400);
        cameraDraw.setSize(400, 400);

        // Create menubar
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);

        JMenu menu = new JMenu("File");
        // Ensure popup menu is drawn in front of OpenGL window
        // (by not using a lightweight window for the menu)
        menu.getPopupMenu().setLightWeightPopupEnabled(false);
        menubar.add(menu);

        // Reset all values
        JMenuItem resetm = menu.add("Reset");
        resetm.addActionListener(
            new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Parameter.blockAction(true);

                    // Reset all parameters
                    World.reset();
                    Camera.reset();
                    Obj.reset();

                    Parameter.blockAction(false);

                    Parameter.onUserAction();
                }
            });


        // Exit when quit selected
        JMenuItem quitm = menu.add("Quit");
        quitm.addActionListener(
            new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    System.exit(0);
                }
            });
        
        // ------------------------------------------------------
        
        // Lay out main window
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();

        Container c = getContentPane();
        c.setLayout(layout);

        con.weightx = 1.0;
        con.weighty = 1.0;
        con.fill = GridBagConstraints.BOTH;

	//----BUG (fixable in a platform dependent way by setting camera_window_added_first up top)
        // cameraDraw should be redrawn before worldDraw - and this order is based on the order things
        // are added to the container, and some combination of the JDK version and platform.  There is
	// presumably a way of controlling this, but no time to figure this out now. :(
        if (camera_window_added_first) {
            // Camera view(right) should be added before world view(left)
            c.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            
            // Right viewport: camera view
            con.gridwidth = 1;
            con.insets  = new Insets(4,4,4,2);
            layout.setConstraints(cameraDraw, con);
            c.add(cameraDraw);
            
            // Left viewport: world view
            con.gridwidth = GridBagConstraints.REMAINDER;
            con.insets  = new Insets(4,2,4,4);
            layout.setConstraints(worldDraw, con);
            c.add(worldDraw);
        } else {
            // World view (left) should be added before camera view(right)
            c.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

            // Left viewport: world view
            con.gridwidth = 1;
            con.insets  = new Insets(4,4,4,2);
            layout.setConstraints(worldDraw, con);
            c.add(worldDraw);

            // Right viewport: camera view
            con.gridwidth = GridBagConstraints.REMAINDER;
            con.insets  = new Insets(4,2,4,4);
            layout.setConstraints(cameraDraw, con);
            c.add(cameraDraw);
        }

        // Labels
        con.fill = GridBagConstraints.NONE;
        con.anchor = GridBagConstraints.CENTER;
        con.weightx = 0.0;
        con.weighty = 0.0;
        con.insets  = new Insets(0,0,0,0);

        JLabel name = new JLabel("Camera view");
        con.gridwidth = 1;
        layout.setConstraints(name, con);
        c.add(name);

        name = new JLabel("World view");
        con.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(name, con);
        c.add(name);

        // Checkboxes
        con.fill = GridBagConstraints.NONE;
        con.anchor = GridBagConstraints.CENTER;

        JCheckBox clip = new JCheckBox("Clipped");
        con.gridwidth = 1;
        layout.setConstraints(clip, con);
        c.add(clip);
        World.clipped().register(clip);

	if (normalized_extension) {
	    JCheckBox norm = new JCheckBox("Normalized Coords");
	    layout.setConstraints(norm, con);
	    c.add(norm);
	    World.normalized().register(norm);
	}

        JCheckBox persp = new JCheckBox("Perspective");
        con.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(persp, con);
        c.add(persp);
        Camera.perspective().register(persp);
        
        // -- Parameter controls

        // Make container for controls
        Container cc = new Container();

        con.gridwidth = GridBagConstraints.REMAINDER;
        con.weightx = 1.0;
        con.weighty = 0.5;
        con.fill = GridBagConstraints.BOTH;
        
        layout.setConstraints(cc, con);
        c.add(cc);

        // Make 3 compartments
        GridBagLayout clayout = new GridBagLayout();
        cc.setLayout(clayout);
        GridBagConstraints ccon = new GridBagConstraints();

        ccon.weightx = 1.0;
        ccon.weighty = 1.0;
        ccon.anchor = GridBagConstraints.NORTH;
        ccon.insets = new Insets(10, 10, 0, 10);
        ccon.fill = GridBagConstraints.BOTH;

        ccon.gridwidth = 1;
        makeControls(cc, clayout, ccon, World.params(),  "World parameters");
        makeControls(cc, clayout, ccon, Camera.params(), "Camera parameters");

        ccon.gridwidth = GridBagConstraints.REMAINDER;
        makeControls(cc, clayout, ccon, Obj.params(),    "Object parameters");

        // ------------------------------------------------------

        // Exit when window closes
        addWindowListener(
            new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    System.exit(0);
                }
            });

        // Placement of window on screen
        setLocation(100, 50);

        pack();
        setVisible(true);
    }
    
    public static void main(String args[])
    {
        boolean debug = false;
        double paramd[] = null;

        // Parse command-line arguments
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-help")) {
                    System.out.println("Usage: java View [-debug]");
                } else if (args[i].equals("-debug")) {
                    debug = true;
                } else if (args[i].equals("-param") && i+1 < args.length) {
                    i++;
                    String params[] = args[i].split(":");
                    paramd = new double[params.length];

                    for (int j = 0; j < params.length; j++) {
                        paramd[j] = Double.valueOf(params[j]).doubleValue();
                    }

                    // -- Specify parameters on command line: for testing only
                    // Default:
                    //  java View -param 0:0:-10:0:0:0:0:0:0:0:-5:0:0:0:2:20:1:0:0:0:0:0:0:1
                    // Example:
                    //  java View -param 0:0.7:-10:0:-25:5:1:0:0:0:-5:0:-12:20:2:15:1:0:0:0:45:-25:-20:3
                } else {
                    throw new Exception("Illegal argument: " + args[i]);
                }
            }
        } catch (final Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

        View m = new View(debug, paramd);
    }
}    
