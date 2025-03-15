module org.freedom.cleanprocess {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.apache.logging.log4j;
    requires jdk.compiler;
    opens org.freedom.cleanprocess.entiy to javafx.base; // 允许 javafx.base 反射访问
    exports org.freedom.cleanprocess.entiy; // 导出包以供其他模块使用（如果需要）

    opens org.freedom.cleanprocess to javafx.fxml;
    exports org.freedom.cleanprocess;
    exports org.freedom.cleanprocess.action;
    opens org.freedom.cleanprocess.action to javafx.fxml;
}