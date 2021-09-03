/*
 * JarChat Server Source Code
 * Free and Open-sourced under the GNU GPL v3 Licence
 * Last Edited: 2021-09-02 22:30Z by SimPilotAdamT
 */

package com.AdamT.JarChat;

//Imports
import java.lang.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Server {
    //Class variables

    public static void main(String[] args) {
        System.out.println("Hi!");
        uix();
    }
    static void uix() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        new myFrame("JarChat - Server");    //Creates instance of the UI's frame
    }
}

class myFrame extends JFrame {
    myFrame(String title) {
        this.setTitle(title);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(800,600);
        /*
         * this.setResizable(false);    //Testing for size
         * ImageIcon iii = new ImageIcon("Path to logo");    //For icon (later)
         * this.setIconImage(iii.getImage());
         */
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(0x2a2e32));
        this.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Color.green);
        header.setPreferredSize(new Dimension(50,50));
        header.setLayout(new BorderLayout());

        JPanel paddingLeft = new JPanel();
        paddingLeft.setBackground(Color.red);
        paddingLeft.setPreferredSize(new Dimension(20,50));
        header.add(paddingLeft,BorderLayout.WEST);

        JLabel heading = new JLabel("JarChat");
        header.add(heading,BorderLayout.CENTER);

        this.add(header,BorderLayout.NORTH);
    }
}