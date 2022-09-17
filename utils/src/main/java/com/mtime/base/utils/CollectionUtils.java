package com.mtime.base.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by liuyu8 on 8/10/2015.
 */
public class CollectionUtils {


    public final static int size(Collection c) {
        return c == null ? 0 : c.size();
    }

    public final static boolean isEmpty(Collection c) {
        return size(c) == 0;
    }

    public final static boolean isNotEmpty(Collection c) {
        return size(c) > 0;
    }

    public final static boolean isLargerThan(Collection c, int mount) {
        return size(c) > mount;
    }

    public final static boolean isNotLargerThan(Collection c, int mount) {
        return size(c) <= mount;
    }

    public final static boolean isNotLessThan(Collection c, int mount) {
        return size(c) >= mount;
    }

    public final static boolean isLessThan(Collection c, int mount) {
        return size(c) < mount;
    }

    public final static boolean isBetween(Collection c, int min, int max) {
        int s = size(c);
        return s > min && s < max;
    }


    public final static <T> List<T> resetList(List<T> list, List<T> newList) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        list.clear();
        if (isNotEmpty(newList)) {
            list.addAll(newList);
        }
        return list;
    }

    public final static <T> ArrayList<T> resetList(ArrayList<T> list, List<T> newList) {
        if (list == null) {
            list = new ArrayList<T>();
        }
        list.clear();
        if (isNotEmpty(newList)) {
            list.addAll(newList);
        }
        return list;
    }

}
