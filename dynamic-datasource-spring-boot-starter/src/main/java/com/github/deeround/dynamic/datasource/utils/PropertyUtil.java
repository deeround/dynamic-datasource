/*
 * Copyright (C) 2013 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.deeround.dynamic.datasource.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A class that reflectively sets bean properties on a target object.
 *
 * @author Brett Wooldridge
 */
public final class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

    private static String toCamelCase(CharSequence name) {
        if (null == name) {
            return null;
        }
        final char symbol = '-';
        final String name2 = name.toString();
        if (name2.contains(String.valueOf(symbol))) {
            final int length = name2.length();
            final StringBuilder sb = new StringBuilder(length);
            boolean upperCase = false;
            for (int i = 0; i < length; i++) {
                char c = name2.charAt(i);

                if (c == symbol) {
                    upperCase = true;
                } else if (upperCase) {
                    sb.append(Character.toUpperCase(c));
                    upperCase = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
            return sb.toString();
        } else {
            return name2;
        }
    }

    public static void remove(final String propName, final Properties target) {
        Set<String> keys = new HashSet<>();
        for (Object o : target.keySet()) {
            if (toCamelCase(o.toString()).equals(toCamelCase(propName)) || toCamelCase(o.toString()).startsWith(toCamelCase(propName) + ".")) {
                keys.add(o.toString());
            }
        }
        for (String key : keys) {
            target.remove(key);
        }
    }

    public static Map<String, Object> getMap(final String propName, final Properties target) {
        Set<String> keys = new HashSet<>();
        for (Object o : target.keySet()) {
            if (toCamelCase(o.toString()).equals(toCamelCase(propName)) || toCamelCase(o.toString()).startsWith(toCamelCase(propName) + ".")) {
                keys.add(o.toString());
            }
        }
        Map<String, Object> map = new HashMap<>();
        for (String key : keys) {
            map.put(toCamelCase(key).replace(toCamelCase(propName) + ".", ""), target.get(key));
        }
        return map;
    }

    public static String getStr(final String propName, final Properties target) {
        return target.getProperty(propName);
    }

    public static boolean getBool(final String propName, final Properties target) {
        String property = target.getProperty(propName);
        return (!StringUtils.isEmpty(property)) && (property.toLowerCase().equals("true") || property.equals("1"));
    }

    public static void putAll(final Properties target, final Properties source) {
        for (Object o : source.keySet()) {
            target.put(toCamelCase(o.toString()), source.get(o));
        }
    }

    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)[A-Z].+");

    private PropertyUtil() {
        // cannot be constructed
    }

    public static void setTargetFromProperties(final Object target, final Properties properties) {
        if (target == null || properties == null) {
            return;
        }

        List<Method> methods = Arrays.asList(target.getClass().getMethods());
        for (Object o : properties.keySet()) {
            try {
                setProperty(target, o.toString(), properties.get(o), methods);
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
