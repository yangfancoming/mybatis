package org.apache.goat.chapter100.temp;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019/12/5.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/12/5---11:46
 */
public class Student {


    /* 有参 有返回*/
    public String study(String course,Integer hour){
        return " Student  study......" +  course + hour;
    }

    public boolean getTitle(){
        System.out.println("Student getTitle......");
        return false;
    }

    public boolean isTitle(){
        System.out.println("Student isTitle......");
      return true;
    }

}
