package com.usesoft.poker.server.domain.model.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.usesoft.poker.server.domain.common.ValueObject;

public class ValueObjectUtil {

    private ValueObjectUtil() {
    }
    
   
    public static <T> void checkValues(ValueObject<T> v1, ValueObject<T> same, ValueObject<T> different){
        assertTrue(v1.equals(v1));
        assertFalse(v1.equals(null));
        
        assertFalse(v1 == same);
        assertFalse(v1 == different);

        assertFalse(v1.sameValueAs(null));
        
        assertTrue(v1.equals(same));
        assertFalse(v1.equals(different));
    }
}
