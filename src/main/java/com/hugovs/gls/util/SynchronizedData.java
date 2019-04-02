package com.hugovs.gls.util;

import java.util.Vector;

public class SynchronizedData<T> {
    private Vector<T> holder = new Vector<>(1);

    public SynchronizedData() {
        this(null);
    }

    public SynchronizedData(T data) {
        holder.add(data);
    }

    public T getData() {
        return holder.get(0);
    }

    public void setData(T data) {
        holder.set(0, data);
    }
}
