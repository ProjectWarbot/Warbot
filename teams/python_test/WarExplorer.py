
def actionWarExplorer():
    if getPerceptsEnemiesWarBase():
        broadcastMessageToAll("EnemyBase","")


    for percept in getPerceptsFood():
        sendMessageToExplorers("FoodFound","")
        setDebugString("View Food");
        if(pickableFood(percept) and isNotBagFull()):
            setDebugString("Take food")
            followTarget(percept)
            return take()
        elif (isNotBagFull()):
            followTarget(percept)

    if(isBagFull()):
        setDebugString("Bag full return base");

        __percept = getPerceptsAlliesWarBase();

        if(haveNoTargets(__percept)):
            for message in getMessages():
                if(isMessageOfWarBase(message)):
                    followTarget(message)

            sendMessageToBases("whereAreYouBase", "");
            messages = getMessages()
            for m in messages:
                if m.getMessage() == "Here":
                    setHeading(m.getAngle())
                    return move()
        else :
            __base = __percept[0];

            if(isPossibleToGiveFood(__base)) :
                giveToTarget(__base);
                return give();
            else:
                followTarget(__base);
                return move();
    else:
        setDebugString("Look for food");

    if (isBlocked()) :
        RandomHeading()
    #setDebugString(`n`)
    setDebugStringColor(Color.BLUE)
    return move();
