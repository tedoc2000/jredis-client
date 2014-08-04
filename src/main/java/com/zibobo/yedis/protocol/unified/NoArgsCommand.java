package com.zibobo.yedis.protocol.unified;

public class NoArgsCommand extends FixedArgsCommand {

    public NoArgsCommand(String command) {
        super(command, 0);
    }
}
