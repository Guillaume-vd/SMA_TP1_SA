package com.company;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class Message {

    private int senderId;
    private int receiverId;
    private String performative;
    private String action;
    private Point parameter;

    Message(int senderId, int receiverId, String performative, String action, Point parameter) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.performative = performative;
        this.action = action;
        this.parameter = parameter;
    }
}
