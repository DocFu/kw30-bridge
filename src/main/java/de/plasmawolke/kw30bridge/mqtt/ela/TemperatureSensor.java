package de.plasmawolke.kw30bridge.mqtt.ela;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.plasmawolke.kw30bridge.hap.BaseHomekitAccessory;
import io.github.hapjava.accessories.TemperatureSensorAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TemperatureSensor extends BaseHomekitAccessory implements TemperatureSensorAccessory, IMqttMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(TemperatureSensor.class);

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Double currentTemperature = Double.valueOf(-30);

    public TemperatureSensor(int accessoryId, String name) {
        super(accessoryId, name);
    }

    @Override
    public CompletableFuture<Double> getCurrentTemperature() {
        return CompletableFuture.completedFuture(currentTemperature);
    }

    @Override
    public void subscribeCurrentTemperature(HomekitCharacteristicChangeCallback callback) {
        setSubscribeCallback(callback);
    }

    @Override
    public void unsubscribeCurrentTemperature() {
        setSubscribeCallback(null);
    }






    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception {


        String json = new String(msg.getPayload());
        List<TemperaturePuckMessage> list = gson.fromJson(json, new TypeToken<List<TemperaturePuckMessage>>() {}.getType());
        if(list.size() > 0){
            String tString = list.get(0).getTemperature();
            currentTemperature =  Double.valueOf(tString);
            logger.info("Got: "+currentTemperature);
            changed();
        }

    }
}
