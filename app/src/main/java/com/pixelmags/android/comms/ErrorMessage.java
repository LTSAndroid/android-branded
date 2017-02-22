package com.pixelmags.android.comms;

/**
 * Created by likith on 2/21/17.
 */
public class ErrorMessage {

    private boolean isError = false;
    private String errorMessage;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "isError=" + isError +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
