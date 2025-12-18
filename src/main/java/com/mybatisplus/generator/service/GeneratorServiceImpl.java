package com.mybatisplus.generator.service;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.mybatisplus.generator.engine.CustomFreemarkerEngine;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.mybatisplus.generator.config.GeneratorConfig;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成器服务实现
 *
 * @author generator
 */
@Service(Service.Level.PROJECT)
public final class GeneratorServiceImpl implements GeneratorService {

    private final Project project;

    public GeneratorServiceImpl(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void generate(GeneratorConfig config) {
        // 构建路径映射
        Map<OutputFile, String> pathInfo = new HashMap<>();
        if (config.entityPath != null && !config.entityPath.isEmpty()) {
            pathInfo.put(OutputFile.entity, config.entityPath);
        }
        if (config.mapperPath != null && !config.mapperPath.isEmpty()) {
            pathInfo.put(OutputFile.mapper, config.mapperPath);
        }
        if (config.mapperXmlPath != null && !config.mapperXmlPath.isEmpty()) {
            pathInfo.put(OutputFile.xml, config.mapperXmlPath);
        }
        if (config.servicePath != null && !config.servicePath.isEmpty()) {
            pathInfo.put(OutputFile.service, config.servicePath);
        }
        if (config.serviceImplPath != null && !config.serviceImplPath.isEmpty()) {
            pathInfo.put(OutputFile.serviceImpl, config.serviceImplPath);
        }
        if (config.controllerPath != null && !config.controllerPath.isEmpty()) {
            pathInfo.put(OutputFile.controller, config.controllerPath);
        }

        // 判断是否生成 Service 接口
        boolean generateServiceInterface = config.servicePath != null && !config.servicePath.isEmpty();

        // 自定义参数
        Map<String, Object> customMap = new HashMap<>(8);
        customMap.put("jsonProperty", config.jsonProperty);
        customMap.put("jsonFormat", config.jsonFormat);

        // 使用 FastAutoGenerator (3.5.x 新 API)
        FastAutoGenerator.create(config.jdbcUrl, config.username, config.password)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author(config.author)
                            .dateType(DateType.TIME_PACK)
                            .disableOpenDir();
                    if (config.swagger2) {
                        builder.enableSwagger();
                    }
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(config.parentPackage)
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .controller("controller");
                    if (!pathInfo.isEmpty()) {
                        builder.pathInfo(pathInfo);
                    }
                    if (config.moduleName != null && !config.moduleName.isEmpty()) {
                        builder.moduleName(config.moduleName);
                    }
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(config.tables);

                    // 表前缀
                    if (config.tablePrefix != null && !config.tablePrefix.isEmpty()) {
                        builder.addTablePrefix(config.tablePrefix.split(","));
                    }

                    // 字段前缀
                    if (config.fieldPrefix != null && !config.fieldPrefix.isEmpty()) {
                        builder.addFieldPrefix(config.fieldPrefix.split(","));
                    }

                    // Entity 策略
                    builder.entityBuilder()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableTableFieldAnnotation()
                            .idType(parseIdType(config.idType));
                    if (config.lombok) {
                        builder.entityBuilder().enableLombok();
                    }
                    if (config.fileOverride) {
                        builder.entityBuilder().enableFileOverride();
                    }

                    // Controller 策略
                    if (config.restController) {
                        builder.controllerBuilder().enableRestStyle();
                    }
                    if (config.fileOverride) {
                        builder.controllerBuilder().enableFileOverride();
                    }

                    // Mapper 策略
                    builder.mapperBuilder().enableBaseResultMap().enableBaseColumnList();
                    if (config.fileOverride) {
                        builder.mapperBuilder().enableFileOverride();
                    }

                    // Service 策略
                    if (config.fileOverride) {
                        builder.serviceBuilder().enableFileOverride();
                    }
                })
                // 注入配置
                .injectionConfig(builder -> {
                    builder.customMap(customMap);
                })
                // 模板配置 (使用 custom-templates 避免与内置模板冲突)
                .templateConfig(builder -> {
                    builder.entity("custom-templates/entity.java")
                            .controller("custom-templates/controller.java");
                    if (generateServiceInterface) {
                        // 生成 Service 接口，使用标准模板
                        builder.service("custom-templates/service.java")
                                .serviceImpl("custom-templates/serviceImpl.java");
                    } else {
                        // 不生成 Service 接口，使用独立 ServiceImpl 模板
                        builder.disable(com.baomidou.mybatisplus.generator.config.TemplateType.SERVICE)
                                .serviceImpl("custom-templates/serviceImplStandalone.java");
                    }
                })
                // 模板引擎
                .templateEngine(new CustomFreemarkerEngine())
                // 执行
                .execute();

        // 刷新项目文件
        ApplicationManager.getApplication().invokeLater(() -> {
            VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
        });
    }

    private IdType parseIdType(String idType) {
        if (idType == null) {
            return IdType.ASSIGN_ID;
        }
        switch (idType) {
            case "AUTO":
                return IdType.AUTO;
            case "NONE":
                return IdType.NONE;
            case "INPUT":
                return IdType.INPUT;
            case "ASSIGN_UUID":
                return IdType.ASSIGN_UUID;
            case "ASSIGN_ID":
            default:
                return IdType.ASSIGN_ID;
        }
    }
}
