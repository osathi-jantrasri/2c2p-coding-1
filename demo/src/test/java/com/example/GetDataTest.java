package com.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.*;

public class GetDataTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private GetData getData;

    @Before
    public void setUp() {
        getData = new GetData();
    }

    @Test
    public void testLoadValidJsonFile() throws Exception {
        String jsonContent = "[" +
            "{\"transaction_id\":\"1\",\"payment_amount\":10.5,\"currency\":\"USD\",\"created_at\":\"1751512490000\"}," +
            "{\"transaction_id\":\"2\",\"payment_amount\":20.0,\"currency\":\"THB\",\"created_at\":\"1751512500000\"}" +
            "]";
        File file = tempFolder.newFile("test.json");
        Files.write(file.toPath(), jsonContent.getBytes());

        List<Transaction> transactions = getData.load(file.toPath());

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertEquals("1", transactions.get(0).transaction_id());
        assertEquals(10.5, transactions.get(0).payment_amount(), 0.01);
        assertEquals("USD", transactions.get(0).currency());
    }

    @Test
    public void testLoadEmptyJsonArray() throws Exception {
        String jsonContent = "[]";
        File file = tempFolder.newFile("empty.json");
        Files.write(file.toPath(), jsonContent.getBytes());

        List<Transaction> transactions = getData.load(file.toPath());

        assertNotNull(transactions);
        assertEquals(0, transactions.size());
    }

    @Test(expected = Exception.class)
    public void testLoadMissingFile() throws Exception {
        Path filePath = Path.of("/nonexistent/path/data.json");
        getData.load(filePath);
    }

    @Test(expected = Exception.class)
    public void testLoadMalformedJson() throws Exception {
        String jsonContent = "[{invalid json}]";
        File file = tempFolder.newFile("malformed.json");
        Files.write(file.toPath(), jsonContent.getBytes());

        getData.load(file.toPath());
    }
}
