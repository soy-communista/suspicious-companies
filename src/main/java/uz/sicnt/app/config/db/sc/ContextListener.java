package uz.sicnt.app.config.db.sc;

import org.springframework.context.annotation.Profile;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

//@Profile("Staging")
@WebListener
public class ContextListener implements ServletContextListener {

    private SShConnection conexionssh;

    public ContextListener() {
        super();
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("Context initialized ... !");
//        connect();
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("Context destroyed ... !");
//        conexionssh.closeSSh();
    }

    void connect(){
        try{
            conexionssh = new SShConnection();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}