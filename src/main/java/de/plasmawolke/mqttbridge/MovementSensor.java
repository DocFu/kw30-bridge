package de.plasmawolke.mqttbridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.hapjava.accessories.MotionSensorAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MovementSensor extends BaseHomekitAccessory implements MotionSensorAccessory, IMqttMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MovementSensor.class);
    private int resetCountDown = 0;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Boolean motionDetected = Boolean.FALSE;
    private int lastCount = -1;


    public MovementSensor(int accessoryId, String name) {
        super(accessoryId, name);
    }

    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception {


        String json = new String(msg.getPayload());
        List<MovementPuckMessage> list = gson.fromJson(json, new TypeToken<List<MovementPuckMessage>>() {
        }.getType());
        logger.info("JSON: " + json);

        if (list.size() > 0) {
            int count = list.get(0).getCount();


            if (lastCount < 0) {
                lastCount = count;
                return;
            }

            if (count > lastCount) {
                motionDetected = Boolean.TRUE;
                lastCount = count;
                changed();
                resetCountDown = 10;
                logger.info("Motion detected!");
            }

            if (resetCountDown > 0) {
                logger.info("Motion detection will be reset after " + resetCountDown + " messages.");
                resetCountDown--;
                if (resetCountDown == 0) {
                    motionDetected = Boolean.FALSE;
                    lastCount = count;
                    changed();
                }
            }


        }

    }

    @Override
    public CompletableFuture<Boolean> getMotionDetected() {
        return CompletableFuture.completedFuture(motionDetected);
    }

    @Override
    public void subscribeMotionDetected(HomekitCharacteristicChangeCallback callback) {
        setSubscribeCallback(callback);
    }

    @Override
    public void unsubscribeMotionDetected() {
        setSubscribeCallback(null);
    }
}
