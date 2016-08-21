package main;

import mainint.MainInterface;
import nc.bs.framework.server.BusinessAppServer;
import nc.bs.framework.server.ComponentMetaImpl;
import org.granite.xv.Xvs;
import xmlParse.ParseTest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by wangqchf on 2016/8/11.
 */
public class Main {
    public static void main(String[] args)
    {
        /*MainInterface main;
        main = new MainInterface() {
            @Override
            public void what() {
                System.out.println("what?");
            }
        };
        main.what();*/
        xmlParse.Prepare prepare = new xmlParse.Prepare();
        prepare.prepare();
        prepare.initialize();
        File file = new File("D:\\working_software\\nchome65\\modules\\baseapp");
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        prepare.newContainer(url,file);
        ParseTest test = new ParseTest();
        test.parse(test.getReader("D:\\working_software\\nchome65\\modules\\baseapp\\META-INF\\appbcmanage.upm"));
    }
}
