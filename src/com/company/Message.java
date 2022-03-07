package com.company;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class Message {

    private int senderId;
    private int receiverId;
    private Point parameter;

    Message(int senderId, int receiverId, Point parameter) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.parameter = parameter;
    }

    // --------------- GETTERS ------------------------

    public int getSenderId() { return senderId; }

    public int getReceiverId() { return receiverId; }

    public Point getParameter() { return parameter; }
}
