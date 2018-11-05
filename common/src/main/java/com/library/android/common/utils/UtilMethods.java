package com.library.android.common.utils;

import android.text.Editable;

// Note: 10/25/2018 by sagar  This class has no plan to be extended by and hence making it final 
// Note: 10/25/2018 by sagar  https://stackoverflow.com/questions/5181578/what-is-the-point-of-final-class-in-java
// Note: 10/25/2018 by sagar  This class is not suppose to be inherited or extended by any other class and hence it is final
public final class UtilMethods {

    // Note: 10/25/2018 by sagar  Supressing constructor as it is never going to be instantiated 
    // Note: 10/25/2018 by sagar  https://stackoverflow.com/questions/25658330/why-java-util-objects-private-constructor-throws-assertionerror
    // Note: 10/25/2018 by sagar  Reflection proof
    private UtilMethods(){};

    /**
     * Verifies whether the given string value is null, empty or filled with value
     *
     * @param value A String that needs to be verified
     * @return true if the given value is neither null nor empty
     * @since 1.0
     */
    public static boolean checkNotNullEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    /**
     * Verifies whether the given {@link Editable} value is null or not
     *
     * @param text An editable that needs to be verified
     * @return true if the given value is not null
     * @since 1.0
     */
    public static boolean checkNotNullEmpty(Editable text) {
        return text != null;
    }
}
