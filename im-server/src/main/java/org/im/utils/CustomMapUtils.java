package org.im.utils;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @Author: levi
 */
public class CustomMapUtils {

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return map != null && map.size() > 0;
    }
}