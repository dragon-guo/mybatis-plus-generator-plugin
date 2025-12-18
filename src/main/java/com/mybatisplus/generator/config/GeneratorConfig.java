package com.mybatisplus.generator.config;

import java.util.List;

public class GeneratorConfig {
    public String jdbcUrl;
    public String username;
    public String password;
    public String schemaName;
    public String driverName;

    public String parentPackage;
    public String moduleName;

    public String entityPath;
    public String mapperPath;
    public String mapperXmlPath;
    public String servicePath;
    public String serviceImplPath;
    public String controllerPath;

    public String author;
    public List<String> tables;
    public String tablePrefix;
    public String fieldPrefix;
    public String idType;

    public boolean swagger2;
    public boolean lombok;
    public boolean restController;
    public boolean fileOverride;
    public boolean jsonFormat;
    public boolean jsonProperty;
}
