package de.plasmawolke.mqttbridge;

import com.beust.jcommander.JCommander;
import de.plasmawolke.mqttbridge.hap.HomekitService;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttBridge {

    private static final Logger logger = LoggerFactory.getLogger(MqttBridge.class);


    public static void main(String[] args) {

        logger.info("Running MQTT Bridge " + Version.getVersionAndRevision() + "...");

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


        TemperatureSensor temperatureSensor = new TemperatureSensor(3, "ELA Temp");
        MovementSensor movementSensor = new MovementSensor(4, "ELA Mov");

        try {
            MqttClient client = new MqttClient("tcp://" + appArguments.getBrokerHost() + ":" + appArguments.getBrokerPort(), "MQTT-Bridge");

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            client.connect(options);


            client.subscribe("puck-temp", temperatureSensor);
            client.subscribe("puck-mov", movementSensor);

        } catch (Exception e) {
            logger.error("x", e);
            exitWithError("Error while starting MQTT client: " + e.getMessage());
        }


        HomekitService homekitService = new HomekitService(appArguments.getPort());
        try {
            homekitService.runWithAccessories(temperatureSensor, movementSensor);
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
