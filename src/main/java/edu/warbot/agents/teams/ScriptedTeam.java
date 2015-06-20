package edu.warbot.agents.teams;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.WarBrain;
import edu.warbot.game.InGameTeam;
import edu.warbot.loader.ImplementationProducer;
import edu.warbot.scriptcore.exceptions.NotFoundConfigurationException;
import edu.warbot.scriptcore.interpreter.ScriptInterpreter;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;
import edu.warbot.scriptcore.team.*;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

/**
 * @author BEUGNON Sébastien, LOPEZ Jimmy
 */
public class ScriptedTeam extends JavaTeam {

    private static Map<WarAgentType, Class<? extends WarBrain>> brains;

    private static List<String> functions;

    static {

        //Prépare l'implémentation standard pour une équipe d'agent scriptée
        ImplementationProducer implementationProducer = new ImplementationProducer();
        ClassPool cp = ClassPool.getDefault();
        brains = new HashMap<>();
        try {
            //Implémentation standard
            brains.put(WarAgentType.WarExplorer,
                    implementationProducer.createWarBrainImplementationClass(cp, ScriptableWarExplorer.class));
            brains.put(WarAgentType.WarRocketLauncher,
                    implementationProducer.createWarBrainImplementationClass(cp, ScriptableWarRocketLauncher.class));
            brains.put(WarAgentType.WarEngineer,
                    implementationProducer.createWarBrainImplementationClass(cp, ScriptableWarEngineer.class));
            brains.put(WarAgentType.WarBase,
                    implementationProducer.createWarBrainImplementationClass(cp, ScriptableWarBase.class));
            brains.put(WarAgentType.WarKamikaze,
                    implementationProducer.createWarBrainImplementationClass(cp, ScriptableWarKamikaze.class));
            brains.put(WarAgentType.WarTurret,
                    implementationProducer.createWarBrainImplementationClass(cp, ScriptableWarTurret.class));

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected ScriptInterpreter interpreter;

    private boolean initFunction = false;


    /**
     * @param teamName    Le nom d'une équipe
     * @param description La description d'une équipe
     * @param imageIcon   Le logo d'une équipe
     */
    public ScriptedTeam(String teamName, String description, ImageIcon imageIcon) {
        super(teamName, description, imageIcon, brains);
    }

    public static List<String> getFunctions() {
        return functions;
    }

    public ScriptInterpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(ScriptInterpreter scriptInterpreter) {
        interpreter = scriptInterpreter;
    }

    @Override
    public ControllableWarAgent instantiateControllableWarAgent(InGameTeam inGameTeam, WarAgentType warAgentType)
            throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        ControllableWarAgent a = super.instantiateControllableWarAgent(inGameTeam, warAgentType);
        ScriptAgent sa = getInterpreter().giveScriptAgent(warAgentType);
        ((Scriptable) a.getBrain()).setScriptAgent(sa);
        sa.link(a.getBrain());
        return a;
    }

    public void initFunctionList() {

        if (!initFunction) {

            String defaultSourceFile = "scripts/function/";
            String defaultNameFile = "function.txt";

            InputStream file = null;

            functions = new ArrayList<>();
            try {
                URL path = getClass().getClassLoader().getResource(defaultSourceFile + defaultNameFile);
                if (path == null)
                    throw new NotFoundConfigurationException(defaultNameFile);

                file = getClass().getClassLoader().getResourceAsStream(defaultSourceFile + defaultNameFile);

                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    functions.add(scanner.nextLine());
                }
                scanner.close();
            } catch (NotFoundConfigurationException e) {
                System.err.println(e.getMessage());
            }

            initFunction = true;
        }
    }


}
