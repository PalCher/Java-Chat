package Chat.Server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients;
    AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        try {
            ServerSocket serv = new ServerSocket(8189);
            clients = new Vector<>();
            authService = new AuthService();

                authService.connect();

            System.out.println("Сервер запущен, ожидаем подключения...");
            while (true)
            {
                Socket socket = serv.accept();
                System.out.println("Клиент подключен");
                new ClientHandler(this,socket);
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка инициализации сервера");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Не удалось запустить сервис авторизации");
        } finally {
            authService.disconnect();
        }
    }

    public void subscribe (ClientHandler clientHandler)
    {
        clients.add(clientHandler);
        BroadcastClientList();
    }

    public void unsubscribe (ClientHandler clientHandler)
    {
        clients.remove(clientHandler);
        BroadcastClientList();
    }


    public void broadcast (String msg)
    {
        for (ClientHandler i: clients) {
            i.sendMsg(msg);
        }
    }

    public boolean whisper (String nick,String msg )
    {
        for (ClientHandler i: clients) {
           if( i.getNick().equals(nick) )
           {
               i.sendMsg(msg);
               return true;
           }
        }
        return false;
    }

    public boolean nickIsBusy (String nick)
    {
        for (ClientHandler i: clients) {
            if( i.getNick().equals(nick) )
            {
                return true;
            }
        }
        return false;
    }

public void BroadcastClientList () {
        StringBuilder sb = new StringBuilder("/clientsList ");
        for (ClientHandler o: clients) {
            sb.append(o.getNick() + " ");
        }
        String out = sb.toString();
        for (ClientHandler o: clients)
        {
            o.sendMsg(out);
        }
}

}
