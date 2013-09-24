//----------------------------------------------------------------------------//
//                                                                            //
//                        I n t e r p r e t a t i o n                         //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">                          //
//  Copyright © Herve Bitteur and others 2000-2013. All rights reserved.      //
//  This software is released under the GNU General Public License.           //
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.   //
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.sig;

import omr.glyph.Shape;
import omr.glyph.facets.Glyph;

import java.awt.Rectangle;
import java.util.Comparator;

/**
 * Interface {@code Interpretation} defines a possible interpretation.
 *
 * @author Hervé Bitteur
 */
public interface Inter
        extends VisitableInter
{
    //~ Static fields/initializers ---------------------------------------------

    /**
     * For comparing interpretations by abscissa.
     */
    public static final Comparator<Inter> byAbscissa = new Comparator<Inter>()
    {
        @Override
        public int compare (Inter i1,
                            Inter i2)
        {
            return Integer.compare(i1.getBounds().x, i2.getBounds().x);
        }
    };

    /**
     * For comparing interpretations by ordinate.
     */
    public static final Comparator<Inter> byOrdinate = new Comparator<Inter>()
    {
        @Override
        public int compare (Inter i1,
                            Inter i2)
        {
            return Integer.compare(i1.getBounds().y, i2.getBounds().y);
        }
    };

    //~ Methods ----------------------------------------------------------------
    /**
     * Report the bounding box for this interpretation.
     *
     * @return the bounding box
     */
    Rectangle getBounds ();

    /**
     * Details for tip.
     *
     * @return infos for a tip
     */
    String getDetails ();

    /**
     * Report the glyph, if any, which is concerned by this interpretation.
     *
     * @return the underlying glyph, or null
     */
    Glyph getGlyph ();

    /**
     * Report the grade (0..1 probability) assigned to interpretation
     *
     * @return the grade
     */
    double getGrade ();

    /**
     * Report the shape related to interpretation.
     *
     * @return the shape
     */
    Shape getShape ();

    /**
     * Report the sig which hosts this interpretation.
     *
     * @return the containing sig
     */
    SIGraph getSig ();

    /**
     * Report whether the interpretation has a good grade.
     *
     * @return true if grade is good
     */
    boolean isGood ();

    /**
     * Report whether this interpretation represents the same thing
     * as that interpretation
     *
     * @param that the other inter to check
     * @return true if identical, false otherwise
     */
    boolean isSameAs (Inter that);

    /**
     * Assign the bounding box for this interpretation.
     * The assigned bounds may be different from the underlying glyph bounds.
     *
     * @param box the bounding box
     */
    void setBounds (Rectangle box);

    /**
     * Assign the interpretation quality value.
     *
     * @param grade the grade to set
     */
    void setGrade (double grade);

    /**
     * Assign the containing SIG
     *
     * @param sig the containing SIG
     */
    void setSig (SIGraph sig);
}