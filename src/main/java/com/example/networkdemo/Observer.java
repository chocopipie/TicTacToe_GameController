package com.example.networkdemo;

//know when update from ui board occurs
public interface Observer {
    public void update(char token, int x, int y);
}
