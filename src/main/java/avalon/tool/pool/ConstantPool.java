package avalon.tool.pool;

import avalon.tool.ConfigSystem;
import avalon.tool.DatabaseConfig;
import avalon.tool.database.DatabaseOperator;
import avalon.tool.database.MySQLDatabaseOperator;
import avalon.tool.database.SQLiteDatabaseOperator;
import avalon.util.servlet.AvalonServlet;
import avalon.util.servlet.CoolqServlet;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;

/**
 * Created by Eldath on 2017/2/3 0003.
 *
 * @author Eldath
 */
public class ConstantPool {
    private ConstantPool() {
    }

    public static class Database {
        private static final String Datasource = DatabaseConfig.getInstance().get("DataSource");
        public static final DatabaseOperator currentDatabaseOperator = "mysql".equals(Datasource) ?
                MySQLDatabaseOperator.getInstance() : SQLiteDatabaseOperator.getInstance();
    }

    public static class Address {
        public static final String servlet = Basic.currentServlet.apiAddress();
        public static final String wechat = addressHandle((String) ConfigSystem.getInstance()
                .get("Mojo-Weixin_API_Address"));
        public static final String servletScriptFile = Basic.currentServlet.scriptFilePath();
        public static final String perlFileOfWechat = Basic.currentPath +
                File.separator + "bin" + File.separator + "Mojo-Weixin.pl";
        public static final String dataPath = Basic.currentPath + File.separator + "data";

        private static String addressHandle(String address) {
            return address.endsWith("/") ? address.substring(0, address.length() - 1) : address;
        }
    }

    public static class Version {
        public static final String avalon = "0.0.1";
        private static final String servlet = Basic.currentServlet.version();
        private static Version instance = null;
        private static String wechat;

        public static Version getInstance() {
            if (instance == null) instance = new Version();
            return instance;
        }

        private Version() {
            try {
                wechat = ((JSONObject) new JSONTokener(
                        new URL(Address.wechat + "/openwx/get_client_info")
                                .openStream()).nextValue()).getString("version");
            } catch (IOException ignore) {
                wechat = "UNKNOWN";
            }
        }

        public String servlet() {
            return servlet;
        }

        public String wechat() {
            return wechat;
        }
    }

    public static class Basic {
        public static final AvalonServlet currentServlet = new CoolqServlet(); // TODO 使用ServletGetter
        public static final boolean localOutput = (boolean) ConfigSystem.getInstance().get("Local_output");
        public static final boolean debug = (boolean) ConfigSystem.getInstance().get("Debug");
        public static final long startTime = System.currentTimeMillis();
        public static final int pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        public static final String currentPath;

        static {
            String path1;
            try {
                path1 = new File("").getCanonicalPath();
            } catch (IOException e) {
                System.out.println("Fatal error: " + e.toString());
                path1 = "";
                System.exit(-1);
            }
            currentPath = path1;
        }
    }

    public static class Setting {
        public static final boolean Block_Words_Punishment_Mode_Enabled =
                (boolean) ConfigSystem.getInstance().get("Block_Words_Punishment_Mode_Enabled");
        public static final boolean AnswerMe_Enabled = (boolean)
                ConfigSystem.getInstance().getCommandConfig("AnswerMe", "Enable");
        public static final int Block_Words_Punish_Frequency =
                (int) ConfigSystem.getInstance().get("Block_Words_Punish_Frequency");
        public static final int Max_Stream_Length = 100;
    }

    public static class GameMode {
        public static final boolean IsEnabled = (boolean) ConfigSystem.getInstance().get("Game_Mode_Enabled");
    }
}