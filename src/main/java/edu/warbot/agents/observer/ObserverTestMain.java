package edu.warbot.agents.observer;

import madkit.action.KernelAction;
import madkit.kernel.AbstractAgent;
import madkit.kernel.Agent;
import madkit.kernel.Madkit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;

/**
 * Created by beugnon on 29/06/15.
 */
public class ObserverTestMain {

    public static void main(String[] args) {
        Madkit madkit = new Madkit(Madkit.BooleanOption.desktop.toString(), "true");
//        madkit.doAction(KernelAction.LAUNCH_AGENT,new GraphicGroupObserver(),"true");
//        madkit.doAction(KernelAction.LAUNCH_AGENT,new GroupObserver(),true);
        madkit.doAction(KernelAction.LAUNCH_AGENT, new Launcher(), true);
        madkit.doAction(KernelAction.LAUNCH_AGENT, new CommunityObserver(), true);
        madkit.doAction(KernelAction.LAUNCH_AGENT, new AGRObserver(), true);
    }

    static class Launcher extends Agent {


        @Override
        public void setupFrame(JFrame frame) {
            JButton button = new JButton("launch");
            frame.setLayout(new BorderLayout());
            frame.getContentPane().add(button, BorderLayout.CENTER);
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new GridLayout(3, 2));
            jPanel.add(new JLabel("community"));
            final JTextField comField = new JTextField("toto");
            jPanel.add(comField);
            jPanel.add(new JLabel("group"));
            final JTextField groupField = new JTextField("toto");
            jPanel.add(groupField);
            final JTextField roleField = new JTextField("toto");
            jPanel.add(new JLabel("role"));
            jPanel.add(roleField);


            frame.getContentPane().add(jPanel, BorderLayout.NORTH);
            final AbstractAgent self = this;
            frame.addWindowListener(new WindowAdapter() {


                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    killAgent(self);
                }

            });
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    launchAgent(new AbstractAgent() {

                        public void activate() {
                            setLogLevel(Level.INFO);
                            createGroupIfAbsent(comField.getText(), groupField.getText());
                            if (roleField.getText().length() > 0)
                                requestRole(comField.getText(), groupField.getText(), roleField.getText());
                        }

                    }, true);
                }
            });


        }

        @Override
        public void live() {
            while (true) {
                ;
            }
        }
    }
}
