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
 * --------------
 * YInterval.java
 * --------------
 * (C) Copyright 2006-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */

package org.jfree.data.xy;

import java.io.Serializable;

/**
 * A y-interval.  This class is used internally by the
 * {@link YIntervalDataItem} class.
 */
public class YInterval implements Serializable {

    /** The y-value. */
    private double y;

    /** The lower bound of the y-interval. */
    private double yLow;

    /** The upper bound of the y-interval. */
    private double yHigh;

    /**
     * Creates a new instance of {@code YInterval}.
     *
     * @param y  the y-value.
     * @param yLow  the lower bound of the y-interval.
     * @param yHigh  the upper bound of the y-interval.
     */
    public YInterval(double y, double yLow, double yHigh) {
        this.y = y;
        this.yLow = yLow;
        this.yHigh = yHigh;
    }

    /**
     * Returns the y-value.
     *
     * @return The y-value.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Returns the lower bound of the y-interval.
     *
     * @return The lower bound of the y-interval.
     */
    public double getYLow() {
        return this.yLow;
    }

    /**
     * Returns the upper bound of the y-interval.
     *
     * @return The upper bound of the y-interval.
     */
    public double getYHigh() {
        return this.yHigh;
    }

    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof YInterval)) {
            return false;
        }
        YInterval that = (YInterval) obj;
        if (this.y != that.y) {
            return false;
        }
        if (this.yLow != that.yLow) {
            return false;
        }
        if (this.yHigh != that.yHigh) {
            return false;
        }
        return true;
    }

}
