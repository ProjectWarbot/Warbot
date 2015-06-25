package edu.warbot.agents.observer;

import madkit.agr.LocalCommunity;
import madkit.kernel.Agent;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

/**
 * Created by beugnon on 30/06/15.
 */
public class AGRObserver extends Agent {

    private DefaultMutableTreeNode agrTree;

    private Map<String, Map<String, Set<String>>> agrModel;


    private JTree jTree;

    public AGRObserver() {
        agrModel = new TreeMap<>();
        agrTree = new DefaultMutableTreeNode("AGR", true);
        jTree = new JTree(new DefaultTreeModel(agrTree));
    }


    @Override
    public void activate() {

        super.activate();
        createGroupIfAbsent(LocalCommunity.NAME, "observer");
        requestRole(LocalCommunity.NAME, "observer", "agrModel");
        update();

    }

    @Override
    public void live() {
        setLogLevel(Level.INFO);
        while (true) {
            update();
            pause(2000);
        }
    }

    @Override
    public void setupFrame(JFrame frame) {
        frame.getJMenuBar().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());

        JScrollPane jScrollPane = new JScrollPane(jTree,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.getContentPane().add(jScrollPane, BorderLayout.CENTER);

        jTree.expandPath(new TreePath(agrTree.getRoot()));
        JButton button = new JButton("refresh");
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                update();
            }
        });

    }


    public void update() {
        logger.finest("** entrée dans update **");
        Set<String> refreshedCommunities = getExistingCommunities();
        agrModel.clear();
        for (String community : refreshedCommunities) {
            logger.finer("community:" + community);
            Set<String> groups = getExistingGroups(community);
            Map<String, Set<String>> gr = new TreeMap<>();
            for (String group : groups) {
                logger.finer("community:" + community + ":group:" + group);

                Set<String> roles = getExistingRoles(community, group);
                gr.put(group, roles);
                logger.finer("community:" + community + ":group:" + group + ":roles:" + roles);
            }
            agrModel.put(community, gr);
        }
        setupTree();
        jTree.expandPath(new TreePath(agrTree.getPath()));
        logger.finest("** sortie dans update **");
    }


    public void setupTree() {
        logger.finest("** entrée dans setupTree **");
        cleanTree();
        updateTree();
        logger.finest("** sortie dans setupTree **");
    }


    public boolean isNewChild(TreeNode tn, String element) {
        return childPosition(tn, element) == tn.getChildCount();
    }

    public int childPosition(TreeNode tn, String element) {
        int i = 0;
        while (i < tn.getChildCount() && !tn.getChildAt(i).toString().equals(element))
            i++;
        return i;
    }

    protected void addNewCommunity(String community) {
        //NEW CHILD WITH ALL HIS STUFF
        DefaultMutableTreeNode com = new DefaultMutableTreeNode(community, true);
        Map<String, Set<String>> groups = agrModel.get(community);

        for (String group : groups.keySet()) {
            DefaultMutableTreeNode gr = new DefaultMutableTreeNode(group, true);

            for (String role : groups.get(group)) {
                DefaultMutableTreeNode rl = new DefaultMutableTreeNode(role, false);
                gr.add(rl);
            }

            com.add(gr);
        }
        agrTree.add(com);
        int indices[] = {agrTree.getChildCount() - 1};
        ((DefaultTreeModel) jTree.getModel()).nodesWereInserted(agrTree, indices);
    }

    protected void addGroup(Map<String, Set<String>> groups, DefaultMutableTreeNode tn, String group) {

        DefaultMutableTreeNode gr = new DefaultMutableTreeNode(group, true);

        for (String role : groups.get(group)) {
            DefaultMutableTreeNode rl = new DefaultMutableTreeNode(role, false);
            gr.add(rl);
        }

        tn.add(gr);
        int indices[] = {tn.getChildCount() - 1};
        ((DefaultTreeModel) jTree.getModel()).nodesWereInserted(tn, indices);
    }

    protected void addRole(String role, DefaultMutableTreeNode gr) {

        DefaultMutableTreeNode rl = new DefaultMutableTreeNode(role, false);
        gr.add(rl);

        int indices[] = {gr.getChildCount() - 1};
        ((DefaultTreeModel) jTree.getModel()).nodesWereInserted(gr, indices);

    }

    protected void updateCommunity(DefaultMutableTreeNode tn) {

        Map<String, Set<String>> groups = agrModel.get(tn.toString());

        for (String group : groups.keySet()) {

            if (isNewChild(tn, group)) {

                logger.info("new group : " + group + " for community " + tn.toString());
                addGroup(groups, tn, group);

            } else {

                int position = childPosition(tn, group);
                DefaultMutableTreeNode gr = (DefaultMutableTreeNode) tn.getChildAt(position);
                updateGroup(tn, gr);

            }
        }
    }

    protected void updateGroup(DefaultMutableTreeNode tn, DefaultMutableTreeNode gr) {

        for (String role : agrModel.get(tn.toString()).get(gr.toString())) {

            if (isNewChild(gr, role)) {

                logger.info("new role : " + role + " for group " + gr.toString() + " for community " + tn.toString());
                addRole(role, gr);

            }

        }
    }


    public void cleanTree() {
        //REMOVE OLD ELEMENTS
        for (int i = 0; i < agrTree.getChildCount(); ++i) {
            TreeNode tn = agrTree.getChildAt(i);
            logger.fine("test community : " + tn.toString());

            if (!agrModel.containsKey(tn.toString())) {
                //NO MORE COMMUNITY IN MODEL
                logger.info("remove community : " + tn.toString());
                agrTree.remove(i);
                int indices[] = {i};
                Object child[] = {tn};
                ((DefaultTreeModel) jTree.getModel()).nodesWereRemoved(agrTree, indices, child);

            } else {
                //STILL HAVE COMMUNITY IN MODEL
                //TESTING GROUP IF EXISTS
                for (int j = 0; j < tn.getChildCount(); ++j) {
                    TreeNode gr = tn.getChildAt(j);
                    logger.fine("test group : " + tn.toString() + ":" + gr.toString());

                    if (!agrModel.get(tn.toString()).containsKey(gr.toString())) {
                        logger.info("remove group : " + tn.toString() + ":" + gr.toString());

                        ((DefaultMutableTreeNode) tn).remove(j);
                        ((DefaultTreeModel) jTree.getModel()).nodeChanged(tn);

                    } else {
                        //STILL HAVE GROUP IN MODEL
                        //TESTING ROLES IF EXISTS
                    }
                }
            }
        }

    }

    public void updateTree() {

        for (String community : agrModel.keySet()) {

            if (isNewChild(agrTree, community)) {

                logger.info("new community : " + community);
                addNewCommunity(community);

            } else {

                int position = childPosition(agrTree, community);
                DefaultMutableTreeNode tn = (DefaultMutableTreeNode) agrTree.getChildAt(position);
                updateCommunity(tn);
                //UPDATE UNDER THIS CHILD
            }
        }
    }


}
