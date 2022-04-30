/*
 * JarChat IRC Client Source Code
 * Free and Open-sourced under the GNU GPL v3 Licence
 *
 * Build using the latest JDK 8 to ensure compatibility with all
 * modern devices. Will change JDK once more devices use JDK 11.
 *
 * Last Edited: 2022-04-29 17:45Z by SimPilotAdamT
 */

package com.AdamT;

//Imports
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class JarChat extends IRCMessageLoop {
    static boolean exit;
    static String input;
    static String channel;
    static JarChat client;
    static boolean valid;
    JarChat(String server, int port) {super(server, port);}
    public static void main(String[] args) {
        System.out.println("\nHi!");
        Scanner con=new Scanner(System.in);
        System.out.print("\nEnter server IP/Hostname: ");
        String server = con.nextLine();
        System.out.print("Enter server port: ");
        String port = con.nextLine();
        valid = false;
        while(!valid) {
            if (isInteger(port) && port.length() == 4) valid=true;
            else {
                System.out.print("\n\nError! Invalid port!\nEnter server port: ");
                port=con.nextLine();
            }
        }

        System.out.print("\nEnter nickname: ");
        String nick=con.nextLine();
        System.out.print("Enter username: ");
        String uname=con.nextLine();
        System.out.print("Enter real name: ");
        String name=con.nextLine();
        System.out.print("\n");

        client = new JarChat(server, Integer.parseInt(port));
        client.nick(nick);
        try {
            client.user(uname, InetAddress.getLocalHost().getHostName(), name);
        } catch (UnknownHostException e) {
            client.user(uname, "null", name);
        }
        client.start();
        exit = false;
        while (!exit) {
            input = con.nextLine();
            if (input.equalsIgnoreCase("/quit")) {
                exit=true;
                quit("JarChat Client Terminated");
            }
            else if (input.startsWith("/join ")){
                if(!channel.isEmpty()) client.part(channel);
                channel=input.substring(6);
                client.join(channel);
            }
            else if (input.equalsIgnoreCase("/leave")) client.part(channel);
            else if (input.startsWith("/msg ")) privmsg(input.substring(5,input.indexOf(" ")),input.substring(input.indexOf(" ")+1),nick);
            else if (!channel.isEmpty()) privmsg(channel,input,nick);
        }
        con.close();System.exit(0);
    }
    private static boolean isInteger(String input) {try {Integer.parseInt(input);return true;} catch(Exception e) {return false;}}
}

// All the classes below this line are taken from Kaecy's gist at https://gist.github.com/kaecy/286f8ad334aec3fcb588516feb727772
abstract class IRCMessageLoop extends Thread {
    Socket server;
    static OutputStream out;
    ArrayList<String> channelList;
    boolean initial_setup_status;

    IRCMessageLoop(String serverName, int port) {channelList = new ArrayList<>();try {server = new Socket(serverName, port);out = server.getOutputStream();} catch (IOException info) {info.printStackTrace();}}

    static void send(String text) {byte[] bytes = (text + "\r\n").getBytes();try {out.write(bytes);} catch (IOException info) {info.printStackTrace();}}

    void nick(String nickname) {String msg = "NICK " + nickname;send(msg);}

    void user(String username, String hostname, String realname) {String msg = "USER " + username + " " + hostname + " " + "null" +  " :" + realname;send(msg);}

    void join(String channel) {if (!initial_setup_status) {channelList.add(channel);return;}String msg = "JOIN " + channel;send(msg);}

    void part(String channel) {String msg = "PART " + channel;send(msg);}

    static void privmsg(String to, String text, @Nullable String from) {String msg = "PRIVMSG " + to + " :" + text;send(msg);System.out.println("PRIVMSG: " + from + ": " + text);}

    void pong(String server) {String msg = "PONG " + server;send(msg);}

    static void quit(String reason) {String msg = "QUIT :Quit: " + reason;send(msg);}

    void initial_setup() {initial_setup_status = true;for (String channel: channelList) {join(channel);}} // now join the channels. you need to wait for message 001 before you join a channel.

    void processMessage(String ircMessage) {
        Message msg = MessageParser.message(ircMessage);
        switch (msg.command) {
            case "privmsg": if (msg.content.equals("\001VERSION\001")) {privmsg(msg.nickname, "JarChat",null);return;}System.out.println("PRIVMSG: " + msg.nickname + ": " + msg.content);break;
            case "001": initial_setup();break;
            case "ping": pong(msg.content);break;
        }
    }

    public void run() {
        InputStream stream;
        try {
            stream = server.getInputStream();
            MessageBuffer messageBuffer = new MessageBuffer();
            byte[] buffer = new byte[512];
            int count;
            while (true) {
                count = stream.read(buffer);if (count == -1) break;
                messageBuffer.append(Arrays.copyOfRange(buffer, 0, count));
                while (messageBuffer.hasCompleteMessage()) {String ircMessage = messageBuffer.getNextMessage();System.out.println("\"" + ircMessage + "\"");processMessage(ircMessage);}
            }
        }
        catch (IOException info) {quit("error in messageLoop");info.printStackTrace();}
    }
}

class Message {public String origin;public String nickname;public String command;
    @SuppressWarnings("unused")
    public String target;public String content;}

class MessageBuffer {String buffer;public MessageBuffer() {buffer = "";}public void append(byte[] bytes) {buffer += new String(bytes);}public boolean hasCompleteMessage() {return buffer.contains("\r\n");}public String getNextMessage() {int index = buffer.indexOf("\r\n");String message = "";if (index > -1) {message = buffer.substring(0, index);buffer = buffer.substring(index + 2);}return message;}}

// class only parses messages it understands. if a message is not understood
// the origin and command are extracted and parsing halts.
class MessageParser {
    static Message message(String ircMessage) {
        Message message = new Message();int spIndex;
        if (ircMessage.startsWith(":")) {spIndex = ircMessage.indexOf(' ');if (spIndex > -1) {message.origin = ircMessage.substring(1, spIndex);ircMessage = ircMessage.substring(spIndex + 1);int uIndex = message.origin.indexOf('!');if (uIndex > -1) message.nickname = message.origin.substring(0, uIndex);}}spIndex = ircMessage.indexOf(' ');
        if (spIndex == -1) {message.command = "null";return message;}message.command = ircMessage.substring(0, spIndex).toLowerCase();ircMessage = ircMessage.substring(spIndex + 1);
        // parse privmsg params
        if (message.command.equals("privmsg")) {spIndex = ircMessage.indexOf(' ');message.target = ircMessage.substring(0, spIndex);ircMessage = ircMessage.substring(spIndex + 1);if (ircMessage.startsWith(":")) message.content = ircMessage.substring(1);else message.content = ircMessage;}
        // parse quit/join
        if (message.command.equals("quit") || message.command.equals("join")) {if (ircMessage.startsWith(":")) message.content = ircMessage.substring(1);else message.content = ircMessage;}
        // parse ping params
        if (message.command.equals("ping")) {spIndex = ircMessage.indexOf(' ');if (spIndex > -1) message.content = ircMessage.substring(0, spIndex);else message.content = ircMessage;}return message;
    }
}