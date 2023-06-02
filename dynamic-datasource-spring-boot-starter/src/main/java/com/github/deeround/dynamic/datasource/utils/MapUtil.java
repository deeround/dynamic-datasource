package com.github.deeround.dynamic.datasource.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author wanghao 913351190@qq.com
 * @create 2023/3/15 16:13
 */
public final class MapUtil {
    private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);

    public static void putAll(final Map<String, Object> target, final Map<String, Object> source) {
        for (String s : source.keySet()) {
            target.put(s, source.get(s));
        }
    }

    public static boolean getBool(final String propName, final Map<String, Object> properties) {
        if (properties.containsKey(propName)) {
            String property = properties.get(propName) == null ? null : properties.get(propName).toString();
            return (!StringUtils.isEmpty(property)) && (property.equalsIgnoreCase("true") || property.equals("1"));
        }
        return false;
    }

    public static void setTargetFromMap(final Object target, final Map<String, Object> properties) {
        if (target == null || properties == null) {
            return;
        }

        List<Method> methods = Arrays.asList(target.getClass().getMethods());
        for (String s : properties.keySet()) {
            try {
                setProperty(target, s, properties.get(s), methods);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static void setProperty(final Object target, final String propName, final Object propValue, final List<Method> methods) {
        // use the english locale to avoid the infamous turkish locale bug
        String methodName = "set" + propName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propName.substring(1);
        Method writeMethod = methods.stream().filter(m -> m.getName().equals(methodName) && m.getParameterCount() == 1).findFirst().orElse(null);

        if (writeMethod == null) {
            String methodName2 = "set" + propName.toUpperCase(Locale.ENGLISH);
            writeMethod = methods.stream().filter(m -> m.getName().equals(methodName2) && m.getParameterCount() == 1).findFirst().orElse(null);
        }

        if (writeMethod == null) {
            throw new RuntimeException(String.format("Property %s does not exist on target %s", propName, target.getClass()));
        }

        try {
            Class<?> paramClass = writeMethod.getParameterTypes()[0];
            if (paramClass == String.class) {
                writeMethod.invoke(target, propValue.toString());
            } else if (paramClass == Integer.class || paramClass == int.class) {
                writeMethod.invoke(target, Integer.parseInt(propValue.toString()));
            } else if (paramClass == Long.class || paramClass == long.class) {
                writeMethod.invoke(target, Long.parseLong(propValue.toString()));
            } else if (paramClass == Boolean.class || paramClass == boolean.class) {
                writeMethod.invoke(target, Boolean.parseBoolean(propValue.toString()));
            } else if (paramClass == Double.class || paramClass == double.class) {
                writeMethod.invoke(target, Double.parseDouble(propValue.toString()));
            } else if (paramClass == Float.class || paramClass == float.class) {
                writeMethod.invoke(target, Float.parseFloat(propValue.toString()));
            } else if (paramClass == Short.class || paramClass == short.class) {
                writeMethod.invoke(target, Short.parseShort(propValue.toString()));
            } else if (paramClass.isEnum()) {
                Method method = paramClass.getMethod("values");
                Enum[] enums = (Enum[]) method.invoke(null);
                Enum value = null;
                for (Enum ee : enums) {
                    if (ee.name().toString().equals(propValue.toString())) {
                        value = ee;
                        break;
                    }
                }
                writeMethod.invoke(target, value);
            } else {
                try {
                    logger.debug("Try to create a new instance of \"{}\"", propValue);
                    writeMethod.invoke(target, Class.forName(propValue.toString()).getDeclaredConstructor().newInstance());
                } catch (InstantiationException | ClassNotFoundException e) {
                    logger.debug("Class \"{}\" not found or could not instantiate it (Default constructor)", propValue);
                    writeMethod.invoke(target, propValue);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
