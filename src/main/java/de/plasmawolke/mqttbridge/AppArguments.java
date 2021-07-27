package de.plasmawolke.mqttbridge;

import com.beust.jcommander.Parameter;

public class AppArguments {


    @Parameter(names = {"-p", "--port"}, description = "The port of the bridge, e.g. 9124")
    private int port = 9124;

    @Parameter(names = {"-bh", "--broker-host"}, description = "The host of the MQTT broker")
    private String brokerHost = "10.4.1.1";

    @Parameter(names = {"-bp", "--broker-port"}, description = "The port of the MQTT broker")
    private int brokerPort = 1883;

    @Parameter(names = {"-rt", "--read-topic"}, description = "The topic to subscribe")
    private String topic = "bluetooth";


    public int getPort() {
        return port;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public int getBrokerPort() {
        return brokerPort;
    }

    public String getTopic() {
        return topic;
    }
}
