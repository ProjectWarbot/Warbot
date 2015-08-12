package edu.warbot.agents.observer;

/*
* GroupObserver.java -an agent which observe MadKit organizations
* Copyright (C) 2000-2002 Jacques Ferber, Olivier Gutknecht
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import madkit.agr.LocalCommunity;
import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import madkit.message.hook.AgentLifeEvent;
import madkit.message.hook.HookMessage;
import madkit.message.hook.MessageEvent;
import madkit.message.hook.OrganizationEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;


class OrgTreeCellRenderer extends DefaultTreeCellRenderer {
    ImageIcon agentIcon;
    ImageIcon roleIcon;
    ImageIcon groupIcon;
    ImageIcon selectedGroupIcon;
    ImageIcon communityIcon;

    public OrgTreeCellRenderer() {
        String imageAgent = "/images/agents/AgentIconColor16.gif";
        String imageRole = "/images/toolbars/org_role16.gif";
        String imageGroup = "/images/toolbars/org_group16.gif";
        String imageSelectedGroup = "/images/toolbars/org_selected_group16.gif";
        String imageCommunity = "/images/toolbars/community.gif";

//        URL url1 = this.getClass().getResource(imageAgent);
//        agentIcon = new ImageIcon(url1);
//
//        URL url2 = this.getClass().getResource(imageRole);
//        roleIcon = new ImageIcon(url2);
//
//        URL url3 = this.getClass().getResource(imageGroup);
//        groupIcon = new ImageIcon(url3);
//
//        URL url4 = this.getClass().getResource(imageSelectedGroup);
//        selectedGroupIcon = new ImageIcon(url4);
//
//        URL url5 = this.getClass().getResource(imageCommunity);
//        communityIcon = new ImageIcon(url5);
    }


    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
        if (leaf) {
            setIcon(agentIcon);
        } else if (!leaf && ((DefaultMutableTreeNode) value).getDepth() == 2) {
            if (((GroupTreeNode) value).isSelected())
                setIcon(selectedGroupIcon);
            else
                setIcon(groupIcon);
        } else if (!leaf && ((DefaultMutableTreeNode) value).getDepth() == 1)
            setIcon(roleIcon);
        else if (!leaf && ((DefaultMutableTreeNode) value).getDepth() == 3)
            setIcon(communityIcon);
        return this;
    }
}

class GroupTreeNode extends DefaultMutableTreeNode {
    boolean selected = false;

    GroupTreeNode(Object o) {
        super(o);
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean b) {
        selected = b;
    }
}

//class AgentTreeNode extends DefaultMutableTreeNode {
//	AgentAddress ag;
//	AgentAddress getAgent(){return ag;}
//	AgentTreeNode(Object o,AgentAddress addr){
//		super(o);
//		ag = addr;
//	}
//}


class TreeGroupObserverPanel extends JPanel implements ActionListener {
    protected DefaultMutableTreeNode top;
    protected DefaultTreeModel treeModel;
    protected DefaultMutableTreeNode publicCommunity;
    protected JTree orgTree;
    // private javax.swing.JButton inspectButton;
    GroupObserver myAgent;
    JPopupMenu groupPopup;
    JPopupMenu communityPopup;
    JPopupMenu agentPopup;
    TreePath popupPath;
    GroupTreeNode selectedElement;
    Hashtable lastOrg = null;
    private JPanel jPanel1;
    private JScrollPane treeScrollPane;
    private JButton refreshButton;

    TreeGroupObserverPanel(GroupObserver _ag) {
        myAgent = _ag;
        setLayout(new BorderLayout());

        treeScrollPane = new JScrollPane();

        // creation of JTree..
        top = new DefaultMutableTreeNode("Madkit organization");
        publicCommunity = new DefaultMutableTreeNode("public");
        top.add(publicCommunity);

        treeModel = new DefaultTreeModel(top);
        // treeModel.addTreeModelListener(new MyTreeModelListener());

        orgTree = new JTree(treeModel);
        //orgTree.setEditable(true);
        orgTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        orgTree.setShowsRootHandles(true);
        orgTree.setRootVisible(false);

        DefaultTreeCellRenderer renderer = new OrgTreeCellRenderer();
        orgTree.setCellRenderer(renderer);

        treeScrollPane.setViewportView(orgTree);
        treeScrollPane.setPreferredSize(new Dimension(300, 200));
        add(treeScrollPane, "Center");

        // popup menu
        //Create the popup menu.
        groupPopup = new JPopupMenu();
        addCommand(groupPopup, "trace messages of this group", "selectGroup");
        addCommand(groupPopup, "trace messages of all groups", "unSelectGroup");
        addCommand(groupPopup, "sequence diagram of this group", "groupSequenceDiagram");

        communityPopup = new JPopupMenu();
        addCommand(communityPopup, "sequence diagram of this community", "communitySequenceDiagram");

        agentPopup = new JPopupMenu();
        addCommand(agentPopup, "properties", "properties");


        //Add listener to components that can bring up popup menus.

        MouseListener ml = new MouseAdapter() {
            JPopupMenu groupPop = groupPopup;
            JPopupMenu communityPop = communityPopup;

            public void mousePressed(MouseEvent e) {
                int selRow = orgTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = orgTree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if ((e.getClickCount() == 1))//&& (GraphicUtils.isPopupTrigger(e)))
                    //((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK))
                    {
                        //System.out.println("single click");
                        //System.out.println("selRow: " + selRow + ", selPath: "+ selPath);
                        if (selPath.getPathCount() == 4) {
                            popupPath = selPath;
                            agentPopup.show(e.getComponent(), e.getX(), e.getY());
                        } else if (selPath.getPathCount() == 3) {
                            popupPath = selPath;
                            groupPop.show(e.getComponent(), e.getX(), e.getY());
                        } else if (selPath.getPathCount() == 2) {
                            popupPath = selPath;
                            communityPop.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        };
        orgTree.addMouseListener(ml);
    }

    public void actionPerformed(ActionEvent ev) {
        String c = ev.getActionCommand();
        Class[] parameterTypes = new Class[]{};
        try {
            Method command = getClass().getMethod(c, parameterTypes);

            command.invoke(this, new Object[]{});
        } catch (NoSuchMethodException e) {
            System.out.println("command : " + c + " unknown");
        } catch (IllegalAccessException e) {
            System.out.println(e);
        } catch (InvocationTargetException e) {
            System.out.println(e + ", command: " + c);
        }
    }

    public void properties() {
        String agent = popupPath.getLastPathComponent().toString();
        System.out.println("properties of : " + agent);
//		PropertyAgent propertyAgent = new PropertyAgent((AbstractAgent)oo);
//		String label = Booter.getBooter().getAgentLabelFromClassName(propertyAgent.getClass().getName());
//		myAgent.launchAgent(propertyAgent,label,true);
    }

    public void selectGroup() {
        //System.out.println("group selected: " + popupPath.getLastPathComponent().toString());
        if (selectedElement != null) {
            selectedElement.setSelected(false);
        }
        selectedElement = (GroupTreeNode) popupPath.getLastPathComponent();
        selectedElement.setSelected(true);
        myAgent.enterGroup(popupPath.getLastPathComponent().toString());
    }

    public void unSelectGroup() {
        if (selectedElement != null) {
            selectedElement.setSelected(false);
        }
        //System.out.println("unSelect groups");
        myAgent.leaveGroups();
    }


    public void groupSequenceDiagram() {
        String group = popupPath.getLastPathComponent().toString();
        HashMap map = new HashMap();
        HashMap toSpy = new HashMap();
        String community = popupPath.getParentPath().getLastPathComponent().toString();
        toSpy.put("Community", community);
        toSpy.put("Group", group);
        map.put("toSpy", toSpy);
        // map.put("autoRemove", new Boolean(true));
        Agent ag = new GraphicGroupObserver(map);
        myAgent.launchAgent(ag, true);
    }

    public void communitySequenceDiagram() {
        String community = popupPath.getLastPathComponent().toString();
        HashMap map = new HashMap();
        HashMap toSpy = new HashMap();
        toSpy.put("Community", community);
        map.put("toSpy", toSpy);
        // map.put("autoRemove", new Boolean(true));
        Agent ag = new GraphicGroupObserver(map);
        myAgent.launchAgent(ag, true);
    }

    void addCommand(JPopupMenu popup, String item, String action) {
        JMenuItem menuItem = new JMenuItem(item);
        menuItem.setActionCommand(action);
        popup.add(menuItem);
        menuItem.addActionListener(this);
    }

    DefaultMutableTreeNode addCommunity(String comm) {
        DefaultMutableTreeNode commNode = new DefaultMutableTreeNode(comm);
        treeModel.insertNodeInto(commNode, top, treeModel.getChildCount(top));
        return commNode;
    }

    DefaultMutableTreeNode addGroup(DefaultMutableTreeNode communityNode, String group, String agent) {
        //System.out.println("adding group: " + group);
        //top.add(new DefaultMutableTreeNode(group));

        DefaultMutableTreeNode groupRoot = new GroupTreeNode(group);
        treeModel.insertNodeInto(groupRoot, communityNode, treeModel.getChildCount(communityNode));
        addRole(groupRoot, "founder", agent);
        return groupRoot;
    }

    void addRole(DefaultMutableTreeNode groupNode, String role, String agent) {
        DefaultMutableTreeNode roleNode = new DefaultMutableTreeNode(role);
        treeModel.insertNodeInto(roleNode, groupNode, treeModel.getChildCount(groupNode));
        DefaultMutableTreeNode agentNode = new DefaultMutableTreeNode(agent);
        treeModel.insertNodeInto(agentNode, roleNode, treeModel.getChildCount(roleNode));
    }


    void createGroup(String community, String group, String agent) {
        DefaultMutableTreeNode commNode = findCommunity(community);
        if (commNode == null) {
            commNode = addCommunity(community);
        }
        DefaultMutableTreeNode grNode = findGroup(commNode, group);
        if (grNode == null) {
            addGroup(commNode, group, agent);
        }
    }

    void enterRole(String community, String group, String role, String agent) {
        DefaultMutableTreeNode commNode = findCommunity(community);
        if (commNode == null) {
            commNode = addCommunity(community);
        }
        DefaultMutableTreeNode grNode = findGroup(commNode, group);
        if (grNode == null) {
            grNode = addGroup(commNode, group, agent);
        }
        DefaultMutableTreeNode roleNode = findRole(grNode, role);
        if (roleNode == null) {
            addRole(grNode, role, agent);
        } else {
            DefaultMutableTreeNode agentNode = new DefaultMutableTreeNode(agent);
            treeModel.insertNodeInto(agentNode, roleNode, treeModel.getChildCount(roleNode));
        }
    }

    void leaveRole(String community, String group, String role, String agent) {
        DefaultMutableTreeNode commNode = findCommunity(community);
        if (commNode == null) return;
        DefaultMutableTreeNode grNode = findGroup(commNode, group);
        if (grNode == null) return;
        DefaultMutableTreeNode roleNode = findRole(grNode, role);
        if (roleNode == null) return;
        Vector nodesToRemove = new Vector();
        for (Enumeration f = roleNode.children(); f.hasMoreElements(); ) {
            DefaultMutableTreeNode agentNode = (DefaultMutableTreeNode) f.nextElement();
            if (((String) agentNode.getUserObject()).equals(agent)) {
                nodesToRemove.addElement(agentNode);
            }
        }
        for (int i = 0; i < nodesToRemove.size(); i++)
            treeModel.removeNodeFromParent((DefaultMutableTreeNode) nodesToRemove.elementAt(i));
        if (roleNode.getChildCount() == 0)
            treeModel.removeNodeFromParent(roleNode);
        if ((grNode.getChildCount() == 0) || ((grNode.getChildCount() == 1) && (findRole(grNode, "founder") != null)))
            treeModel.removeNodeFromParent(grNode);
        if ((commNode.getChildCount() == 0) && !((String) commNode.getUserObject()).equals("public"))
            treeModel.removeNodeFromParent(commNode);
    }

    void leaveGroup(String community, String group, String agent) {
        DefaultMutableTreeNode commNode = findCommunity(community);
        if (commNode == null) return;
        DefaultMutableTreeNode grNode = findGroup(commNode, group);
        if (grNode == null) return;
        DefaultMutableTreeNode roleNode = null;
        Vector nodesToRemove = new Vector();
        for (Enumeration e = grNode.children(); e.hasMoreElements(); ) {
            boolean removed = false;
            roleNode = (DefaultMutableTreeNode) e.nextElement();
            for (Enumeration f = roleNode.children(); f.hasMoreElements(); ) {
                DefaultMutableTreeNode agentNode = (DefaultMutableTreeNode) f.nextElement();
                if (((String) agentNode.getUserObject()).equals(agent)) {
                    nodesToRemove.addElement(agentNode);
                    removed = true;
                    //agentNode.removeFromParent();
                }
            }
            if (removed && roleNode.getChildCount() == 1)
                nodesToRemove.addElement(roleNode);
            //roleNode.removeFromParent();
        }
        for (int i = 0; i < nodesToRemove.size(); i++)
            treeModel.removeNodeFromParent((DefaultMutableTreeNode) nodesToRemove.elementAt(i));
        if (grNode.getChildCount() == 0)
            treeModel.removeNodeFromParent(grNode);
        if ((commNode.getChildCount() == 0) && !((String) commNode.getUserObject()).equals("public"))
            treeModel.removeNodeFromParent(commNode);
    }

    // JF: not really good way to implement that, because I traverse the tree twice!!
    // should use something like the leaveGroup above, but anyway, it works that way :-)
    void killAgent(String agent) {
        Vector toRemove = new Vector();
        for (Enumeration e = top.children(); e.hasMoreElements(); ) {
            DefaultMutableTreeNode commNode = (DefaultMutableTreeNode) e.nextElement();
            for (Enumeration c = commNode.children(); c.hasMoreElements(); ) {
                DefaultMutableTreeNode groupNode = (DefaultMutableTreeNode) c.nextElement();
                for (Enumeration g = groupNode.children(); g.hasMoreElements(); ) {
                    DefaultMutableTreeNode roleNode = (DefaultMutableTreeNode) g.nextElement();
                    for (Enumeration f = roleNode.children(); f.hasMoreElements(); ) {
                        DefaultMutableTreeNode agentNode = (DefaultMutableTreeNode) f.nextElement();
                        if (((String) agentNode.getUserObject()).equals(agent)) {
                            String[] st = new String[3];
                            st[0] = (String) commNode.getUserObject();
                            st[1] = (String) groupNode.getUserObject();
                            st[2] = agent;
                            toRemove.addElement(st);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < toRemove.size(); i++) {
            String[] st = (String[]) toRemove.elementAt(i);
            leaveGroup(st[0], st[1], st[2]);
        }
    }

    DefaultMutableTreeNode findGroup(DefaultMutableTreeNode commNode, String group) {
        for (Enumeration e = commNode.children(); e.hasMoreElements(); ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (((String) node.getUserObject()).equals(group))
                return node;
        }
        return null;
    }

    DefaultMutableTreeNode findCommunity(String community) {
        for (Enumeration c = top.children(); c.hasMoreElements(); ) {
            DefaultMutableTreeNode commNode = (DefaultMutableTreeNode) c.nextElement();
            if (((String) commNode.getUserObject()).equals(community))
                return commNode;
        }
        return null;
    }

    DefaultMutableTreeNode findRole(DefaultMutableTreeNode groupNode, String role) {
        if (groupNode == null)
            return null;
        for (Enumeration e = groupNode.children(); e.hasMoreElements(); ) {
            DefaultMutableTreeNode roleNode = (DefaultMutableTreeNode) e.nextElement();
            if (((String) roleNode.getUserObject()).equals(role))
                return roleNode;
        }
        return null;
    }

    void deleteCommunity(String community) {
        if (!community.equals("public")) {
            DefaultMutableTreeNode node = findCommunity(community);
            if (node != null)
                treeModel.removeNodeFromParent(node);
        }
    }

    void refresh() {
        Hashtable org = null;
        myAgent.requestDumpCommunities();
    }


    void installCommunities(Map communities) {
        top.removeAllChildren();
        for (Iterator i = communities.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            top.add(new DefaultMutableTreeNode(e.getKey()));
            installOrg((String) e.getKey(), (Hashtable) e.getValue());
        }
    }

    void addCommunities(Map communities) {
        for (Iterator i = communities.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            String comm = (String) e.getKey();
            if (findCommunity(comm) == null) {
                top.add(new DefaultMutableTreeNode(comm));
                installOrg((String) e.getKey(), (Hashtable) e.getValue());
            }
        }
    }

    /**
     * Install a community by removing and reinserting all nodes
     */
    void installOrg(String communityName, Hashtable org) {
        DefaultMutableTreeNode commNode = findCommunity(communityName);
        if (commNode != null)
            commNode.removeFromParent();
        commNode = new DefaultMutableTreeNode(communityName);
        top.add(commNode);
        installOrg(commNode, org);
    }

    void installOrg(DefaultMutableTreeNode communityNode, Hashtable org) {
        lastOrg = org;

        DefaultMutableTreeNode groupNode = null;
        DefaultMutableTreeNode roleNode = null;
        DefaultMutableTreeNode agentNode = null;

        String groupName = "";
        String roleName;
        Vector roles;
        Hashtable group;
        String agentName;
        AgentAddress ag;

        // clear();
        //top.removeAllChildren();
        // BUG - JF: for the moment, the dumpOrg does not send a complete org,
        // but only the public community
        // we will change it as soon as possible
        communityNode.removeAllChildren();

        for (Enumeration e1 = org.keys(); e1.hasMoreElements(); ) {
            groupName = (String) e1.nextElement();
            groupNode = new GroupTreeNode(groupName);
            communityNode.add(groupNode);

            group = (Hashtable) org.get(groupName);
            if (group != null)
                for (Enumeration e2 = group.keys(); e2.hasMoreElements(); ) {
                    roleName = (String) e2.nextElement();
                    roleNode = new DefaultMutableTreeNode(roleName);
                    groupNode.add(roleNode);

                    roles = (Vector) group.get(roleName);

                    for (Enumeration e3 = roles.elements(); e3.hasMoreElements(); ) {
                        //stem.err.println(e3.nextElement());

                        ag = (AgentAddress) e3.nextElement();
                        // if (displayAddresses)
                        //     agentName = ag.getName();
                        // else
                        agentName = ag.toString();
                        agentNode = new DefaultMutableTreeNode(agentName);
                        roleNode.add(agentNode);
                    }
                }
        }
        treeModel.reload();
    }

    public void clear() {
        top.removeAllChildren();
        treeModel.reload();
    }
}

/**
 * The GUI for the GroupObserver
 */
class GroupObserverGUI extends JPanel {
    JTable messagesTable;
    JTable actionsTable;
    DefaultTableModel messagesModel;
    DefaultTableModel actionsModel;
    JSplitPane jp;

    Vector messages = new Vector();

    GroupObserver myAgent;

    JLabel messageDisplay = new JLabel("msg:");
    TreeGroupObserverPanel tree;
    String[] messagesColumnNames = {"Sender", "Receiver", "Message Class", "Content", "Date"};
    String[] actionsColumnNames = {"Agent", "Action", "Community", "Group", "Role", "Date"};

    public GroupObserverGUI(GroupObserver _ag) {
        myAgent = _ag;
        //      setSize(200,100);
        setLayout(new BorderLayout());
        // setTitle("GroupMessageTracer");

        JToolBar toolbar = new JToolBar();
        // addTool(toolbar,"observe group","/demo/agents/system/joingroup.gif"); // "/demo/agents/system/joingroup.gif"
        addTool(toolbar, "clear messages", "/images/toolbars/clear_messages24.gif"); // "/demo/agents/system/joingroup.gif"
        addTool(toolbar, "clear actions", "/images/toolbars/clear_actions24.gif"); // "/demo/agents/system/joingroup.gif"
        addTool(toolbar, "refresh", "/toolbarButtonGraphics/general/Refresh24.gif"); // "/demo/agents/system/joingroup.gif"
        addTool(toolbar, "show messages", "/images/toolbars/run.gif"); // "/demo/agents/system/joingroup.gif"
        addTool(toolbar, "stop messages", "/images/toolbars/halt.gif"); // "/demo/agents/system/joingroup.gif"
        add("North", toolbar);

        tree = new TreeGroupObserverPanel(myAgent);
        messagesModel = new DefaultTableModel(messagesColumnNames, 0);
        messagesTable = new JTable(messagesModel);
        JScrollPane scrollMessagesPane = new JScrollPane(messagesTable);

        actionsModel = new DefaultTableModel(actionsColumnNames, 0);
        actionsTable = new JTable(actionsModel);
        JScrollPane scrollActionsPane = new JScrollPane(actionsTable);

        tree.setPreferredSize(new Dimension(380, 200));
        tree.setMinimumSize(new Dimension(100, 80));
        //messagesTable.setPreferredScrollableViewportSize(new Dimension(300, 100));
        //actionsTable.setPreferredScrollableViewportSize(new Dimension(300, 100));

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.add("Messages", scrollMessagesPane);
        tabbed.add("Actions", scrollActionsPane);

        tabbed.setPreferredSize(new Dimension(380, 200));
        tabbed.setMinimumSize(new Dimension(100, 80));

        jp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tree, tabbed);
        jp.setOneTouchExpandable(true);
        jp.setDividerSize(8);

        add("Center", jp);
        jp.setDividerLocation(0.5);
        add("South", messageDisplay);
    }

    void setMessageDisplay(String s) {
        messageDisplay.setText("msg:" + s);
    }

    TreeGroupObserverPanel getTree() {
        return tree;
    }

    void addTool(JToolBar toolBar, String name, String imageName) {
        JButton b;
        if ((imageName == null) || (imageName.equals(""))) {
            b = (JButton) toolBar.add(new JButton(name));
            b.setActionCommand(name);
        } else {
            ImageIcon i = null;
            java.net.URL u = this.getClass().getResource(imageName);
            if (u != null)
                i = new ImageIcon(u);

            if ((i != null) && (i.getImage() != null))
                b = (JButton) toolBar.add(new JButton(i));
            else
                b = (JButton) toolBar.add(new JButton(name));
            b.setActionCommand(name);
        }

        b.setToolTipText(name);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        command(e.getActionCommand());
                    }
                });
    }

    public void addMessage(String s, String r, String cl, String c, String d) {
        String[] m = new String[5];
        m[0] = s;
        m[1] = r;
        m[2] = cl;
        m[3] = c;
        m[4] = d;
        messagesModel.addRow(m);
    }

    public void addAction(String s, String a, String c, String g, String r, String d) {
        String[] m = new String[6];
        m[0] = s;
        m[1] = a;
        m[2] = c;
        m[3] = g;
        m[4] = r;
        m[5] = d;
        actionsModel.addRow(m);
    }

    void command(String a) {
        if (a.equals("observe group")) observeGroup();
        else if (a.equals("clear messages")) {
            messagesModel = new DefaultTableModel(messagesColumnNames, 0);
            messagesTable.setModel(messagesModel);
            // tree.clear();
        } else if (a.equals("clear actions")) {
            actionsModel = new DefaultTableModel(actionsColumnNames, 0);
            actionsTable.setModel(actionsModel);
            // tree.clear();
        } else if (a.equals("refresh")) {
            tree.refresh();
            //jp.resetToPreferredSizes();
            jp.validate();
        } else if (a.equals("show messages"))
            myAgent.setShowMessageState(true);
        else if (a.equals("stop messages"))
            myAgent.setShowMessageState(false);
    }

    void observeGroup() {
        // new GroupObserverDialog(myAgent);
    }

    void setTitle(String title) {
        Container c = this;
        while (!((c instanceof JFrame) ||
                (c instanceof Frame)
                || (c instanceof JInternalFrame))) {
            if (c == null)
                return;
            else
                c = c.getParent();
        }
        if (c instanceof Frame) {
            ((Frame) c).setTitle(title);
        } else if (c instanceof JInternalFrame) {
            ((JInternalFrame) c).setTitle(title);
        }
    }

}


/**
 * This is the third major version of the MessageTracer Agent.
 * As it names implies, this tracer traces only messages of a given group.
 *
 * @author Olivier Gutknecht and Jacques Ferber (for GroupModifications)
 * @version 1.1d
 */


public class GroupObserver extends Agent {
    GroupObserverGUI gui;

    String targetGroup;
    Vector agents;

    Hashtable orgdump;

    boolean showMessages = false;
    boolean parseTime = true;

    void setShowMessageState(boolean b) {
        showMessages = b;
    }

    /**
     * Get the value of parseTime.
     *
     * @return Value of parseTime.
     */
    public boolean getParseTime() {
        return parseTime;
    }

    /**
     * Set the value of parseTime.
     *
     * @param v Value to assign to parseTime.
     */
    public void setParseTime(boolean v) {
        this.parseTime = v;
    }


    @Override
    public void setupFrame(JFrame frame) {
        gui = new GroupObserverGUI(this);
        frame.getContentPane().add(gui);
    }

    public void activate() {
        super.activate();
        setLogLevel(Level.ALL);
        createGroupIfAbsent(LocalCommunity.NAME, LocalCommunity.Groups.SYSTEM);
        requestRole("local", "system", "tracer");

        AgentAddress mykernel = getAgentWithRole(LocalCommunity.NAME, LocalCommunity.Groups.SYSTEM, LocalCommunity.GROUP_MANAGER_ROLE);
        logger.fine("myKernel=null ? -> " + (mykernel == null));


        Set<String> set = getExistingCommunities();

        for (String community : set) {
            logger.info(community);
        }

        //sendMessage(mykernel,new KernelMessage(KernelMessage.INVOKE,Kernel.DUMP_ORGANIZATION));
        sendMessage(mykernel,
                new HookMessage(HookMessage.AgentActionEvent.SEND_MESSAGE));
        sendMessage(mykernel,
                new HookMessage(HookMessage.AgentActionEvent.BROADCAST_MESSAGE));
        sendMessage(mykernel,
                new HookMessage(HookMessage.AgentActionEvent.CREATE_GROUP));
        sendMessage(mykernel,
                new HookMessage(HookMessage.AgentActionEvent.LEAVE_GROUP));
        sendMessage(mykernel,
                new HookMessage(HookMessage.AgentActionEvent.REQUEST_ROLE));
        sendMessage(mykernel,
                new HookMessage(HookMessage.AgentActionEvent.LEAVE_ROLE));
        sendMessage(mykernel,
                new HookMessage(HookMessage.AgentActionEvent.AGENT_STARTED));
        sendMessage(mykernel,
                new HookMessage(HookMessage.AgentActionEvent.AGENT_TERMINATED));
//        sendMessage(mykernel,
//                new HookMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.MIGRATION));
//        sendMessage(mykernel,
//                new HookMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.RESTORE_AGENT));
//        sendMessage(mykernel,
//                new HookMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.CONNECTED_TO));
//        sendMessage(mykernel,
//                new HookMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.DISCONNECTED_FROM));
//        sendMessage(mykernel,
//                new HookMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.NEW_COMMUNITY));
//        sendMessage(mykernel,
//                new HookMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.DELETE_COMMUNITY));
    }

    public void enterGroup(String group) {
        if ((targetGroup != null) && (!group.equals(targetGroup))) {
            leaveGroup("", targetGroup);
        }
//        if (group != null){
//            int i=requestRole(group,"$MessageTracer");
//            targetGroup = group;
//        }
    }

    public void leaveGroups() {
        if (targetGroup != null) {
            leaveGroup(targetGroup, "tracer");
            targetGroup = null;
        }
    }


    protected void requestDumpCommunities() {
//        sendMessage(mykernel,new KernelMessage(KernelMessage.INVOKE,Kernel.DUMP_COMMUNITIES));
    }

    protected Vector getAllGroups() {
        Vector groups = new Vector();
        for (Enumeration e = orgdump.keys(); e.hasMoreElements(); )
            groups.addElement((String) e.nextElement());
        groups.removeElement("system");
        return groups;
    }

    public void live() {
        while (true) {
            Message e = waitNextMessage();
            if (e instanceof HookMessage)
                handleMessage((HookMessage) e);
        }
    }

    public Vector getAgents(String group) {
        if (group != null) {
            Vector<AgentAddress> agents = new Vector<AgentAddress>();
            Set<String> targetRoles = getExistingRoles(LocalCommunity.NAME, targetGroup);
            for (String targetRole : targetRoles) {
                List<AgentAddress> agArray = this.getAgentsWithRole(LocalCommunity.NAME, targetGroup, targetRole);
                for (AgentAddress ref : agArray)
                    agents.addElement(ref);
            }
            return (agents);
        }
        return null;
    }

    protected String showTime() {
        Date d = new Date();
        if (parseTime) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss SSSS");
            return formatter.format(d);
        } else
            return Long.toString(d.getTime());
    }

    protected void handleAgentLifeEvent(AgentLifeEvent message) {
        AgentLifeEvent ale = (AgentLifeEvent) message;
        logger.info("hookmessage:source-agent:" + ale.getSource());

        if (ale.getContent().equals(HookMessage.AgentActionEvent.AGENT_STARTED)) {
            Object o = message.getContent().name();
            AgentAddress ad = null;
            ad = ((AgentLifeEvent) message).getSender();

            if (hasGUI() && (ad != null)) {
                gui.addAction(((AgentLifeEvent) message).getSource().getName(), "agent_started", "", "", "", showTime());
                gui.getTree();
            } else
                logger.info("Agent be : " + o);
        } else if (ale.getContent().equals(HookMessage.AgentActionEvent.AGENT_TERMINATED)) {
            Object o = message.getContent().name();
            AgentAddress ad = null;
//            if (o instanceof AGRTrio){
//                ad = ((AGRTrio) o).getAgent();
//            } else if (o instanceof AgentInformation){
//                ad = ((AgentInformation) o).getAddress();
//            }
            ad = ((AgentLifeEvent) message).getSender();
            if (ad == null)
                ad = message.getReceiver();
            logger.info("" + hasGUI());
            logger.info("" + (ad != null));
            if (hasGUI() && (ad != null)) {
                gui.addAction(((AgentLifeEvent) message).getSource().getName(), "agent_terminated", "", "", "", showTime());
                gui.getTree().killAgent(ad.toString());
            } else
                logger.info("Agent is terminated : " + o);
        } else {
            logger.warning("Unrecognized AgentLifeEvent");
        }
    }

    protected void handleOrganizationEvent(OrganizationEvent message) {
        message.getContent().equals(HookMessage.AgentActionEvent.CREATE_GROUP);
        message.getContent().equals(HookMessage.AgentActionEvent.LEAVE_GROUP);
        message.getContent().equals(HookMessage.AgentActionEvent.REQUEST_ROLE);
        message.getContent().equals(HookMessage.AgentActionEvent.LEAVE_ROLE);
    }

    /**
     * Handling kernel messages
     */
    protected void handleMessage(HookMessage message) {
        logger.info("hookmessage:sender:" + message.getSender());
        logger.info("hookmessage:ObjectMessage:" + message.getContent());
        logger.info("hookmessage:name:" + message.getContent().name());
        logger.info("hookmessage:ordinal:" + message.getContent().ordinal());
        logger.info("hookmessage:conversation-id:" + message.getConversationID());
        logger.info("hookmessage:receiver:" + message.getReceiver());

        if (message instanceof AgentLifeEvent)
            handleAgentLifeEvent((AgentLifeEvent) message);

        else if (message instanceof OrganizationEvent)
            handleOrganizationEvent((OrganizationEvent) message);
        else {
            MessageEvent me = (MessageEvent) message;
            me.getContent().equals(HookMessage.AgentActionEvent.SEND_MESSAGE);
            me.getContent().equals(HookMessage.AgentActionEvent.BROADCAST_MESSAGE);
        }
        //System.out.println(">>> GroupObserver: Received a kernel message");
//        if (m.getOperation() == Kernel.DUMP_ORGANIZATION){
//            Hashtable org = null;
//            // reply from DUMP_ORG
//            if (m.getType() == KernelMessage.REPLY) {
//                org = (Hashtable) m.getArgument();
//                Object subject = m.getSubject();
//                if (!(subject instanceof String))
//                    subject = "public";
//                orgdump = org;//joinGroupInteractively(org);
//                if (hasGUI()){
//                    ((GroupObserverGUI)getGUIObject()).getTree().installOrg((String) subject,org);
//                }
//            }
//        } else if (m.getOperation() == Kernel.DUMP_COMMUNITIES){
//            Map communities = null;
//            // reply from DUMP_ORG
//            if (m.getType() == KernelMessage.REPLY) {
//                communities = (Map) m.getArgument();
//                if (hasGUI()){
//                    ((GroupObserverGUI)getGUIObject()).getTree().installCommunities(communities);
//                }
//            }
//        } else if (m.getOperation() == Kernel.SEND_MESSAGE){
//            Message orig = (Message) m.getArgument();
//            if (hasGUI()){
//                if (showMessages){
//                    if ((targetGroup!=null) && (agents != null)){ // filtering of messages external to the group
//                        if (!(agents.contains(orig.getSender()) && agents.contains(orig.getReceiver())))
//                            return;
//                    }
//                    String s =  orig.getClass().getName();
//                    s = s.substring(s.lastIndexOf('.')+1);
//
//                    String time;
//
//                    if (parseTime){
//                        Date d = orig.getCreationDate();
//                        SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm:ss SSSS");
//                        time = formatter.format(d);
//                    }
//                    else
//                        time = Long.toString(orig.getCreationDate().getTime());
//                    gui.addMessage(orig.getSender().getName(), orig.getReceiver().getName(),
//                            s,//orig.getClass().toString(),
//                            orig.toString(),
//                            time);
//                }
//            } else
//                println("Trace"+orig);
//        } else if (m.getOperation() == Kernel.SEND_BROADCAST_MESSAGE){
//            Vector v = (Vector)m.getArgument();
//            String g = (String) v.elementAt(0);
//            String r = (String) v.elementAt(1);
//            Message orig = (Message) v.elementAt(2);
//            String s =  orig.getClass().getName();
//            s = s.substring(s.lastIndexOf('.')+1);
//
//            if (hasGUI()){
//                if (showMessages){
//                    if ((targetGroup!=null) && (agents != null)){ // filtering of messages external to the group
//                        if (!(targetGroup.equals(g)) && (agents.contains(orig.getSender())))
//                            return;
//                    }
//                    String time;
//
//                    if (parseTime)
//                    {
//                        Date d = orig.getCreationDate();
//                        SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm:ss SSSS");
//                        time = formatter.format(d);
//                    }
//                    else
//                        time = Long.toString(orig.getCreationDate().getTime());
//                    gui.addMessage(orig.getSender().getName(),
//                            "<"+g+","+r+">", s, orig.toString(), time);
//                }
//            }
//        } else if (m.getOperation() == Kernel.NEW_COMMUNITY){
//            String comm = (String) m.getArgument();
//            //sendMessage(mykernel,new KernelMessage(KernelMessage.INVOKE,Kernel.DUMP_ORGANIZATION,comm));
//            requestDumpCommunities();
//            /** AGRTrio agr = (AGRTrio) m.getArgument();
//             if (hasGUI()){
//             // System.out.println("foundgroup: " + grObj );
//             String gr=(String) agr.getGroup();
//             gui.getTree().createCommunity(agr.getCommunity());
//             // gui.addAction(agr.getAgent().getName(), "createCommunity", gr, "", showTime());
//             }
//             else
//             println("NEW_COMMUNITY <"+agr.getGroup()+"> by "+agr.getAgent().getName()); */
//        } else if (m.getOperation() == Kernel.DELETE_COMMUNITY){
//            String comm = (String) m.getArgument();
//            if (hasGUI()){
//                gui.getTree().deleteCommunity(comm);
//            }
//            else
//                println("REMOVE_COMMUNITY <"+comm+"> ");
//        }  else if (m.getOperation() == Kernel.CREATE_GROUP){
//            agents=getAgents(targetGroup);
//            AGRTrio agr = (AGRTrio) m.getArgument();
//            if (hasGUI()){
//                // System.out.println("foundgroup: " + grObj );
//                String gr=(String) agr.getGroup();
//                String comm = (String) agr.getCommunity();
//                gui.getTree().createGroup(comm,gr,agr.getAgent().toString());
//                gui.addAction(agr.getAgent().getName(),
//                        "createGroup", comm, gr, "", showTime());
//            }
//            else
//                println("CREATE_GROUP <"+agr.getGroup()+"> by "+agr.getAgent().getName());
//        } else if (m.getOperation() == Kernel.ADD_MEMBER_ROLE){
//            agents=getAgents(targetGroup);
//            AGRTrio agr = (AGRTrio) m.getArgument();
//            if (hasGUI()){
//                // System.out.println("add member role: " + agr );
//                String gr=(String) agr.getGroup();
//                String role=(String) agr.getRole();
//                String comm = (String) agr.getCommunity();
//                gui.getTree().enterRole(comm,gr,role,agr.getAgent().toString());
//                gui.addAction(agr.getAgent().getName(), "add_member_role",comm,gr,role,showTime());
//            } else
//                println("ADD_MEMBER_ROLE <"+agr.getGroup()+"> by "+agr.getAgent().getName());
//        } else if (m.getOperation() == Kernel.REMOVE_MEMBER_ROLE) {
//            agents=getAgents(targetGroup);
//            AGRTrio agr = (AGRTrio) m.getArgument();
//            if (hasGUI()){
//                // System.out.println("remove member role: " + agr );
//                String gr=(String) agr.getGroup();
//                String role=(String) agr.getRole();
//                String comm = (String) agr.getCommunity();
//                gui.getTree().leaveRole(comm,gr,role,agr.getAgent().toString());
//                gui.addAction(agr.getAgent().getName(),"remove_member_role",comm,gr,role,showTime());
//            } else
//                println("REMOVE_MEMBER_ROLE <"+agr.getGroup()+"> by "+agr.getAgent().getName());
//        } else if (m.getOperation() == Kernel.LEAVE_GROUP){
//            //sendMessage(mykernel,new KernelMessage(KernelMessage.INVOKE,Kernel.DUMP_ORGANIZATION));
//            agents=getAgents(targetGroup);
//            AGRTrio agr = (AGRTrio) m.getArgument();
//            if (hasGUI()){
//                //  System.out.println("leave group: " + agr );
//                String gr=(String) agr.getGroup();
//                String comm = (String) agr.getCommunity();
//                gui.getTree().leaveGroup(comm,gr,agr.getAgent().toString());
//                gui.addAction(agr.getAgent().getName(), "leave_group", comm, gr, "", showTime());
//            } else
//                println("LEAVE_GROUP <"+agr.getGroup()+"> by "+agr.getAgent().getName());
//        } else if (m.getOperation() == Kernel.KILL_AGENT){
//            Object o = m.getArgument();
//            AgentAddress ad=null;
//            if (o instanceof AGRTrio){
//                ad = ((AGRTrio) o).getAgent();
//            } else if (o instanceof AgentInformation){
//                ad = ((AgentInformation) o).getAddress();
//            }
//            if (hasGUI() && (ad != null)){
//                gui.addAction(ad.getName(), "kill_agent", "","", "", showTime());
//                gui.getTree().killAgent(ad.toString());
//            } else
//                println("KILL_AGENT "+ad.getName());
//        } else  if (m.getOperation() == Kernel.CONNECTED_TO){
//            Object o = m.getArgument();
//            if (o instanceof KernelAddress){
//                String host = ((KernelAddress) o).getHost();
//                gui.setMessageDisplay("connection to " + host);
//                //sendMessage(mykernel,new KernelMessage(KernelMessage.INVOKE,Kernel.DUMP_ORGANIZATION));
//            }
//        } else  if (m.getOperation() == Kernel.DISCONNECTED_FROM){
//            Object o = m.getArgument();
//            if (o instanceof KernelAddress){
//                String host = ((KernelAddress) o).getHost();
//                gui.setMessageDisplay("disconnected from " + host);
//                //sendMessage(mykernel,new KernelMessage(KernelMessage.INVOKE,Kernel.DUMP_ORGANIZATION));
//            }
//        }
    }

}