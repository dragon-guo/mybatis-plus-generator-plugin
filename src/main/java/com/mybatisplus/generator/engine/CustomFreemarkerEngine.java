package com.mybatisplus.generator.engine;

import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 自定义 Freemarker 模板引擎
 * 解决 IntelliJ 插件类加载器问题
 */
public class CustomFreemarkerEngine extends FreemarkerTemplateEngine {

    private Configuration configuration;

    @Override
    public @NotNull FreemarkerTemplateEngine init(@NotNull ConfigBuilder configBuilder) {
        super.init(configBuilder);
        configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        // 使用当前类的类加载器来加载模板（空字符串表示 classpath 根目录）
        configuration.setClassLoaderForTemplateLoading(
                CustomFreemarkerEngine.class.getClassLoader(), "");
        return this;
    }

    @Override
    public void writer(@NotNull Map<String, Object> objectMap, @NotNull String templatePath, @NotNull File outputFile) throws Exception {
        // templatePath 已经包含 .ftl 后缀，直接使用
        Template template = configuration.getTemplate(templatePath);
        // 确保父目录存在
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {
            template.process(objectMap, writer);
        }
    }
}
