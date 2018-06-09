package Chat.Client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    TextArea textArea;
    @FXML
    TextField msgField;
    @FXML
    HBox authPanel;
    @FXML
    HBox msgPanel;
    @FXML
    TextField loginField;
    @FXML
    TextField passField;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

final String SEREVER_IP = "localhost";
final int SERVER_PORT = 8189;

private boolean authorized;

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
        if (authorized){
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);
            authPanel.setVisible(false);
            authPanel.setManaged(false);
        } else {
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
            authPanel.setVisible(true);
            authPanel.setManaged(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthorized(false);
    }

    public void connect() {
        try {
            socket = new Socket(SEREVER_IP, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread t = new Thread(() -> {

                try {
                    while (true)
                    {
                        String   s = in.readUTF();
                        Timer t1 = new Timer(120000, e-> {if (!s.equals("/authok"))
                        {System.out.println("Соединение закрыто");
                            try {
                                socket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        });
                        t1.start();

                        if (s.equals("/authok"))
                            setAuthorized(true);
                        break;
                    }
                    //textArea.appendText(s + "\n");

                    while (true)
                    {
                        String   s = in.readUTF();
                        textArea.appendText(s + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    setAuthorized(false);

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });

            t.setDaemon(true);
            t.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


public void sendAuthMsg ()
{
    try {
        if (socket == null || socket.isClosed())
        {
            connect();
        }
        out.writeUTF("/auth " + loginField.getText() + " " +  passField.getText());
        loginField.clear();
        passField.clear();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}



