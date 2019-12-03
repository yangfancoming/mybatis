#
    SqlSessionFactoryBuilder#build
    XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);// reader , null , null
        public XMLConfigBuilder(Reader reader, String environment, Properties props)
              this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), null, null);
                new XPathParser(reader, true, props, new XMLMapperEntityResolver())
                    XPathParser this.xpath = XPathFactory.newInstance().newXPath()
                    XPathParser this.document = createDocument(new InputSource(reader));
    Configuration configuration = parser.parse();   
        XMLConfigBuilder#parse    
        XNode xNode = parser.evalNode("/configuration");
            XPathParser#evalNode
            public XNode evalNode(String expression) 
            evalNode(document, expression)
            public XNode evalNode(Object root, String expression) 
            private Object evaluate(String expression, Object root, QName returnType)