package com.crypticbit.javelin.js;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestUtils {

    void enableLog(String path, Level level) {
	Logger LOG = Logger.getLogger(path);
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(level);
	LOG.addHandler(handler);
	LOG.setLevel(level);
    }

}