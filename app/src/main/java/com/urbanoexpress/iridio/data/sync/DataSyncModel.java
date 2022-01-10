package com.urbanoexpress.iridio.data.sync;

import java.util.Collections;
import java.util.List;

/**
 * Created by mick on 09/09/16.
 */

public abstract class DataSyncModel<T> {
    private List<T> data = Collections.emptyList();
    private int countData = 0;
    private int totalData = 0;
    private boolean syncDone = true;

    public DataSyncModel() { }

    public abstract void sync();
    public abstract void finishSync();
    protected abstract void executeSync();
    public abstract void loadData();

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getCountData() {
        return countData;
    }

    public void setCountData(int countData) {
        this.countData = countData;
    }

    public int getTotalData() {
        return totalData;
    }

    public void setTotalData(int totalData) {
        this.totalData = totalData;
    }

    public void nextData() {
        countData++;
    }

    public boolean isSyncDone() {
        return syncDone;
    }

    public void setSyncDone(boolean syncDone) {
        this.syncDone = syncDone;
    }
}