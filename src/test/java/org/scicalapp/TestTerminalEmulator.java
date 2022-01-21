package org.scicalapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.scicalapp.TerminalEmulator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTerminalEmulator
{
    private String actualInput = "myCommand";
    private ByteArrayOutputStream out;

    @BeforeEach
    public void setUp() {
        // mock the System in/out streams for testing I/O behaviour
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        System.setIn(new ByteArrayInputStream(actualInput.getBytes()));
    }

    @Test
    public void testReadDefault() {
        String input = TerminalEmulator.read();
        assertEquals(input, actualInput);
        String outString = out.toString();
        assertEquals(outString, "> ");
    }

    @Test
    public void testReadCustomPrompt() {
        String prompt = "$> ";
        String input = TerminalEmulator.read(prompt);
        assertEquals(input, actualInput);
        String outString = out.toString();
        assertEquals(outString, prompt);
    }
}