package edu.warbot.loader;

import edu.warbot.brains.WarBrain;
import edu.warbot.brains.capacities.Agressive;
import edu.warbot.brains.implementations.WarBrainImplementation;
import javassist.*;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by beugnon on 18/06/15.
 *
 * ImplementationProducer, permet l'implémentation de classes WarBrain avec leurs capacités
 * afin d'être utilisé pour générer le cerveau des agents
 *
 * @author BEUGNON Sébastien
 *
 * @since 3.2.3
 */
public class ImplementationProducer {

    /**
     * logger
     */
    private Logger logger = Logger.getLogger(ImplementationProducer.class.getCanonicalName());

    /**
     * Méthode produisant une implémentation d'un cerveau d'agent avec ses capacités à partir d'une autre classe
     *
     * @param brainClassName le nom d'une classe
     * @return une classe héritant de WarBrain
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    public Class<? extends WarBrain> createWarBrainImplementationClass(String brainClassName)
            throws NotFoundException, CannotCompileException, IOException {
        //Chargement d'une pool par défaut
        ClassPool classPool = ClassPool.getDefault();

        //Récupération de l'implémentation de WarBrain commune
        CtClass brainImplementationClass = classPool.get(WarBrainImplementation.class.getName());
        if (!brainImplementationClass.isFrozen()) {

            //Modification totale
            brainImplementationClass.setModifiers(java.lang.reflect.Modifier.PUBLIC);
            //Modification du nom de la classe
            brainImplementationClass.setName(brainClassName + "BrainImplementation");

            //Récupération de la classe demandée (WarExplorer, WarBase avec une méthode action modifiée)
            CtClass brainClass = classPool.get(brainClassName);


            //Rajoute l'implémentation des capacités de la classe
            brainImplementationClass = produceImplementedBrain(classPool, brainImplementationClass, brainClass);

            brainImplementationClass.setSuperclass(brainClass);

            return (Class<? extends WarBrain>) brainImplementationClass.toClass().asSubclass(WarBrain.class);
        } else {
            return null;
        }
    }


    /**
     * Rajoute les implémentations des capacités à la classe du cerveau en cours
     *
     * @param classPool                une JavassistPool avec des classes préchargées
     * @param brainImplementationClass la classe à laquelle on rajoute les implémentations de capacités
     * @param brainClass               la classe contenant les catacités à récupérer
     * @return la classe avec les ajouts d'implémentations
     * @throws NotFoundException
     * @throws CannotCompileException
     */
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

    /**
     *
     * @param classPool
     * @param brainClassName
     * @return
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
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
