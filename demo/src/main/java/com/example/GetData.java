package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GetData {
    public List<Transaction> load(Path filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(
            filePath.toFile(),
            new TypeReference<List<Transaction>>() {
            }
        );
    }
}
