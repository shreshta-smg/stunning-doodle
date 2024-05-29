package tech.shreshtasmg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class JsonFileReader {
    private final ObjectMapper mapper;

    @Inject
    public JsonFileReader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> List<T> readFile(String fileContents, Class<T> elementClass) throws IOException {
        CollectionType listType =
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, elementClass);
        return mapper.readValue(fileContents, listType);
    }
}
