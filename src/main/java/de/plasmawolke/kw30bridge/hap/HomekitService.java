package de.plasmawolke.kw30bridge.hap;


import de.plasmawolke.kw30bridge.Version;
import io.github.hapjava.server.impl.HomekitRoot;
import io.github.hapjava.server.impl.HomekitServer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Random;

public class HomekitService {

    private static final Logger logger = LoggerFactory.getLogger(HomekitService.class);

    private static final File authFile = new File("kw30-bridge-auth.bin");


    private static final String manufacturer = "https://github.com/DocFu/kw30-bridge";
    private static final String model = "KW30 Bridge";
    private static final String serialNumber = "21";
    private static final String firmwareRevision = Version.getVersionAndRevision();
    private static final String hardwareRevision = "-";


    private int port;


    public HomekitService(int port) {
        this.port = port;
    }


    public void runWithAccessories(BaseHomekitAccessory... accessories) throws Exception {

        HomekitServer homekitServer = new HomekitServer(port);
        AuthInfo authInfo = createAuthInfo();
        HomekitRoot bridge = homekitServer.createBridge(authInfo, model, manufacturer, model, serialNumber, firmwareRevision, hardwareRevision);

        for (BaseHomekitAccessory accessory : accessories) {
            logger.info("Adding HomeKit Accessory: " + accessory);
            bridge.addAccessory(accessory);
        }

        bridge.start();

        authInfo.onChange(state -> {
            try {
                logger.debug("Updating auth file after state has changed.");
                FileOutputStream fileOutputStream = new FileOutputStream(authFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(state);
                objectOutputStream.flush();
                objectOutputStream.close();
            } catch (IOException e) {
                logger.error("Updating auth file has failed!", e);
            }
        });


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping homekit service.");
            homekitServer.stop();
        }));


        String pp = authInfo.getPin();

        logger.info("Started homekit service successfully on port " + port + ".");
        logger.info("****************");
        logger.info("**    PIN:    **");
        logger.info("** " + pp + " **");
        logger.info("****************");
    }


    private AuthInfo createAuthInfo() throws Exception {
        AuthInfo authInfo;
        if (authFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(authFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            try {
                logger.debug("Using state from existing auth file.");
                AuthState authState = (AuthState) objectInputStream.readObject();
                authInfo = new AuthInfo(authState);
            } finally {
                objectInputStream.close();
            }
        } else {
            authInfo = new AuthInfo(createRandomPin());
        }


        return authInfo;
    }


    private String createRandomPin() {
        Random random = new Random();

        String number1 = String.valueOf(random.nextInt(999));
        String number2 = String.valueOf(random.nextInt(99));
        String number3 = String.valueOf(random.nextInt(999));

        number1 = StringUtils.leftPad(number1, 3, "0");
        number2 = StringUtils.leftPad(number2, 2, "0");
        number3 = StringUtils.leftPad(number3, 3, "0");

        return number1 + "-" + number2 + "-" + number3;
    }


}
