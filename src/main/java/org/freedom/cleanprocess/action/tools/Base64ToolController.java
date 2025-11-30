package org.freedom.cleanprocess.action.tools;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freedom.cleanprocess.ProcessApp;
import org.freedom.cleanprocess.component.NotificationUtil;
import org.freedom.cleanprocess.util.SceneUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

/**
 * @description: Base64编解码工具控制器
 * @author: freedom
 * @date: 2025/11/29
 */
public class Base64ToolController implements Initializable {
    private static final Logger logger = LogManager.getLogger(Base64ToolController.class);
    
    @FXML
    private Button backButton;
    
    @FXML
    private TextArea inputTextArea;
    
    @FXML
    private TextArea outputTextArea;
    
    @FXML
    private Label fileNameLabel;
    
    private File loadedFile;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Base64编解码工具初始化");
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
     * Base64编码
     */
    @FXML
    public void handleEncodeButtonClick() {
        String input = inputTextArea.getText();
        
        if (input.isEmpty()) {
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入需要编码的内容");
            return;
        }
        
        try {
            // Base64编码
            String encoded = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
            outputTextArea.setText(encoded);
            
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "✅ Base64编码成功！");
            
        } catch (Exception e) {
            logger.error("Base64编码失败", e);
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showError(stage, "编码失败: " + e.getMessage());
        }
    }
    
    /**
     * Base64解码
     */
    @FXML
    public void handleDecodeButtonClick() {
        String input = inputTextArea.getText().trim();
        
        if (input.isEmpty()) {
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入需要解码的Base64字符串");
            return;
        }
        
        try {
            // Base64解码
            byte[] decoded = Base64.getDecoder().decode(input);
            String decodedStr = new String(decoded, StandardCharsets.UTF_8);
            outputTextArea.setText(decodedStr);
            
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "✅ Base64解码成功！");
            
        } catch (IllegalArgumentException e) {
            logger.error("Base64解码失败：格式错误", e);
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showError(stage, "解码失败：不是有效的Base64字符串");
        } catch (Exception e) {
            logger.error("Base64解码失败", e);
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showError(stage, "解码失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空内容
     */
    @FXML
    public void handleClearButtonClick() {
        inputTextArea.clear();
        outputTextArea.clear();
        fileNameLabel.setText("");
        loadedFile = null;
        
        Stage stage = (Stage) inputTextArea.getScene().getWindow();
        NotificationUtil.showInfo(stage, "已清空所有内容");
    }
    
    /**
     * 复制输入内容
     */
    @FXML
    public void handleCopyInputButtonClick() {
        String input = inputTextArea.getText();
        
        if (input.isEmpty()) {
            Stage stage = (Stage) inputTextArea.getScene().getWindow();
            NotificationUtil.showWarning(stage, "输入内容为空，无法复制");
            return;
        }
        
        copyToClipboard(input);
        
        Stage stage = (Stage) inputTextArea.getScene().getWindow();
        NotificationUtil.showSuccess(stage, "📋 已复制输入内容到剪贴板！");
    }
    
    /**
     * 复制输出内容
     */
    @FXML
    public void handleCopyOutputButtonClick() {
        String output = outputTextArea.getText();
        
        if (output.isEmpty()) {
            Stage stage = (Stage) outputTextArea.getScene().getWindow();
            NotificationUtil.showWarning(stage, "输出内容为空，无法复制");
            return;
        }
        
        copyToClipboard(output);
        
        Stage stage = (Stage) outputTextArea.getScene().getWindow();
        NotificationUtil.showSuccess(stage, "📋 已复制输出内容到剪贴板！");
    }
    
    /**
     * 加载文件
     */
    @FXML
    public void handleLoadFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择文件");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("所有文件", "*.*"),
            new FileChooser.ExtensionFilter("文本文件", "*.txt", "*.log", "*.json", "*.xml"),
            new FileChooser.ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        
        Stage stage = (Stage) inputTextArea.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                // 读取文件内容
                byte[] fileContent = Files.readAllBytes(file.toPath());
                
                // 如果是文本文件，显示文本内容；否则显示提示
                if (isTextFile(file)) {
                    String content = new String(fileContent, StandardCharsets.UTF_8);
                    inputTextArea.setText(content);
                } else {
                    // 对于二进制文件，直接进行Base64编码
                    String encoded = Base64.getEncoder().encodeToString(fileContent);
                    inputTextArea.setText(encoded);
                    outputTextArea.setText(""); // 清空输出
                }
                
                loadedFile = file;
                fileNameLabel.setText("已加载: " + file.getName() + " (" + formatFileSize(file.length()) + ")");
                
                NotificationUtil.showSuccess(stage, "✅ 文件加载成功！");
                
            } catch (IOException e) {
                logger.error("加载文件失败", e);
                NotificationUtil.showError(stage, "加载文件失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 保存文件
     */
    @FXML
    public void handleSaveFileButtonClick() {
        String output = outputTextArea.getText();
        
        if (output.isEmpty()) {
            Stage stage = (Stage) outputTextArea.getScene().getWindow();
            NotificationUtil.showWarning(stage, "输出内容为空，无法保存");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存文件");
        fileChooser.setInitialFileName("decoded_output.txt");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("文本文件", "*.txt"),
            new FileChooser.ExtensionFilter("所有文件", "*.*")
        );
        
        Stage stage = (Stage) outputTextArea.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try {
                // 写入文件
                Files.write(file.toPath(), output.getBytes(StandardCharsets.UTF_8));
                
                NotificationUtil.showSuccess(stage, "💾 文件保存成功！");
                
            } catch (IOException e) {
                logger.error("保存文件失败", e);
                NotificationUtil.showError(stage, "保存文件失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 复制到剪贴板
     */
    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
    
    /**
     * 判断是否为文本文件
     */
    private boolean isTextFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".txt") || name.endsWith(".log") || 
               name.endsWith(".json") || name.endsWith(".xml") ||
               name.endsWith(".csv") || name.endsWith(".md");
    }
    
    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
}
