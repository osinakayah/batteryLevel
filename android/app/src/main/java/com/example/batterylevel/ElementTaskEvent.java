package com.example.batterylevel;

public class ElementTaskEvent {
    final byte[] imageByte;
    final String message;

    public ElementTaskEvent(byte[] imageByte, String message) {
        this.imageByte = imageByte;
        this.message = message;
    }
}