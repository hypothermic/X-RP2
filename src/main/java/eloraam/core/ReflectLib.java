/* X-RP - decompiled with CFR */
package eloraam.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectLib {

    public static void callClassMethod(String string, String string2, Class[] arrclass, Object[] arrobject) {
	Method method;
	Class class_;
	try {
	    class_ = Class.forName(string);
	} catch (ClassNotFoundException classNotFoundException) {
	    return;
	}
	try {
	    method = class_.getDeclaredMethod(string2, arrclass);
	} catch (NoSuchMethodException noSuchMethodException) {
	    return;
	}
	try {
	    method.invoke(null, arrobject);
	} catch (IllegalAccessException illegalAccessException) {
	    return;
	} catch (InvocationTargetException invocationTargetException) {
	    return;
	}
    }

    public static Object getStaticField(String string, String string2, Class class_) {
	Object object;
	Class class_2;
	Field field;
	try {
	    class_2 = Class.forName(string);
	} catch (ClassNotFoundException classNotFoundException) {
	    return null;
	}
	try {
	    field = class_2.getDeclaredField(string2);
	} catch (NoSuchFieldException noSuchFieldException) {
	    return null;
	}
	try {
	    object = field.get(null);
	} catch (IllegalAccessException illegalAccessException) {
	    return null;
	} catch (NullPointerException nullPointerException) {
	    return null;
	}
	if (!class_.isInstance(object)) {
	    return null;
	}
	return object;
    }
}
