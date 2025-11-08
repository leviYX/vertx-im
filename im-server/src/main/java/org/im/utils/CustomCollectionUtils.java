package org.im.utils;


import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @Description:
 * @Author: levi
 * @Date: 2023/4/2 15:00
 */
public class CustomCollectionUtils {

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return collection != null && collection.size() > 0;
    }
}