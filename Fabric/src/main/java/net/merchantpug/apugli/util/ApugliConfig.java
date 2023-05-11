package net.merchantpug.apugli.util;

import eu.midnightdust.lib.config.MidnightConfig;

// TODO: Introduce an in-house config library.
public class ApugliConfig extends MidnightConfig {
    @Comment
    public static Comment fileDownloadOptions;
    @Entry(name = "apugli.config.fileSizeLimit")
    public static String fileSizeLimit = "1MB";
    @Entry(name = "apugli.config.fileConnectionTimeout")
    public static int fileConnectionTimeout = 30000;

    @Server
    public static int resetTimerTicks = 100;
    @Server
    public static boolean performVersionCheck = true;
}
