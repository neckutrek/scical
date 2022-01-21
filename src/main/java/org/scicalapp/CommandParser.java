package org.scicalapp;

public class CommandParser {

    public CommandCode parse(String cmd) {
        if (cmd.compareTo(":quit") == 0 || cmd.compareTo(":q") == 0) {
            return CommandCode.CMD_QUIT;
        }
        else if (cmd.charAt(0) == ':') {
            return CommandCode.CMD_INVALID;
        }
        return CommandCode.CMD_NOTACOMMAND;
    }
}
