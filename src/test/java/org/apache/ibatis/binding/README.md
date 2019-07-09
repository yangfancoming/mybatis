#   resultMap是Mybatis最强大的元素，它可以将查询到的复杂数据（比如查询到几个表中数据）映射到一个结果集当中
    <!--column不做限制，可以为任意表的字段，而property须为type 定义的pojo属性-->
    
    <resultMap id="唯一的标识" type="映射的pojo对象">
    
      <id column="表的主键字段，或者可以为查询语句中的别名字段" jdbcType="字段类型" property="映射pojo对象的主键属性" />
      
      <result column="表的一个字段（可以为任意表的一个字段）" jdbcType="字段类型" property="映射到pojo对象的一个属性（须为type定义的pojo对象中的一个属性）"/>
      
      <association property="pojo的一个对象属性" javaType="pojo关联的pojo对象">
        <id column="关联pojo对象对应表的主键字段" jdbcType="字段类型" property="关联pojo对象的主席属性"/>
        <result  column="任意表的字段" jdbcType="字段类型" property="关联pojo对象的属性"/>
      </association>
      
      <!-- 集合中的property须为 oftype 定义的pojo对象的属性-->
      <collection property="pojo的集合属性" ofType="集合中的pojo对象">
        <id column="集合中pojo对象对应的表的主键字段" jdbcType="字段类型" property="集合中pojo对象的主键属性" />
        <result column="可以为任意表的字段" jdbcType="字段类型" property="集合中的pojo对象的属性" />  
      </collection>
      
    </resultMap>