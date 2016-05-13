package com.alibaba.imt.util;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;

public class JsonUtils {
    public static String jsonEncode(Object obj) {
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.setJsonPropertyFilter(new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                if (value != null) {
                    if (Number.class.isAssignableFrom(value.getClass())) {
                        return false;
                    }
                    if (value.getClass().equals(String.class)) {
                        return false;
                    }
                    if (value.getClass().equals(Date.class)) {
                        return false;
                    }
                    if (value.getClass().equals(Boolean.class)) {
                        return false;
                    }
                    if (value.getClass().equals(JSONObject.class)) {
                        return false;
                    }
                }
                return true;
            }
        });
        if (obj instanceof List<?>) {
            JSONArray jsonArray = JSONArray.fromObject(obj, jsonConfig);
            return jsonArray.toString();
        } else {
            JSONObject jsonObject = JSONObject.fromObject(obj, jsonConfig);
            return jsonObject.toString();
        }
    }

    public static String jsonEncode(Object obj, Boolean override) {

        if (obj instanceof List<?>) {
            JSONArray jsonArray = JSONArray.fromObject(obj);
            return jsonArray.toString();
        } else {
            JSONObject jsonObject = JSONObject.fromObject(obj);
            return jsonObject.toString();
        }
    }

    public static Object jsonDecode(String s, Class<?> type) {

        JSONObject json = JSONObject.fromObject(s);
        return JSONObject.toBean(json, type);
    }

    public static Object jsonDecode(String s, Class<?> type, Boolean isList) {
        if (!isList)
            return jsonDecode(s, type);

        JSONArray json = JSONArray.fromObject(s);
        return JSONArray.toCollection(json, type);
    }

}
