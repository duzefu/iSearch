package server.commonutils;

import org.apache.log4j.Logger;

import server.info.config.Log4jConstString;

public class LogU {

	private static LogU loggerUtil = null;
	private static Logger consoleLogger = null;

	private LogU() {

	}

	private static LogU getInstance() {
		if (null == loggerUtil) {
			synchronized (LogU.class) {
				loggerUtil = new LogU();
			}
		}
		return loggerUtil;
	}

	private static Logger getConsoleLogger() {

		if (null == consoleLogger) {
			synchronized (LogU.class) {
				consoleLogger = Logger.getLogger(Log4jConstString.consoleLoggerName);
			}
		}
		return consoleLogger;
	}

	public static Logger getInfoLogger(Class reqClass) {

		return LogU.getInfoLogger(loggerUtil.getClassName(reqClass));
	}
	
	public static Logger getInfoLogger(String strClassName) {

		Logger infoLogger = Logger.getLogger(Log4jConstString.infoLoggerName);
		if (null == infoLogger)
			infoLogger = Logger.getRootLogger();
		if (null == strClassName || "".equals(strClassName))
			return infoLogger;
		Logger ret = Logger.getLogger(strClassName);
		ret.addAppender(infoLogger.getAppender(Log4jConstString.infoAppenderName));
		return ret;
	}
	
	public static Logger getDebugLogger(String strClassName) {

		Logger debugLogger = Logger.getLogger(Log4jConstString.debugLoggerName);
		if (null == debugLogger)
			debugLogger = Logger.getRootLogger();
		if (null == strClassName || "".equals(strClassName))
			return debugLogger;
		Logger ret = Logger.getLogger(strClassName);
		ret.addAppender(debugLogger.getAppender(Log4jConstString.debugAppenderName));
		return ret;
	}

	public static Logger getDebugLogger(Class reqClass) {

		return LogU.getDebugLogger(loggerUtil.getClassName(reqClass));
	}
	
	public static Logger getErrorLogger(String strClassName) {

		Logger errorLogger = Logger.getLogger(Log4jConstString.errorLoggerName);
		if (null == errorLogger)
			errorLogger = Logger.getRootLogger();
		if (null == strClassName || "".equals(strClassName))
			return errorLogger;
		Logger ret = Logger.getLogger(strClassName);
		ret.addAppender(errorLogger.getAppender(Log4jConstString.errorAppenderName));
		return ret;
	}
	
	public static Logger getErrorLogger(Class reqClass) {

		return LogU.getErrorLogger(loggerUtil.getClassName(reqClass));
	}

	public static void printConsole(String className, String msg) {

		if (msg == null)
			msg = "null";
		Logger consoleLogger = LogU.getConsoleLogger(), realLogger = null;
		if (consoleLogger == null)
			consoleLogger = Logger.getRootLogger();
		if (!consoleLogger.isDebugEnabled())
			return;
		if (null != className && !className.isEmpty()) {
			realLogger = Logger.getLogger(className);
			realLogger.addAppender(consoleLogger
					.getAppender(Log4jConstString.consoleAppenderName));
		} else {
			realLogger = consoleLogger;
		}
		realLogger.debug(msg);
	}
	
	public static void printConsole(Class reqClass, String msg) {

		String className=loggerUtil.getClassName(reqClass);
		if (msg == null)
			msg = "null";
		loggerUtil.printConsole(className, msg);
	}
	
	private static String getClassName(Class reqClass){
		
		return null==reqClass?null:reqClass.getName();
	}

	public static void print(String msg){
		printConsole(LogU.class, msg);
	}
}
