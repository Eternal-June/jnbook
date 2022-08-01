package com.jn.db.dao.ex;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public interface StatMapper {
    List<Map> statUser();

    List<Map> statOrder();

    List<Map> statBooks();
}