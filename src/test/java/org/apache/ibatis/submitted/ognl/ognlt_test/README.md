#  MyBatis常用OGNL表达式
    e1 or e2
    e1 and e2
    e1 == e2,e1 eq e2
    e1 != e2,e1 neq e2
    e1 lt e2：小于
    e1 lte e2：小于等于，其他gt（大于）,gte（大于等于）
    e1 in e2
    e1 not in e2
    e1 + e2,e1 * e2,e1/e2,e1 - e2,e1%e2
    !e,not e：非，求反
    e.method(args)调用对象方法
    e.property对象属性值
    e1[ e2 ]按索引取值，List,数组和Map
    @class @method(args)调用类的静态方法
    @class @field调用类的静态字段值
    
    上述内容只是合适在MyBatis中使用的OGNL表达式，完整的表达式点击这里。
    
# MyBatis中可以使用OGNL的地方有两处：

    动态SQL表达式中
    ${param}参数中