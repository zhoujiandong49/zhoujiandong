package JAVATEST;

import java.io.*;
import java.net.*;
import java.util.*;

public class Service {
    boolean started = false;
    ServerSocket ss = null;

    List<Service.Client> clients = new ArrayList<Client>();

    public static void main(String[] args) {
        new Service().start();
    }

    public void start() {
        try {
            ss = new ServerSocket(8888);
            started = true;
        } catch (BindException e) {
            System.out.println("已经有客户端链接....");
            System.out.println("请退出该链接");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }//new出一个ServerSocket，并将started状态改为true

        try {

            while(started) {
                Socket s = ss.accept();
                Client c = new Client(s);
                System.out.println("有新客户端链接!");
                new Thread(c).start();
                clients.add(c);
                //dis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    class Client implements Runnable {
        private Socket s;
        private DataInputStream dis = null;
        private DataOutputStream dos = null;
        private boolean bConnected = false;

        public Client(Socket s) {
            this.s = s;
            try {
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());
                bConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String str) {
            try {
                dos.writeUTF(str);
            } catch (IOException e) {
                clients.remove(this);
                System.out.println(".......");
            }
        }

        public void run() {
            try {
                while(bConnected) {
                    String str = dis.readUTF();
                    // System.out.println(str);
                    for(int i=0; i<clients.size(); i++) {
                        Service.Client c = clients.get(i);
                        c.send(str);
                    }
                }
            } catch (EOFException e) {
                System.out.println("客户端断开链接!");
            } catch (SocketException e) {
                System.out.println("客户端断开链接!");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(dis != null) dis.close();
                    if(dos != null) dos.close();
                    if(s != null) s.close();
                    s.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
