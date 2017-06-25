package com.squirrel.logger;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;


public class SquirrelLogger implements Logger {
	
	public final org.slf4j.Logger logger;

	public SquirrelLogger(String name) {
		this.logger = LoggerFactory.getLogger(name);
	}

	@Override
	public void debug(String arg0) {
		logger.debug(arg0);
	}

	@Override
	public void debug(String arg0, Object arg1) {
		logger.debug(arg0, arg1);
	}

	@Override
	public void debug(String arg0, Object... arg1) {
		logger.debug(arg0, arg1);
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
		logger.debug(arg0, arg1);
	}

	@Override
	public void debug(Marker arg0, String arg1) {
		logger.debug(arg0, arg1);
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2) {
		logger.debug(arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2) {
		logger.debug(arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object... arg2) {
		logger.debug(arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Throwable arg2) {
		logger.debug(arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
		logger.debug(arg0, arg1, arg2, arg3);
	}

	@Override
	public void error(String arg0) {
		logger.error(arg0);
	}

	@Override
	public void error(String arg0, Object arg1) {
		logger.error(arg0, arg1);
	}

	@Override
	public void error(String arg0, Object... arg1) {
		logger.error(arg0, arg1);
	}

	@Override
	public void error(String arg0, Throwable arg1) {
		logger.error(arg0, arg1);
	}

	@Override
	public void error(Marker arg0, String arg1) {
		logger.error(arg0, arg1);
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2) {
		logger.error(arg0, arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2) {
		logger.error(arg0, arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Object... arg2) {
		logger.error(arg0, arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Throwable arg2) {
		logger.error(arg0, arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
		logger.error(arg0, arg1, arg2, arg3);
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public void info(String arg0) {
		logger.info(arg0);
	}

	@Override
	public void info(String arg0, Object arg1) {
		logger.info(arg0, arg1);
	}

	@Override
	public void info(String arg0, Object... arg1) {
		logger.info(arg0, arg1);
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		logger.info(arg0, arg1);
	}

	@Override
	public void info(Marker arg0, String arg1) {
		logger.info(arg0, arg1);
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2) {
		logger.info(arg0, arg1, arg2);
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2) {
		logger.info(arg0, arg1, arg2);
	}

	@Override
	public void info(Marker arg0, String arg1, Object... arg2) {
		logger.info(arg0, arg1, arg2);
	}

	@Override
	public void info(Marker arg0, String arg1, Throwable arg2) {
		logger.info(arg0, arg1, arg2);
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
		logger.info(arg0, arg1, arg2, arg3);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isDebugEnabled(Marker arg0) {
		return logger.isDebugEnabled(arg0);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isErrorEnabled(Marker arg0) {
		return logger.isErrorEnabled(arg0);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isInfoEnabled(Marker arg0) {
		return logger.isInfoEnabled(arg0);
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public boolean isTraceEnabled(Marker arg0) {
		return logger.isTraceEnabled(arg0);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isWarnEnabled(Marker arg0) {
		return logger.isWarnEnabled(arg0);
	}

	@Override
	public void trace(String arg0) {
		logger.trace(arg0);
	}

	@Override
	public void trace(String arg0, Object arg1) {
		logger.trace(arg0, arg1);
	}

	@Override
	public void trace(String arg0, Object... arg1) {
		logger.trace(arg0, arg1);
	}

	@Override
	public void trace(String arg0, Throwable arg1) {
		logger.trace(arg0, arg1);
	}

	@Override
	public void trace(Marker arg0, String arg1) {
		logger.trace(arg0, arg1);
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2) {
		logger.trace(arg0, arg1, arg2);
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2) {
		logger.trace(arg0, arg1, arg2);
	}

	@Override
	public void trace(Marker arg0, String arg1, Object... arg2) {
		logger.trace(arg0, arg1, arg2);
	}

	@Override
	public void trace(Marker arg0, String arg1, Throwable arg2) {
		logger.trace(arg0, arg1, arg2);
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
		logger.trace(arg0, arg1, arg2, arg3);
	}

	@Override
	public void warn(String arg0) {
		logger.warn(arg0);
	}

	@Override
	public void warn(String arg0, Object arg1) {
		logger.warn(arg0, arg1);
	}

	@Override
	public void warn(String arg0, Object... arg1) {
		logger.warn(arg0, arg1);
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		logger.warn(arg0, arg1);
	}

	@Override
	public void warn(Marker arg0, String arg1) {
		logger.warn(arg0, arg1);
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2) {
		logger.warn(arg0, arg1, arg2);
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2) {
		logger.warn(arg0, arg1, arg2);
	}

	@Override
	public void warn(Marker arg0, String arg1, Object... arg2) {
		logger.warn(arg0, arg1, arg2);
	}

	@Override
	public void warn(Marker arg0, String arg1, Throwable arg2) {
		logger.warn(arg0, arg1, arg2);
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
		logger.warn(arg0, arg1, arg2, arg3);
	}

}