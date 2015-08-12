package edu.warbot.agents.observer;

import madkit.agr.LocalCommunity;
import madkit.kernel.Agent;
import madkit.message.hook.HookMessage;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 * Created by beugnon on 30/06/15.
 */
public class CommunityObserver extends Agent {


    //MODEL
    private Set<String> communities;


    //VIEW
    private JList<String> list;


    public CommunityObserver() {
        communities = new TreeSet<>();
        list = new JList<>(new DefaultListModel<String>());
    }

    @Override
    public void activate() {
        super.activate();
        createGroupIfAbsent(LocalCommunity.NAME, "observer");
        requestRole(LocalCommunity.NAME, "observer", "community");
        update();

        sendMessage
                (LocalCommunity.NAME, LocalCommunity.Groups.SYSTEM, LocalCommunity.GROUP_MANAGER_ROLE,
                        new HookMessage(HookMessage.AgentActionEvent.CREATE_GROUP));
        sendMessage
                (LocalCommunity.NAME, LocalCommunity.Groups.SYSTEM, LocalCommunity.GROUP_MANAGER_ROLE,
                        new HookMessage(HookMessage.AgentActionEvent.LEAVE_GROUP));
    }

    public void live() {
        while (true) {
            update();
            pause(2000);
        }
    }

    public void update() {
        Set<String> refreshedCommunities = getExistingCommunities();
        communities.clear();
        communities.addAll(refreshedCommunities);//Update with existing communities
//        String[] data = new String [communities.size()];
//        communities.toArray(data);
        DefaultListModel<String> l = (DefaultListModel<String>) list.getModel();

        for (String com : communities)
            if (!l.contains(com))
                l.addElement(com);

        for (int i = 0; i < l.getSize(); ++i)
            if (!communities.contains(l.getElementAt(i)))
                l.removeElementAt(i);
//        list.setListData(data);
    }

    @Override
    public void setupFrame(JFrame frame) {
        frame.getJMenuBar().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JLabel("Community List"), BorderLayout.NORTH);
        frame.getContentPane().add(list, BorderLayout.CENTER);
        JButton button = new JButton("refresh");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                update();
            }
        });
        frame.getContentPane().add(button, BorderLayout.SOUTH);


        setLogLevel(Level.FINE);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

                if (listSelectionEvent.getFirstIndex() == listSelectionEvent.getLastIndex())
                    logger.fine("value:" + list.
                            getModel().getElementAt(listSelectionEvent.getFirstIndex()));
                else {
                    logger.fine("values:");
                    for (int i = listSelectionEvent.getFirstIndex(); i <= listSelectionEvent.getLastIndex(); ++i)
                        logger.fine("value-" + i + ":" + list.
                                getModel().getElementAt(i));
                }
            }
        });
    }
}
