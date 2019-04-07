package com.hugovs.util;

import java.util.Vector;

/**
 * Wrapper class for synchronized objects.
 * Use this class to guarantee read/write conformity when using multiple threads to access a single data.
 *
 * @param <T>: the class type of the data.
 *
 * @author Hugo Sartori
 */
public class SynchronizedData<T> {


    /**
     * An {@link Vector} to hold a single data.
     * A {@link Vector} is used instead a simple value because, by nature, it is designed to be synchronous.
     */
    private Vector<T> holder = new Vector<>(1);

    /**
     * Create an instance of this class with a null data.
     */
    public SynchronizedData() {
        this(null);
    }

    /**
     * Create an instance of this class with a predefined data.
     *
     * @param data: the value of type {@code T} to be used.
     */
    public SynchronizedData(T data) {
        holder.add(data);
    }

    /**
     * Multi-thread safe get.
     *
     * @return the data.
     */
    public T getData() {
        return holder.get(0);
    }

    /**
     * Multi-thread safe set.
     *
     * @param data: set the data to the given one.
     */
    public void setData(T data) {
        holder.set(0, data);
    }
}
