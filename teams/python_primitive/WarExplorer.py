
def actionExplorer():

    for percept in getPerceptsResources() :
        if(pickableFood(percept) and isNotBagFull()):
            setHeading(percept.getAngle());
            return take();
        elif (isNotBagFull()):
            setHeading(percept.getAngle());

    if(isBagFull()):
        setDebugString("Bag full return base");


    return move()
