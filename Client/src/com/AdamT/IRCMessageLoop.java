// All of these classes are taken from Kaecy's gist at https://gist.github.com/kaecy/286f8ad334aec3fcb588516feb727772,
// with my own edits to ensure they better suited for use as an actual client, as well as to add comments to the code.

package com.AdamT;

// Imports
import com.sun.istack.internal.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.ArrayList;
import java.util.Arrays;

abstract class IRCMessageLoop extends Thread {
    // Variables (local to the class as many methods require them)
    static OutputStream out;
    ArrayList<String> channelList = new ArrayList<>();
    boolean initial_setup_status;
    InputStream stream;
    IRCMessageLoop(String serverName, int port) {
        try { // Both outcomes of this if statement can throw exceptions, so need to be encased in a try-catch
            if (port == 6697 || port == 7000 || port == 7070){ // Connection is a TLS connection
                // Initialise and start the secure socket
                SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
                SSLSocket server = (SSLSocket)factory.createSocket(serverName, port);
                server.startHandshake();
                // Allow the program to read everything being received from the socket, as well as to send info back to the server
                out = server.getOutputStream();
                stream = server.getInputStream();
            }
            else {
                Socket server = new Socket(serverName, port); // Initialise plaintext connection (this is automatically started)
                // Allow the program to read everything being received from the socket, as well as to send info back to the server
                out = server.getOutputStream();
                stream = server.getInputStream();
            }
        } catch (Exception info) { info.printStackTrace(); } // Print information about the error to the terminal for debugging in case it's needed
    }
    static void send(String text) {
        byte[] bytes = (text + "\r\n").getBytes(); // Ensure the message being sent ends with a CRLF line break and not na LF line break
        try { out.write(bytes); } catch (IOException info) { info.printStackTrace(); } // Try to send the message, and print out error info if it fails (without exiting the program)
    }
    void nick(String nickname) { String msg = "NICK " + nickname; send(msg); } // Set the nickname as seen by the server and other users
    // Set the rest of the user's info
    void user(String username, String hostname, String real_name) { String msg = "USER " + username + " " + hostname + " " + "null" +  " :" + real_name; send(msg); }
    void join(String channel) { if (!initial_setup_status) { channelList.add(channel); return; } String msg = "JOIN " + channel; send(msg); } // Join the channel as requested by the user
    void part(String channel) { String msg = "PART " + channel; send(msg); } // Leave the channel as requested by the user, without disconnecting from the server
    // Send messages, either directly or as a DM
    static void privmsg(String to, String text, @Nullable String from) { String msg = "PRIVMSG " + to + " :" + text; send(msg); System.out.println("PRIVMSG: " + from + ": " + text); }
    void pong(String server) { String msg = "PONG " + server; send(msg); } // Respond back to the server every few minutes to ensure the connection isn't forcibly removed
    static void quit(String reason) { String msg = "QUIT :Quit: " + reason; send(msg); } // Disconnect from the server as requested by the user
    void initial_setup() {
        initial_setup_status = true;
        for (String channel: channelList) { join(channel); } // Now you can join the channels. You need to wait for message 001 before you join a channel.
    }
    // Method to call the other methods here as required depending on the message received from the server or other users.
    void processMessage(String ircMessage) {
        Message msg = MessageParser.message(ircMessage);
        switch (msg.command) {
            case "privmsg": if (msg.content.equals("\001VERSION\001")) { privmsg(msg.nickname, "JarChat",null); return; } // Reflect this client's name in the response
            System.out.println("PRIVMSG: " + msg.nickname + ": " + msg.content); break; // Show the recieved message in the log
            case "001": initial_setup(); break; // Initial message as sent by the server
            case "ping": pong(msg.content);break; // see comment on line 53
        }
    }

    public void run() {
        try {
            MessageBuffer messageBuffer = new MessageBuffer();
            byte[] buffer = new byte[512];
            int count;
            while (true) {
                count = stream.read(buffer);
                if (count == -1) break;
                messageBuffer.append(Arrays.copyOfRange(buffer, 0, count));
                while (messageBuffer.hasCompleteMessage()) {
                    String ircMessage = messageBuffer.getNextMessage();
                    System.out.println("\"" + ircMessage + "\"");
                    processMessage(ircMessage);
                }
            }
        }
        catch (IOException info) {quit("error in messageLoop");info.printStackTrace();}
    }
}

class Message {public String origin;public String nickname;public String command;@SuppressWarnings("unused") public String target;public String content;}

class MessageBuffer {
    String buffer;public MessageBuffer() {buffer = "";}
    public void append(byte[] bytes) {buffer += new String(bytes);}
    public boolean hasCompleteMessage() {return buffer.contains("\r\n");}
    public String getNextMessage() {
        int index = buffer.indexOf("\r\n");
        String message = "";
        if (index > -1) {
            message = buffer.substring(0, index);
            buffer = buffer.substring(index + 2);
        }
        return message;
    }
}

// class only parses messages it understands. if a message is not understood
// the origin and command are extracted and parsing halts.
class MessageParser {
    static Message message(String ircMessage) {
        Message message = new Message();int spIndex;
        if (ircMessage.startsWith(":")) {
            spIndex = ircMessage.indexOf(' ');
            if (spIndex > -1) {
                message.origin = ircMessage.substring(1, spIndex);
                ircMessage = ircMessage.substring(spIndex + 1);
                int uIndex = message.origin.indexOf('!');
                if (uIndex > -1) message.nickname = message.origin.substring(0, uIndex);
            }
        }
        spIndex = ircMessage.indexOf(' ');
        if (spIndex == -1) {
            message.command = "null";
            return message;
        }
        message.command = ircMessage.substring(0, spIndex).toLowerCase();
        ircMessage = ircMessage.substring(spIndex + 1);
        // parse privmsg params
        if (message.command.equals("privmsg")) {
            spIndex = ircMessage.indexOf(' ');
            message.target = ircMessage.substring(0, spIndex);
            ircMessage = ircMessage.substring(spIndex + 1);
            if (ircMessage.startsWith(":")) message.content = ircMessage.substring(1);
            else message.content = ircMessage;
        }
        // parse quit/join
        if (message.command.equals("quit") || message.command.equals("join")) {
            if (ircMessage.startsWith(":")) message.content = ircMessage.substring(1);
            else message.content = ircMessage;
        }
        // parse ping params
        if (message.command.equals("ping")) {
            spIndex = ircMessage.indexOf(' ');
            if (spIndex > -1) message.content = ircMessage.substring(0, spIndex);
            else message.content = ircMessage;
        }
        return message;
    }
}