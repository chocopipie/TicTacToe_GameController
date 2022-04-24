package com.example.networkdemo;

import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static com.example.networkdemo.HumanTypes.CREATE_GAME;

public class Main extends Application {

    ObjectOutputStream toServer = null;
    ObjectInputStream fromServer = null;
    int handlerNumber;
    MessageHandler handler;


    @Override
    public void start(Stage primaryStage) {

        // Button to send message.
        Button sendButton = new Button("send");
        sendButton.setDefaultButton(true);

        // Panel p to hold the label and text field
        BorderPane paneForTextField = new BorderPane();
        paneForTextField.setPadding(new Insets(5, 5, 5, 5));
        paneForTextField.setStyle("-fx-border-color: green");
        paneForTextField.setLeft(new Label("Enter message: "));
        paneForTextField.setRight(sendButton);

        TextField tf = new TextField();
        tf.setAlignment(Pos.BOTTOM_RIGHT);
        paneForTextField.setCenter(tf);

        BorderPane mainPane = new BorderPane();
        // Text area to display contents
        TextArea ta = new TextArea();
        mainPane.setCenter(new ScrollPane(ta));
        mainPane.setBottom(paneForTextField);

        // Create a scene and place it in the stage
        Scene scene = new Scene(mainPane, 450, 200);
        primaryStage.setTitle("Game Controller"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 8000);
            // Socket socket = new Socket("130.254.204.36", 8000);
            // Socket socket = new Socket("drake.Armstrong.edu", 8000);

            // Create an output stream to send data to the server
            toServer = new ObjectOutputStream(socket.getOutputStream());

            // Create an input stream to receive data from the server
            fromServer = new ObjectInputStream(socket.getInputStream());

            handler = new MessageHandler(toServer, fromServer);

        } catch (IOException ex) {
            ta.appendText(ex.toString() + '\n');
        }

        Thread sendMessage = new Thread( () -> {
                while (true) {
                    try {
                        // read the message sent to this client
                        Object msg = fromServer.readObject();

                        // Downcast message from Object
                        Message messageReceived = (Message)msg;
                        // Downcast humanTypes from Typess
                        HumanTypes messageType = (HumanTypes) messageReceived.getType();

                        // display the text area
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                // Display to the text area
                                ta.appendText(messageReceived.getType().getDescription() + "\n");
                            }
                        });

                        switch (messageType) {
                            
                            case CREATE_GAME: handler.gameCreatedHandler(messageReceived);
                                break;
                            case MAKE_MOVE: handler.makeMoveHandler(messageReceived);
                                break;
                            case QUIT: handler.quitHandler(messageReceived);
                                break;
                            case REMATCH_ACCEPT: handler.rematchAcceptHandler(messageReceived);
                                break;
                            case REMATCH_REJECT: handler.rematchRejectHandler(messageReceived);
                                break;
                        }

                    } catch (IOException ex) {
                        System.out.println("Server Disconnected");
                        break;
                    }
                    catch (ClassNotFoundException ex){
                        ex.printStackTrace();
                    }
                }
            });
            sendMessage.start();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
