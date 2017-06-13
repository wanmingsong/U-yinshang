package com.ums.upos.uapi.device.led;

interface LEDDriver {
    void setLed(int light, boolean isOn);
}
