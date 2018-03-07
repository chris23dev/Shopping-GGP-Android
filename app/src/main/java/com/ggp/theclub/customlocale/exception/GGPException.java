package com.ggp.theclub.customlocale.exception;

import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by john.curtis on 4/17/17.
 */
public class GGPException extends RuntimeException{
    public GGPException() {
    }

    public GGPException(String message) {
        super(message);
    }

    public GGPException(String message, Throwable cause) {
        super(message, cause);
    }

    public GGPException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GGPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
