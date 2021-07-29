package de.plasmawolke.kw30bridge.mqtt.ela;

public class MovementPuckMessage {


    int state;

    int count;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
