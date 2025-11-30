package org.freedom.cleanprocess.action.tools;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freedom.cleanprocess.ProcessApp;
import org.freedom.cleanprocess.component.NotificationUtil;
import org.freedom.cleanprocess.util.SceneUtil;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @description: 日期转换器控制器
 * @author: freedom
 * @date: 2025/11/29
 */
public class DateConverterController implements Initializable {
    private static final Logger logger = LogManager.getLogger(DateConverterController.class);
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label currentTimestampSecondLabel;
    
    @FXML
    private Label currentTimestampMillisLabel;
    
    @FXML
    private Label currentDateTimeLabel;
    
    @FXML
    private TextField timestampInput;
    
    @FXML
    private ComboBox<String> timestampTypeCombo;
    
    @FXML
    private TextField timestampResultField;
    
    @FXML
    private TextField dateInput;
    
    @FXML
    private ComboBox<String> dateFormatCombo;
    
    @FXML
    private TextField dateResultSecondField;
    
    @FXML
    private TextField dateResultMillisField;
    
    private Timeline currentTimeUpdateTimeline;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("日期转换器初始化");
        
        // 初始化时间戳类型下拉框
        timestampTypeCombo.getItems().addAll("秒（10位）", "毫秒（13位）");
        timestampTypeCombo.getSelectionModel().select(0);
        
        // 初始化日期格式下拉框
        dateFormatCombo.getItems().addAll(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd",
            "yyyy年MM月dd日 HH:mm:ss",
            "yyyy年MM月dd日",
            "yyyyMMddHHmmss",
            "yyyyMMdd"
        );
        dateFormatCombo.getSelectionModel().select(0);
        
        // 启动当前时间更新定时器
        startCurrentTimeUpdate();
    }
    
    /**
     * 启动当前时间更新
     */
    private void startCurrentTimeUpdate() {
        currentTimeUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateCurrentTime()));
        currentTimeUpdateTimeline.setCycleCount(Animation.INDEFINITE);
        currentTimeUpdateTimeline.play();
        
        // 立即更新一次
        updateCurrentTime();
    }
    
    /**
     * 更新当前时间显示
     */
    private void updateCurrentTime() {
        long currentMillis = System.currentTimeMillis();
        long currentSecond = currentMillis / 1000;
        
        currentTimestampSecondLabel.setText(String.valueOf(currentSecond));
        currentTimestampMillisLabel.setText(String.valueOf(currentMillis));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateTimeLabel.setText(sdf.format(new Date(currentMillis)));
    }
    
    /**
     * 返回工具集合页面
     */
    @FXML
    public void handleBackButtonClick() {
        // 停止定时器
        if (currentTimeUpdateTimeline != null) {
            currentTimeUpdateTimeline.stop();
        }
        
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneUtil.switchScene(stage, "fxml/toolbox.fxml");
    }
    
    /**
     * 时间戳转日期
     */
    @FXML
    public void handleConvertToDateClick() {
        String timestampStr = timestampInput.getText().trim();
        
        if (timestampStr.isEmpty()) {
            Stage stage = (Stage) timestampInput.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入时间戳");
            return;
        }
        
        try {
            long timestamp = Long.parseLong(timestampStr);
            
            // 判断是秒还是毫秒
            String selectedType = timestampTypeCombo.getSelectionModel().getSelectedItem();
            long millis;
            
            if (selectedType.contains("秒")) {
                millis = timestamp * 1000;
            } else {
                millis = timestamp;
            }
            
            // 转换为日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = sdf.format(new Date(millis));
            
            timestampResultField.setText(dateStr);
            
            Stage stage = (Stage) timestampInput.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "✅ 转换成功！");
            
        } catch (NumberFormatException e) {
            logger.error("时间戳格式错误", e);
            Stage stage = (Stage) timestampInput.getScene().getWindow();
            NotificationUtil.showError(stage, "时间戳格式错误，请输入数字");
        } catch (Exception e) {
            logger.error("时间戳转换失败", e);
            Stage stage = (Stage) timestampInput.getScene().getWindow();
            NotificationUtil.showError(stage, "转换失败: " + e.getMessage());
        }
    }
    
    /**
     * 使用当前时间戳
     */
    @FXML
    public void handleUseCurrentTimestamp() {
        String selectedType = timestampTypeCombo.getSelectionModel().getSelectedItem();
        
        if (selectedType.contains("秒")) {
            timestampInput.setText(currentTimestampSecondLabel.getText());
        } else {
            timestampInput.setText(currentTimestampMillisLabel.getText());
        }
        
        Stage stage = (Stage) timestampInput.getScene().getWindow();
        NotificationUtil.showInfo(stage, "已填入当前时间戳");
    }
    
    /**
     * 日期转时间戳
     */
    @FXML
    public void handleConvertToTimestampClick() {
        String dateStr = dateInput.getText().trim();
        
        if (dateStr.isEmpty()) {
            Stage stage = (Stage) dateInput.getScene().getWindow();
            NotificationUtil.showWarning(stage, "请输入日期时间");
            return;
        }
        
        try {
            String formatPattern = dateFormatCombo.getSelectionModel().getSelectedItem();
            SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
            Date date = sdf.parse(dateStr);
            
            long millis = date.getTime();
            long second = millis / 1000;
            
            dateResultSecondField.setText(String.valueOf(second));
            dateResultMillisField.setText(String.valueOf(millis));
            
            Stage stage = (Stage) dateInput.getScene().getWindow();
            NotificationUtil.showSuccess(stage, "✅ 转换成功！");
            
        } catch (Exception e) {
            logger.error("日期转换失败", e);
            Stage stage = (Stage) dateInput.getScene().getWindow();
            NotificationUtil.showError(stage, "日期格式错误，请检查日期格式");
        }
    }
    
    /**
     * 使用当前日期
     */
    @FXML
    public void handleUseCurrentDate() {
        String formatPattern = dateFormatCombo.getSelectionModel().getSelectedItem();
        SimpleDateFormat sdf = new SimpleDateFormat(formatPattern);
        String currentDate = sdf.format(new Date());
        
        dateInput.setText(currentDate);
        
        Stage stage = (Stage) dateInput.getScene().getWindow();
        NotificationUtil.showInfo(stage, "已填入当前日期时间");
    }
}
