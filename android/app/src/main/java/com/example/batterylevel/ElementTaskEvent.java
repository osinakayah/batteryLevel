package com.example.batterylevel;

public class ElementTaskEvent {
    private final byte[] imageByte;
    private final String message;

    public ElementTaskEvent(byte[] imageByte, String message) {
        this.imageByte = imageByte;
        this.message = message;
    }

    public byte[] getImageByte() {
        return imageByte;
    }

    public String getMessage() {
        return message;
    }
}