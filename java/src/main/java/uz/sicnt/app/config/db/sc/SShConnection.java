package uz.sicnt.app.config.db.sc;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class SShConnection {

    private final static int POSTGRESQL_LOCAL_PORT = 5436;
    private final static int POSTGRESQL_REMOTE_PORT = 5432;
    private final static String POSTGRESQL_REMOTE_SERVER = "";
    private final static String SSH_USER = "";
    private final static String SSH_PASSWORD = "";
    private final static String SSH_REMOTE_SERVER = "";
    private final static int SSH_REMOTE_PORT = 22;

    private Session session;

    public void closeSSh() { session.disconnect(); }

    public SShConnection() throws Throwable {
        JSch jsch = null;

        jsch = new JSch();
        session = jsch.getSession(SSH_USER, SSH_REMOTE_SERVER, SSH_REMOTE_PORT);
        session.setPassword(SSH_PASSWORD);

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect();
        session.sendKeepAliveMsg();
        session.setServerAliveInterval(30 * 1000);

        // postgresql
        session.setPortForwardingL(POSTGRESQL_LOCAL_PORT, POSTGRESQL_REMOTE_SERVER, POSTGRESQL_REMOTE_PORT);

    }

}

