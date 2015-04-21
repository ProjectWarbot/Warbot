package edu.warbot.scriptcore;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.WarAgent;
import edu.warbot.agents.WarBuilding;
import edu.warbot.agents.WarProjectile;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Agressive;
import edu.warbot.brains.implementations.WarBrainImplementation;
import edu.warbot.game.Team;
import edu.warbot.scriptcore.exceptions.NotFoundConfigurationException;
import edu.warbot.scriptcore.interpreter.ScriptInterpreter;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;
import edu.warbot.scriptcore.team.Scriptable;
import javassist.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ScriptedTeam extends Team {

    private static boolean initFunction = false;
    private static List<String> functions = new ArrayList<String>();
    protected ScriptInterpreter interpreter;

    public ScriptedTeam(String name, HashMap<String, Class<? extends WarBrain>> brainControllers) {

        super(name);
        for (String agentName : brainControllers.keySet())
            addBrainControllerClassForAgent(agentName, brainControllers.get(agentName));
    }

    public ScriptedTeam(String nom, Color color, ImageIcon logo,
                        String description,
                        ArrayList<ControllableWarAgent> controllableAgents,
                        ArrayList<WarProjectile> projectiles,
                        ArrayList<WarBuilding> buildings,
                        HashMap<WarAgentType, Integer> nbUnitsLeft,
                        HashMap<String, Class<? extends WarBrain>> brainControllers,
                        ArrayList<WarAgent> dyingAgents) {
        super(nom, color, logo, description, controllableAgents, projectiles,
                buildings, brainControllers, nbUnitsLeft, dyingAgents);
    }

    public static List<String> getFunctions() {
        return functions;
    }

    public void initFunctionList() {
        if (!initFunction) {

            String defaultSourceFile = "scripts/function/";
            String defaultNameFile = "function.txt";

            File file = null;

            try {
                URL path = getClass().getClassLoader().getResource(defaultSourceFile + defaultNameFile);
                if (path == null)
                    throw new NotFoundConfigurationException(defaultNameFile);
                file = new File(path.getFile());

                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    functions.add(scanner.nextLine());
                }
                scanner.close();
            } catch (NotFoundConfigurationException e) {
                System.err.println(e.getMessage());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            initFunction = true;
        }
    }

    private Class<? extends WarBrain> createNewWarBrainImplementationClass(String brainClassName) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass brainImplementationClass = classPool.get(WarBrainImplementation.class.getName());
        if (!brainImplementationClass.isFrozen()) {
            brainImplementationClass.setName(brainClassName + "BrainImplementation");
            brainImplementationClass.setModifiers(Modifier.PUBLIC);

            CtClass brainClass = classPool.get(brainClassName);
            String capacitiesPackageName = Agressive.class.getPackage().getName();
            for (CtClass brainInterface : brainClass.getSuperclass().getInterfaces()) {
                if (brainInterface.getPackageName().equals(capacitiesPackageName)) {
                    CtClass brainInterfaceImplementation = classPool.get(WarBrainImplementation.class.getPackage().getName() + ".War" + brainInterface.getSimpleName() + "BrainImplementation");
                    for (CtMethod interfaceImplementationMethod : brainInterface.getDeclaredMethods()) {
                        brainImplementationClass.addMethod(new CtMethod(
                                brainInterfaceImplementation.getDeclaredMethod(interfaceImplementationMethod.getName(), interfaceImplementationMethod.getParameterTypes()),
                                brainImplementationClass, null));
                    }
                }
            }

            brainImplementationClass.setSuperclass(brainClass);

            return (Class<? extends WarBrain>) brainImplementationClass.toClass().asSubclass(WarBrain.class);
        } else {
            return null;
        }
    }

    public ScriptInterpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(ScriptInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public ControllableWarAgent instantiateNewControllableWarAgent(String agentName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {

        ControllableWarAgent a = super.instantiateNewControllableWarAgent(agentName);
        ScriptAgent sa = getInterpreter().giveScriptAgent(WarAgentType.valueOf(agentName));
        ((Scriptable) a.getBrain()).setScriptAgent(sa);
        sa.link(a.getBrain());
        return a;
    }

    @Override
    public Team duplicate(String name) {
        ScriptedTeam scteam = new ScriptedTeam(name,
                ((this.getColor() == null) ? null : (new Color(this.getColor().getRGB()))),
                this.getImage(),
                this.getDescription(),
                new ArrayList<>(this.getControllableAgents()),
                new ArrayList<>(this.getProjectiles()),
                new ArrayList<>(this.getBuildings()),
                new HashMap<>(this.getAllNbUnitsLeft()),
                new HashMap<>(this.getAllBrainControllers()),
                new ArrayList<>(this.getDyingAgents())
        );

        scteam.setInterpreter(this.getInterpreter());

        return scteam;

    }


}
