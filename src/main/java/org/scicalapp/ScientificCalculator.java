package org.scicalapp;

import org.scical.Expression;

public class ScientificCalculator {

    public static void main(String[] args)
    {
        CommandParser cmdParser = new CommandParser();

        int run = 1;
        while (run != 0)
        {
            String input = TerminalEmulator.read();
            CommandCode cmdCode = cmdParser.parse(input);
            switch (cmdCode)
            {
                case CMD_QUIT:
                    run = 0;
                    break;
                case CMD_INVALID:
                    System.out.println("Invalid command '" + input + "'!");
                    break;
                case CMD_NOTACOMMAND:
                    Expression expr = new Expression(input);
                    double answer = expr.eval();
                    System.out.println(answer);
                    break;
            }
        }
    }
}
