package com.example.networkdemo;

public class Winner {
    private char token;
    private String room_id;

    Winner(char token, String room_id) {
        this.token = token;
        this.room_id = room_id;
    }

    void setToken(char token) { this.token = token;}
    void setRoom_id(String room_id) {this.room_id = room_id;}
    char getToken() {return token;}
    String getRoom_id() {return room_id;}
}
