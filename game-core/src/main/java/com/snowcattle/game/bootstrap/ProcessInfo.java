package com.snowcattle.game.bootstrap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class ProcessInfo {
    public static String getProcessName() {
        try {
            // 执行 "jps" 命令获取 Java 进程信息
            Process process = Runtime.getRuntime().exec("jps -l");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(getProcessId())) {
                    String[] parts = line.split(" ")[1].split("\\.");
                    return parts[parts.length-1];  // 获取进程名（类名）
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    public static String getProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];  // 获取当前进程 ID
    }
}
