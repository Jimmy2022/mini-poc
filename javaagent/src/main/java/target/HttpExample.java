/**
 * Copyright:Copyright (c) 2021
 * Create By Jimmy
 * Create On 2021-21.7.12 at 0:27
 **/
package target;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;

public class HttpExample {
    private String name;

    public static void main(String[] args) throws Exception {
        new HttpExample("JavaAgent").query("https://www.baidu.com");
    }

    public HttpExample(String name){
        this.name = name;
    }

    public void query(String url) throws Exception{
        HttpClient client = HttpClients.createDefault();
        HttpResponse rsp = client.execute(new HttpGet(url));
        InputStream in = rsp.getEntity().getContent();
        byte[] content = new byte[4096];
        in.read(content);
        System.out.println(new String(content).replace(">",">\n"));

    }
}
