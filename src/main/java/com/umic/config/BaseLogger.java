package com.umic.config; /**
 * Created by umic_ on 2018/3/24.
 */

import org.apache.log4j.*;

public class BaseLogger {
    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    protected Logger getLogger() {
        return LogManager.getLogger(this.getClass());
    }
}
