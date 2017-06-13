package com.ums.upos.uapi.device.serialport;

interface SerialPortDriver {
    int connect(String cfg);

    int send(in byte[] data, int dataLen);

    int recv(out byte[] buffer, int recvLen, long timeout);

    int disconnect();

    void clrBuffer();
}
