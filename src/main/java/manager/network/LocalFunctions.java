// 
// Decompiled by Procyon v0.5.36
// 

package manager.network;

import game.rules.play.moves.Moves;
import manager.Manager;
import util.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalFunctions
{
    static ServerSocket serverSocket;
    static Socket socket;
    
    public static void initialiseServerSocket(final int port) {
        new Thread(() -> {
            try {
                LocalFunctions.serverSocket = new ServerSocket(port);
                while (true) {
                    LocalFunctions.socket = LocalFunctions.serverSocket.accept();
                    final DataInputStream dis = new DataInputStream(LocalFunctions.socket.getInputStream());
                    final String message = dis.readUTF();
                    System.out.println("message= " + message);
                    String reply = "";
                    if (message.length() >= 9 && message.startsWith("move", 5)) {
                        reply = "move failure";
                        final Context context = Manager.ref().context();
                        final Moves legal = context.game().moves(context);
                        for (int i = 0; i < legal.moves().size(); ++i) {
                            if (i == Integer.parseInt(message.substring(10).trim())) {
                                Manager.ref().applyHumanMoveToGame(context.game().moves(context).moves().get(i));
                                reply = "move success";
                            }
                        }
                        LocalFunctions.initialiseClientSocket(Integer.parseInt(message.substring(0, 4)), reply);
                    }
                    else if (message.length() >= 10 && message.startsWith("legal", 5)) {
                        final Context context = Manager.ref().context();
                        final Moves legal = context.game().moves(context);
                        for (int i = 0; i < legal.moves().size(); ++i) {
                            reply = reply + i + " - " + legal.moves().get(i).getAllActions(context) + "\n";
                        }
                        LocalFunctions.initialiseClientSocket(Integer.parseInt(message.substring(0, 4)), "legal\n" + reply);
                    }
                    else if (message.length() >= 11 && message.startsWith("player", 5)) {
                        reply = Integer.toString(Manager.ref().context().state().mover());
                        LocalFunctions.initialiseClientSocket(Integer.parseInt(message.substring(0, 4)), "player " + reply);
                    }
                    System.out.println("Reply= " + reply);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    LocalFunctions.serverSocket.close();
                    LocalFunctions.socket.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }).start();
    }
    
    public static void initialiseClientSocket(final int port, final String Message) {
        new Thread(() -> {
            try {
                final Socket clientSocket = new Socket("localhost", port);
                final DataOutputStream dout = new DataOutputStream(clientSocket.getOutputStream());
                dout.writeUTF(Message);
                dout.flush();
                dout.close();
                clientSocket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
