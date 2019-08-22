# 
    ========================思考================================	
    public Employee getEmp(@Param("id")Integer id,String lastName);
    	取值：id==>#{id/param1}   lastName==>#{param2}
    
    public Employee getEmp(Integer id,@Param("e")Employee emp);
    	取值：id==>#{param1}    lastName===>#{param2.lastName/e.lastName}
    
    ##特别注意：如果是Collection（List、Set）类型或者是数组，
    		 也会特殊处理。也是把传入的list或者数组封装在map中。
    			key：Collection（collection）,如果是List还可以使用这个key(list)
    				数组(array)
    public Employee getEmpById(List<Integer> ids);
    	取值：取出第一个id的值：   #{list[0]}