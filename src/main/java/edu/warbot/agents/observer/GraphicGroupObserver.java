/*
* GroupObserver.java -an agent which observe MadKit organizations
* Copyright (C) 2002 David Pujol
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
package edu.warbot.agents.observer;

import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import madkit.message.hook.HookMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.*;

public class GraphicGroupObserver extends Agent {
    GraphicGroupObserverGUI gui;
    HashMap map;

    public GraphicGroupObserver() {
        HashMap toSpy = new HashMap();
        toSpy.put("Community", "travel");
        map = new HashMap();
        map.put("toSpy", toSpy);
//		map.put("autoRemove", new Boolean(true));
        initMap();
    }

    public GraphicGroupObserver(HashMap map) {
        this.map = map;
        initMap();
    }

    public void initGUI() {
        gui = new GraphicGroupObserverGUI(this);
//		setGUIObject(gui);
    }

    public void activate() {
        logger.info("GraphicGroupObserver - activate");
//		createGroup(LocalCommunity.NAME,LocalCommunity.Groups.SYSTEM,"tracer");
        requestRole("public", "system", "tracer", null);

//		AgentAddress kernel = getAgentWithRole("system","kernel");

//		sendMessage(kernel, new KernelMessage(KernelMessage.INVOKE,Kernel.DUMP_COMMUNITIES));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.SEND_MESSAGE));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.SEND_BROADCAST_MESSAGE));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.CREATE_GROUP));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.LEAVE_GROUP));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.ADD_MEMBER_ROLE));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.REMOVE_MEMBER_ROLE));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.KILL_AGENT));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.MIGRATION));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.RESTORE_AGENT));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.CONNECTED_TO));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.DISCONNECTED_FROM));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.NEW_COMMUNITY));
//		sendMessage(kernel, new KernelMessage(KernelMessage.REQUEST_MONITOR_HOOK, Kernel.DELETE_COMMUNITY));
    }

    public void live() {
        while (true) {
            Message message = waitNextMessage();
//			if (message instanceof KernelMessage) handleMessage((KernelMessage)message);
        }
    }

    protected void initMap() {
        Object o = map.get("toSpy");
        if (o == null) {
            o = new HashMap();
            map.put("toSpy", o);
        }
//		o = map.get("colorLife");
//		if (o == null){
//			o = Color.cyan;
//			map.put("colorLife", o);
//		}
//		o = map.get("colorIn");
//		if (o == null){
//			o = Color.green;
//			map.put("colorIn", o);
//		}o = map.get("colorGroupIn");
        if (o == null) {
            o = Color.BLUE;
            map.put("colorCreateGroup", o);
        }
        o = map.get("colorGroupIn");
        if (o == null) {
            o = Color.MAGENTA;
            map.put("colorGroupIn", o);
        }
        o = map.get("colorGroupOut");
        if (o == null) {
            o = Color.orange;
            map.put("colorGroupOut", o);
        }
        o = map.get("colorRoleIn");
        if (o == null) {
            o = Color.green;
            map.put("colorRoleIn", o);
        }
        o = map.get("colorRoleOut");
        if (o == null) {
            o = Color.PINK;
            map.put("colorRoleOut", o);
        }
//		o = map.get("colorOut");
//		if (o == null){
//			o = Color.orange;
//			map.put("colorOut", o);
//		}
        o = map.get("colorKill");
        if (o == null) {
            o = Color.red;
            map.put("colorKill", o);
        }
        o = map.get("colorMess");
        if (o == null) {
            o = Color.black;
            map.put("colorMess", o);
        }
        o = map.get("colorMessToAll");
        if (o == null) {
            o = Color.DARK_GRAY;
            map.put("colorMessToAll", o);
        }
        o = map.get("addFirst");
        if (o == null) {
            o = new Boolean(true);
            map.put("addFirst", o);
        }
        o = map.get("autoRemove");
        if (o == null) {
            o = new Boolean(false);
            map.put("autoRemove", o);
        }
    }

    protected void dumpCommunities(Map list) {
        Iterator it = list.keySet().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            HashMap map = new HashMap();
            map.put("Community", o.toString());
//System.out.println(o);
            dumpGroup((Map) list.get(o), map);
        }
    }

    protected void dumpGroup(Map list, HashMap m) {
        Iterator it = list.keySet().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            HashMap map = new HashMap(m);
            map.put("Group", o.toString());
//System.out.println("\t"+o);
            dumpRole((Map) list.get(o), map);
        }
    }

    protected void dumpRole(Map list, HashMap m) {
        Iterator it = list.keySet().iterator();
        while (it.hasNext()) {
            Object o = it.next();
            HashMap map = new HashMap(m);
            map.put("Role", o.toString());
//System.out.println("\t\t"+o);
            dumpAgent((Vector) list.get(o), map);
        }
    }

    protected void dumpAgent(Vector v, HashMap map) {
        for (Enumeration e = v.elements(); e.hasMoreElements(); ) {
            gui.addExistingAgent((AgentAddress) e.nextElement(), map);
        }
    }

    protected void handleMessage(HookMessage message) {


//		if (message.getOperation() == Kernel.DUMP_COMMUNITIES){
//			Map communities = null;
//			 reply from DUMP_ORG
//			if (message.getType() == KernelMessage.REPLY) {
//				if (hasGUI()){
//					dumpCommunities((Map) message.getArgument());
//				}
//			}
//		}
//
//		else if (message.getOperation() == Kernel.CREATE_GROUP){
//			AGRTrio agr = (AGRTrio) message.getArgument();
//			if (hasGUI()){
//				AgentEventCreateGroup event = new AgentEventCreateGroup(AgentEvent.ADD_TO,
//													new Date().getTime(),
//													agr.getAgent(),
//													agr.getCommunity(),
//													agr.getGroup()
//													);
//				event.setColor((Color) this.map.get("colorCreateGroup"));
//				HashMap map = new HashMap();
//				map.put("Community", agr.getCommunity());
//				map.put("Group", agr.getGroup());
//				gui.addMemberRole(event, map);
//			}
//			else
//				println("ADD_MEMBER_ROLE <"+agr.getGroup()+"> by "+agr.getAgent().getName());
//		}
//
//		else if (message.getOperation() == Kernel.ADD_MEMBER_ROLE){
//			AGRTrio agr = (AGRTrio) message.getArgument();
//			if (hasGUI()){
//				AgentEventAddTo event = new AgentEventAddTo(AgentEvent.ADD_TO,
//													new Date().getTime(),
//													agr.getAgent(),
//													agr.getCommunity(),
//													agr.getGroup(),
//													agr.getRole()
//													);
//				event.setColor((Color) this.map.get("colorRoleIn"));
//				HashMap map = new HashMap();
//				map.put("Community", agr.getCommunity());
//				map.put("Group", agr.getGroup());
//				map.put("Role", agr.getRole());
//				gui.addMemberRole(event, map);
//			}
//			else
//				println("ADD_MEMBER_ROLE <"+agr.getGroup()+"> by "+agr.getAgent().getName());
//		}
//
//		else if (message.getOperation() == Kernel.REMOVE_MEMBER_ROLE) {
//			AGRTrio agr = (AGRTrio) message.getArgument();
//    		if (hasGUI()){
//				AgentEventRemoveFromRole event = new AgentEventRemoveFromRole(AgentEvent.REMOVE_FROM, new Date().getTime(),  agr.getAgent(),
//						agr.getCommunity(), agr.getGroup(), agr.getRole());
//				event.setColor((Color) this.map.get("colorRoleOut"));
//				HashMap map = new HashMap();
//				map.put("Community", agr.getCommunity());
//				map.put("Group", agr.getGroup());
//				map.put("Role", agr.getRole());
//				gui.removeFrom(event, map);
//			}
//			else
//				println("REMOVE_MEMBER_ROLE <"+agr.getGroup()+"> by "+agr.getAgent().getName());
//		}
//
//		else if (message.getOperation() == Kernel.LEAVE_GROUP){
//			AGRTrio agr = (AGRTrio) message.getArgument();
//			if (hasGUI()){
//				AgentEventRemoveFromGroup event = new AgentEventRemoveFromGroup(AgentEvent.REMOVE_FROM, new Date().getTime(),  agr.getAgent(),
//						agr.getCommunity(), agr.getGroup());
//				event.setColor((Color)this.map.get("colorGroupOut"));
//				HashMap map = new HashMap();
//				map.put("Community", agr.getCommunity());
//				map.put("Group", agr.getGroup());
//				gui.removeFrom(event, map);
//			}
//			else
//				println("LEAVE_GROUP <"+agr.getGroup()+"> by "+agr.getAgent().getName());
//		}
//
//		else if (message.getOperation() == Kernel.KILL_AGENT){
//			Object o = message.getArgument();
//			AgentAddress ad = null;
//			if (o instanceof AGRTrio){
//				ad = ((AGRTrio) o).getAgent();
//			}
//			else if (o instanceof AgentInformation){
//				ad = ((AgentInformation) o).getAddress();
//			}
//			if (hasGUI() && (ad != null)){
//				AgentEventKillAgent event = new AgentEventKillAgent(AgentEvent.KILL_AGENT, new Date().getTime(), ad);
//				event.setColor((Color)this.map.get("colorKill"));
//				gui.killAgent(event);
//			}
//			else
//				println("KILL_AGENT "+ad.getName());
//		}
//
//		else if (message.getOperation() == Kernel.SEND_MESSAGE){
//			Message orig = (Message) message.getArgument();
//			if (hasGUI()){
//
//				String messType =  orig.getClass().getName();
//				messType = messType.substring(messType.lastIndexOf('.') + 1);
//
//				AgentEventSendMessage event = new AgentEventSendMessage(AgentEvent.SEND_MESSAGE,
//													orig.getCreationDate().getTime(),
//													orig.getSender(),
//													orig.getReceiver(),
//													messType,
//													orig.toString());
//
//				event.setColor((Color)this.map.get("colorMess"));
//				gui.addMessage(event);
//			}
//			else println("Trace " + orig);
//		}
//
//		else if (message.getOperation() == Kernel.SEND_BROADCAST_MESSAGE){
//			Vector v = (Vector)message.getArgument();
//			String g = (String) v.elementAt(0);
//			String r = (String) v.elementAt(1);
//			Message orig = (Message) v.elementAt(2);
//
//			String messType =  orig.getClass().getName();
//			messType = messType.substring(messType.lastIndexOf('.')+1);
//
//			if (hasGUI()){
//				HashMap map = new HashMap();
//				map.put("Group", g);
//				map.put("Role", r);
//				AgentEventSendBroadcastMessage event = new AgentEventSendBroadcastMessage(AgentEvent.SEND_BROADCAST_MESSAGE,
//													orig.getCreationDate().getTime(),
//													orig.getSender(),
//													map,
//													messType,
//													orig.toString());
//				event.setColor((Color)this.map.get("colorMessToAll"));
//				gui.addBroadcastMessage(event);
//			}
//		}
//
//		else if (message.getOperation() == Kernel.DELETE_COMMUNITY){
//			String comm = (String) message.getArgument();
//			if (hasGUI()){
//				HashMap map = new HashMap();
//				map.put("Community", comm);
//				AgentEventSimple event = new AgentEventSimple(AgentEvent.REMOVE_ALL_FROM, new Date().getTime());
//				gui.removeAllFrom(event, map);
//			}
//			else
//				println("REMOVE_COMMUNITY <"+comm+"> ");
//		}
    }

    public HashMap getMap() {
        return map;
    }
}

class GraphicGroupObserverGUI extends JPanel implements ActionListener {
    GraphicGroupObserverGUIPane graphic;
    GraphicGroupObserver agent;

    public GraphicGroupObserverGUI(GraphicGroupObserver agent) {
        super(new BorderLayout());

        this.agent = agent;
        graphic = new GraphicGroupObserverGUIPane(this);

        JScrollPane sp = new JScrollPane(graphic);
        add(sp, "Center");

        JMenuBar menubar = new JMenuBar();
        //menubar.setBackground(Color.blue);
        JMenu te = new JMenu("Tools");
        JMenuItem mi = te.add("Remove killed agent");
        mi.setActionCommand("removeKilled");
        mi.addActionListener(this);

        if (((Boolean) agent.getMap().get("addFirst")).booleanValue()) {
            mi = te.add("Add agent at the end");
            mi.setActionCommand("addEnd");
            mi.addActionListener(this);
        } else {
            mi = te.add("Add agent at the begining");
            mi.setActionCommand("addFirst");
            mi.addActionListener(this);
        }

        menubar.add(te);
        add(menubar, BorderLayout.NORTH);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("removeKilled")) {
            graphic.removeKilledAgent();
        } else if (command.equals("addEnd")) {
            ((JMenuItem) e.getSource()).setActionCommand("addFirst");
            ((JMenuItem) e.getSource()).setText("Add agent at the begining");
            agent.getMap().put("addFirst", new Boolean(false));
        } else if (command.equals("addFirst")) {
            ((JMenuItem) e.getSource()).setActionCommand("addEnd");
            ((JMenuItem) e.getSource()).setText("Add agent at the end");
            agent.getMap().put("addFirst", new Boolean(true));
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(500, 400);
    }

    public HashMap getMap() {
        return agent.getMap();
    }

    public void addExistingAgent(AgentAddress agent, HashMap map) {
        graphic.addExistingAgent(agent, map);
    }

    public void addMemberRole(AgentEvent event, HashMap map) {
        graphic.addMemberRole(event, map);
    }

    public void removeFrom(AgentEvent event, HashMap map) {
        graphic.removeFrom(event, map);
    }

    public void removeAllFrom(AgentEvent event, HashMap map) {
        graphic.removeAllFrom(event, map);
    }

    public void killAgent(AgentEvent event) {
        graphic.killAgent(event);
    }

    public void addMessage(AgentEvent event) {
        graphic.addMessage(event);
    }

    public void addBroadcastMessage(AgentEvent event) {
        graphic.addBroadcastMessage(event);
    }
}

class GraphicGroupObserverGUIPane extends JPanel implements MouseMotionListener, MouseListener {
    protected LinkedList agents = null;
    protected LinkedList events = null;
    protected GraphicHeader graphicHeader;
    protected GraphicBody graphicBody;
    protected GraphicLeft graphicLeft;
    protected GraphicDown graphicDown;
    protected int agentH, agentW, staticStep, margeX, margeY, newCol, currentCol;
    protected HashMap toSpy;
    protected GraphicGroupObserverGUI gui;
    protected AgentEvent currentEvent;

    public GraphicGroupObserverGUIPane(GraphicGroupObserverGUI gui) {
        super(new BorderLayout());
        this.gui = gui;
        this.toSpy = (HashMap) gui.getMap().get("toSpy");

        JPanel pane = new JPanel(new BorderLayout());
        graphicHeader = new GraphicHeader(this);
        pane.add(graphicHeader, BorderLayout.NORTH);
        graphicBody = new GraphicBody(this);
        pane.add(graphicBody, BorderLayout.CENTER);
        add(pane, BorderLayout.CENTER);

        graphicLeft = new GraphicLeft(this);
        add(graphicLeft, BorderLayout.WEST);

        graphicDown = new GraphicDown(this);
        add(graphicDown, BorderLayout.SOUTH);

        agentW = 80;
        agentH = 60;
        staticStep = 10;
        margeX = 10;
        margeY = 10;
        currentCol = -1;

        agents = new LinkedList();
        events = new LinkedList();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        if (agents.size() > 0) {
            Stroke stroke = g2D.getStroke();

            g2D.setColor(Color.LIGHT_GRAY);
            float dash[] = {10, 5, 5, 5};
            g2D.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

            g2D.drawLine(graphicLeft.getWidth(), agentH, (agents.size() * agentW) + graphicLeft.getWidth(), agentH);
            g2D.drawLine(graphicLeft.getWidth(), getHeight() - graphicDown.getHeight(), (agents.size() * agentW) + graphicLeft.getWidth(), getHeight() - graphicDown.getHeight());

            int i = 0;
            g2D.drawLine(graphicLeft.getWidth(), 0, graphicLeft.getWidth(), getHeight() - graphicDown.getHeight());
            Iterator it = agents.iterator();
            while (it.hasNext()) {
                AgentSpy agentSpy = (AgentSpy) it.next();

                i++;
                int x = i * agentW + graphicLeft.getWidth();
                g2D.drawLine(x, 0, x, getHeight() - graphicDown.getHeight());
            }
            //draw the arrow
            g2D.setStroke(stroke);
            int y = margeY + events.size() * staticStep + agentH;
            int[] X = {graphicLeft.getWidth(), graphicLeft.getWidth() - 8, graphicLeft.getWidth() - 8};
            int[] Y = {y, y - 6, y + 6};

            g2D.setColor(Color.red);
            g2D.fill(new Polygon(X, Y, 3));

            g2D.setColor(Color.black);
            g2D.draw(new Polygon(X, Y, 3));

            g2D.drawLine(graphicLeft.getWidth(), y, (agents.size() * agentW) + graphicLeft.getWidth(), y);
        }
        if (currentCol >= 0) {

            int x1 = newCol * agentW + graphicLeft.getWidth();
            int x2 = currentCol * agentW + graphicLeft.getWidth();

            g2D.setColor(Color.red);
            g2D.draw(new Rectangle(x2 + (margeX / 2), margeY / 2, agentW - margeX, getHeight() - margeY - graphicDown.getHeight()));

            Stroke stroke = g2D.getStroke();
            g2D.setStroke(new BasicStroke(2f));

            g2D.setColor(Color.blue);
            g2D.drawLine(x1, margeX, x1, getHeight() - margeY - graphicDown.getHeight());

            g2D.setStroke(stroke);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (currentCol >= 0) {
            newCol = (int) Math.round((double) (e.getX() - graphicLeft.getWidth()) / (double) agentW);
            newCol = Math.min(newCol, agents.size());
            newCol = Math.max(newCol, 0);
            repaint();
        } else mousePressed(e);
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        int j = (e.getX() - graphicLeft.getWidth());
        int i = j / agentW;
        if (j >= 0 && i < agents.size()) currentCol = i;
        else currentCol = -1;
        newCol = currentCol;
    }

    public void mouseReleased(MouseEvent e) {
        if (currentCol >= 0) {
            if (newCol > currentCol) {
                newCol--;
            }
            if (newCol != currentCol) {
                Object o = agents.remove(currentCol);
                agents.add(newCol, o);
            }
            currentCol = -1;
            repaint();
        }
    }


    protected synchronized void addAgentFirst(AgentSpy agentSpy) {
        agents.addFirst(agentSpy);
        Dimension d = getSize();
        d.width += getAgentWidth();
        setSize(d);
        validate();
    }

    protected synchronized void addAgentLast(AgentSpy agentSpy) {
        agents.addLast(agentSpy);
        Dimension d = getSize();
        d.width += getAgentWidth();
        setSize(d);
        validate();
    }

    protected synchronized void removeAgent(AgentSpy agentSpy) {
        if (agents.remove(agentSpy)) {
            Dimension d = getSize();
            d.width -= getAgentWidth();
            setSize(d);
            validate();
        }
    }

    protected synchronized void addEvent(AgentEvent event) {
        if (events.add(event)) {
            Dimension d = getSize();
            d.height += getStaticStep();
            setSize(d);
            validate();
        }
    }

    protected synchronized void removeEvent(AgentEvent event) {
        if (events.remove(event)) {
            Dimension d = getSize();
            d.height -= getStaticStep();
            setSize(d);
            validate();
        }
    }

    public synchronized void addExistingAgent(AgentAddress agent, HashMap map) {
        if (spy(map)) {
            AgentSpy agentSpy = haveAgent(agent);
            if (agentSpy == null) {
                agentSpy = new AgentSpy(this, agent);
                //agentSpy.setColor((Color)gui.getMap().get("colorLife"));
                agentSpy.addMap(toSpy);
                agentSpy.addMap(map);
                agentSpy.setComeEvent(null);

                if (((Boolean) gui.getMap().get("addFirst")).booleanValue()) addAgentFirst(agentSpy);
                else addAgentLast(agentSpy);
            } else {
                agentSpy.addMap(map);
            }
            repaint();
        }
    }

    public void addMemberRole(AgentEvent event, HashMap map) {
        if (spy(map)) {
            AgentSpy agentSpy = haveAgent(event.getSource());
            if (agentSpy == null) {
                agentSpy = new AgentSpy(this, event.getSource());
                // agentSpy.setColor((Color)gui.getMap().get("colorLife"));
                agentSpy.addMap(toSpy);
                agentSpy.addMap(map);
                agentSpy.setComeEvent(event);

                if (((Boolean) gui.getMap().get("addFirst")).booleanValue()) addAgentFirst(agentSpy);
                else addAgentLast(agentSpy);
            } else {
                if (!agentSpy.Spied()) agentSpy.setComeEvent(event);
                if (agentSpy.getNbMap() <= 0) agentSpy.addMap(toSpy);
                agentSpy.addMap(map);
            }
            addEvent(event);
            repaint();
        }
    }

    public void removeFrom(AgentEvent event, HashMap map) {
        AgentSpy agentSpy = haveAgent(event.getSource());
        if (agentSpy != null) {
            agentSpy.removeMaps(map);
            if (agentSpy.Spied()) {
                if (agentSpy.getNbMap() <= 0) agentSpy.setQuitEvent(event);
                addEvent(event);
            }
            repaint();
        }
    }

    public void removeAllFrom(AgentEvent event, HashMap map) {
        Iterator it = agents.iterator();
        boolean bool = false;
        while (it.hasNext()) {
            AgentSpy agentSpy = (AgentSpy) it.next();
            if (agentSpy != null) {
                bool = true;
                agentSpy.removeMaps(map);
                if (agentSpy.Spied()) {
                    if (agentSpy.getNbMap() <= 0) agentSpy.setQuitEvent(event);
                }
            }
        }
        if (bool) {
            addEvent(event);
            repaint();
        }
    }

    public void killAgent(AgentEvent event) {
        AgentSpy agentSpy = haveAgent(event.getSource());
        if (agentSpy != null) {
            if (agentSpy.Spied()) agentSpy.setQuitEvent(event);
            agentSpy.setKilled(true);
            addEvent(event);
            if (((Boolean) gui.getMap().get("autoRemove")).booleanValue()) removeKilledAgent();
        }
        repaint();
    }

    public void addMessage(AgentEvent event) {
        AgentSpy agentSpy1 = haveAgent(event.getSource());
        AgentSpy agentSpy2 = haveAgent(event.getDest());
        if (agentSpy1 != null && agentSpy2 != null && agentSpy1 != agentSpy2) {
            if (agentSpy1.Spied() && agentSpy2.Spied()) addEvent(event);
        }
    }

    public void addBroadcastMessage(AgentEvent event) {
        AgentSpy agentSpy = haveAgent(event.getSource());
        if (agentSpy != null) {
            if (agentSpy.Spied()) addEvent(event);
        }
    }

    protected AgentSpy haveAgent(AgentAddress agent) {
        Iterator it = agents.iterator();
        while (it.hasNext()) {
            AgentSpy agentSpy = (AgentSpy) it.next();
            if (agentSpy.getAgentAddress().equals(agent)) return agentSpy;
        }
        return null;
    }

    public boolean spy(HashMap map) {
        Iterator it = toSpy.keySet().iterator();
        while (it.hasNext()) {
            Object o1 = it.next();
            Object o2 = toSpy.get(o1);
            if (!o2.equals(map.get(o1))) return false;
        }
        return true;
    }

    public int getPos(AgentAddress ad) {
        Iterator it = agents.iterator();
        int i = 0;
        while (it.hasNext()) {
            AgentSpy agentSpy = (AgentSpy) it.next();
            if (agentSpy.getAgentAddress().equals(ad)) return i;
            i++;
        }
        return -1;
    }

    public synchronized void removeKilledAgent() {
        Iterator it = agents.iterator();
        while (it.hasNext()) {
            AgentSpy agentSpy = (AgentSpy) it.next();
            if (agentSpy.isKilled()) it.remove();
        }

        it = events.iterator();
        while (it.hasNext()) {
            AgentEvent event = (AgentEvent) it.next();
            if (!event.valid(this)) it.remove();
        }
        repaint();
    }

    public AgentEvent getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(AgentEvent event) {
        currentEvent = event;
        repaint();
    }

    public LinkedList getListAgent() {
        return agents;
    }

    public LinkedList getListEvent() {
        return events;
    }

    public int getAgentWidth() {
        return agentW;
    }

    public int getHeaderHeight() {
        return agentH;
    }

    public int getStaticStep() {
        return staticStep;
    }

    public int getMargeX() {
        return margeX;
    }

    public int getMargeY() {
        return margeY;
    }
}

class GraphicHeader extends JPanel {
    GraphicGroupObserverGUIPane parent;

    public GraphicHeader(GraphicGroupObserverGUIPane parent) {
        this.parent = parent;
        this.setBackground(Color.WHITE);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        int i = 0;
        Iterator it = parent.getListAgent().iterator();
        while (it.hasNext()) {
            AgentSpy agentSpy = (AgentSpy) it.next();

            int x = i * parent.getAgentWidth();
            Rectangle r = new Rectangle(x + parent.getMargeX(), parent.getMargeY(), parent.getAgentWidth() - 2 * parent.getMargeX(), parent.getHeaderHeight() - 2 * parent.getMargeY());
            g2D.draw(r);

            i++;
            int X = x + parent.getMargeX() + 3;
            int Y = parent.getMargeY() + 3;
            int W = parent.getAgentWidth() - 6 - 2 * parent.getMargeX();
            int H = parent.getHeaderHeight() - 6 - 2 * parent.getMargeY();

            Rectangle rect = g2D.getClipBounds();
            g2D.clipRect(X, Y, W, H);

            float drawPosY = Y;
            String str = agentSpy.getAgentAddress().getAgentNetworkID();
            if (str.length() > 0) {
                AttributedString as = new AttributedString(str, g2D.getFont().getAttributes());
                AttributedCharacterIterator paragraph = as.getIterator();
                FontRenderContext frc = new FontRenderContext(null, false, false);
                int paragraphStart = paragraph.getBeginIndex();
                int paragraphEnd = paragraph.getEndIndex();
                LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
                lineMeasurer.setPosition(paragraphStart);

                try {
                    while (lineMeasurer.getPosition() < paragraphEnd) {
                        TextLayout layout = lineMeasurer.nextLayout(W);
                        drawPosY += g2D.getFontMetrics(g2D.getFont()).getAscent();
                        float drawPosX = X;
                        layout.draw(g2D, drawPosX, drawPosY);
                        drawPosY += g2D.getFontMetrics(g2D.getFont()).getDescent() + g2D.getFontMetrics(g2D.getFont()).getLeading();
                    }
                } catch (Exception e) {
                }
            }
            if (rect != null)
                g2D.setClip((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
            else g2D.setClip(null);
        }
    }

    public Dimension getPreferredSize() {
        int x = Math.max(parent.getListAgent().size(), 1) * parent.getAgentWidth();
        int y = parent.getHeaderHeight();

        return new Dimension(x, y);
    }
}

class GraphicBody extends JPanel implements MouseMotionListener, MouseListener {
    GraphicGroupObserverGUIPane parent;

    public GraphicBody(GraphicGroupObserverGUIPane parent) {
        this.parent = parent;
        this.setBackground(Color.WHITE);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        int i = 0;
        Iterator it = parent.getListAgent().iterator();
        while (it.hasNext()) {
            AgentSpy agentSpy = (AgentSpy) it.next();
            agentSpy.paint(g2D, parent, i);
            i++;
        }

        i = 0;
        it = parent.getListEvent().iterator();
        while (it.hasNext()) {
            AgentEvent event = (AgentEvent) it.next();
            event.paint(g2D, parent, i);
            i++;
        }
    }

    public Dimension getPreferredSize() {
        int x = Math.max(parent.getListAgent().size(), 1) * parent.getAgentWidth();
        int y = Math.max(parent.getListEvent().size(), 1) * parent.getStaticStep() + 2 * parent.getMargeY();

        return new Dimension(x, y);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        e.translatePoint(parent.graphicLeft.getWidth(), 0);
        parent.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        e.translatePoint(parent.graphicLeft.getWidth(), 0);
        parent.mouseReleased(e);
    }

    public void mouseDragged(MouseEvent e) {
        e.translatePoint(parent.graphicLeft.getWidth(), 0);
        parent.mouseDragged(e);
    }

    public void mouseMoved(MouseEvent e) {
        boolean bool = false;
        int i = 0;
        Iterator it = parent.getListEvent().iterator();
        while (it.hasNext() && !bool) {
            AgentEvent event = (AgentEvent) it.next();
            if (event.contain(parent, i, e.getX(), e.getY())) {
                bool = true;
                //event.setColor(new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
                parent.setCurrentEvent(event);
            }
            i++;
        }
        //if (bool) parent.repaint();
        if (!bool) parent.setCurrentEvent(null);
    }

}

class GraphicLeft extends JPanel {
    GraphicGroupObserverGUIPane parent;

    public GraphicLeft(GraphicGroupObserverGUIPane parent) {
        this.parent = parent;
        this.setBackground(Color.WHITE);
    }

    public void paint(Graphics g) {
        super.paint(g);
    }

    public Dimension getPreferredSize() {
        return new Dimension(20, 50);
    }
}

class GraphicDown extends JPanel {
    GraphicGroupObserverGUIPane parent;

    public GraphicDown(GraphicGroupObserverGUIPane parent) {
        this.parent = parent;
        this.setBackground(Color.WHITE);
    }

    public void paint(Graphics g) {
        super.paint(g);
        AgentEvent event = parent.getCurrentEvent();
        if (event != null) event.paintMess(g);
    }

    public Dimension getPreferredSize() {
        return new Dimension(50, 30);
    }
}

class AgentSpy {
    AgentAddress agentAddress;
    long start, end;
    AgentEvent comeEvent, quitEvent;
    String name;
    ArrayList list;
    LinkedList beginEndEvent;
    int step;
    Color color;
    boolean killed;
    GraphicGroupObserverGUIPane parent;

    public AgentSpy(GraphicGroupObserverGUIPane parent, AgentAddress agentAddress) {
        this.agentAddress = agentAddress;
        this.parent = parent;
        start = -1;
        end = -1;
        comeEvent = null;
        quitEvent = null;
        list = new ArrayList();
        beginEndEvent = new LinkedList();

//		if (agentAddress.getKernelAddress())
//			color = Color.cyan;
//		else
//			color = Color.LIGHT_GRAY;
        killed = false;
    }

    public void addMap(HashMap map) {
        list.add(map);
    }

    public void removeMaps(HashMap map) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            HashMap map2 = (HashMap) it.next();
            if (contain(map2, map)) {
                it.remove();
            }
        }
    }

    public boolean contain(HashMap map1, HashMap map2) {
        Iterator it = map2.keySet().iterator();
        while (it.hasNext()) {
            Object o1 = it.next();
            Object o2 = map2.get(o1);
            if (!o2.equals(map1.get(o1))) return false;
        }
        return true;
    }

    public AgentAddress getAgentAddress() {
        return (this.agentAddress);
    }

    public void setName(AgentAddress agentAddress) {
        this.agentAddress = agentAddress;
    }

    public long getStart() {
        return (this.start);
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return (this.end);
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public AgentEvent getComeEvent() {
        return (this.comeEvent);
    }

    public void setComeEvent(AgentEvent comeEvent) {
        this.comeEvent = comeEvent;
        OPoint o = new OPoint();
        o.o1 = comeEvent;
        o.o2 = null;
        beginEndEvent.add(o);
    }

    public int getNbMap() {
        return list.size();
    }

    public AgentEvent getQuitEvent() {
        return (this.quitEvent);
    }

    public void setQuitEvent(AgentEvent quitEvent) {
        this.quitEvent = quitEvent;
        OPoint p = (OPoint) (beginEndEvent.getLast());
        p.o2 = quitEvent;
    }

    public boolean isKilled() {
        return killed;
    }

    public void setKilled(boolean bool) {
        killed = bool;
    }

    public boolean Spied() {
        return (((OPoint) (beginEndEvent.getLast())).o2 == null);
    }

    public boolean SpiedAt(int step) {
        Iterator it = beginEndEvent.iterator();
        while (it.hasNext()) {
            OPoint p = (OPoint) it.next();
            if (p.o2 == null) {
                if (step >= parent.getListEvent().indexOf(p.o1)) return true;
            } else {
                if (step >= parent.getListEvent().indexOf(p.o1) && step <= parent.getListEvent().indexOf(p.o2))
                    return true;
            }
        }

        return false;
    }
    //public LinkedList getBeginEndEvent(){ return beginEndEvent; }

    public void setColor(Color color) {
        this.color = color;
    }

    public void paint(Graphics2D g2D, GraphicGroupObserverGUIPane parent, int pos) {
        int x = (int) ((pos + 0.5) * parent.getAgentWidth()) - 10;
        int nbStep = parent.getListEvent().size();

        Iterator it2 = beginEndEvent.iterator();
        while (it2.hasNext()) {
            OPoint p = (OPoint) it2.next();
            int X = parent.getListEvent().indexOf(p.o1);
            int Y = parent.getListEvent().indexOf(p.o2);

            int y = parent.getMargeY() + (int) (X * parent.getStaticStep());
            int h;
            if (Y >= 0)
                h = (int) (Y - X) * parent.getStaticStep();
            else
                h = (int) (nbStep - X) * parent.getStaticStep();

            g2D.setColor(color);
            g2D.fill(new Rectangle(x, y, 20, h));

            g2D.setColor(Color.black);
            g2D.draw(new Rectangle(x, y, 20, h));
        }
    }
}

class OPoint {
    Object o1, o2;
}

abstract class AgentEvent {
    public static final int SEND_MESSAGE = 1;
    public static final int SEND_BROADCAST_MESSAGE = 2;
    public static final int ADD_TO = 3;
    public static final int REMOVE_FROM = 4;
    public static final int REMOVE_ALL_FROM = 5;
    public static final int KILL_AGENT = 6;

    protected Date date;
    protected int event;
    protected AgentAddress source, dest;
    protected HashMap map;
    protected String messType, mess;
    protected Color color;

    public AgentEvent() {
    }

    protected void init(int event, long date, AgentAddress source, AgentAddress dest, HashMap m, String messType, String mess) {
        this.date = new Date(date);
        this.event = event;
        this.source = source;
        this.dest = dest;
        if (m == null) map = null;
        else map = new HashMap(m);
        this.messType = messType;
        this.mess = mess;
    }

    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss SSSS");
        return formatter.format(date);
    }

    public int getEvent() {
        return event;
    }

    public AgentAddress getSource() {
        return source;
    }

    public AgentAddress getDest() {
        return dest;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    abstract public void paintMess(Graphics g);

    abstract public boolean contain(GraphicGroupObserverGUIPane parent, int step, int X, int Y);

    abstract public boolean valid(GraphicGroupObserverGUIPane parent);

    abstract public void paint(Graphics2D g2D, GraphicGroupObserverGUIPane parent, int step);
}

class AgentEventSimple extends AgentEvent {
    public AgentEventSimple(int event, long date) {
        init(event, date, null, null, null, null, null);
    }

    public boolean contain(GraphicGroupObserverGUIPane parent, int step, int X, int Y) {
        return false;
    }

    public boolean valid(GraphicGroupObserverGUIPane parent) {
        return (parent.getListEvent().indexOf(this) == 0);
    }

    public void paintMess(Graphics g) {
    }

    public void paint(Graphics2D g2D, GraphicGroupObserverGUIPane parent, int step) {
    }
}

abstract class AgentEventOrg extends AgentEvent {
    public AgentEventOrg(int event, long date, AgentAddress source) {
        init(event, date, source, null, null, null, null);
        color = Color.green;
    }

    public boolean contain(GraphicGroupObserverGUIPane parent, int step, int X, int Y) {
        int x = (int) ((parent.getPos(this.getSource()) + 0.5) * parent.getAgentWidth()) - 10;
        int y = parent.getMargeY() + step * parent.getStaticStep() - 2;
        return (new Rectangle(x, y, 20, 4).contains(X, Y));
    }

    public boolean valid(GraphicGroupObserverGUIPane parent) {
        return (parent.getPos(getSource()) >= 0);
    }

    public void paintMess(Graphics g) {
        g.drawString(toString(), 10, 13);
        g.drawString("Event : " + event + "  Time : " + this.getDate(), 10, 26);
    }

    public void paint(Graphics2D g2D, GraphicGroupObserverGUIPane parent, int step) {
        int x = (int) ((parent.getPos(this.getSource()) + 0.5) * parent.getAgentWidth()) - 10;
        int y = parent.getMargeY() + step * parent.getStaticStep() - 2;

        g2D.setColor(color);
        g2D.fill(new Rectangle(x, y, 20, 4));

        g2D.setColor(Color.black);
        g2D.draw(new Rectangle(x, y, 20, 4));
    }
}

class AgentEventAddTo extends AgentEventOrg {
    String community = "public";
    String group = "???";
    String role = "???";

    public AgentEventAddTo(int event, long date, AgentAddress source,
                           String comm, String gr, String ro) {
        super(event, date, source);
        community = comm;
        group = gr;
        role = ro;
        color = Color.green;
    }

    public String toString() {
        return "Agent " + source.toString() + "enter role " + role + " in " + group + " [" + community + "]";
    }
}

class AgentEventRemoveFromGroup extends AgentEventOrg {
    String community = "public";
    String group = "???";

    public AgentEventRemoveFromGroup(int event, long date, AgentAddress source,
                                     String comm, String gr) {
        super(event, date, source);
        community = comm;
        group = gr;
        color = Color.ORANGE;
    }

    public String toString() {
        return "Agent " + source.toString() + " quit group " + group + " [" + community + "]";
    }
}

class AgentEventCreateGroup extends AgentEventOrg {
    String community = "public";
    String group = "???";

    public AgentEventCreateGroup(int event, long date, AgentAddress source,
                                 String comm, String gr) {
        super(event, date, source);
        community = comm;
        group = gr;
        color = Color.BLUE;
    }

    public String toString() {
        return "Agent " + source.toString() + " create group " + group + " [" + community + "]";
    }
}

class AgentEventRemoveFromRole extends AgentEventRemoveFromGroup {
    String role = "???";

    public AgentEventRemoveFromRole(int event, long date, AgentAddress source,
                                    String comm, String gr, String ro) {
        super(event, date, source, comm, gr);
        role = ro;
        color = Color.PINK;
    }

    public String toString() {
        return "Agent " + source.toString() + " quit role " + role + " in " + group + " [" + community + "]";
    }
}

class AgentEventKillAgent extends AgentEvent {
    public AgentEventKillAgent(int event, long date, AgentAddress source) {
        init(event, date, source, null, null, null, null);
        color = Color.red;
    }

    public String toString() {
        return "Agent " + source.toString() + " was killed";
    }

    public boolean contain(GraphicGroupObserverGUIPane parent, int step, int X, int Y) {
        int x = (int) ((parent.getPos(this.getSource()) + 0.5) * parent.getAgentWidth()) - 6;
        int y = parent.getMargeY() + step * parent.getStaticStep() - 4;
        return (new Rectangle(x, y, 12, 8).contains(X, Y));
    }

    public boolean valid(GraphicGroupObserverGUIPane parent) {
        return (parent.getPos(getSource()) >= 0);
    }

    public void paintMess(Graphics g) {
        g.drawString(toString(), 10, 13);
        g.drawString("Event : " + event + "  Time : " + this.getDate(), 10, 26);
    }

    public void paint(Graphics2D g2D, GraphicGroupObserverGUIPane parent, int step) {
        int x = (int) ((parent.getPos(this.getSource()) + 0.5) * parent.getAgentWidth()) - 6;
        int y = parent.getMargeY() + step * parent.getStaticStep() - 4;

        Stroke stroke = g2D.getStroke();

        g2D.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2D.setColor(color);
        g2D.drawLine(x, y, x + 12, y + 8);
        g2D.drawLine(x + 12, y, x, y + 8);

        g2D.setStroke(stroke);
    }
}

class AgentEventSendMessage extends AgentEvent {
    public AgentEventSendMessage(int event, long date, AgentAddress source, AgentAddress dest, String messType, String mess) {
        init(event, date, source, dest, null, messType, mess);
        color = Color.black;
    }

    public String toString() {
        return "Agent " + source.toString() + " send message to :" + dest.toString();
    }

    public boolean contain(GraphicGroupObserverGUIPane parent, int step, int X, int Y) {
        int x1 = (int) ((parent.getPos(this.getSource()) + 0.5) * parent.getAgentWidth());
        int x2 = (int) ((parent.getPos(this.getDest()) + 0.5) * parent.getAgentWidth());
        int y = parent.getMargeY() + step * parent.getStaticStep();
        if (x1 < x2) {
            return (new Rectangle(x1 + 10, y - 2, (x2 - 20) - x1, 4).contains(X, Y));
        } else if (x1 > x2) {
            return (new Rectangle(x2 + 10, y - 2, (x1 - 20) - x2, 4).contains(X, Y));
        }
        return false;
    }

    public boolean valid(GraphicGroupObserverGUIPane parent) {
        return ((parent.getPos(getSource()) >= 0) &&
                (parent.getPos(getDest()) >= 0));
    }

    public void paintMess(Graphics g) {
        g.drawString(toString(), 10, 13);
        g.drawString("Event : " + event + "  Time : " + this.getDate() + " Message : " + messType + "  " + mess, 10, 26);
    }

    public void paint(Graphics2D g2D, GraphicGroupObserverGUIPane parent, int step) {
        int x1 = (int) ((parent.getPos(this.getSource()) + 0.5) * parent.getAgentWidth());
        int x2 = (int) ((parent.getPos(this.getDest()) + 0.5) * parent.getAgentWidth());
        int y = parent.getMargeY() + step * parent.getStaticStep();

        Stroke stroke = g2D.getStroke();
        g2D.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

        g2D.setColor(color);
        if (x1 < x2) {
            g2D.drawLine(x1 + 10, y, x2 - 10, y);
            g2D.drawLine(x2 - 10, y, x2 - 20, y + 4);
            g2D.drawLine(x2 - 10, y, x2 - 20, y - 4);
        } else if (x1 > x2) {
            g2D.drawLine(x1 - 10, y, x2 + 10, y);
            g2D.drawLine(x2 + 10, y, x2 + 20, y + 4);
            g2D.drawLine(x2 + 10, y, x2 + 20, y - 4);
        }
        g2D.setStroke(stroke);
    }
}

class AgentEventSendBroadcastMessage extends AgentEvent {
    public AgentEventSendBroadcastMessage(int event, long date, AgentAddress source, HashMap map, String messType, String mess) {
        init(event, date, source, null, map, messType, mess);
        color = Color.DARK_GRAY;
    }

    public String toString() {
        return "Agent " + source.toString() + " send message to all group/role";
    }

    public boolean contain(GraphicGroupObserverGUIPane parent, int step, int X, int Y) {
        int x1 = (int) ((parent.getPos(this.getSource()) + 0.5) * parent.getAgentWidth());
        int y = parent.getMargeY() + step * parent.getStaticStep();

        Iterator it2 = parent.getListAgent().iterator();
        while (it2.hasNext()) {
            AgentSpy dest = (AgentSpy) it2.next();
            if (dest != null && dest.getAgentAddress() != getSource()) {
                if (dest.SpiedAt(step)) {
                    int x2 = (int) ((parent.getPos(dest.getAgentAddress()) + 0.5) * parent.getAgentWidth());

                    if (x1 < x2) {
                        if (new Rectangle(x1 + 10, y - 2, (x2 - 20) - x1, 4).contains(X, Y)) return true;
                    } else if (x1 > x2) {
                        if (new Rectangle(x2 + 10, y - 2, (x1 - 20) - x2, 4).contains(X, Y)) return true;
                    }

                }
            }
        }
        return false;
    }

    public boolean valid(GraphicGroupObserverGUIPane parent) {
        if (parent.getPos(getSource()) >= 0) {
            Iterator it2 = parent.getListAgent().iterator();
            boolean bool = false;
            int i = parent.getListEvent().indexOf(this);
            while (it2.hasNext() && !bool) {
                AgentSpy dest = (AgentSpy) it2.next();
                if (dest != null && dest.getAgentAddress() != getSource()) {
                    if (dest.SpiedAt(i)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void paintMess(Graphics g) {
        g.drawString(toString(), 10, 13);
        g.drawString("Event : " + event + "  Time : " + this.getDate() + " Message : " + messType + "  " + mess, 10, 26);
    }

    public void paint(Graphics2D g2D, GraphicGroupObserverGUIPane parent, int step) {
        int x1 = (int) ((parent.getPos(this.getSource()) + 0.5) * parent.getAgentWidth());
        int y = parent.getMargeY() + step * parent.getStaticStep();

        Iterator it2 = parent.getListAgent().iterator();
        while (it2.hasNext()) {
            AgentSpy dest = (AgentSpy) it2.next();
            if (dest != null && dest.getAgentAddress() != getSource()) {
                if (dest.SpiedAt(step)) {
                    int x2 = (int) ((parent.getPos(dest.getAgentAddress()) + 0.5) * parent.getAgentWidth());

                    Stroke stroke = g2D.getStroke();
                    g2D.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));

                    g2D.setColor(color);
                    if (x1 < x2) {
                        g2D.drawLine(x1 + 10, y, x2 - 10, y);
                        g2D.drawLine(x2 - 10, y, x2 - 20, y + 4);
                        g2D.drawLine(x2 - 10, y, x2 - 20, y - 4);
                    } else if (x1 > x2) {
                        g2D.drawLine(x1 - 10, y, x2 + 10, y);
                        g2D.drawLine(x2 + 10, y, x2 + 20, y + 4);
                        g2D.drawLine(x2 + 10, y, x2 + 20, y - 4);
                    }
                    g2D.setStroke(stroke);
                }
            }
        }
    }
}
