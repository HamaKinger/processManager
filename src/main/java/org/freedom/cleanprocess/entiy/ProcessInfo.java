package org.freedom.cleanprocess.entiy;

import javafx.scene.control.Button;
import lombok.Builder;
import lombok.Data;


/**
 * @description: 进程信息
 * @author: freedom
 * @date: 2025/3/14
 */
@Data
@Builder
public class ProcessInfo {
    /**
     * 进程名称
     */
    private String name;
    /**
     * 进程ID
     */
    private String pid;
    /**
     * 内存使用
     */
    private String memory;
    /**
     * 进程类型
     */
    private String processType;
    /**
     * jvm参数
     */
    private String jvm;
    private Button button;

}
