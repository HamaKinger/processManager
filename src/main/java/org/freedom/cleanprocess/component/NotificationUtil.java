package org.freedom.cleanprocess.component;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @description: 自定义通知弹框工具类
 * @author: freedom
 * @date: 2025/11/29
 */
public class NotificationUtil {
    
    private static final int NOTIFICATION_WIDTH = 320;
    private static final int NOTIFICATION_HEIGHT = 80;
    private static final int OFFSET_X = 20;
    private static final int OFFSET_Y = 20;
    
    /**
     * 显示成功通知
     */
    public static void showSuccess(Stage owner, String message) {
        showNotification(owner, message, NotificationType.SUCCESS);
    }
    
    /**
     * 显示错误通知
     */
    public static void showError(Stage owner, String message) {
        showNotification(owner, message, NotificationType.ERROR);
    }
    
    /**
     * 显示信息通知
     */
    public static void showInfo(Stage owner, String message) {
        showNotification(owner, message, NotificationType.INFO);
    }
    
    /**
     * 显示警告通知
     */
    public static void showWarning(Stage owner, String message) {
        showNotification(owner, message, NotificationType.WARNING);
    }
    
    /**
     * 显示通知
     */
    private static void showNotification(Stage owner, String message, NotificationType type) {
        Popup popup = new Popup();
        
        // 创建通知内容容器
        HBox container = createNotificationContainer(message, type, popup);
        
        popup.getContent().add(container);
        popup.setAutoHide(false);
        
        // 计算弹框位置（右上角）
        double x = owner.getX() + owner.getWidth() - NOTIFICATION_WIDTH - OFFSET_X;
        double y = owner.getY() + OFFSET_Y + 60; // 60是顶部标题栏高度
        
        popup.show(owner, x, y);
        
        // 添加进入动画
        playEnterAnimation(container);
        
        // 3秒后自动关闭
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> playExitAnimation(container, popup));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * 创建通知容器
     */
    private static HBox createNotificationContainer(String message, NotificationType type, Popup popup) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(15, 20, 15, 20));
        container.setSpacing(12);
        container.setPrefSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
        container.setMaxSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
        
        // 设置样式
        String baseStyle = "-fx-background-radius: 8; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 3); " +
                          "-fx-border-radius: 8; ";
        
        String colorStyle;
        String icon;
        
        switch (type) {
            case SUCCESS:
                colorStyle = "-fx-background-color: white; -fx-border-color: #48bb78; -fx-border-width: 2;";
                icon = "✅";
                break;
            case ERROR:
                colorStyle = "-fx-background-color: white; -fx-border-color: #f56565; -fx-border-width: 2;";
                icon = "❌";
                break;
            case WARNING:
                colorStyle = "-fx-background-color: white; -fx-border-color: #ed8936; -fx-border-width: 2;";
                icon = "⚠️";
                break;
            case INFO:
            default:
                colorStyle = "-fx-background-color: white; -fx-border-color: #4299e1; -fx-border-width: 2;";
                icon = "ℹ️";
                break;
        }
        
        container.setStyle(baseStyle + colorStyle);
        
        // 图标标签
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        // 消息标签
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2d3748; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(NOTIFICATION_WIDTH - 100);
        
        // 关闭按钮
        Label closeButton = new Label("×");
        closeButton.setStyle("-fx-font-size: 20px; -fx-text-fill: #a0aec0; -fx-cursor: hand; " +
                           "-fx-padding: 0 5 0 5;");
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(
            "-fx-font-size: 20px; -fx-text-fill: #718096; -fx-cursor: hand; -fx-padding: 0 5 0 5;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle(
            "-fx-font-size: 20px; -fx-text-fill: #a0aec0; -fx-cursor: hand; -fx-padding: 0 5 0 5;"));
        closeButton.setOnMouseClicked(e -> playExitAnimation(container, popup));
        
        // 组装
        HBox messageBox = new HBox(iconLabel, messageLabel);
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.setSpacing(12);
        HBox.setHgrow(messageBox, javafx.scene.layout.Priority.ALWAYS);
        
        container.getChildren().addAll(messageBox, closeButton);
        
        return container;
    }
    
    /**
     * 进入动画
     */
    private static void playEnterAnimation(HBox container) {
        // 淡入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // 滑入动画
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), container);
        slideIn.setFromX(50);
        slideIn.setToX(0);
        
        fadeIn.play();
        slideIn.play();
    }
    
    /**
     * 退出动画
     */
    private static void playExitAnimation(HBox container, Popup popup) {
        // 淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), container);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // 滑出动画
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), container);
        slideOut.setFromX(0);
        slideOut.setToX(50);
        
        fadeOut.setOnFinished(e -> popup.hide());
        
        fadeOut.play();
        slideOut.play();
    }
    
    /**
     * 通知类型枚举
     */
    private enum NotificationType {
        SUCCESS, ERROR, INFO, WARNING
    }
}
