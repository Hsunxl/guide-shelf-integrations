package io.github.hsunxl.guideshelf;

import java.util.*;
import java.util.function.Function;

public final class DiscoveryPolicy {
    private DiscoveryPolicy() {}
    /** Original entries win, preserving native names, components and stable pin IDs. */
    public static <T> List<T> merge(List<T> original,List<T> added,Function<T,String> key) {
        var entries=new LinkedHashMap<String,T>();
        for(T e:original) entries.putIfAbsent(key.apply(e),e);
        for(T e:added) entries.putIfAbsent(key.apply(e),e);
        return new ArrayList<>(entries.values());
    }
    public static String wikiPath(String wiki) {
        // Native IDs are strings. Reject invalid paths rather than silently renaming pins.
        return wiki!=null && !wiki.isEmpty() && wiki.matches("[a-z0-9/._-]+") ? "wiki/"+wiki : null;
    }
}
