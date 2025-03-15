package org.freedom.cleanprocess.action;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.freedom.cleanprocess.entiy.ProcessInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
public class IndexController implements Initializable {
    private static final Logger logger = LogManager.getLogger(IndexController.class);

    @FXML
    private TableView<ProcessInfo> processTableView;

    @FXML
    private TableColumn<ProcessInfo, String> processNameColumn;

    @FXML
    private TableColumn<ProcessInfo, String> processIdColumn;

    @FXML
    private TableColumn<ProcessInfo, String> processMemoryColumn;

    @FXML
    private TableColumn<ProcessInfo, String> processProcessTypeColumn;
    @FXML
    private TableColumn<ProcessInfo, String> processJvmParam;
    @FXML
    private TableColumn<ProcessInfo, String> actionColumn;
    @FXML
    private Button refreshButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        processTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // 设置按钮样式
        refreshButton.setStyle(
                "-fx-background-color: #4CAF50; " + // 背景颜色
                        "-fx-text-fill: white; " +         // 文字颜色
                        "-fx-font-size: 14px; " +          // 字体大小
                        "-fx-padding: 8px 15px;"          // 内边距
        );
        // 自定义 CellValueFactory
        processNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        processIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPid()));
        processMemoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMemory()));
        processProcessTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProcessType()));
        processJvmParam.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getJvm()));
        actionColumn.setCellFactory(getButtonCellFactory());
        loadProcessInfo();
    }

    private void loadProcessInfo() {
        try {
            Process process = Runtime.getRuntime().exec("cmd /c jps -l");
            Map<String,String> processMapByL = getProcessMapByL(process);
            processMapByL.forEach((pid,name) -> {
                String memory ="";
                try {
                    Map<String,String> pidMemory = getPidMemory(pid);
                    memory = pidMemory.get(pid);
                }catch(IOException e) {
                    throw new RuntimeException(e);
                }
                ProcessInfo processInfo = ProcessInfo.builder().name(name).pid(pid).memory(memory)
                        .processType("java").jvm("")
                        .build();
                processTableView.getItems().add(processInfo);
            });
        } catch (Exception e) {
            logger.error("loadProcessInfo error:",e);
        }
    }

    private Map<String,String> getProcessMapByL (Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        Map<String,String> processMap = new HashMap<>(16);
        while ((line = reader.readLine()) != null) {
            logger.info("line = {}" , line);
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                String pid = parts[0];
                String name = parts[1];
                if(name.contains("jps") || name.endsWith(".exe")){
                    continue;
                }
                if(!name.contains("idea")){
                    name = name.substring(name.lastIndexOf(".")+1);
                }
                processMap.put(pid,name);
            }
        }
        return processMap;
    }
    private Map<String,String> getPidMemory (String pid) throws IOException {
        // 执行 wmic 命令
        Map<String,String> processMemory = new HashMap<>(16);
        // 执行 jstat 命令
        Process process = Runtime.getRuntime().exec("jstat -gc " + pid);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // 读取命令输出
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                // 解析 jstat 输出
                String[] parts = line.split("\\s+");
                if (parts.length >= 10) {
                    try {
                        // 校验并解析使用的堆内存（KB）
                        Double usedHeap = parseLongSafely(parts[3],(double) -1);
                        if (usedHeap == -1) {
                            logger.info("Invalid heap memory value:{} " , parts[3]);
                            continue; // 跳过非法数据
                        }
                        // 校验并解析使用的非堆内存（KB）
                        Double usedNonHeap = parseLongSafely(parts[7],Double.valueOf(-1));
                        if (usedNonHeap == -1) {
                            logger.info("Invalid non-heap memory value: {}",parts[7]);
                            continue; // 跳过非法数据
                        }
                        // 计算总内存并转换为 MB
                        Double totalMemoryMB = (usedHeap + usedNonHeap) / 1024;
                        processMemory.put(pid, String.valueOf(totalMemoryMB.intValue())+" MB");
                    } catch (Exception e) {
                        logger.error("Error processing memory data for PID: {}",pid);
                    }
                }

            }
        }
        // 关闭资源
        reader.close();
        return processMemory;
    }

    private static Double parseLongSafely(String value, Double defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Callback<TableColumn<ProcessInfo, String>,TableCell<ProcessInfo, String>> getButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button btn = new Button("结束进程");
            {
                btn.setOnAction(event -> {
                    ProcessInfo process = getTableView().getItems().get(getIndex());
                    terminateProcess(process);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                // 使用 HBox 实现按钮居中
                HBox hbox = new HBox(btn);
                hbox.setAlignment(Pos.CENTER);
                setGraphic(empty ? null : hbox);
            }

        };
    }

    public void terminateProcess(ProcessInfo processInfo) {
        try {
            // 使用 taskkill 命令结束进程
            Process killProcess = Runtime.getRuntime().exec("taskkill /F /PID " + processInfo.getPid());
            int exitCode = killProcess.waitFor();
            if (exitCode == 0) {
                logger.info("进程 {} 已成功结束", processInfo.getPid());
                // 从 TableView 中移除已结束的进程
                processTableView.getItems().remove(processInfo);
            } else {
                logger.error("结束进程 {} 失败", processInfo.getPid());
            }
        } catch (IOException | InterruptedException e) {
            logger.error("结束进程时发生错误", e);
        }
    }

    @FXML
    public void handleRefreshButtonClick () {
        processTableView.getItems().clear();
        loadProcessInfo();
        // 创建提示框
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null); // 不显示标题
        alert.setContentText("数据已刷新！");
        alert.showAndWait(); // 显示提示框并等待用户关闭
    }
}