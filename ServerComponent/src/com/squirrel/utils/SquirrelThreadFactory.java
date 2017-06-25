package com.squirrel.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SquirrelThreadFactory implements ThreadFactory {

	private final AtomicInteger integer = new AtomicInteger(1);
	private String prefix = "";

	public SquirrelThreadFactory(String prefix) {
		this.prefix = prefix;
	}

	public Thread newThread(Runnable r) {
		return new Thread(r, prefix + "-" + integer.getAndIncrement());
	}

}
