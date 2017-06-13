package com.ums.upos.uapi.device.printer;

import android.graphics.Bitmap;
import com.ums.upos.uapi.device.printer.OnPrintListener;

interface Printer {
    int initPrinter();

    void setConfig(in Bundle bundle);

    int startPrint(in OnPrintListener listener);

    int getStatus();

    int appendPrnStr(String text, int fontsize, boolean isBoldFont);

    int appendImage(in Bitmap bitmap);

    void feedPaper(int value, int unit);

    void cutPaper();
}
