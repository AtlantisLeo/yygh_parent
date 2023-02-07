package com.whozm.yygh.hosp.utils;

import com.sun.javafx.collections.MappingChange;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author HZM
 * @date 2023/1/22
 */
public class HttpRequestHelper {
    public static Map<String, Object> switchMap(Map<String, String[]> parameterMap) {
        Map<String, Object> result = new HashMap<>();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue()[0];
            result.put(key,value);
        }
        return result;
    }
}
