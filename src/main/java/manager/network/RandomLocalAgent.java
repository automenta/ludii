// 
// Decompiled by Procyon v0.5.36
// 

package manager.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLocalAgent
{
    static final int playerNumber = 2;
    static final int portNumberLudii = 4444;
    static final int portNumberAgent = 5555;
    static int currentPlayerNumber;
    static String currentLegalMoves;
    static ServerSocket serverSocket;
    static Socket socket;
    
    public static void initialiseServerSocket(final int port) {
        new Thread(() -> {
            try {
                RandomLocalAgent.serverSocket = new ServerSocket(port);
                while (true) {
                    RandomLocalAgent.socket = RandomLocalAgent.serverSocket.accept();
                    final DataInputStream dis = new DataInputStream(RandomLocalAgent.socket.getInputStream());
                    final String message = dis.readUTF();
                    if (message.startsWith("legal")) {
                        RandomLocalAgent.currentLegalMoves = message.substring(6);
                    }
                    if (message.startsWith("player")) {
                        RandomLocalAgent.currentPlayerNumber = Integer.parseInt(message.substring(7));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    RandomLocalAgent.serverSocket.close();
                    RandomLocalAgent.socket.close();
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }).start();
    }
    
    public static void initialiseClientSocket(final int port, final String Message) {
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
    }
    
    public static void main(final String[] args) {
        initialiseServerSocket(5555);
        final Runnable runnableUpdateValues = () -> {
            while (true) {
                RandomLocalAgent.initialiseClientSocket(4444, "5555 player");
                RandomLocalAgent.initialiseClientSocket(4444, "5555 legal");
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException ex) {}
            }
        };
        final Thread repeatUpdateValuesThread = new Thread(runnableUpdateValues);
        repeatUpdateValuesThread.start();
        final long timeInterval = 100L;
        final Runnable runnableMakeMove = () -> {
            while (true) {
                if (2 == RandomLocalAgent.currentPlayerNumber) {
                    final String[] allLegalMoves = RandomLocalAgent.currentLegalMoves.split("\n");
                    final int randomNum = ThreadLocalRandom.current().nextInt(0, allLegalMoves.length);
                    RandomLocalAgent.initialiseClientSocket(4444, "5555 move " + randomNum);
                }
                try {
                    Thread.sleep(100L);
                }
                catch (InterruptedException ex) {}
            }
        };
        final Thread repeatMakeMoveThread = new Thread(runnableMakeMove);
        repeatMakeMoveThread.start();
    }
    
    static {
        RandomLocalAgent.currentPlayerNumber = 0;
        RandomLocalAgent.currentLegalMoves = "";
    }
}
