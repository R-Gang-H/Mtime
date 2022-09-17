package com.mtime.widgets;

import com.mtime.beans.URLData;

/**
 * Created by yinguanping on 17/2/23.
 */

public interface ParserInterface {
    Object handle(final String data, final URLData url);
}
