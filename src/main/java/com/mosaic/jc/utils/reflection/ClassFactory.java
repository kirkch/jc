package com.mosaic.jc.utils.reflection;

import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.utils.Validate;

/**
 *
 */
public class ClassFactory<T> implements Function0<T> {

    private Class<T> typeClass;

    public ClassFactory( Class<T> typeClass ) {
        Validate.notNull(typeClass,"typeClass");

        this.typeClass = typeClass;
    }

    @Override
    public T invoke() {
        return ReflectionUtils.newInstance(typeClass);
    }

}
