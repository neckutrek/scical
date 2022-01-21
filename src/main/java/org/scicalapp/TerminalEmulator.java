package org.scicalapp;

import java.util.Scanner;

public class TerminalEmulator {

    public static String read()
    {
        return read("> ");
    }

    public static String read(String prompt)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        String cmd = scanner.nextLine();
        return cmd;
    }
}
