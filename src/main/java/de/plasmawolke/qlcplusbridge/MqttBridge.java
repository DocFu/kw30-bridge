package de.plasmawolke.qlcplusbridge;

import com.beust.jcommander.JCommander;
import de.plasmawolke.qlcplusbridge.hap.HomekitService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttBridge {

    private static final Logger logger = LoggerFactory.getLogger(MqttBridge.class);


    public static void main(String[] args) {

        logger.info("Running QLC+ Bridge " + Version.getVersionAndRevision() + "...");

        AppArguments appArguments = new AppArguments();
        JCommander commander = JCommander.newBuilder()
                .addObject(appArguments)
                .build();
        try {
            commander.parse(args);
        } catch (Exception e) {
            commander.usage();
            exitWithError("Invalid command line arguments detected: " + e.getMessage());
        }


        HomekitService homekitService = new HomekitService(appArguments.getPort());
        try {
            homekitService.runWithAccessories();
        } catch (Exception e) {
            exitWithError("Error while starting HomekitService: " + e.getMessage());
        }


    }


    private static void exitWithError(String message) {
        if (StringUtils.isBlank(message)) {
            logger.error("Exiting with undefined error.");
        } else {
            logger.error(message);
        }

        logger.info("Exiting with error.");
        System.exit(1);
    }


}
