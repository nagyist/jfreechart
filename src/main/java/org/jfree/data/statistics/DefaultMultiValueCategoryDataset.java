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
 * -------------------------------------
 * DefaultMultiValueCategoryDataset.java
 * -------------------------------------
 * (C) Copyright 2007-present, by David Forslund and Contributors.
 *
 * Original Author:  David Forslund;
 * Contributor(s):   David Gilbert;
 * 
 */

package org.jfree.data.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jfree.chart.internal.Args;
import org.jfree.chart.api.PublicCloneable;

import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;

/**
 * A category dataset that defines multiple values for each item.
 *
 * @param <R> the row key type.
 * @param <C> the column key type.
 */
public class DefaultMultiValueCategoryDataset<R extends Comparable<R>, C extends Comparable<C>>
        extends AbstractDataset implements MultiValueCategoryDataset<R, C>, 
        RangeInfo, PublicCloneable {

    /**
     * Storage for the data.
     */
    protected KeyedObjects2D<R, C> data;

    /**
     * The minimum range value.
     */
    private Number minimumRangeValue;

    /**
     * The maximum range value.
     */
    private Number maximumRangeValue;

    /**
     * The range of values.
     */
    private Range rangeBounds;

    /**
     * Creates a new dataset.
     */
    public DefaultMultiValueCategoryDataset() {
        super();
        this.data = new KeyedObjects2D<>();
        this.minimumRangeValue = null;
        this.maximumRangeValue = null;
        this.rangeBounds = new Range(0.0, 0.0);
    }

    /**
     * Adds a list of values to the dataset ({@code null} and Double.NaN
     * items are automatically removed) and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param values  a list of values ({@code null} not permitted).
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     */
    public void add(List<? extends Number> values, R rowKey, C columnKey) {

        Args.nullNotPermitted(values, "values");
        Args.nullNotPermitted(rowKey, "rowKey");
        Args.nullNotPermitted(columnKey, "columnKey");
        List<Double> vlist = new ArrayList<>(values.size());
        for (Number v : values) {
            if (v != null && !Double.isNaN(v.doubleValue())) {
                vlist.add(v.doubleValue());
            }
        }
        Collections.sort(vlist);
        this.data.addObject(vlist, rowKey, columnKey);

        if (!vlist.isEmpty()) {
            double maxval = Double.NEGATIVE_INFINITY;
            double minval = Double.POSITIVE_INFINITY;
            for (Double n : vlist) {
                minval = Math.min(minval, n);
                maxval = Math.max(maxval, n);
            }

            // update the cached range values...
            if (this.maximumRangeValue == null) {
                this.maximumRangeValue = maxval;
            }
            else if (maxval > this.maximumRangeValue.doubleValue()) {
                this.maximumRangeValue = maxval;
            }

            if (this.minimumRangeValue == null) {
                this.minimumRangeValue = minval;
            }
            else if (minval < this.minimumRangeValue.doubleValue()) {
                this.minimumRangeValue = minval;
            }
            this.rangeBounds = new Range(this.minimumRangeValue.doubleValue(),
                    this.maximumRangeValue.doubleValue());
        }

        fireDatasetChanged();
    }

    /**
     * Returns a list (possibly empty) of the values for the specified item.
     * The returned list should be unmodifiable.
     *
     * @param row  the row index (zero-based).
     * @param column   the column index (zero-based).
     *
     * @return The list of values.
     */
    @Override
    public List<? extends Number> getValues(int row, int column) {
        List values = (List) this.data.getObject(row, column);
        if (values != null) {
            return Collections.unmodifiableList(values);
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Returns a list (possibly empty) of the values for the specified item.
     * The returned list should be unmodifiable.
     *
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @return The list of values.
     */
    @Override
    public List<? extends Number> getValues(R rowKey, C columnKey) {
        return Collections.unmodifiableList((List) this.data.getObject(rowKey,
                columnKey));
    }

    /**
     * Returns the average value for the specified item.
     *
     * @param row  the row key.
     * @param column  the column key.
     *
     * @return The average value.
     */
    @Override
    public Number getValue(R row, C column) {
        List l = (List) this.data.getObject(row, column);
        double average = 0.0d;
        int count = 0;
        if (l != null && !l.isEmpty()) {
            for (int i = 0; i < l.size(); i++) {
                Number n = (Number) l.get(i);
                average += n.doubleValue();
                count += 1;
            }
            if (count > 0) {
                average = average / count;
            }
        }
        if (count == 0) {
            return null;
        }
        return average;
    }

    /**
     * Returns the average value for the specified item.
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return The average value.
     */
    @Override
    public Number getValue(int row, int column) {
        List l = (List) this.data.getObject(row, column);
        double average = 0.0d;
        int count = 0;
        if (l != null && !l.isEmpty()) {
            for (int i = 0; i < l.size(); i++) {
                Number n = (Number) l.get(i);
                average += n.doubleValue();
                count += 1;
            }
            if (count > 0) {
                average = average / count;
            }
        }
        if (count == 0) {
            return null;
        }
        return average;
    }

    /**
     * Returns the column index for a given key.
     *
     * @param key  the column key.
     *
     * @return The column index.
     */
    @Override
    public int getColumnIndex(C key) {
        return this.data.getColumnIndex(key);
    }

    /**
     * Returns a column key.
     *
     * @param column the column index (zero-based).
     *
     * @return The column key.
     */
    @Override
    public C getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    /**
     * Returns the column keys.
     *
     * @return The keys.
     */
    @Override
    public List<C> getColumnKeys() {
        return this.data.getColumnKeys();
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key the row key.
     *
     * @return The row index.
     */
    @Override
    public int getRowIndex(R key) {
        return this.data.getRowIndex(key);
    }

    /**
     * Returns a row key.
     *
     * @param row the row index (zero-based).
     *
     * @return The row key.
     */
    @Override
    public R getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    /**
     * Returns the row keys.
     *
     * @return The keys.
     */
    @Override
    public List<R> getRowKeys() {
        return this.data.getRowKeys();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The row count.
     */
    @Override
    public int getRowCount() {
        return this.data.getRowCount();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The column count.
     */
    @Override
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    /**
     * Returns the minimum y-value in the dataset.
     *
     * @param includeInterval a flag that determines whether the
     *                        y-interval is taken into account.
     *
     * @return The minimum value.
     */
    @Override
    public double getRangeLowerBound(boolean includeInterval) {
        double result = Double.NaN;
        if (this.minimumRangeValue != null) {
            result = this.minimumRangeValue.doubleValue();
        }
        return result;
    }

    /**
     * Returns the maximum y-value in the dataset.
     *
     * @param includeInterval a flag that determines whether the
     *                        y-interval is taken into account.
     *
     * @return The maximum value.
     */
    @Override
    public double getRangeUpperBound(boolean includeInterval) {
        double result = Double.NaN;
        if (this.maximumRangeValue != null) {
            result = this.maximumRangeValue.doubleValue();
        }
        return result;
    }

    /**
     * Returns the range of the values in this dataset's range.
     *
     * @param includeInterval a flag that determines whether the
     *                        y-interval is taken into account.
     * @return The range.
     */
    @Override
    public Range getRangeBounds(boolean includeInterval) {
        return this.rangeBounds;
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
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
        if (!(obj instanceof DefaultMultiValueCategoryDataset)) {
            return false;
        }
        DefaultMultiValueCategoryDataset that
                = (DefaultMultiValueCategoryDataset) obj;
        return this.data.equals(that.data);
    }

    @Override
    public int hashCode(){
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.data);
        return hash;
    }

    /**
     * Returns a clone of this instance.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the dataset cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultMultiValueCategoryDataset clone
                = (DefaultMultiValueCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
        return clone;
    }
}
