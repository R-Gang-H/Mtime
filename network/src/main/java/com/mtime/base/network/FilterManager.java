package com.mtime.base.network;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by LiJiaZhi on 17/3/24.
 */
public class FilterManager<T> {

    private final Vector<Filter<T>> filters = new Vector<>();

    public void registerFilter(Filter<T> filter) {
        filters.add(filter);
    }

    public void unregisterFilter(Filter<T> filter) {
        filters.remove(filter);
    }

    public boolean sendNext(T t, String message) {

        Iterator<Filter<T>> iterator  = filters.iterator();

        while (iterator.hasNext()) {
            Filter<T> filter = iterator.next();
            if (filter.onFilter(t, message)) {
                return true;
            }
        }

        return false;
    }
}
