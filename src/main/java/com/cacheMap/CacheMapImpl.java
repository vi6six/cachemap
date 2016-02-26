package com.cacheMap;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CacheMapImpl<KeyType, ValueType> implements CacheMap<KeyType, ValueType> {

    /**
     * Default timeToLive is 1 hour
     */
    public static final int DEFAULT_EXPIRATION_TIME = 3600000;

    /**
     * Entry contains: Key and Pair.
     * Pair holds expirationTime and value
     */
    private Map<KeyType, Pair<Long, ValueType>> map = new HashMap<KeyType, Pair<Long, ValueType>>();
    private long timeToLive = DEFAULT_EXPIRATION_TIME;

    @Override
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public long getTimeToLive() {
        return timeToLive;
    }

    @Override
    public ValueType put(KeyType key, ValueType value) {
        ValueType returnValue = get(key);

        if (key != null) {


            Pair<Long, ValueType> pair = getEntryForPut(value);
            map.put(key, pair);
        }

        return returnValue;
    }

    private Pair<Long, ValueType> getEntryForPut(ValueType value) {
        return new Pair<Long, ValueType>(getExpirationTime(), value);
    }

    private long getExpirationTime() {
        return Clock.getTime() + getTimeToLive();
    }

    @Override
    public void clearExpired() {
        Iterator<Map.Entry<KeyType, Pair<Long, ValueType>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<KeyType, Pair<Long, ValueType>> entry = it.next();
            if (isEntryExpired(entry.getKey())) {
                it.remove();
            }
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        clearExpired();
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        clearExpired();
        for (Map.Entry<KeyType, Pair<Long, ValueType>> entry : map.entrySet()) {
            return get(entry.getKey()).equals(value);
        }

        return map.containsValue(value);
    }

    @Override
    public ValueType get(Object key) {
        Pair<Long, ValueType> pair = map.get(key);

        if (pair != null && !isEntryExpired(key)) {
            return pair.getValue();
        } else
            return null;
    }

    private boolean isEntryExpired(Object key) {
        Pair<Long, ValueType> pair = map.get(key);

        Long expires = pair.getKey();
        Long now = Clock.getTime();

        return expires <= now;
    }

    @Override
    public boolean isEmpty() {
        clearExpired();
        if (map.isEmpty()) return true;
        else return false;
    }

    @Override
    public ValueType remove(Object key) {
        ValueType previousValue = get(key);
        map.remove(key);
        return previousValue;
    }

    @Override
    public int size() {
        clearExpired();
        return map.size();
    }
}
