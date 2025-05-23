/* ======================================================
 * JFreeChart : a chart library for the Java(tm) platform
 * ======================================================
 *
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Project Info:  https://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -----------------
 * CategoryTick.java
 * -----------------
 * (C) Copyright 2003-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */

package org.jfree.chart.axis;

import org.jfree.chart.text.TextBlock;
import org.jfree.chart.text.TextBlockAnchor;
import org.jfree.chart.text.TextAnchor;

import java.util.Objects;

/**
 * A tick for a {@link CategoryAxis}.
 */
public class CategoryTick extends Tick {

    /** The category. */
    private Comparable<?> category;

    /** The label. */
    private TextBlock label;

    /** The label anchor. */
    private TextBlockAnchor labelAnchor;

    /**
     * Creates a new tick.
     *
     * @param category  the category.
     * @param label  the label.
     * @param labelAnchor  the label anchor.
     * @param rotationAnchor  the rotation anchor.
     * @param angle  the rotation angle (in radians).
     */
    public CategoryTick(Comparable<?> category, TextBlock label,
            TextBlockAnchor labelAnchor, TextAnchor rotationAnchor, 
            double angle) {

        super("", TextAnchor.CENTER, rotationAnchor, angle);
        this.category = category;
        this.label = label;
        this.labelAnchor = labelAnchor;

    }

    /**
     * Returns the category.
     *
     * @return The category.
     */
    public Comparable<?> getCategory() {
        return this.category;
    }

    /**
     * Returns the label.
     *
     * @return The label.
     */
    public TextBlock getLabel() {
        return this.label;
    }

    /**
     * Returns the label anchor.
     *
     * @return The label anchor.
     */
    public TextBlockAnchor getLabelAnchor() {
        return this.labelAnchor;
    }

    /**
     * Tests this category tick for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CategoryTick && super.equals(obj)) {
            CategoryTick that = (CategoryTick) obj;
            if (!Objects.equals(this.category, that.category)) {
                return false;
            }
            if (!Objects.equals(this.label, that.label)) {
                return false;
            }
            if (!Objects.equals(this.labelAnchor, that.labelAnchor)) {
                return false;
           }
            return true;
        }
        return false;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = 41;
        result = 37 * result + this.category.hashCode();
        result = 37 * result + this.label.hashCode();
        result = 37 * result + this.labelAnchor.hashCode();
        return result;
    }
}
