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
 * ---------------------
 * CrosshairOverlay.java
 * ---------------------
 * (C) Copyright 2011-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   John Matthews, Michal Wozniak;
 *
 */

package org.jfree.chart.swing;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.api.RectangleInsets;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.api.RectangleAnchor;
import org.jfree.chart.api.RectangleEdge;
import org.jfree.chart.text.TextAnchor;
import org.jfree.chart.internal.CloneUtils;
import org.jfree.chart.internal.Args;
import org.jfree.chart.api.PublicCloneable;

/**
 * An overlay for a {@link ChartPanel} that draws crosshairs on a chart.  If 
 * you are using the JavaFX extensions for JFreeChart, then you should use
 * the {@code CrosshairOverlayFX} class.
 */
public class CrosshairOverlay extends AbstractOverlay implements Overlay,
        PropertyChangeListener, PublicCloneable, Cloneable, Serializable {

    /** Storage for the crosshairs along the x-axis. */
    protected List<Crosshair> xCrosshairs;

    /** Storage for the crosshairs along the y-axis. */
    protected List<Crosshair> yCrosshairs;

    /**
     * Creates a new overlay that initially contains no crosshairs.
     */
    public CrosshairOverlay() {
        super();
        this.xCrosshairs = new ArrayList<>();
        this.yCrosshairs = new ArrayList<>();
    }

    /**
     * Adds a crosshair against the domain axis (x-axis) and sends an
     * {@link OverlayChangeEvent} to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #removeDomainCrosshair(org.jfree.chart.plot.Crosshair)
     * @see #addRangeCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void addDomainCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        this.xCrosshairs.add(crosshair);
        crosshair.addPropertyChangeListener(this);
        fireOverlayChanged();
    }

    /**
     * Removes a domain axis crosshair and sends an {@link OverlayChangeEvent}
     * to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #addDomainCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void removeDomainCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        if (this.xCrosshairs.remove(crosshair)) {
            crosshair.removePropertyChangeListener(this);
            fireOverlayChanged();
        }
    }

    /**
     * Clears all the domain crosshairs from the overlay and sends an
     * {@link OverlayChangeEvent} to all registered listeners (unless there
     * were no crosshairs to begin with).
     */
    public void clearDomainCrosshairs() {
        if (this.xCrosshairs.isEmpty()) {
            return;  // nothing to do - avoids firing change event
        }
        for (Crosshair c : getDomainCrosshairs()) {
            this.xCrosshairs.remove(c);
            c.removePropertyChangeListener(this);
        }
        fireOverlayChanged();
    }

    /**
     * Returns a new list containing the domain crosshairs for this overlay.
     *
     * @return A list of crosshairs.
     */
    public List<Crosshair> getDomainCrosshairs() {
        return new ArrayList<>(this.xCrosshairs);
    }

    /**
     * Adds a crosshair against the range axis and sends an
     * {@link OverlayChangeEvent} to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     */
    public void addRangeCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        this.yCrosshairs.add(crosshair);
        crosshair.addPropertyChangeListener(this);
        fireOverlayChanged();
    }

    /**
     * Removes a range axis crosshair and sends an {@link OverlayChangeEvent}
     * to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #addRangeCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void removeRangeCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        if (this.yCrosshairs.remove(crosshair)) {
            crosshair.removePropertyChangeListener(this);
            fireOverlayChanged();
        }
    }

    /**
     * Clears all the range crosshairs from the overlay and sends an
     * {@link OverlayChangeEvent} to all registered listeners (unless there
     * were no crosshairs to begin with).
     */
    public void clearRangeCrosshairs() {
        if (this.yCrosshairs.isEmpty()) {
            return;  // nothing to do - avoids change notification
        }
        for (Crosshair c : getRangeCrosshairs()) {
            this.yCrosshairs.remove(c);
            c.removePropertyChangeListener(this);
        }
        fireOverlayChanged();
    }

    /**
     * Returns a new list containing the range crosshairs for this overlay.
     *
     * @return A list of crosshairs.
     */
    public List<Crosshair> getRangeCrosshairs() {
        return new ArrayList<>(this.yCrosshairs);
    }

    /**
     * Receives a property change event (typically a change in one of the
     * crosshairs).
     *
     * @param e  the event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        fireOverlayChanged();
    }

    /**
     * Renders the crosshairs in the overlay on top of the chart that has just
     * been rendered in the specified {@code chartPanel}.  This method is
     * called by the JFreeChart framework, you won't normally call it from
     * user code.
     *
     * @param g2  the graphics target.
     * @param chartPanel  the chart panel.
     */
    @Override
    public void paintOverlay(Graphics2D g2, ChartPanel chartPanel) {
        Shape savedClip = g2.getClip();
        Rectangle2D dataArea = chartPanel.getScreenDataArea();
        g2.clip(dataArea);
        JFreeChart chart = chartPanel.getChart();
        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        RectangleEdge xAxisEdge = plot.getDomainAxisEdge();
        for (Crosshair ch : getDomainCrosshairs()) {
            if (ch.isVisible()) {
                double x = ch.getValue();
                double xx = xAxis.valueToJava2D(x, dataArea, xAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    drawVerticalCrosshair(g2, dataArea, xx, ch);
                } else {
                    drawHorizontalCrosshair(g2, dataArea, xx, ch);
                }
            }
        }
        ValueAxis yAxis = plot.getRangeAxis();
        RectangleEdge yAxisEdge = plot.getRangeAxisEdge();
        for (Crosshair ch : getRangeCrosshairs()) {
            if (ch.isVisible()) {
                double y = ch.getValue();
                double yy = yAxis.valueToJava2D(y, dataArea, yAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    drawHorizontalCrosshair(g2, dataArea, yy, ch);
                } else {
                    drawVerticalCrosshair(g2, dataArea, yy, ch);
                }
            }
        }
        g2.setClip(savedClip);
    }

    /**
     * Draws a crosshair horizontally across the plot.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param y  the y-value in Java2D space.
     * @param crosshair  the crosshair.
     */
    protected void drawHorizontalCrosshair(Graphics2D g2, Rectangle2D dataArea,
            double y, Crosshair crosshair) {

        if (y >= dataArea.getMinY() && y <= dataArea.getMaxY()) {
            Line2D line = new Line2D.Double(dataArea.getMinX(), y,
                    dataArea.getMaxX(), y);
            Paint savedPaint = g2.getPaint();
            Stroke savedStroke = g2.getStroke();
            g2.setPaint(crosshair.getPaint());
            g2.setStroke(crosshair.getStroke());
            g2.draw(line);
            if (crosshair.isLabelVisible()) {
                String label = crosshair.getLabelGenerator().generateLabel(
                        crosshair);
                if (label != null && !label.isEmpty()) {
                    Font savedFont = g2.getFont();
                    g2.setFont(crosshair.getLabelFont());
                    RectangleAnchor anchor = crosshair.getLabelAnchor();
                    RectangleInsets padding = crosshair.getLabelPadding();
                    Point2D pt = calculateLabelPoint(line, anchor, crosshair.getLabelXOffset(), crosshair.getLabelYOffset(), padding);
                    float xx = (float) pt.getX();
                    float yy = (float) pt.getY();
                    TextAnchor alignPt = textAlignPtForLabelAnchorH(anchor);
                    Shape hotspot = TextUtils.calculateRotatedStringBounds(
                            label, g2, xx, yy, alignPt, 0.0, TextAnchor.CENTER);
                    hotspot = padding.createOutsetRectangle(hotspot.getBounds2D());
                    if (!dataArea.contains(hotspot.getBounds2D())) {
                        anchor = flipAnchorV(anchor);
                        pt = calculateLabelPoint(line, anchor, crosshair.getLabelXOffset(), crosshair.getLabelYOffset(), padding);
                        xx = (float) pt.getX();
                        yy = (float) pt.getY();
                        if (anchor == RectangleAnchor.CENTER || alignPt.isHalfAscent()) {
                            double labelHeight = hotspot.getBounds2D().getHeight();
                            double minY = dataArea.getY() + (labelHeight + padding.getTop() - padding.getBottom()) / 2.0;
                            double maxY = dataArea.getY() + dataArea.getHeight() - (labelHeight + padding.getBottom() - padding.getTop()) / 2.0;
                            if (yy < minY) {
                                yy = (float) (minY);
                            } else if (yy > maxY) {
                                yy = (float) (maxY);
                            }
                        }
                        alignPt = textAlignPtForLabelAnchorH(anchor);
                        hotspot = TextUtils.calculateRotatedStringBounds(
                               label, g2, xx, yy, alignPt, 0.0, TextAnchor.CENTER);
                        hotspot = padding.createOutsetRectangle(hotspot.getBounds2D());
                    }

                    g2.setPaint(crosshair.getLabelBackgroundPaint());
                    g2.fill(hotspot);
                    if (crosshair.isLabelOutlineVisible()) {
                        g2.setPaint(crosshair.getLabelOutlinePaint());
                        g2.setStroke(crosshair.getLabelOutlineStroke());
                        g2.draw(hotspot);
                    }
                    g2.setPaint(crosshair.getLabelPaint());
                    TextUtils.drawAlignedString(label, g2, xx, yy, alignPt);
                    g2.setFont(savedFont);
                }
            }
            g2.setPaint(savedPaint);
            g2.setStroke(savedStroke);
        }
    }

    /**
     * Draws a crosshair vertically on the plot.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param x  the x-value in Java2D space.
     * @param crosshair  the crosshair.
     */
    protected void drawVerticalCrosshair(Graphics2D g2, Rectangle2D dataArea,
            double x, Crosshair crosshair) {

        if (x >= dataArea.getMinX() && x <= dataArea.getMaxX()) {
            Line2D line = new Line2D.Double(x, dataArea.getMinY(), x,
                    dataArea.getMaxY());
            Paint savedPaint = g2.getPaint();
            Stroke savedStroke = g2.getStroke();
            g2.setPaint(crosshair.getPaint());
            g2.setStroke(crosshair.getStroke());
            g2.draw(line);
            if (crosshair.isLabelVisible()) {
                String label = crosshair.getLabelGenerator().generateLabel(
                        crosshair);
                if (label != null && !label.isEmpty()) {
                    Font savedFont = g2.getFont();
                    g2.setFont(crosshair.getLabelFont());
                    RectangleAnchor anchor = crosshair.getLabelAnchor();
                    RectangleInsets padding = crosshair.getLabelPadding();
                    Point2D pt = calculateLabelPoint(line, anchor, crosshair.getLabelXOffset(), crosshair.getLabelYOffset(), padding);
                    float xx = (float) pt.getX();
                    float yy = (float) pt.getY();
                    TextAnchor alignPt = textAlignPtForLabelAnchorV(anchor);
                    Shape hotspot = TextUtils.calculateRotatedStringBounds(
                            label, g2, xx, yy, alignPt, 0.0, TextAnchor.CENTER);
                    hotspot = padding.createOutsetRectangle(hotspot.getBounds2D());
                    if (!dataArea.contains(hotspot.getBounds2D())) {
                        anchor = flipAnchorH(anchor);
                        pt = calculateLabelPoint(line, anchor, crosshair.getLabelXOffset(), crosshair.getLabelYOffset(), padding);
                        xx = (float) pt.getX();
                        yy = (float) pt.getY();
                        if (alignPt.isHorizontalCenter()) {
                            double labelWidth = hotspot.getBounds2D().getWidth();
                            double minX = dataArea.getX() + (labelWidth + padding.getLeft() - padding.getRight()) / 2.0;
                            double maxX = dataArea.getX() + dataArea.getWidth() - (labelWidth + padding.getRight() - padding.getLeft()) / 2.0;
                            if (xx < minX) {
                                xx = (float) (minX);
                            } else if (xx > maxX) {
                                xx = (float) (maxX);
                            }
                        }
                        alignPt = textAlignPtForLabelAnchorV(anchor);
                        hotspot = TextUtils.calculateRotatedStringBounds(
                               label, g2, xx, yy, alignPt, 0.0, TextAnchor.CENTER);
                        hotspot = padding.createOutsetRectangle(hotspot.getBounds2D());
                    }
                    g2.setPaint(crosshair.getLabelBackgroundPaint());
                    g2.fill(hotspot);
                    if (crosshair.isLabelOutlineVisible()) {
                        g2.setPaint(crosshair.getLabelOutlinePaint());
                        g2.setStroke(crosshair.getLabelOutlineStroke());
                        g2.draw(hotspot);
                    }
                    g2.setPaint(crosshair.getLabelPaint());
                    TextUtils.drawAlignedString(label, g2, xx, yy, alignPt);
                    g2.setFont(savedFont);
                }
            }
            g2.setPaint(savedPaint);
            g2.setStroke(savedStroke);
        }
    }

    /**
     * Calculates the anchor point for a label.
     *
     * @param line  the line for the crosshair.
     * @param anchor  the anchor point.
     * @param deltaX  the x-offset.
     * @param deltaY  the y-offset.
     * @param padding the label padding
     *
     * @return The anchor point.
     */
    private Point2D calculateLabelPoint(Line2D line, RectangleAnchor anchor,
            double deltaX, double deltaY, RectangleInsets padding) {
        double x, y;
        boolean left = (anchor == RectangleAnchor.BOTTOM_LEFT 
                || anchor == RectangleAnchor.LEFT 
                || anchor == RectangleAnchor.TOP_LEFT);
        boolean right = (anchor == RectangleAnchor.BOTTOM_RIGHT 
                || anchor == RectangleAnchor.RIGHT 
                || anchor == RectangleAnchor.TOP_RIGHT);
        boolean top = (anchor == RectangleAnchor.TOP_LEFT 
                || anchor == RectangleAnchor.TOP 
                || anchor == RectangleAnchor.TOP_RIGHT);
        boolean bottom = (anchor == RectangleAnchor.BOTTOM_LEFT
                || anchor == RectangleAnchor.BOTTOM
                || anchor == RectangleAnchor.BOTTOM_RIGHT);
        Rectangle rect = line.getBounds();
        
        // we expect the line to be vertical or horizontal
        if (line.getX1() == line.getX2()) {  // vertical
            x = line.getX1();
            y = (line.getY1() + line.getY2()) / 2.0;
            if (left) {
                x = x - deltaX - padding.getRight();
            } else if (right) {
                x = x + deltaX + padding.getLeft();
            } else {
                x = x + (padding.getLeft() - padding.getRight()) / 2.0;
            }
            if (top) {
                y = Math.min(line.getY1(), line.getY2()) + deltaY + padding.getTop();
            } else if (bottom) {
                y = Math.max(line.getY1(), line.getY2()) - deltaY - padding.getBottom();
            } else {
                y = y + (padding.getTop() - padding.getBottom()) / 2.0;
            }
        }
        else {  // horizontal
            x = (line.getX1() + line.getX2()) / 2.0;
            y = line.getY1();
            if (left) {
                x = Math.min(line.getX1(), line.getX2()) + deltaX + padding.getLeft();
            } else if (right) {
                x = Math.max(line.getX1(), line.getX2()) - deltaX - padding.getRight();
            } else {
                x = x + (padding.getLeft() - padding.getRight()) / 2.0;
            }
            if (top) {
                y = y - deltaY - padding.getBottom();
            } else if (bottom) {
                y = y + deltaY + padding.getTop();
            } else {
                y = y + (padding.getTop() - padding.getBottom()) / 2.0;
            }
        }
        return new Point2D.Double(x, y);
    }

    /**
     * Returns the text anchor that is used to align a label to its anchor 
     * point.
     * 
     * @param anchor  the anchor.
     * 
     * @return The text alignment point.
     */
    private TextAnchor textAlignPtForLabelAnchorV(RectangleAnchor anchor) {
        TextAnchor result = TextAnchor.CENTER;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            result = TextAnchor.TOP_RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.TOP)) {
            result = TextAnchor.TOP_CENTER;
        }
        else if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            result = TextAnchor.TOP_LEFT;
        }
        else if (anchor.equals(RectangleAnchor.LEFT)) {
            result = TextAnchor.HALF_ASCENT_RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.RIGHT)) {
            result = TextAnchor.HALF_ASCENT_LEFT;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = TextAnchor.BOTTOM_RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM)) {
            result = TextAnchor.BOTTOM_CENTER;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = TextAnchor.BOTTOM_LEFT;
        }
        return result;
    }

    /**
     * Returns the text anchor that is used to align a label to its anchor
     * point.
     *
     * @param anchor  the anchor.
     *
     * @return The text alignment point.
     */
    private TextAnchor textAlignPtForLabelAnchorH(RectangleAnchor anchor) {
        TextAnchor result = TextAnchor.CENTER;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            result = TextAnchor.BOTTOM_LEFT;
        }
        else if (anchor.equals(RectangleAnchor.TOP)) {
            result = TextAnchor.BOTTOM_CENTER;
        }
        else if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            result = TextAnchor.BOTTOM_RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.LEFT)) {
            result = TextAnchor.HALF_ASCENT_LEFT;
        }
        else if (anchor.equals(RectangleAnchor.RIGHT)) {
            result = TextAnchor.HALF_ASCENT_RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = TextAnchor.TOP_LEFT;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM)) {
            result = TextAnchor.TOP_CENTER;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = TextAnchor.TOP_RIGHT;
        }
        return result;
    }

    private RectangleAnchor flipAnchorH(RectangleAnchor anchor) {
        RectangleAnchor result = anchor;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            result = RectangleAnchor.TOP_RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            result = RectangleAnchor.TOP_LEFT;
        }
        else if (anchor.equals(RectangleAnchor.LEFT)) {
            result = RectangleAnchor.RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.RIGHT)) {
            result = RectangleAnchor.LEFT;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = RectangleAnchor.BOTTOM_RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = RectangleAnchor.BOTTOM_LEFT;
        }
        return result;
    }

    private RectangleAnchor flipAnchorV(RectangleAnchor anchor) {
        RectangleAnchor result = anchor;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            result = RectangleAnchor.BOTTOM_LEFT;
        }
        else if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            result = RectangleAnchor.BOTTOM_RIGHT;
        }
        else if (anchor.equals(RectangleAnchor.TOP)) {
            result = RectangleAnchor.BOTTOM;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM)) {
            result = RectangleAnchor.TOP;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = RectangleAnchor.TOP_LEFT;
        }
        else if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = RectangleAnchor.TOP_RIGHT;
        }
        return result;
    }

    /**
     * Tests this overlay for equality with an arbitrary object.
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
        if (!(obj instanceof CrosshairOverlay)) {
            return false;
        }
        CrosshairOverlay that = (CrosshairOverlay) obj;
        if (!this.xCrosshairs.equals(that.xCrosshairs)) {
            return false;
        }
        if (!this.yCrosshairs.equals(that.yCrosshairs)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of this instance.
     *
     * @return A clone of this instance.
     *
     * @throws java.lang.CloneNotSupportedException if there is some problem
     *     with the cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        CrosshairOverlay clone = (CrosshairOverlay) super.clone();
        clone.xCrosshairs = (List) CloneUtils.cloneList(this.xCrosshairs);
        clone.yCrosshairs = (List) CloneUtils.cloneList(this.yCrosshairs);
        return clone;
    }

}
