package com.larry.service;

import java.util.List;

public interface BaseService {
    public <T> List<T> getList(List<?> ids,T clazz);
}
