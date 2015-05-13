package com.ganhuo.app.interfaces;

import java.util.List;

public interface DataProvider<T> {
    List<T> doInitialize() throws Exception;

    List<T> doRefresh() throws Exception;

    List<T> doMore() throws Exception;
}
