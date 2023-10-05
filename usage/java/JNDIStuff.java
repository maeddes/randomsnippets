   private void readManifestFile(InputStream inputStream) {

        System.out.println(inputStream);

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
            System.out.println("Props: " + properties.toString());
            System.out.println(properties.getProperty("Implementation-Version"));
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void identifyVersion() {

        displayBinds();

                InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("/META-INF/MANIFEST.MF");
                readManifestFile(inputStream);
        
                inputStream = this.getClass().getClassLoader().getResourceAsStream("/META-INF/MANIFEST.MF");
                readManifestFile(inputStream);
        
                inputStream = this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
                readManifestFile(inputStream);

    }

    public void displayBinds() {

                System.out.println("\n\n global binds");
                listBinds("java:global");
                System.out.println("\n\n app binds");
                listBinds("java:app");
                System.out.println("\n\n module binds");
                listBinds("java:module");
        System.out.println("\n CORBA binds");
        listBinds("corbaname:");
        System.out.println("\n java");
        listBinds("java:");
                System.out.println("\n\n all");
                listBinds("");

                System.out.println("\n\n recursive all");
                try {
                    Map map = toMap(new InitialContext());
                    System.out.println(map);
                } catch (NamingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

        System.out.println("\n\n recursive global");
        try {
            Map map = toMap((Context) new InitialContext().lookup("java:global"));
            System.out.println(map);
            BufferedWriter writer = new BufferedWriter(new FileWriter("java_global.txt"));
            writer.write(map.toString());

            writer.close();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        System.out.println("\n\n recursive app");
        try {
            Map map = toMap((Context) new InitialContext().lookup("java:app"));
            //System.out.println(map);
            BufferedWriter writer = new BufferedWriter(new FileWriter("java_app.txt"));
            writer.write(map.toString());

            writer.close();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

    }

    public void listBinds(String bind) {

        NamingEnumeration<NameClassPair> en = null;

        try {
            Context root = (Context) new InitialContext().lookup(bind);
            en = root.list("");

        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (en.hasMoreElements()) {
            NameClassPair ncp = en.nextElement();
            System.out.println(ncp.toString());
            String nameInNameSpace = "unset";
            try {

                nameInNameSpace = ncp.getNameInNamespace();

            } catch (UnsupportedOperationException e) {
                nameInNameSpace = "unsupported";
            }
            System.out.println("Name: " + ncp.getName() + " ClassName: " + ncp.getClassName() + " NameinNamespace: " + nameInNameSpace);

        }

        NamingEnumeration<Binding> binds = null;

        try {
            binds = new InitialContext().listBindings(bind);

        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (binds.hasMoreElements()) {
            Binding b = binds.nextElement();
            System.out.println(b.toString());
            String nameInNameSpace = "unset";
            //JavaURLContext ctx;
            try {

                nameInNameSpace = b.getNameInNamespace();

            } catch (UnsupportedOperationException e) {
                nameInNameSpace = "unsupported";
            }
            System.out.println("Name: " + b.getName() + " Object: " + b.getObject() + " NameinNamespace: " + nameInNameSpace);

        }

    }

    public void testLookup() {

        System.out.println("### Test Lookup");
        NamingEnumeration<NameClassPair> en = null;

        try {
            Context root = (Context) new InitialContext()
                    .listBindings(name);
            en = root.list("");

        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (en.hasMoreElements()) {
            NameClassPair ncp = en.nextElement();
            System.out.println(ncp.toString());
            String nameInNameSpace = "unset";
            try {

                nameInNameSpace = ncp.getNameInNamespace();

            } catch (UnsupportedOperationException e) {
                nameInNameSpace = "unsupported";
            }
            System.out.println("Name: " + ncp.getName() + " ClassName: " + ncp.getClassName() + " NameinNamespace: " + nameInNameSpace);

        }

    }

    public static Map toMap(Context ctx) throws NamingException {
        String namespace = ctx instanceof InitialContext ? ctx.getNameInNamespace() : "";
        HashMap<String, Object> map = new HashMap<String, Object>();
        //System.out.println("> Listing namespace: " + namespace);
        NamingEnumeration<NameClassPair> list = ctx.list(namespace);
        while (list.hasMoreElements()) {
            NameClassPair next = list.next();
            String name = next.getName();
            String jndiPath = namespace + name;
            Object lookup;
            try {
                //System.out.println("> Looking up name: " + jndiPath);
                Object tmp = ctx.lookup(jndiPath);
                if (tmp instanceof Context) {
                    lookup = toMap((Context) tmp);
                } else {
                    lookup = tmp.toString();
                }
            } catch (Throwable t) {
                lookup = t.getMessage();
                System.out.println("error: " + t);
            }

            if (!name.startsWith("{") && !name.endsWith("}"))
                name = "\"" + name + "\"";
            else if (name.startsWith("{"))
                name = name + "\"";
            else if (name.endsWith("}"))
                name = "\"" + name;
            map.put(name, "\"" + lookup + "\"");

        }
        return map;
    }


