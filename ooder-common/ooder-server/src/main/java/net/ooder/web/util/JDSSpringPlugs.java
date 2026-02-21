package net.ooder.web.util;

import net.ooder.common.JDSException;
import net.ooder.esb.util.EsbFactory;
import net.ooder.server.JDSServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

//@Service
//@Component("JDSSpringPlugs")
public class JDSSpringPlugs implements
        ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent ev) {
        if (ev.getApplicationContext().getParent() == null) {
            System.out.println(">>>>>>>>>>>>>>容器交由Spring 接管启动");
            try {
                JDSServer.getInstance();
                EsbFactory.initBus();
            } catch (final JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }


}
