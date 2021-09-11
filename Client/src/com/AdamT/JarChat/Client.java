/*
 * JarChat IRC Client Source Code
 * Free and Open-sourced under the GNU GPL v3 Licence
 *
 * Built using the latest JDK 8 to ensure compatibility with all
 * modern devices. Will change JDK once more devices use JDK 11.
 *
 * Last Edited: 2021-09-06 07:35Z by SimPilotAdamT
 */

package com.AdamT.JarChat;

//Imports
import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class Client {
    private static Scanner con;
    private static Socket socket;
    private static String nick;
    private static String uname;
    private static String name;
    private static PrintWriter out;
    private static Scanner in;

    public static void main(String[] args) throws IOException {
        System.out.println("\nHi!");

        con = new Scanner(System.in);

        System.out.print("\nEnter nickname: "); nick = con.nextLine();
        System.out.print("\nEnter username: "); uname = con.nextLine();
        System.out.print("\nEnter real name: "); name = con.nextLine();

        System.out.print("\n");

        socket = new Socket("irc.libera.chat",6697);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());

        write("NICK", nick);
        write("USER", uname + " 0 * :" + name);

        while (in.hasNext()) {
            System.out.println("<<<" + in.nextLine());
        }

        //uix();

        in.close();
        out.close();
        socket.close();
        con.close();
    }
    private static void write(String comm, String mess) {
        String fullMess = comm + " " + mess;
        System.out.println(">>> " + fullMess);
        out.print(fullMess+"\r\n");
        out.flush();
    }
    private static void uix() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        new firstFrame("JarChat - Client");    //Creates instance of the UI's frame
    }
}

class firstFrame extends JFrame {
    JPanel pnP;
    JTextField tfName;
    JTextField tfPass;
    JButton btLogIn;
    JButton btRegBut;
    JLabel lbLab0;
    JLabel lbLab1;
    firstFrame(String title) {
        pnP = new JPanel();
        pnP.setBorder(BorderFactory.createTitledBorder(""));
        GridBagLayout gbP = new GridBagLayout();
        GridBagConstraints gbcP = new GridBagConstraints();
        pnP.setLayout(gbP);
        pnP.setBorder(new EmptyBorder(5,5,5,5));

        tfName = new JTextField();
        gbcP.gridx = 5;
        gbcP.gridy = 7;
        gbcP.gridwidth = 16;
        gbcP.gridheight = 2;
        gbcP.fill = GridBagConstraints.BOTH;
        gbcP.weightx = 1;
        gbcP.weighty = 0;
        gbcP.anchor = GridBagConstraints.NORTH;
        gbP.setConstraints(tfName, gbcP);
        pnP.add(tfName);

        tfPass = new JTextField();
        gbcP.gridx = 5;
        gbcP.gridy = 11;
        gbcP.gridwidth = 16;
        gbcP.gridheight = 2;
        gbcP.fill = GridBagConstraints.BOTH;
        gbcP.weightx = 1;
        gbcP.weighty = 0;
        gbcP.anchor = GridBagConstraints.NORTH;
        gbP.setConstraints(tfPass, gbcP);
        pnP.add(tfPass);

        btLogIn = new JButton("Log In");
        gbcP.gridx = 5;
        gbcP.gridy = 14;
        gbcP.gridwidth = 4;
        gbcP.gridheight = 2;
        gbcP.fill = GridBagConstraints.BOTH;
        gbcP.weightx = 1;
        gbcP.weighty = 0;
        gbcP.anchor = GridBagConstraints.NORTH;
        gbP.setConstraints(btLogIn, gbcP);
        btLogIn.setFont(btLogIn.getFont().deriveFont(16f));
        pnP.add(btLogIn);

        btRegBut = new JButton("Register");
        gbcP.gridx = 11;
        gbcP.gridy = 14;
        gbcP.gridwidth = 4;
        gbcP.gridheight = 2;
        gbcP.fill = GridBagConstraints.BOTH;
        gbcP.weightx = 1;
        gbcP.weighty = 0;
        gbcP.anchor = GridBagConstraints.NORTH;
        gbP.setConstraints(btRegBut, gbcP);
        btRegBut.setFont(btRegBut.getFont().deriveFont(16f));
        pnP.add(btRegBut);

        lbLab0 = new JLabel("Username:");
        gbcP.gridx = 5;
        gbcP.gridy = 5;
        gbcP.gridwidth = 16;
        gbcP.gridheight = 2;
        gbcP.fill = GridBagConstraints.BOTH;
        gbcP.weightx = 1;
        gbcP.weighty = 1;
        gbcP.anchor = GridBagConstraints.NORTH;
        gbP.setConstraints(lbLab0, gbcP);
        lbLab0.setFont(lbLab0.getFont().deriveFont(16f));
        pnP.add(lbLab0);

        lbLab1 = new JLabel("Password:");
        gbcP.gridx = 5;
        gbcP.gridy = 9;
        gbcP.gridwidth = 16;
        gbcP.gridheight = 2;
        gbcP.fill = GridBagConstraints.BOTH;
        gbcP.weightx = 1;
        gbcP.weighty = 1;
        gbcP.anchor = GridBagConstraints.NORTH;
        gbP.setConstraints(lbLab1, gbcP);
        lbLab1.setFont(lbLab1.getFont().deriveFont(16f));
        pnP.add(lbLab1);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setContentPane(pnP);
        this.setTitle(title+" | Login");
        this.setSize(new Dimension(320,160));
        this.setResizable(false);
        this.setVisible(true);
    }
}
