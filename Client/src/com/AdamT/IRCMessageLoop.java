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

// All of these classes are taken from Kaecy's gist at https://gist.github.com/kaecy/286f8ad334aec3fcb588516feb727772,
// with my own edits to ensure they better suited for use as an actual client.
abstract class IRCMessageLoop extends Thread {
    static OutputStream out;
    ArrayList<String> channelList;
    boolean initial_setup_status;
    InputStream stream;
    IRCMessageLoop(String serverName, int port) {
        channelList = new ArrayList<>();
        try {
            if (port == 6697 || port == 7000 || port == 7070){
                SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
                SSLSocket server = (SSLSocket)factory.createSocket(serverName, port);
                server.startHandshake();
                out = server.getOutputStream();
                stream = server.getInputStream();
            }
            else {
                Socket server = new Socket(serverName, port);
                out = server.getOutputStream();
                stream = server.getInputStream();
            }
        } catch (Exception info) {info.printStackTrace();}
    }
    static void send(String text) {byte[] bytes = (text + "\r\n").getBytes();try {out.write(bytes);} catch (IOException info) {info.printStackTrace();}}
    void nick(String nickname) {String msg = "NICK " + nickname;send(msg);}
    void user(String username, String hostname, String realname) {String msg = "USER " + username + " " + hostname + " " + "null" +  " :" + realname;send(msg);}
    void join(String channel) {if (!initial_setup_status) {channelList.add(channel);return;}String msg = "JOIN " + channel;send(msg);}
    void part(String channel) {String msg = "PART " + channel;send(msg);}
    static void privmsg(String to, String text, @Nullable String from) {String msg = "PRIVMSG " + to + " :" + text;send(msg);System.out.println("PRIVMSG: " + from + ": " + text);}
    void pong(String server) {String msg = "PONG " + server;send(msg);}
    static void quit(String reason) {String msg = "QUIT :Quit: " + reason;send(msg);}
    void initial_setup() {
        initial_setup_status = true;
        for (String channel: channelList) {join(channel);} // now join the channels. you need to wait for message 001 before you join a channel.
    }
    void processMessage(String ircMessage) {
        Message msg = MessageParser.message(ircMessage);
        switch (msg.command) {
            case "privmsg": if (msg.content.equals("\001VERSION\001")) {
                privmsg(msg.nickname, "JarChat",null);
                return;
            }
            System.out.println("PRIVMSG: " + msg.nickname + ": " + msg.content);break;
            case "001": initial_setup();break;
            case "ping": pong(msg.content);break;
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