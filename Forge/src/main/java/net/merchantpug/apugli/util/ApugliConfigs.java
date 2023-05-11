package net.merchantpug.apugli.util;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

// TODO: Introduce an in-house config library.
public class ApugliConfigs {

    public static class Server {

        public final HitsOnTargetOptions hitsOnTargetOptions;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("hitsOnTarget");
            this.hitsOnTargetOptions = new HitsOnTargetOptions(builder);
            builder.pop();
        }

        public static class HitsOnTargetOptions {

            private final ForgeConfigSpec.IntValue resetTimerTicks;

            public HitsOnTargetOptions(ForgeConfigSpec.Builder builder) {
                this.resetTimerTicks = builder
                        .translation("apugli.config.resetTimerTicks")
                        .defineInRange("resetTimerTicks", 0, 0, Integer.MAX_VALUE);
            }

            public int getResetTimerTicks() {
                return resetTimerTicks.get();
            }

        }

    }

    public static class Client {

        public final FileDownloadOptions fileDownloadOptions;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("fileDownload");
            this.fileDownloadOptions = new FileDownloadOptions(builder);
            builder.pop();
        }

        public static class FileDownloadOptions {
            private final ForgeConfigSpec.ConfigValue<String> fileSizeLimit;
            private final ForgeConfigSpec.IntValue fileConnectionTimeout;

            public FileDownloadOptions(ForgeConfigSpec.Builder builder) {
                this.fileSizeLimit = builder
                        .translation("apugli.config.fileSizeLimit")
                        .define("fileSizeLimit", "1MB");
                this.fileConnectionTimeout = builder
                        .translation("apugli.config.fileConnectionTimeout")
                        .defineInRange("fileConnectionTimeout", 30000,1, Integer.MAX_VALUE);
            }

            public String getFileSizeLimit() {
                return fileSizeLimit.get();
            }

            public int getFileConnectionTimeout() {
                return fileConnectionTimeout.get();
            }

        }

    }

    public static final ForgeConfigSpec CLIENT_SPECS;
    public static final ForgeConfigSpec SERVER_SPECS;

    public static final Client CLIENT;
    public static final Server SERVER;

    static {
        Pair<Client, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Client::new);
        Pair<Server, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(Server::new);
        CLIENT_SPECS = common.getRight();
        SERVER_SPECS = server.getRight();
        CLIENT = common.getLeft();
        SERVER = server.getLeft();
    }

}
