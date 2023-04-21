package net.merchantpug.apugli.util;

import eu.midnightdust.lib.config.MidnightConfig;

// TODO: Introduce an in-house config library to replace MidnightLib.
public class ApugliConfig extends MidnightConfig {
    @Comment
    public static Comment fileDownloadOptions;
    @Entry
    public static String fileSizeLimit = "1MB";
    @Entry
    public static int fileConnectionTimeout = 30000;

    @Comment
    public static Comment hitsOnTargetOptions;
    @Entry
    public static int resetTimerTicks = 100;

    @Server
    public static boolean performVersionCheck = true;
}
