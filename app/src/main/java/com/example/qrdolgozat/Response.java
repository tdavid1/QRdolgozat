package com.example.qrdolgozat;

public class Response {
    public Response(int responsecode, String responsemessage) {
        this.responsecode = responsecode;
        this.responsemessage = responsemessage;
    }

    public int getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(int responsecode) {
        this.responsecode = responsecode;
    }

    public String getResponsemessage() {
        return responsemessage;
    }

    public void setResponsemessage(String responsemessage) {
        this.responsemessage = responsemessage;
    }

    private int responsecode;
    private String responsemessage;
}
