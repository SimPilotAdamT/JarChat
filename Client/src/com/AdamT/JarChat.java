/*
 * JarChat IRC Client Source Code
 * Free and Open-sourced under the GNU GPL v3 Licence
 *
 * Build using the latest JDK 8 to ensure compatibility with all
 * modern devices. Will change JDK once more devices use JRE 11.
 *
 * Last Edited: 2022-05-02 14:13Z by SimPilotAdamT
 */

package com.AdamT;

// Imports
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

// Main class
public class JarChat extends IRCMessageLoop {
    JarChat(String server, int port) { super(server, port); }
    public static void main(String[] args) {
        // Variables (local to this method instead of the class in case any variables with these names or similar are required elsewhere)
        boolean exit; String input; String channel = ""; JarChat client; boolean valid; Scanner con; String server; String port; String nick; String uname; String name;

        // Welcome and entering server details
        System.out.println("\nHi!");
        con = new Scanner(System.in);
        System.out.print("\nEnter server IP/Hostname: ");
        server = con.nextLine();
        System.out.print("Enter server port: ");
        port = con.nextLine();
        valid = false;

        // Check the port is valid (most servers use a port with 4 digits
        while(!valid) { if (isInteger(port) && port.length() == 4) valid=true; else { System.out.print("Error! Invalid port!\nEnter server port: "); port=con.nextLine(); } }

        // Get user's details
        System.out.print("\nEnter nickname: ");
        nick = con.nextLine();
        System.out.print("Enter username (most of the time this is the same as the nickname): ");
        uname = con.nextLine();
        System.out.print("Enter real name: ");
        name = con.nextLine();
        System.out.print("\n");

        // Start the connection
        client = new JarChat(server, Integer.parseInt(port)); client.nick(nick);
        try { client.user(uname, InetAddress.getLocalHost().getHostName(), name); } catch (UnknownHostException ignored) { client.user(uname, "null", name); } client.start();

        // String.equalsIgnoreCase() is used instead of String.equals() because this will save the user lots of hassle with typing commands
        // A long if statement is used instead of a switch statement due to the String.startsWith() method being called, making a switch statement impossible
        exit = false;
        while (!exit) {
            input = con.nextLine();
            if (input.equalsIgnoreCase("/quit")) { exit=true; quit("JarChat Client Terminated"); } // Exit the program should the user send the command
            else if (input.startsWith("/join ")){
                if (!channel.isEmpty()) client.part(channel);
                channel=input.substring(6); // Remove the "/join " part of the command
                client.join(channel);
            }
            else if (input.startsWith("/msg ")) {
                input = input.substring(5); // Remove the "/msg " part of the command
                String[] message = input.split(" ",2); // Split the command into username and message
                privmsg(message[0],message[1],nick); // Send the DM
            }
            else if (input.startsWith("/me ") && !channel.isEmpty()) privmsg(channel,"*"+input.substring(4)+"*",nick); // Fun command used in IRC RP
            else if (input.equalsIgnoreCase("/leave") && !channel.isEmpty()) client.part(channel); // Core command
            else if (!channel.isEmpty()) privmsg(channel,input,nick); // Send a message to the channel the user is in
            else System.out.println("Join a channel before sending messages!"); // Suitable message to let the user know that they aren't in any channel
        }

        // Ensure everything closes gracefully
        con.close();
        System.exit(0);
    }
    private static boolean isInteger(String input){ try { Integer.parseInt(input); return true; } catch (Exception ignored) { return false; } } // To check if the port is a valid number
}