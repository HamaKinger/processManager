package org.freedom.cleanprocess.action.tools;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
 * @description: 文本对比工具控制器
 * @author: freedom
 * @date: 2025/11/29
 */
public class TextDiffController implements Initializable {
    private static final Logger logger = LogManager.getLogger(TextDiffController.class);
    
    @FXML
    private Button backButton;
    
    @FXML
    private TextArea textAreaLeft;
    
    @FXML
    private TextArea textAreaRight;
    
    @FXML
    private TextArea resultTextArea;
    
    @FXML
    private CheckBox ignoreWhitespaceCheckBox;
    
    @FXML
    private CheckBox ignoreCaseCheckBox;
    
    @FXML
    private Label totalLinesLabel;
    
    @FXML
    private Label sameLinesLabel;
    
    @FXML
    private Label diffLinesLabel;
    
    @FXML
    private Label similarityLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("文本对比工具初始化");
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
     * 对比文本
     */
    @FXML
    public void handleCompareButtonClick() {
        String textLeft = textAreaLeft.getText();
        String textRight = textAreaRight.getText();
        
        if (textLeft.isEmpty() && textRight.isEmpty()) {
            Stage stage = (Stage) textAreaLeft.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入要对比的文本");
            return;
        }
        
        try {
            // 分割成行
            String[] linesLeft = textLeft.split("\n", -1);
            String[] linesRight = textRight.split("\n", -1);
            
            int maxLines = Math.max(linesLeft.length, linesRight.length);
            StringBuilder result = new StringBuilder();
            
            int sameCount = 0;
            int diffCount = 0;
            
            // 逐行对比
            for (int i = 0; i < maxLines; i++) {
                String lineLeft = i < linesLeft.length ? linesLeft[i] : "";
                String lineRight = i < linesRight.length ? linesRight[i] : "";
                
                // 根据选项处理文本
                String processedLeft = processLine(lineLeft);
                String processedRight = processLine(lineRight);
                
                boolean isSame = processedLeft.equals(processedRight);
                
                if (isSame) {
                    sameCount++;
                    result.append(String.format("[=] 行 %d: %s\n", i + 1, lineLeft));
                } else {
                    diffCount++;
                    result.append(String.format("[!] 行 %d:\n", i + 1));
                    result.append(String.format("    A: %s\n", lineLeft.isEmpty() ? "(空行)" : lineLeft));
                    result.append(String.format("    B: %s\n", lineRight.isEmpty() ? "(空行)" : lineRight));
                }
            }
            
            // 显示结果
            resultTextArea.setText(result.toString());
            
            // 更新统计信息
            totalLinesLabel.setText("总行数: " + maxLines);
            sameLinesLabel.setText("相同: " + sameCount);
            diffLinesLabel.setText("不同: " + diffCount);
            
            // 计算相似度
            double similarity = maxLines > 0 ? (double) sameCount / maxLines * 100 : 0;
            similarityLabel.setText(String.format("相似度: %.1f%%", similarity));
            
            Stage stage = (Stage) textAreaLeft.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "文本对比完成！");
            
        } catch (Exception e) {
            logger.error("文本对比失败", e);
            Stage stage = (Stage) textAreaLeft.getScene().getWindow();
            NotificationUtil.showError(stage, "对比失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空内容
     */
    @FXML
    public void handleClearButtonClick() {
        textAreaLeft.clear();
        textAreaRight.clear();
        resultTextArea.clear();
        
        totalLinesLabel.setText("总行数: 0");
        sameLinesLabel.setText("相同: 0");
        diffLinesLabel.setText("不同: 0");
        similarityLabel.setText("相似度: 0%");
        
        Stage stage = (Stage) textAreaLeft.getScene().getWindow();
        NotificationUtil.showInfo(stage, "已清空所有内容");
    }
    
    /**
     * 交换文本
     */
    @FXML
    public void handleSwapButtonClick() {
        String temp = textAreaLeft.getText();
        textAreaLeft.setText(textAreaRight.getText());
        textAreaRight.setText(temp);
        
        Stage stage = (Stage) textAreaLeft.getScene().getWindow();
        NotificationUtil.showInfo(stage, "文本已交换");
    }
    
    /**
     * 处理行内容（根据选项）
     */
    private String processLine(String line) {
        String processed = line;
        
        // 忽略空格
        if (ignoreWhitespaceCheckBox.isSelected()) {
            processed = processed.replaceAll("\\s+", "");
        }
        
        // 忽略大小写
        if (ignoreCaseCheckBox.isSelected()) {
            processed = processed.toLowerCase();
        }
        
        return processed;
    }
}
