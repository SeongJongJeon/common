package com.common.web.common.utils;

/**
 * Created by alex.
 * Date: 2018-12-07
 */
public class FlagUtil {
    public static String getEnvFromArgs(String[] args) {
        String env = "local";
        if (args.length > 0) {
            switch (args[0]) {
                case "dev":
                    env = "dev";
                    break;
                case "prod":
                    env = "prod";
                    break;
            }
        }

        return env;
    }
}
