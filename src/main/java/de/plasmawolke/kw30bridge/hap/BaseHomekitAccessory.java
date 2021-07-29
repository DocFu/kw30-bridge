package de.plasmawolke.kw30bridge.hap;

import io.github.hapjava.accessories.HomekitAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public abstract class BaseHomekitAccessory implements HomekitAccessory {

    private static final Logger logger = LoggerFactory.getLogger(BaseHomekitAccessory.class);

    private HomekitCharacteristicChangeCallback subscribeCallback = null;

    private int accessoryId;
    private String name;

    private String serialNumber = "1234-5678"; // TODO
    private String model = getClass().getSimpleName();
    private String manufacturer = "KW30 Bridge";
    private String firmwareRevision = "0.1"; // TODO


    public BaseHomekitAccessory(int accessoryId, String name) {
        this.accessoryId = accessoryId;
        this.name = name;
    }


    @Override
    public int getId() {
        return accessoryId;
    }

    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(name);
    }

    @Override
    public void identify() {
        logger.info("Identifying " + this.toString());
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture(serialNumber);
    }

    @Override
    public CompletableFuture<String> getModel() {
        return CompletableFuture.completedFuture(model);
    }

    @Override
    public CompletableFuture<String> getManufacturer() {
        return CompletableFuture.completedFuture(manufacturer);
    }

    @Override
    public CompletableFuture<String> getFirmwareRevision() {
        return CompletableFuture.completedFuture(firmwareRevision);
    }


    public HomekitCharacteristicChangeCallback getSubscribeCallback() {
        return subscribeCallback;
    }

    public void setSubscribeCallback(HomekitCharacteristicChangeCallback subscribeCallback) {
        this.subscribeCallback = subscribeCallback;
    }

    public void changed() {
        if (subscribeCallback != null) {
            subscribeCallback.changed();
        }
    }
}
