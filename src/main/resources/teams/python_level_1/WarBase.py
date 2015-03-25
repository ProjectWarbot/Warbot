class WarBase(PyWarBase):
	def __init__(self):
		pass

	def action(self):
		global WABASE
		WABASE = self

		__message = self.getMessages()

		for i in range (0, __message.size()):
			if (__message.get(i).getMessage() == "whereAreYou") :
				self.setDebugString("I'm here base PY")
				self.sendMessage(__message.get(i).getSenderID(), "here", "")

		return actionBase();

def actionBase():

	printCodeur();

	for messages in WABASE.getMessagesV2() :
		print(type(messages))

	return idle()


def idle() :
	return WABASE.idle()
