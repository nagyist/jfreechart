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
 * -----------------------------
 * OHLCSeriesCollectionTest.java
 * -----------------------------
 * (C) Copyright 2006-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */

package org.jfree.data.time.ohlc;

import org.jfree.chart.TestUtils;
import org.jfree.chart.internal.CloneUtils;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.Year;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link OHLCSeriesCollection} class.
 */
public class OHLCSeriesCollectionTest implements DatasetChangeListener {

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    @Test
    public void testEquals() {
        OHLCSeriesCollection<String> c1 = new OHLCSeriesCollection<>();
        OHLCSeriesCollection<String> c2 = new OHLCSeriesCollection<>();
        assertEquals(c1, c2);

        // add a series
        OHLCSeries<String> s1 = new OHLCSeries<>("Series");
        s1.add(new Year(2006), 1.0, 1.1, 1.2, 1.3);
        c1.addSeries(s1);
        assertNotEquals(c1, c2);
        OHLCSeries<String> s2 = new OHLCSeries<>("Series");
        s2.add(new Year(2006), 1.0, 1.1, 1.2, 1.3);
        c2.addSeries(s2);
        assertEquals(c1, c2);

        // add an empty series
        c1.addSeries(new OHLCSeries<>("Empty Series"));
        assertNotEquals(c1, c2);
        c2.addSeries(new OHLCSeries<>("Empty Series"));
        assertEquals(c1, c2);

        c1.setXPosition(TimePeriodAnchor.END);
        assertNotEquals(c1, c2);
        c2.setXPosition(TimePeriodAnchor.END);
        assertEquals(c1, c2);

    }

    /**
     * Confirm that cloning works.
     */
    @Test
    public void testCloning() throws CloneNotSupportedException {
        OHLCSeriesCollection<String> c1 = new OHLCSeriesCollection<>();
        OHLCSeries<String> s1 = new OHLCSeries<>("Series");
        s1.add(new Year(2006), 1.0, 1.1, 1.2, 1.3);
        c1.addSeries(s1);
        OHLCSeriesCollection<String> c2 = CloneUtils.clone(c1);
        assertNotSame(c1, c2);
        assertSame(c1.getClass(), c2.getClass());
        assertEquals(c1, c2);

        // check independence
        c1.setXPosition(TimePeriodAnchor.END);
        assertNotEquals(c1, c2);
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        OHLCSeriesCollection<String> c1 = new OHLCSeriesCollection<>();
        OHLCSeries<String> s1 = new OHLCSeries<>("Series");
        s1.add(new Year(2006), 1.0, 1.1, 1.2, 1.3);
        c1.addSeries(s1);
        OHLCSeriesCollection<String> c2 = TestUtils.serialised(c1);
        assertEquals(c1, c2);
    }

    /**
     * A test for bug report 1170825 (originally affected XYSeriesCollection,
     * this test is just copied over).
     */
    @Test
    public void test1170825() {
        OHLCSeries<String> s1 = new OHLCSeries<>("Series1");
        OHLCSeriesCollection<String> dataset = new OHLCSeriesCollection<>();
        dataset.addSeries(s1);
        try {
            /* XYSeries s = */ dataset.getSeries(1);
        }
        catch (IllegalArgumentException e) {
            // correct outcome
        }
        catch (IndexOutOfBoundsException e) {
            fail();  // wrong outcome
        }
    }

    /**
     * Two objects that are equal are required to return the same hashCode.
     */
    @Test
    public void testHashcode() {
        OHLCSeriesCollection<String> c1 = new OHLCSeriesCollection<>();
        OHLCSeries<String> s1 = new OHLCSeries<>("S");
        s1.add(new Year(2009), 1.0, 4.0, 0.5, 2.0);
        c1.addSeries(s1);
        OHLCSeriesCollection<String> c2 = new OHLCSeriesCollection<>();
        OHLCSeries<String> s2 = new OHLCSeries<>("S");
        s2.add(new Year(2009), 1.0, 4.0, 0.5, 2.0);
        c2.addSeries(s2);
        assertEquals(c1, c2);
        int h1 = c1.hashCode();
        int h2 = c2.hashCode();
        assertEquals(h1, h2);
    }

    /**
     * Some checks for the {@link OHLCSeriesCollection#removeSeries(int)}
     * method.
     */
    @Test
    public void testRemoveSeries_int() {
        OHLCSeriesCollection<String> c1 = new OHLCSeriesCollection<>();
        OHLCSeries<String> s1 = new OHLCSeries<>("Series 1");
        OHLCSeries<String> s2 = new OHLCSeries<>("Series 2");
        OHLCSeries<String> s3 = new OHLCSeries<>("Series 3");
        OHLCSeries<String> s4 = new OHLCSeries<>("Series 4");
        c1.addSeries(s1);
        c1.addSeries(s2);
        c1.addSeries(s3);
        c1.addSeries(s4);
        c1.removeSeries(2);
        assertEquals(c1.getSeries(2), s4);
        c1.removeSeries(0);
        assertEquals(c1.getSeries(0), s2);
        assertEquals(2, c1.getSeriesCount());
    }

    /**
     * Some checks for the
     * {@link OHLCSeriesCollection#removeSeries(OHLCSeries)} method.
     */
    @Test
    public void testRemoveSeries() {
        OHLCSeriesCollection<String> c1 = new OHLCSeriesCollection<>();
        OHLCSeries<String> s1 = new OHLCSeries<>("Series 1");
        OHLCSeries<String> s2 = new OHLCSeries<>("Series 2");
        OHLCSeries<String> s3 = new OHLCSeries<>("Series 3");
        OHLCSeries<String> s4 = new OHLCSeries<>("Series 4");
        c1.addSeries(s1);
        c1.addSeries(s2);
        c1.addSeries(s3);
        c1.addSeries(s4);
        c1.removeSeries(s3);
        assertEquals(c1.getSeries(2), s4);
        c1.removeSeries(s1);
        assertEquals(c1.getSeries(0), s2);
        assertEquals(2, c1.getSeriesCount());
    }

    /**
     * A simple check for the removeAllSeries() method.
     */
    @Test
    public void testRemoveAllSeries() {
        OHLCSeriesCollection<String> c1 = new OHLCSeriesCollection<>();
        c1.addChangeListener(this);

        // there should be no change event when clearing an empty series
        this.lastEvent = null;
        c1.removeAllSeries();
        assertNull(this.lastEvent);

        OHLCSeries<String> s1 = new OHLCSeries<>("Series 1");
        OHLCSeries<String> s2 = new OHLCSeries<>("Series 2");
        c1.addSeries(s1);
        c1.addSeries(s2);
        c1.removeAllSeries();
        assertEquals(0, c1.getSeriesCount());
        assertNotNull(this.lastEvent);
        this.lastEvent = null;  // clean up
    }

    /** The last received event. */
    private DatasetChangeEvent lastEvent;

    /**
     * Receives dataset change events.
     *
     * @param event  the event.
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        this.lastEvent = event;
    }

}
