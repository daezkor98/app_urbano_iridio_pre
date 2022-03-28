package com.urbanoexpress.iridio3.data.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LongGsonTypeAdapter extends TypeAdapter<Long> {

    @Override
    public Long read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return 0L;
        }
        try {
            return Long.valueOf(reader.nextString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    @Override
    public void write(JsonWriter writer, Long value) throws IOException {
        if (value == null) {
            writer.value(0L);
            return;
        }
        writer.value(value);
    }
}