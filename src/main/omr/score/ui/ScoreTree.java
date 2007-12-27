//----------------------------------------------------------------------------//
//                                                                            //
//                             S c o r e T r e e                              //
//                                                                            //
//  Copyright (C) Herve Bitteur 2000-2007. All rights reserved.               //
//  This software is released under the GNU General Public License.           //
//  Contact author at herve.bitteur@laposte.net to report bugs & suggestions. //
//----------------------------------------------------------------------------//
//
package omr.score.ui;

import omr.constant.Constant;
import omr.constant.ConstantSet;

import omr.score.Score;
import omr.score.entity.ScoreNode;

import omr.util.Dumper;
import omr.util.Implement;
import omr.util.TreeNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * Class <code>ScoreTree</code> provides a user interface (a frame) where the
 * whole score hierarchy can be browsed as a tree.
 *
 * @author Herv&eacute; Bitteur
 * @version $Id$
 */
public class ScoreTree
{
    //~ Static fields/initializers ---------------------------------------------

    /** Specific application parameters */
    private static final Constants constants = new Constants();

    /** Default window height in pixels */
    private static final int WINDOW_HEIGHT = 700;

    /** Default width in pixels for the left part (the tree) */
    private static final int LEFT_WIDTH = 400;

    /** Default width in pixels for the right part (the detail) */
    private static final int RIGHT_WIDTH = 500;

    /** Default windows width in pixels */
    private static final int WINDOW_WIDTH = LEFT_WIDTH + RIGHT_WIDTH;

    //~ Instance fields --------------------------------------------------------

    /** Concrete UI component */
    private JPanel component;

    //~ Constructors -----------------------------------------------------------

    //-----------//
    // ScoreTree //
    //-----------//
    private ScoreTree (Score score)
    {
        component = new JPanel();

        // Set up the tree
        JTree       tree = new JTree(new Adapter(score));

        // Build left-side view
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(new Dimension(LEFT_WIDTH, WINDOW_HEIGHT));

        // Build right-side view
        final JEditorPane htmlPane = new JEditorPane("text/html", "");
        htmlPane.setEditable(false);

        JScrollPane htmlView = new JScrollPane(htmlPane);
        htmlView.setPreferredSize(new Dimension(RIGHT_WIDTH, WINDOW_HEIGHT));

        // Allow only single selections
        tree.getSelectionModel()
            .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Display lines to explicit relationships
        tree.putClientProperty("JTree.lineStyle", "Angled");

        // Wire the two views together. Use a selection listener
        // created with an anonymous inner-class adapter.
        // Listen for when the selection changes.
        tree.addTreeSelectionListener(
            new TreeSelectionListener() {
                    public void valueChanged (TreeSelectionEvent e)
                    {
                        TreePath p = e.getNewLeadSelectionPath();

                        if (p != null) {
                            ScoreNode node = (ScoreNode) p.getLastPathComponent();
                            htmlPane.setText(Dumper.htmlDumpOf(node));
                        }
                    }
                });

        // Build split-pane view
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            treeView,
            htmlView);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerLocation(LEFT_WIDTH);
        splitPane.setBorder(null);
        splitPane.setDividerSize(2);
        splitPane.setPreferredSize(
            new Dimension(WINDOW_WIDTH + 10, WINDOW_HEIGHT + 10));

        // Add GUI components
        component.setLayout(new BorderLayout());
        component.add("Center", splitPane);
    }

    //~ Methods ----------------------------------------------------------------

    //     //------//
    //     // main //
    //     //------//
    //     /**
    //      * This class can be used in stand-alone, to browse a score specified
    //      * in the command line
    //      *
    //      * @param argv only one argument : the name of the score XML file
    //      */
    //     public static void main (String[] argv)
    //     {
    //         // Global OMR properties
    //         //Constant.loadResource ("/User.properties");
    //         // Load score from an XML file
    //         Score score = ScoreManager.getInstance().load(new File(argv[0]));

    //         // Build the display frame
    //         JFrame frame = makeFrame(argv[0], score);
    //         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     }

    //-----------//
    // makeFrame //
    //-----------//
    /**
     * Create a frame for the score tree
     *
     * @param name  the score name
     * @param score the score entity
     *
     * @return the created frame
     */
    public static JFrame makeFrame (String name,
                                    Score  score)
    {
        // Set up a GUI framework
        JFrame          frame = new JFrame("Tree of " + name);

        // Set up the tree, the views, and display it all
        final ScoreTree scoreTree = new ScoreTree(score);
        frame.getContentPane()
             .add("Center", scoreTree.component);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.pack();
        frame.setLocation(100, 100);
        frame.setVisible(true);

        return frame;
    }

    //~ Inner Classes ----------------------------------------------------------

    // This adapter converts the current Score into a JTree model.
    private static class Adapter
        implements TreeModel
    {
        //~ Instance fields ----------------------------------------------------

        private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
        private Score                   score;

        //~ Constructors -------------------------------------------------------

        //---------//
        // Adapter //
        //---------//
        public Adapter (Score score)
        {
            this.score = score;
        }

        //~ Methods ------------------------------------------------------------

        //----------//
        // getChild //
        //----------//
        @Implement(TreeModel.class)
        public Object getChild (Object parent,
                                int    index)
        {
            return (ScoreNode) getRelevantChildren(parent)
                                   .get(index);
        }

        //---------------//
        // getChildCount //
        //---------------//
        @Implement(TreeModel.class)
        public int getChildCount (Object parent)
        {
            return getRelevantChildren(parent)
                       .size();
        }

        //-----------------//
        // getIndexOfChild //
        //-----------------//
        @Implement(TreeModel.class)
        public int getIndexOfChild (Object parent,
                                    Object child)
        {
            return getRelevantChildren(parent)
                       .indexOf(child);
        }

        //--------//
        // isLeaf //
        //--------//
        @Implement(TreeModel.class)
        public boolean isLeaf (Object node)
        {
            // Determines whether the icon shows up to the left.
            // Return true for any node with no children
            ScoreNode scoreNode = (ScoreNode) node;

            return getChildCount(scoreNode) == 0;
        }

        //---------//
        // getRoot //
        //---------//
        @Implement(TreeModel.class)
        public Object getRoot ()
        {
            return score;
        }

        /*
         * Use these methods to add and remove event listeners.
         * (Needed to satisfy TreeModel interface, but not used.)
         */

        //----------------------//
        // addTreeModelListener //
        //----------------------//
        @Implement(TreeModel.class)
        public void addTreeModelListener (TreeModelListener listener)
        {
            if ((listener != null) && !listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        //-------------------------//
        // removeTreeModelListener //
        //-------------------------//
        @Implement(TreeModel.class)
        public void removeTreeModelListener (TreeModelListener listener)
        {
            if (listener != null) {
                listeners.remove(listener);
            }
        }

        //---------------------//
        // valueForPathChanged //
        //---------------------//
        @Implement(TreeModel.class)
        public void valueForPathChanged (TreePath path,
                                         Object   newValue)
        {
            // Null. We won't be making changes in the GUI.  If we did, we would
            // ensure the new value was really new and then fire a
            // TreeNodesChanged event.
        }

        //------------//
        // isRelevant //
        //------------//
        private boolean isRelevant (Object node)
        {
            // We display dummy containers only when they are not empty
            if (constants.hideEmptyDummies.getValue() &&
                (node.getClass().getDeclaredFields().length == 0)) {
                ScoreNode scoreNode = (ScoreNode) node;

                return scoreNode.getChildren()
                                .size() > 0;
            } else {
                return true;
            }
        }

        //---------------------//
        // getRelevantChildren //
        //---------------------//
        private List<TreeNode> getRelevantChildren (Object node)
        {
            List<TreeNode> relevantChildren = new ArrayList<TreeNode>();
            ScoreNode      scoreNode = (ScoreNode) node;

            for (TreeNode n : scoreNode.getChildren()) {
                if (isRelevant(n)) {
                    relevantChildren.add(n);
                }
            }

            return relevantChildren;
        }
    }

    //-----------//
    // Constants //
    //-----------//
    private static final class Constants
        extends ConstantSet
    {
        //~ Instance fields ----------------------------------------------------

        /** Should we hide empty dummy containers */
        Constant.Boolean hideEmptyDummies = new Constant.Boolean(
            true,
            "Should we hide empty dummy containers");
    }
}