package Chat.Server;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;

    public String getNick() {
        return nick;
    }

    public ClientHandler(Server server, Socket socket) {

        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {

                    //timer(1000,e->System.out.println("asdad"));
                    while (true) {
                        String msg = in.readUTF();

                        if (msg.startsWith("/auth ")){
                            String [] data = msg.split("\\s");
                            String newNick = server.getAuthService().getNickByLoginAndPass(data[1], data[2]);
                            if ( newNick!= null){
                                if (!server.nickIsBusy(newNick)) {
                                    nick = newNick;
                                    sendMsg("/authok");
                                    server.subscribe(this);
                                    break;
                                } else {sendMsg("Это ник уже используется");}
                            } else {
                                sendMsg("Неверный логин/пароль");
                            }


                        }



                    }
                    while (true) {
                        String msg = in.readUTF();
////////////////////////////////////////////////////////////////////////////////
                        //ДЗ.
                        if (msg.startsWith("/w " )) {
                            String [] data = msg.split("\\s",3);
                            System.out.println(data[1]);
                            if(server.whisper(data[1], "Whispering from " + nick +": " + data[2])){
                                sendMsg("Whispering to "+ data[1] + ": " + data[2]);

                            }
                            else {
                                sendMsg("Этого пользователя нет в чате");
                            }
                        }

                        System.out.println(nick + ": " + msg);
                        if (msg.equals("/end")) break;
                        if (!msg.startsWith("/w ")) {
                            server.broadcast(nick + ": " + msg);
                        }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    nick = null;
                    server.unsubscribe(this);
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void sendMsg (String msg)
    {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
