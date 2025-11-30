package org.freedom.cleanprocess.action.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freedom.cleanprocess.ProcessApp;
import org.freedom.cleanprocess.component.NotificationUtil;
import org.freedom.cleanprocess.util.SceneUtil;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description: XML转JSON工具控制器
 * @author: freedom
 * @date: 2025/11/29
 */
public class XmlToJsonController implements Initializable {
    private static final Logger logger = LogManager.getLogger(XmlToJsonController.class);
    
    @FXML
    private Button backButton;
    
    @FXML
    private TextArea inputTextArea;
    
    @FXML
    private TextArea outputTextArea;
    
    @FXML
    private CheckBox prettyPrintCheckBox;
    
    private final Gson prettyGson;
    private final Gson compactGson;
    
    public XmlToJsonController() {
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
        logger.info("XML转JSON工具初始化");
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
     * XML转JSON
     */
    @FXML
    public void handleXmlToJsonClick() {
        String input = inputTextArea.getText().trim();
        
        if (input.isEmpty()) {
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入XML数据");
            return;
        }
        
        try {
            // 使用org.json库将XML转换为JSON
            JSONObject jsonObject = XML.toJSONObject(input);
            String jsonString = jsonObject.toString();
            
            // 根据选择决定是否格式化
            if (prettyPrintCheckBox.isSelected()) {
                JsonElement jsonElement = JsonParser.parseString(jsonString);
                jsonString = prettyGson.toJson(jsonElement);
            }
            
            outputTextArea.setText(jsonString);
            
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "✅ XML转JSON成功！");
            
        } catch (Exception e) {
            logger.error("XML转JSON失败", e);
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showError(stage, "XML格式错误: " + e.getMessage());
        }
    }
    
    /**
     * JSON转XML
     */
    @FXML
    public void handleJsonToXmlClick() {
        String input = inputTextArea.getText().trim();
        
        if (input.isEmpty()) {
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入JSON数据");
            return;
        }
        
        try {
            // 解析JSON
            JSONObject jsonObject = new JSONObject(input);
            
            // 转换为XML
            String xmlString = XML.toString(jsonObject);
            
            // 添加XML声明和根元素包装（如果需要格式化）
            if (prettyPrintCheckBox.isSelected()) {
                xmlString = formatXml(xmlString);
            }
            
            outputTextArea.setText(xmlString);
            
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "✅ JSON转XML成功！");
            
        } catch (Exception e) {
            logger.error("JSON转XML失败", e);
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
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
        
        Stage stage = (Stage) inputTextArea.getScene().getWindow();
        NotificationUtil.showInfo(stage, "已清空所有内容");
    }
    
    /**
     * 复制结果到剪贴板
     */
    @FXML
    public void handleCopyButtonClick() {
        String output = outputTextArea.getText().trim();
        
        if (output.isEmpty()) {
            Stage stage = (Stage) outputTextArea.getScene().getWindow();
            NotificationUtil.showWarning(stage, "输出结果为空，无法复制");
            return;
        }
        
        // 复制到剪贴板
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(output);
        clipboard.setContent(content);
        
        Stage stage = (Stage) outputTextArea.getScene().getWindow();
        NotificationUtil.showSuccess(stage, "📋 已复制到剪贴板！");
    }
    
    /**
     * 格式化XML字符串
     */
    private String formatXml(String xml) {
        try {
            // 简单的XML格式化
            StringBuilder formatted = new StringBuilder();
            int indent = 0;
            boolean inTag = false;
            boolean inClosingTag = false;
            
            for (int i = 0; i < xml.length(); i++) {
                char c = xml.charAt(i);
                
                if (c == '<') {
                    if (i + 1 < xml.length() && xml.charAt(i + 1) == '/') {
                        inClosingTag = true;
                        indent--;
                        if (!inTag) {
                            formatted.append('\n').append("  ".repeat(Math.max(0, indent)));
                        }
                    } else {
                        if (!inTag && i > 0) {
                            formatted.append('\n').append("  ".repeat(indent));
                        }
                    }
                    inTag = true;
                    formatted.append(c);
                } else if (c == '>') {
                    formatted.append(c);
                    if (!inClosingTag) {
                        // 检查是否是自闭合标签
                        if (i > 0 && xml.charAt(i - 1) != '/') {
                            indent++;
                        }
                    }
                    inTag = false;
                    inClosingTag = false;
                } else {
                    formatted.append(c);
                }
            }
            
            return formatted.toString().trim();
        } catch (Exception e) {
            logger.error("XML格式化失败", e);
            return xml;
        }
    }
}
