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

public class Server {
    //Class variables
    static JFrame f;
    static ImageIcon iii;

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

        f = new JFrame("JarChat - Server");    //Creates instance of the UI's frame
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(800,600);
        /*
         * f.setResizable(false);    //Testing for size
         * iii = new ImageIcon("Path to logo");    //For icon (later)
         * f.setIconImage(iii.getImage());
         */
        f.setVisible(true);
    }
}