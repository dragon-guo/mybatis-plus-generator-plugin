package com.mybatisplus.generator.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 代码生成器全局配置
 *
 * @author generator
 */
@Service
@State(
        name = "MybatisPlusGeneratorSettings",
        storages = @Storage("mybatis-plus-generator.xml")
)
public final class GeneratorSettings implements PersistentStateComponent<GeneratorSettings> {

    // 数据库配置
    public String dbType = "MySQL";
    public String jdbcUrl = "";
    public String driverName = "com.mysql.cj.jdbc.Driver";
    public String username = "";
    public String password = "";
    public String schemaName = "";

    // 包配置
    public String parentPackage = "com.example";
    public String moduleName = "";
    public String entityPackage = "entity";
    public String mapperPackage = "mapper";
    public String servicePackage = "service";
    public String serviceImplPackage = "service.impl";
    public String controllerPackage = "controller";

    // 路径配置
    public String entityPath = "";
    public String mapperPath = "";
    public String mapperXmlPath = "";
    public String servicePath = "";
    public String serviceImplPath = "";
    public String controllerPath = "";

    // 全局配置
    public String author = "generator";
    public boolean swagger2 = true;
    public boolean lombok = true;
    public boolean restController = true;
    public boolean fileOverride = true;
    public boolean baseResultMap = true;
    public boolean baseColumnList = true;

    // 策略配置
    public String tablePrefix = "";
    public String fieldPrefix = "";
    public boolean entityTableFieldAnnotation = true;
    public boolean jsonFormat = true;
    public boolean jsonProperty = false;

    // 主键策略
    public String idType = "ASSIGN_UUID";

    public static GeneratorSettings getInstance() {
        return ApplicationManager.getApplication().getService(GeneratorSettings.class);
    }

    @Nullable
    @Override
    public GeneratorSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull GeneratorSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
