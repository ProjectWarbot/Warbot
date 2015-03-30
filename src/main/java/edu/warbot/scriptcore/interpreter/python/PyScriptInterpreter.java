package edu.warbot.scriptcore.interpreter.python;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import edu.warbot.scriptcore.exceptions.NotFoundConfigurationFilePythonException;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.scriptcore.interpreter.ScriptInterpreter;
import edu.warbot.scriptcore.script.PyScript;
import edu.warbot.scriptcore.script.Script;
import edu.warbot.scriptcore.scriptagent.ScriptAgent;
import teams.engineer.WarExplorerBrainController;


public class PyScriptInterpreter extends PythonInterpreter implements ScriptInterpreter{

	protected Map<WarAgentType, Script> scripts = new HashMap<WarAgentType, Script>();

	private static boolean initConfigurationFile = false;
	private static Map<String, File>  pyScriptsFirst = new HashMap<String, File>();
	private static Map<String, File> pyScriptsSecond = new HashMap<String, File>();

	private static boolean initTemplateTeam = false;
	private static Map<String, File> team = new HashMap<String, File>();

	public PyScriptInterpreter() {
	}

	/**
	 * Cette méthode ne peut pas être en static car on a besoin de chemin dynamic de la class
	 */
	public void initConfigurationFilePython() {

		if(!initConfigurationFile) {
			String defaultSourceFile = "scripts/python/";

			File pyWarTools = getFileConfigPython(defaultSourceFile, FileConfigPython.PyWarTools);
			File pyWarAgent = getFileConfigPython(defaultSourceFile, FileConfigPython.PyWarAgent);

			File pyWarBase = getFileConfigPython(defaultSourceFile, FileConfigPython.PyWarBase);
			File pyWarEngineer = getFileConfigPython(defaultSourceFile, FileConfigPython.PyWarEngineer);
			File pyWarExplorer = getFileConfigPython(defaultSourceFile, FileConfigPython.PyWarExplorer);
			File pyWarKamikaze = getFileConfigPython(defaultSourceFile, FileConfigPython.PyWarKamikaze);
			File pyWarRocketLauncher = getFileConfigPython(defaultSourceFile, FileConfigPython.PyWarRocketLauncher);
			File pyWarTurret = getFileConfigPython(defaultSourceFile, FileConfigPython.PyWarTurret);

			pyScriptsFirst.put(FileConfigPython.PyWarAgent.getNameFile(), pyWarAgent);
			pyScriptsFirst.put(FileConfigPython.PyWarTools.getNameFile(), pyWarTools);

			pyScriptsSecond.put(FileConfigPython.PyWarBase.getNameFile(), pyWarBase);
			pyScriptsSecond.put(FileConfigPython.PyWarEngineer.getNameFile(), pyWarEngineer);
			pyScriptsSecond.put(FileConfigPython.PyWarExplorer.getNameFile(), pyWarExplorer);
			pyScriptsSecond.put(FileConfigPython.PyWarKamikaze.getNameFile(), pyWarKamikaze);
			pyScriptsSecond.put(FileConfigPython.PyWarRocketLauncher.getNameFile(), pyWarRocketLauncher);
			pyScriptsSecond.put(FileConfigPython.PyWarTurret.getNameFile(), pyWarTurret);

			initConfigurationFile = true;
		}
		instanciedPyScript(pyScriptsFirst);
		instanciedPyScript(pyScriptsSecond);
	}


	public void initConfigurationTeamPython(){
		if(!initTemplateTeam) {
			String defaultSourceFile = "team/python/";


			File WarBase = getFileTeamPython(defaultSourceFile, FileTeamPython.WarBase);
			File WarEngineer = getFileTeamPython(defaultSourceFile, FileTeamPython.WarEngineer);
			File WarExplorer = getFileTeamPython(defaultSourceFile, FileTeamPython.WarExplorer);
			File WarKamikaze = getFileTeamPython(defaultSourceFile, FileTeamPython.WarKamikaze);
			File WarRocketLauncher = getFileTeamPython(defaultSourceFile, FileTeamPython.WarRocketLauncher);
			File WarTurret = getFileTeamPython(defaultSourceFile, FileTeamPython.WarTurret);


			team.put(FileTeamPython.WarBase.getNameFile(),WarBase);
			team.put(FileTeamPython.WarEngineer.getNameFile(),WarEngineer);
			team.put(FileTeamPython.WarExplorer.getNameFile(),WarExplorer);
			team.put(FileTeamPython.WarKamikaze.getNameFile(),WarKamikaze);
			team.put(FileTeamPython.WarRocketLauncher.getNameFile(),WarRocketLauncher);
			team.put(FileTeamPython.WarTurret.getNameFile(),WarTurret);

			initTemplateTeam = true;
		}
	}

	private File getFileConfigPython(String pathPython, FileConfigPython nameFilePython) {
		File file = null;
		try {
			URL path = getClass().getClassLoader().getResource(pathPython+nameFilePython);
			//System.out.println(path);
			if(path == null)
				throw new NotFoundConfigurationFilePythonException(nameFilePython.getNameFile());
			file =  new File(path.getFile());
		}catch (NotFoundConfigurationFilePythonException e) {
			System.out.println("Config file " + e.getMessage());
			e.printStackTrace();
		}

		return file;
	}

	private File getFileTeamPython(String pathPython, FileTeamPython nameFilePython) {
		File file = null;
		try {
			URL path = getClass().getClassLoader().getResource(pathPython+nameFilePython);
			System.out.println(pathPython+nameFilePython);
			if(path == null)
				throw new NotFoundConfigurationFilePythonException(nameFilePython.getNameFile());
			file =  new File(path.getFile());
		}catch (NotFoundConfigurationFilePythonException e) {
			System.out.println("Team file " + e.getMessage());
			e.printStackTrace();
		}

		return file;
	}


	private void instanciedPyScript(Map<String, File> map){
		try {
			Iterator<Entry<String, File>> entries = map.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, File> thisEntry = entries.next();
				Script s = createScript(thisEntry.getValue());
				if(thisEntry.getKey().equals(FileConfigPython.PyWarTools.getNameFile()))
					giveScriptToolsAgent(s, thisEntry.getKey());
				else
					giveScriptAgent(s, thisEntry.getKey());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}


	}

	private PyInstance createClass(final String className) {
		return (PyInstance) eval(className + "()");
	}

	private PyObjectDerived createDerivedClass(final String className) {
		return (PyObjectDerived) eval(className + "()");
	}


	/**
	 * Permet de créer un script agent avec le bon code
	 * @param agent : le type d'agent qu'on veut créer
	 * @return un ScriptAgent
	 */

	@Override
	public ScriptAgent giveScriptAgent(WarAgentType agent) {
		Script s = scripts.get(agent);
		Script template = getTemplateAgent(agent);
		template.addCodeToAgent(s);
		exec(template.getCodeAgent().toString());
		PyObjectDerived a = createDerivedClass(agent.toString());
		return (ScriptAgent) a.__tojava__(ScriptAgent.class);
	}

	public Script getTemplateAgent(WarAgentType agent) {
		//System.out.println(team.size());

		Iterator<Entry<String, File>> entries = team.entrySet().iterator();

		try {
			while (entries.hasNext()) {
				Entry<String, File> thisEntry = entries.next();
				//System.out.println(thisEntry.getKey());
				if(thisEntry.getKey().equals(agent.toString()))
					return createScript(thisEntry.getValue());

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Pour gérer les classes que vont hériter les class coder pas user
	 * @param script
	 * @param agentName
	 * @return
	 */

	@Override
	public ScriptAgent giveScriptAgent(Script script, String agentName) {
		exec(script.getCodeAgent().toString());
		PyObjectDerived a = createDerivedClass(agentName);
		return (ScriptAgent) a.__tojava__(ScriptAgent.class);
	}

	@Override
	public void addScript(InputStream file,WarAgentType agent) throws IOException
	{
		Scanner scanner = new Scanner(file);
		StringBuilder sb = new StringBuilder();
		while(scanner.hasNext())
		{
			sb.append(scanner.nextLine());
			sb.append("\n");
		}
		addScript(sb, agent);
		scanner.close();
	}

	@Override
	public void addScript(StringBuilder file, WarAgentType agent) {
		PyScript pScript = new PyScript(file);
		scripts.put(agent, pScript);
	}

	@Override
	public void addSCript(Script script, WarAgentType agent) {
		PyScript pyScript = new PyScript(new StringBuilder());
		pyScript.addCodeToAgent(script);
		scripts.put(agent, pyScript);
	}

	@Override
	public Map<WarAgentType, Script> getScripts() {

		return scripts;
	}

	@Override
	public Script getScript(WarAgentType agent) {
		return scripts.get(agent);
	}

	@Override
	public Script createScript(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		StringBuilder sb = new StringBuilder();

		while(scanner.hasNext())
		{
			sb.append(scanner.nextLine());
			sb.append("\n");
		}
		scanner.close();

		return new PyScript(sb);
	}


	@Override
	public Object giveScriptToolsAgent(Script script, String toolName) {
		exec(script.getCodeAgent().toString());
		PyObjectDerived a = createDerivedClass(toolName);
		return a.__tojava__(Object.class);
	}

	@Override
	public Script createScript(InputStream file) throws IOException {

		Scanner scanner = new Scanner(file);
		StringBuilder sb = new StringBuilder();
		while(scanner.hasNext())
		{
			sb.append(scanner.nextLine());
			sb.append("\n");
		}
		Script script = new PyScript(sb);
		scanner.close();
		return script;
	}


}
