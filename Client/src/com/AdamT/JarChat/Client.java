/*
 * JarChat Client Source Code
 * Free and Open-sourced under the GNU GPL v3 Licence
 * Last Edited: 2021-09-02 22:30Z by SimPilotAdamT
 */

package com.AdamT.JarChat;

//Imports
import java.lang.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class Client {
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
    JLabel lbLab2;
    firstFrame(String title) {
        pnP = new JPanel();
        pnP.setBorder(BorderFactory.createTitledBorder(""));
        GridBagLayout gbP = new GridBagLayout();
        GridBagConstraints gbcP = new GridBagConstraints();
        pnP.setLayout(gbP);

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
        pnP.add(btRegBut);

        lbLab0 = new JLabel("Username");
        gbcP.gridx = 5;
        gbcP.gridy = 5;
        gbcP.gridwidth = 16;
        gbcP.gridheight = 2;
        gbcP.fill = GridBagConstraints.BOTH;
        gbcP.weightx = 1;
        gbcP.weighty = 1;
        gbcP.anchor = GridBagConstraints.NORTH;
        gbP.setConstraints(lbLab0, gbcP);
        pnP.add(lbLab0);

        lbLab2 = new JLabel("Password");
        gbcP.gridx = 5;
        gbcP.gridy = 9;
        gbcP.gridwidth = 16;
        gbcP.gridheight = 2;
        gbcP.fill = GridBagConstraints.BOTH;
        gbcP.weightx = 1;
        gbcP.weighty = 1;
        gbcP.anchor = GridBagConstraints.NORTH;
        gbP.setConstraints(lbLab2, gbcP);
        pnP.add(lbLab2);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JScrollPane scpP = new JScrollPane(pnP);
        this.setContentPane(scpP);
        this.setTitle(title+" | Login");
        this.setVisible(true);
        this.setSize(new Dimension(320,160));
    }
}