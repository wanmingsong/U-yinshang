package com.ums.upos.uapi.device.scanner;

import com.ums.upos.uapi.device.scanner.OnScannedListener;

interface InnerScanner {
    void initScanner(in Bundle bundle);

    int startScan(int timeout, in OnScannedListener listener);

    void stopScan();
}
