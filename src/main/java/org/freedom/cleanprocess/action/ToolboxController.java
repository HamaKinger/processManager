package org.freedom.cleanprocess.action;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freedom.cleanprocess.ProcessApp;
import org.freedom.cleanprocess.util.SceneUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description: 工具集合控制器
 * @author: freedom
 * @date: 2025/11/29
 */
public class ToolboxController implements Initializable {
    private static final Logger logger = LogManager.getLogger(ToolboxController.class);
    
    @FXML
    private Button backButton;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("工具集合页面初始化");
    }
    
    /**
     * 返回主页面
     */
    @FXML
    public void handleBackButtonClick() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            SceneUtil.switchScene(stage, "fxml/index.fxml");
        } catch (Exception e) {
            logger.error("返回主页面失败", e);
        }
    }
    
    @FXML
    public void handleBackMouseEntered() {
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-text-fill: white; " +
                          "-fx-font-size: 13px; -fx-padding: 8 16; -fx-background-radius: 6; " +
                          "-fx-cursor: hand; -fx-font-weight: bold;");
    }
    
    @FXML
    public void handleBackMouseExited() {
        backButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; " +
                          "-fx-font-size: 13px; -fx-padding: 8 16; -fx-background-radius: 6; " +
                          "-fx-cursor: hand; -fx-font-weight: bold;");
    }
    
    /**
     * 打开JSON格式化工具
     */
    @FXML
    public void openJsonFormatter() {
        loadToolPage("fxml/tools/json-formatter.fxml", "JSON 格式化工具");
    }
    
    /**
     * 打开日期转换器
     */
    @FXML
    public void openDateConverter() {
        loadToolPage("fxml/tools/date-converter.fxml", "日期转换器");
    }
    
    /**
     * 打开Base64工具
     */
    @FXML
    public void openBase64Tool() {
        loadToolPage("fxml/tools/base64-tool.fxml", "Base64 编解码");
    }
    
    /**
     * 打开XML转JSON工具
     */
    @FXML
    public void openXmlToJsonTool() {
        loadToolPage("fxml/tools/xml-to-json.fxml", "XML 转 JSON");
    }

    /**
     * 打开文本对比工具
     */
    @FXML
    public void openTextDiffTool() {
        loadToolPage("fxml/tools/text-diff.fxml", "文本对比");
    }
    
    /**
     * 加载工具页面
     */
    private void loadToolPage(String fxmlFile, String title) {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            
            // 保存当前窗口状态
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();
            boolean maximized = stage.isMaximized();
            boolean fullScreen = stage.isFullScreen();
            
            FXMLLoader fxmlLoader = new FXMLLoader(ProcessApp.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            
            // 加载CSS样式
            URL cssResource = ProcessApp.class.getResource("styles.css");
            if(cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
            
            stage.setScene(scene);
            
            // 恢复窗口状态
            if (fullScreen) {
                stage.setFullScreen(true);
            } else if (maximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(width);
                stage.setHeight(height);
                stage.setX(x);
                stage.setY(y);
            }
            
            logger.info("打开工具页面: {}", title);
        } catch (IOException e) {
            logger.error("加载工具页面失败: {}", title, e);
        }
    }
}
