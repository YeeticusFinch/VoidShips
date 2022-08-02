package com.lerdorf.voidships;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionUtil {

    public static Class getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Throwable ignored) {
            return Class.class;
        }
    }

    public static Method getMethod(Class<?> clazz, String method, Class<?>... parameterTypes) {
        try {
            Method m = clazz.getDeclaredMethod(method, parameterTypes);
            m.setAccessible(true);
            return m;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static Field getField(Class<?> clazz, String field) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f;
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static Object getFieldValue(Class<?> clazz, Object target, String field) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(target);
        } catch (Throwable ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Object getFieldValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static boolean setFieldValue(Field field, Object target, Object value) {
        try {
            field.set(target, value);
            return true;
        } catch (Throwable e) {
            return false;
        }

    }

    public static boolean setFieldValue(String fieldName, Object target, Object value) {
        try {
            Field field = getField(target.getClass(), fieldName);
            if (field != null) {
                field.set(target, value);
                return true;
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static void setFinalStaticValue(Field field, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    public static boolean isTypeOf(Class<?> clazz, Class<?> superClass) {
        if (!clazz.equals(superClass)) {
            clazz = clazz.getSuperclass();
            return !clazz.equals(Object.class) && isTypeOf(clazz, superClass);
        }
        return true;
    }
}
