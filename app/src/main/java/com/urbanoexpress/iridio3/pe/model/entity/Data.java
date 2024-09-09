package com.urbanoexpress.iridio3.pe.model.entity;

/**
 * Created by mick on 02/08/16.
 */
public class Data {

    public interface Sync {
        int PENDING = 0;
        int SYNCHRONIZED = 1;
        int MANUAL = 2;
    }

    public interface Delete {
        int NO = 0;
        int YES = 1;
    }

    public interface Validate {
        int PENDING = 0;
        int VALID = 1;
    }

}
