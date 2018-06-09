package Chat.Server;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import java.awt.event.ActionListener;

public class MainServer {
    public static void main(String[] args) {

        new Server();

    }

}
