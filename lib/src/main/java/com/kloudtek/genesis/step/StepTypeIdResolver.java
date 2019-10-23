package com.kloudtek.genesis.step;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StepTypeIdResolver extends TypeIdResolverBase {
    private final Map<String, Class<? extends Step>> mappings = new HashMap<>();
    private final Map<Class<? extends Step>, String> rev = new HashMap<>();

    public StepTypeIdResolver() {
        mappings.put("input", Input.class);
        mappings.put("conditional", ConditionalSteps.class);
    }

    @Override
    public String idFromValue(Object o) {
        return rev.get(o.getClass());
    }

    @Override
    public String idFromValueAndType(Object o, Class<?> aClass) {
        return idFromValue(o);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        Class<? extends Step> cl = mappings.get(id);
        if(cl!=null) {
            return TypeFactory.defaultInstance().constructSpecializedType(TypeFactory.unknownType(),cl);
        } else {
            return TypeFactory.unknownType();
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
}
