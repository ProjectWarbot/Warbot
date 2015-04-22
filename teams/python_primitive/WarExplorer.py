
def actionExplorer():



    for percept in getPerceptsFood() :
        setDebugString("View Food");
        if(pickableFood(percept) and isNotBagFull()):
            setDebugString("Take food");
            followTarget(percept);
            return take();
        elif (isNotBagFull()):
            followTarget(percept);

    if(isBagFull()):
        setDebugString("Bag full return base");

        __percept = getPerceptsAlliesWarBase();

        if(haveNoTargets(__percept)):
            for message in getMessages():
                if(isMessageOfWarBase(message)):
                    followTarget(message);

            sendMessageToBases("whereAreYou", "");

        else :
            __base = __percept[0];

            if(isPossibleToGiveFood(__base)) :
                giveToTarget(__base);
                return give();
            else:
                followTarget(__base);
                return move();
    else :
        setDebugString("Chercher food");

    if (isBlocked()) :
        RandomHeading()

    return move();
