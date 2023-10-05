print "Hello"
debug = 0;

file = open("messages.log", "r")
for line in file: 
	if line.find("server is binding") != -1:
		
		if debug == 1: print line
		
		interface = line.split("the")[1].split("interface")[0].strip()
		module = line.split("the")[3].split("module")[0].strip()
		impl = line.split("is:")[1].strip()
		
		if debug == 1: print "Interface: "+interface+" Module: "+module+" Class: "+impl
		
		print "if(serviceName.equals(\""+interface+"\"))\n returnString = \""+impl+"\";"
		
		
