module org.freedom.cleanprocess {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.apache.logging.log4j;
    requires jdk.compiler;
    requires com.google.gson;
    requires org.json;
    opens org.freedom.cleanprocess.entiy to javafx.base; // 允许 javafx.base 反射访问
    exports org.freedom.cleanprocess.entiy; // 导出包以供其他模块使用（如果需要）

    opens org.freedom.cleanprocess to javafx.fxml;
    exports org.freedom.cleanprocess;
    exports org.freedom.cleanprocess.action;
    opens org.freedom.cleanprocess.action to javafx.fxml;
    exports org.freedom.cleanprocess.action.tools;
    opens org.freedom.cleanprocess.action.tools to javafx.fxml;
    exports org.freedom.cleanprocess.component;
    opens org.freedom.cleanprocess.component to javafx.fxml;
    exports org.freedom.cleanprocess.util;
    opens org.freedom.cleanprocess.util to javafx.fxml;
}