package com.product.inventory.sysetem.service;

public interface RedisService {
    Object getValue(String key);
    void setValue(String key, Object value);
}
