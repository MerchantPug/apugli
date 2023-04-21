package net.merchantpug.apugli.platform.services;

public interface IConfigHelper {

    String getFileSizeLimit();

    int getFileConnectionTimeout();

    int getResetTimerTicks();

    boolean shouldPerformVersionCheck();

}
