# 涉及到的 设计模式 
    适配器模式
    代理模式

#  logging 配置加载
    我们先从日志的配置加载开始阅读， MyBatis 的各项配置的加载过程都可以从 XMLConfigBuilder 类中找到，我们定位到该类下的日志加载方法 loadCustomLogImpl：
    
    private void loadCustomLogImpl(Properties props) {
        // 从 MyBatis 的 TypeAliasRegistry 中查找 logImpl 键所对应值的类对象
        // 这里 logImpl 对应的 value 值可以从 org.apache.ibatis.session.Configuration 的构造方法中找到
        // 注意 Log 类，这是 MyBatis 内部对日志对象的抽象
        Class<? extends Log> logImpl = resolveClass(props.getProperty("logImpl"));
        // 将查找到的 Class 对象设置到 Configuration 对象中
        configuration.setLogImpl(logImpl);
    }
    很简单的一个方法，每行都有注释，其中 configuration.setLogImpl() 里面调用了 LogFactory.useCustomLogging()，这出现了新类 LogFactory 类，接下来我们就来聊聊这个类。


