package com.ggp.theclub.customlocale;

import com.ggp.theclub.R;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Predicate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by john.curtis on 4/17/17.
 */

public class ResourceMatcher {

    private static  Map<Integer, String> matchMap;

    public ResourceMatcher() {
        matchMap = new HashMap<>();
        Field[] declaredFields = R.string.class.getFields();
        Stream.of(declaredFields).filter(new Predicate<Field>() {
            @Override
            public boolean test(Field field) {
                return field.getType().isPrimitive() && field.getType().equals(Integer.TYPE);
            }
        }).forEach(new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                int key = 0;
                String value;
                value = field.getName();
                try {
                    key = (int) field.get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                matchMap.put(key, value);
            }
        });
    }

    public Map<Integer, String> getMatchMap(){
        return matchMap;
    }
}
