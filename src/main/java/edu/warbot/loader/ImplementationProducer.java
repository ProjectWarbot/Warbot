package edu.warbot.loader;

import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Agressive;
import edu.warbot.brains.implementations.WarBrainImplementation;
import javassist.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by beugnon on 18/06/15.
 */
public class ImplementationProducer {

    private Logger logger = Logger.getLogger(ImplementationProducer.class.getCanonicalName());

    public Class<? extends WarBrain> createWarBrainImplementationClass(String brainClassName)
            throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass brainImplementationClass = classPool.get(WarBrainImplementation.class.getName());
        if (!brainImplementationClass.isFrozen()) {
            brainImplementationClass.setModifiers(java.lang.reflect.Modifier.PUBLIC);
            brainImplementationClass.setName(brainClassName + "BrainImplementation");

            CtClass brainClass = classPool.get(brainClassName);
            brainImplementationClass = produceImplementedBrain(classPool, brainImplementationClass, brainClass);
            brainImplementationClass.setSuperclass(brainClass);

            return (Class<? extends WarBrain>) brainImplementationClass.toClass().asSubclass(WarBrain.class);
        } else {
            return null;
        }
    }


    protected CtClass produceImplementedBrain(ClassPool classPool, CtClass brainImplementationClass, CtClass brainClass)
            throws NotFoundException, CannotCompileException {
        //Récupération du chemin du package des capacités
        String capacitiesPackageName = Agressive.class.getPackage().getName();

        //Pour toutes les interfaces de la classe War<Agent>Brain
        for (CtClass brainInterface : brainClass.getSuperclass().getInterfaces()) {

            //Si l'interface appartient au package capacities
            if (brainInterface.getPackageName().equals(capacitiesPackageName)) {

                //Chargement des implémentation de capacités
                CtClass brainInterfaceImplementation = classPool.get(WarBrainImplementation.class.getPackage().getName()
                        + ".War" + brainInterface.getSimpleName() + "BrainImplementation");

                //Copie des méthodes implémentées vers l'implémentation finale
                for (CtMethod interfaceImplementationMethod : brainInterface.getDeclaredMethods()) {
                    brainImplementationClass.addMethod(new CtMethod(
                            brainInterfaceImplementation.getDeclaredMethod(interfaceImplementationMethod.getName(),
                                    interfaceImplementationMethod.getParameterTypes()),
                            brainImplementationClass, null));
                }
            }
        }

        return brainImplementationClass;
    }

    public Class<? extends WarBrain> createWarBrainImplementationClass(ClassPool classPool, String brainClassName)
            throws NotFoundException, CannotCompileException, IOException {
        //
        CtClass brainImplementationClass = classPool.get(WarBrainImplementation.class.getName());

        if (!brainImplementationClass.isFrozen()) {

            brainImplementationClass.setName(brainClassName + "BrainImplementation");
            brainImplementationClass.setModifiers(java.lang.reflect.Modifier.PUBLIC);

            CtClass brainClass = classPool.get(brainClassName);
            classPool.find(brainClassName);

            brainImplementationClass = produceImplementedBrain(classPool, brainImplementationClass, brainClass);
            brainImplementationClass.setSuperclass(brainClass);

            return brainImplementationClass.toClass().asSubclass(WarBrain.class);
        } else {
            return null;
        }
    }

    public Class<? extends WarBrain> createWarBrainImplementationClass(ClassPool classPool, Class<?> brainClassN)
            throws NotFoundException, CannotCompileException, IOException {
        return createWarBrainImplementationClass(classPool, brainClassN.getCanonicalName());
    }
}
