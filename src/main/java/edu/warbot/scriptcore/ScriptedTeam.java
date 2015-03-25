package edu.warbot.scriptcore;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import edu.warbot.agents.ControllableWarAgent;
import edu.warbot.agents.WarAgent;
import edu.warbot.agents.WarBuilding;
import edu.warbot.agents.WarProjectile;
import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarBaseBrain;
import edu.warbot.brains.capacities.Agressive;
import edu.warbot.brains.implementations.WarBrainImplementation;
import edu.warbot.game.Team;
import edu.warbot.scriptcore.exceptions.DangerousFunctionPythonException;
import edu.warbot.scriptcore.exceptions.NotFoundConfigurationFilePythonException;
import edu.warbot.scriptcore.interpreter.ScriptInterpreter;
import edu.warbot.scriptcore.interpreter.ScriptInterpreterLangage;
import edu.warbot.scriptcore.script.Script;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;
import edu.warbot.scriptcore.team.Scriptable;
import javassist.*;

public class ScriptedTeam extends Team {

    protected static HashMap<String, Class<? extends WarBrain>> brainControllers;
    protected ScriptInterpreter interpreter;

    private static boolean initFunction = false;
    private static List<String> functions = new ArrayList<String>();

    public ScriptedTeam(String name) {

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
                        ArrayList<WarAgent> dyingAgents) {
        super(nom, color, logo, description, controllableAgents, projectiles,
                buildings, ScriptedTeam.brainControllers, nbUnitsLeft, dyingAgents);
    }

    public void initListeFunction() {
        if(!initFunction) {

            String defaultSourceFile = "scripts/function/";
            String defaultNameFile = "function.txt";

            File file = null;

            try {
                URL path = getClass().getClassLoader().getResource(defaultSourceFile+defaultNameFile);
                if(path == null)
                    throw new NotFoundConfigurationFilePythonException(defaultNameFile);
                file =  new File(path.getFile());

                Scanner scanner = new Scanner(file);
                while(scanner.hasNext())
                {
                    functions.add(scanner.nextLine());
                }
                scanner.close();
            }catch (NotFoundConfigurationFilePythonException e) {
                System.out.println(e.getMessage());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            initFunction = true;
        }
    }

    public Script checkDangerousFunction(InputStream input, WarAgentType agent) throws IOException, DangerousFunctionPythonException{

        Script sc = getInterpreter().createScript(input);
        String lg = "";
        ScriptInterpreterLangage lang = sc.getLangage();

        if(lang.equals(ScriptInterpreterLangage.PYTHON))
            lg = "def";
        else if (lang.equals(ScriptInterpreterLangage.JAVASCRIPT))
            lg = "var";
        else if (lang.equals(ScriptInterpreterLangage.RUBY))
            lg = "def";

        for (String s : functions) {
            Pattern p = Pattern.compile(".*(" + lg + ")\\s+(" + s + ")");
            Matcher m = p.matcher(sc.getCodeAgent().toString());
            if(m.find())
                throw new DangerousFunctionPythonException(s, agent.toString());
        }
        return sc;
    }

    private static Class<? extends WarBrain> createNewWarBrainImplementationClass(String brainClassName) throws NotFoundException, CannotCompileException, IOException {
       ClassPool classPool = ClassPool.getDefault();
        CtClass brainImplementationClass = classPool.get(WarBrainImplementation.class.getName());
        if(! brainImplementationClass.isFrozen()) {
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
    static
    {
        brainControllers = new HashMap<String,Class<? extends WarBrain>>();
        try {
            brainControllers.put("WarBase",createNewWarBrainImplementationClass("edu.warbot.scriptcore.team.ScriptableWarBase"));
            brainControllers.put("WarExplorer",createNewWarBrainImplementationClass("edu.warbot.scriptcore.team.ScriptableWarExplorer"));
            brainControllers.put("WarEngineer",createNewWarBrainImplementationClass("edu.warbot.scriptcore.team.ScriptableWarEngineer"));
            brainControllers.put("WarRocketLauncher",createNewWarBrainImplementationClass("edu.warbot.scriptcore.team.ScriptableWarRocketLauncher"));
            brainControllers.put("WarKamikaze",createNewWarBrainImplementationClass("edu.warbot.scriptcore.team.ScriptableWarKamikaze"));
            brainControllers.put("WarTurret",createNewWarBrainImplementationClass("edu.warbot.scriptcore.team.ScriptableWarTurret"));
        }catch(NotFoundException| CannotCompileException| IOException e)
        {
            e.printStackTrace();
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
		( (Scriptable) a.getBrain()).setScriptAgent(sa);
		sa.link(a.getBrain());
		return a;
	}

    @Override
    public Team duplicate (String name) {
        ScriptedTeam scteam = new ScriptedTeam(name,
                ((this.getColor()==null)?null:(new Color(this.getColor().getRGB()))),
                this.getImage(),
                this.getDescription(),
                new ArrayList<>(this.getControllableAgents()),
                new ArrayList<>(this.getProjectiles()),
                new ArrayList<>(this.getBuildings()),
                new HashMap<>(this.getAllNbUnitsLeft()),
                new ArrayList<>(this.getDyingAgents())
        );

        scteam.setInterpreter(this.getInterpreter());

        return scteam;

    }





	
	
	

}
