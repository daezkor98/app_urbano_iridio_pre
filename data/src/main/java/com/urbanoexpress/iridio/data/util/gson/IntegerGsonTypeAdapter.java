package com.urbanoexpress.iridio.data.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class IntegerGsonTypeAdapter extends TypeAdapter<Integer> {

    @Override
    public Integer read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return 0;
        }
        try {
            return Integer.valueOf(reader.nextString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void write(JsonWriter writer, Integer value) throws IOException {
        if (value == null) {
            writer.value(0);
            return;
        }
        writer.value(value);
    }
}