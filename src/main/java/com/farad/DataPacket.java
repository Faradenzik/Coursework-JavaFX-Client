package com.farad;

import java.io.Serializable;
import java.util.List;

public class DataPacket<T> implements Serializable {
    private List<T> data;

    public DataPacket(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }
}