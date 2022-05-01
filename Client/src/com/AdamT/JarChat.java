/*
 * JarChat IRC Client Source Code
 * Free and Open-sourced under the GNU GPL v3 Licence
 *
 * Build using the latest JDK 8 to ensure compatibility with all
 * modern devices. Will change JDK once more devices use JRE 11.
 *
 * Last Edited: 2022-05-01 14:32Z by SimPilotAdamT
 */

package com.AdamT;

// Imports
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

// Main class
public class JarChat extends IRCMessageLoop {
    JarChat(String server, int port) {super(server, port);}
    public static void main(String[] args) {

        // Variables
        boolean exit;
        String input;
        String channel = "";
        JarChat client;
        boolean valid;
        Scanner con;
        String server;
        String port;
        String nick;
        String uname;
        String name;

        // Welcome and entering server details
        System.out.println("\nHi!");
        con = new Scanner(System.in);
        System.out.print("\nEnter server IP/Hostname: ");
        server = con.nextLine();
        System.out.print("Enter server port: ");
        port = con.nextLine();
        valid = false;
        // Check the port is valid
        while(!valid) {
            if (isInteger(port) && port.length() == 4) valid=true;
            else {
                System.out.print("Error! Invalid port!\nEnter server port: ");
                port=con.nextLine();
            }
        }

        // Get user's details
        System.out.print("\nEnter nickname: ");
        nick = con.nextLine();
        System.out.print("Enter username: ");
        uname = con.nextLine();
        System.out.print("Enter real name: ");
        name = con.nextLine();
        System.out.print("\n");

        // start the connection
        client = new JarChat(server, Integer.parseInt(port));
        client.nick(nick);
        try {client.user(uname, InetAddress.getLocalHost().getHostName(), name);} catch (UnknownHostException ignored) {client.user(uname, "null", name);}
        client.start();

        // Allow the user to chat and send commands
        exit = false;
        while (!exit) {
            input = con.nextLine();
            if (input.equalsIgnoreCase("/quit")) {
                exit=true;
                quit("JarChat Client Terminated");
            }
            else if (input.startsWith("/join ")){
                if (!channel.isEmpty()) client.part(channel);
                channel=input.substring(6);
                client.join(channel);
            }
            else if (input.startsWith("/msg ")) {
                input = input.substring(5);
                String[] message = input.split(" ",2);
                privmsg(message[0],message[1],nick);
            }
            else if (input.startsWith("/me ") && !channel.isEmpty()) privmsg(channel,"*"+input.substring(4)+"*",nick);
            else if (input.equalsIgnoreCase("/leave") && !channel.isEmpty()) client.part(channel);
            else if (!channel.isEmpty()) privmsg(channel,input,nick);
            else System.out.println("Last input disregarded");
        }
        // Ensure everything closes gracefully
        con.close();
        System.exit(0);
    }
    private static boolean isInteger(String input){try{Integer.parseInt(input);return true;}catch(Exception ignored){return false;}} // To check if the port is a valid number
}