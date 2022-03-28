package com.urbanoexpress.iridio3.data.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DoubleGsonTypeAdapter extends TypeAdapter<Double> {

    @Override
    public Double read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return 0D;
        }
        try {
            return Double.valueOf(reader.nextString());
        } catch (NumberFormatException e) {
            return 0D;
        }
    }

    @Override
    public void write(JsonWriter writer, Double value) throws IOException {
        if (value == null) {
            writer.value(0D);
            return;
        }
        writer.value(value);
    }
}