package org.freedom.cleanprocess.action.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freedom.cleanprocess.ProcessApp;
import org.freedom.cleanprocess.component.NotificationUtil;
import org.freedom.cleanprocess.util.SceneUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description: JSON格式化工具控制器
 * @author: freedom
 * @date: 2025/11/29
 */
public class JsonFormatterController implements Initializable {
    private static final Logger logger = LogManager.getLogger(JsonFormatterController.class);
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button formatButton;
    
    @FXML
    private Button compressButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private Button copyButton;
    
    @FXML
    private TextArea inputTextArea;
    
    @FXML
    private TextArea outputTextArea;
    
    private final Gson prettyGson;
    private final Gson compactGson;
    
    public JsonFormatterController() {
        // 创建格式化的Gson实例
        prettyGson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        
        // 创建压缩的Gson实例
        compactGson = new GsonBuilder()
                .serializeNulls()
                .create();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("JSON格式化工具初始化");
    }
    
    /**
     * 返回工具集合页面
     */
    @FXML
    public void handleBackButtonClick() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneUtil.switchScene(stage, "fxml/toolbox.fxml");
    }
    
    /**
     * 格式化JSON
     */
    @FXML
    public void handleFormatButtonClick() {
        String input = inputTextArea.getText().trim();
        
        if (input.isEmpty()) {
            Stage stage = (Stage) formatButton.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入JSON数据");
            return;
        }
        
        try {
            // 解析并格式化JSON
            Object jsonObject = JsonParser.parseString(input);
            String formattedJson = prettyGson.toJson(jsonObject);
            
            outputTextArea.setText(formattedJson);
            
            Stage stage = (Stage) formatButton.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "✅ JSON格式化成功！");
            
        } catch (Exception e) {
            logger.error("JSON格式化失败", e);
            Stage stage = (Stage) formatButton.getScene().getWindow();
            NotificationUtil.showError(stage, "JSON格式错误: " + e.getMessage());
        }
    }
    
    /**
     * 压缩JSON
     */
    @FXML
    public void handleCompressButtonClick() {
        String input = inputTextArea.getText().trim();
        
        if (input.isEmpty()) {
            Stage stage = (Stage) compressButton.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入JSON数据");
            return;
        }
        
        try {
            // 解析并压缩JSON
            Object jsonObject = JsonParser.parseString(input);
            String compressedJson = compactGson.toJson(jsonObject);
            
            outputTextArea.setText(compressedJson);
            
            Stage stage = (Stage) compressButton.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "✅ JSON压缩成功！");
            
        } catch (Exception e) {
            logger.error("JSON压缩失败", e);
            Stage stage = (Stage) compressButton.getScene().getWindow();
            NotificationUtil.showError(stage, "JSON格式错误: " + e.getMessage());
        }
    }
    
    /**
     * 清空内容
     */
    @FXML
    public void handleClearButtonClick() {
        inputTextArea.clear();
        outputTextArea.clear();
        
        Stage stage = (Stage) clearButton.getScene().getWindow();
        NotificationUtil.showInfo(stage, "已清空所有内容");
    }
    
    /**
     * 复制结果到剪贴板
     */
    @FXML
    public void handleCopyButtonClick() {
        String output = outputTextArea.getText().trim();
        
        if (output.isEmpty()) {
            Stage stage = (Stage) copyButton.getScene().getWindow();
            NotificationUtil.showWarning(stage, "输出结果为空，无法复制");
            return;
        }
        
        // 复制到剪贴板
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(output);
        clipboard.setContent(content);
        
        Stage stage = (Stage) copyButton.getScene().getWindow();
        NotificationUtil.showSuccess(stage, "📋 已复制到剪贴板！");
    }
}
