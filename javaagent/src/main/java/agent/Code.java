/**
 * Copyright:Copyright (c) 2021
 * Create By Jimmy
 * Create On 2021-21.7.12 at 20:34
 **/
package agent;

public class Code {
    /**
     * 输出 {@link target.HttpExample#query(String)}的url
     * @param url url
     */
    public static void before(String url){
        System.out.println("this is method before, target method url is:" + url);
    }


    public static void after(){
        System.out.println("this is method after");
    }
}
