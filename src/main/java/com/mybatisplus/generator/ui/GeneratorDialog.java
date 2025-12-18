package com.mybatisplus.generator.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.*;
import com.intellij.ui.table.JBTable;
import com.mybatisplus.generator.config.GeneratorConfig;
import com.mybatisplus.generator.service.GeneratorService;
import com.mybatisplus.generator.settings.GeneratorSettings;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器配置对话框
 */
public class GeneratorDialog extends DialogWrapper {

    private final Project project;
    private final String basePath;

    // 数据库配置
    private JComboBox<String> dbTypeCombo;
    private JBTextField jdbcUrlField;
    private JBTextField usernameField;
    private JBPasswordField passwordField;
    private JBTextField schemaNameField;

    // 表选择
    private JBTable tableListTable;
    private DefaultTableModel tableModel;
    private JBTextField tableFilterField;

    // 包配置
    private JBTextField parentPackageField;
    private JBTextField moduleNameField;

    // 路径配置
    private TextFieldWithBrowseButton entityPathField;
    private TextFieldWithBrowseButton mapperPathField;
    private TextFieldWithBrowseButton mapperXmlPathField;
    private TextFieldWithBrowseButton servicePathField;
    private TextFieldWithBrowseButton serviceImplPathField;
    private TextFieldWithBrowseButton controllerPathField;

    // 策略配置
    private JBTextField tablePrefixField;
    private JBTextField fieldPrefixField;
    private JBTextField authorField;
    private JComboBox<String> idTypeCombo;

    // 选项配置
    private JBCheckBox swagger2Check;
    private JBCheckBox lombokCheck;
    private JBCheckBox restControllerCheck;
    private JBCheckBox fileOverrideCheck;
    private JBCheckBox jsonFormatCheck;
    private JBCheckBox jsonPropertyCheck;

    public GeneratorDialog(Project project, String basePath) {
        super(project);
        this.project = project;
        this.basePath = basePath;

        setTitle("MyBatis Plus 代码生成器");
        setSize(850, 750);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("数据库配置", createDatabasePanel());
        tabbedPane.addTab("表选择", createTableSelectionPanel());
        tabbedPane.addTab("路径配置", createPathConfigPanel());
        tabbedPane.addTab("代码配置", createCodeConfigPanel());
        tabbedPane.addTab("高级选项", createAdvancedPanel());
        return tabbedPane;
    }

    private JPanel createDatabasePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        GeneratorSettings settings = GeneratorSettings.getInstance();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JBLabel("数据库类型:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dbTypeCombo = new JComboBox<>(new String[]{"MySQL", "PostgreSQL", "Oracle", "DM"});
        dbTypeCombo.setSelectedItem(settings.dbType);
        dbTypeCombo.addActionListener(e -> updateJdbcUrlTemplate());
        panel.add(dbTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JBLabel("JDBC URL:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        jdbcUrlField = new JBTextField(settings.jdbcUrl);
        panel.add(jdbcUrlField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JBLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        usernameField = new JBTextField(settings.username);
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JBLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        passwordField = new JBPasswordField();
        passwordField.setText(settings.password);
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JBLabel("Schema:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        schemaNameField = new JBTextField(settings.schemaName);
        panel.add(schemaNameField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton testConnectionBtn = new JButton("测试连接");
        testConnectionBtn.addActionListener(e -> testConnection());
        JButton loadTablesBtn = new JButton("加载表");
        loadTablesBtn.addActionListener(e -> loadTables());
        buttonPanel.add(testConnectionBtn);
        buttonPanel.add(loadTablesBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        gbc.gridy = 6; gbc.weighty = 1;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createTableSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel filterPanel = new JPanel(new BorderLayout(5, 5));
        filterPanel.add(new JBLabel("表名过滤:"), BorderLayout.WEST);
        tableFilterField = new JBTextField();
        tableFilterField.getEmptyText().setText("输入表名关键字进行过滤...");
        filterPanel.add(tableFilterField, BorderLayout.CENTER);

        JButton filterBtn = new JButton("过滤");
        filterBtn.addActionListener(e -> filterTables());
        filterPanel.add(filterBtn, BorderLayout.EAST);
        panel.add(filterPanel, BorderLayout.NORTH);

        String[] columns = {"选择", "表名", "注释"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        tableListTable = new JBTable(tableModel);
        tableListTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableListTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        tableListTable.getColumnModel().getColumn(2).setPreferredWidth(300);

        panel.add(new JBScrollPane(tableListTable), BorderLayout.CENTER);

        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllBtn = new JButton("全选");
        selectAllBtn.addActionListener(e -> selectAllTables(true));
        JButton deselectAllBtn = new JButton("取消全选");
        deselectAllBtn.addActionListener(e -> selectAllTables(false));
        selectPanel.add(selectAllBtn);
        selectPanel.add(deselectAllBtn);
        panel.add(selectPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPathConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        GeneratorSettings settings = GeneratorSettings.getInstance();
        String defaultJavaPath = basePath + "/src/main/java";
        String defaultResourcePath = basePath + "/src/main/resources/mapper";

        FileChooserDescriptor dirDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);

        int row = 0;

        // 提示
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JBLabel tipLabel = new JBLabel("<html>配置各模块的输出路径<br/><font color='#999999'>清空路径则不生成对应模块</font></html>");
        panel.add(tipLabel, gbc);
        gbc.gridwidth = 1;

        // Entity 路径
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JBLabel("Entity 路径:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        entityPathField = new TextFieldWithBrowseButton();
        entityPathField.setText(settings.entityPath.isEmpty() ? defaultJavaPath : settings.entityPath);
        entityPathField.addBrowseFolderListener("选择 Entity 输出路径", null, project, dirDescriptor);
        panel.add(entityPathField, gbc);
        row++;

        // Mapper 路径
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JBLabel("Mapper 路径:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        mapperPathField = new TextFieldWithBrowseButton();
        mapperPathField.setText(settings.mapperPath.isEmpty() ? defaultJavaPath : settings.mapperPath);
        mapperPathField.addBrowseFolderListener("选择 Mapper 输出路径", null, project, dirDescriptor);
        panel.add(mapperPathField, gbc);
        row++;

        // Mapper XML 路径
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JBLabel("Mapper XML 路径:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        mapperXmlPathField = new TextFieldWithBrowseButton();
        mapperXmlPathField.setText(settings.mapperXmlPath.isEmpty() ? defaultResourcePath : settings.mapperXmlPath);
        mapperXmlPathField.addBrowseFolderListener("选择 Mapper XML 输出路径", null, project, dirDescriptor);
        panel.add(mapperXmlPathField, gbc);
        row++;

        // Service 路径 - 添加特殊提示
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JBLabel("<html>Service 路径:<br/><font color='#999999' size='2'>(清空则不生成接口)</font></html>"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        servicePathField = new TextFieldWithBrowseButton();
        servicePathField.setText(settings.servicePath);
        servicePathField.addBrowseFolderListener("选择 Service 输出路径", null, project, dirDescriptor);
        panel.add(servicePathField, gbc);
        row++;

        // ServiceImpl 路径
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JBLabel("ServiceImpl 路径:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        serviceImplPathField = new TextFieldWithBrowseButton();
        serviceImplPathField.setText(settings.serviceImplPath.isEmpty() ? defaultJavaPath : settings.serviceImplPath);
        serviceImplPathField.addBrowseFolderListener("选择 ServiceImpl 输出路径", null, project, dirDescriptor);
        panel.add(serviceImplPathField, gbc);
        row++;

        // Controller 路径
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JBLabel("Controller 路径:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controllerPathField = new TextFieldWithBrowseButton();
        controllerPathField.setText(settings.controllerPath.isEmpty() ? defaultJavaPath : settings.controllerPath);
        controllerPathField.addBrowseFolderListener("选择 Controller 输出路径", null, project, dirDescriptor);
        panel.add(controllerPathField, gbc);
        row++;

        // 填充
        gbc.gridy = row; gbc.weighty = 1;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createCodeConfigPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        GeneratorSettings settings = GeneratorSettings.getInstance();

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JBLabel("父包名:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        parentPackageField = new JBTextField(settings.parentPackage);
        panel.add(parentPackageField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JBLabel("模块名:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        moduleNameField = new JBTextField(settings.moduleName);
        panel.add(moduleNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JBLabel("作者:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        authorField = new JBTextField(settings.author);
        panel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JBLabel("表前缀:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        tablePrefixField = new JBTextField(settings.tablePrefix);
        panel.add(tablePrefixField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JBLabel("字段前缀:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        fieldPrefixField = new JBTextField(settings.fieldPrefix);
        panel.add(fieldPrefixField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JBLabel("主键策略:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        idTypeCombo = new JComboBox<>(new String[]{"AUTO", "NONE", "INPUT", "ASSIGN_ID", "ASSIGN_UUID"});
        idTypeCombo.setSelectedItem(settings.idType);
        panel.add(idTypeCombo, gbc);

        gbc.gridy = 6; gbc.weighty = 1;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private JPanel createAdvancedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        GeneratorSettings settings = GeneratorSettings.getInstance();

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(new JBLabel("代码风格选项:"), gbc);

        gbc.gridy = row++;
        swagger2Check = new JBCheckBox("启用 Swagger2 注解", settings.swagger2);
        panel.add(swagger2Check, gbc);

        gbc.gridy = row++;
        lombokCheck = new JBCheckBox("启用 Lombok 注解", settings.lombok);
        panel.add(lombokCheck, gbc);

        gbc.gridy = row++;
        restControllerCheck = new JBCheckBox("使用 RestController", settings.restController);
        panel.add(restControllerCheck, gbc);

        gbc.gridy = row++;
        fileOverrideCheck = new JBCheckBox("覆盖已有文件", settings.fileOverride);
        panel.add(fileOverrideCheck, gbc);

        gbc.gridy = row++;
        panel.add(new JSeparator(), gbc);

        gbc.gridy = row++;
        panel.add(new JBLabel("JSON 选项:"), gbc);

        gbc.gridy = row++;
        jsonFormatCheck = new JBCheckBox("启用 @JsonFormat 日期格式化", settings.jsonFormat);
        panel.add(jsonFormatCheck, gbc);

        gbc.gridy = row++;
        jsonPropertyCheck = new JBCheckBox("启用 @JsonProperty 注解", settings.jsonProperty);
        panel.add(jsonPropertyCheck, gbc);

        gbc.gridy = row; gbc.weighty = 1;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    private void updateJdbcUrlTemplate() {
        String dbType = (String) dbTypeCombo.getSelectedItem();
        switch (dbType) {
            case "MySQL":
                jdbcUrlField.setText("jdbc:mysql://localhost:3306/database?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
                break;
            case "PostgreSQL":
                jdbcUrlField.setText("jdbc:postgresql://localhost:5432/database");
                break;
            case "Oracle":
                jdbcUrlField.setText("jdbc:oracle:thin:@localhost:1521:orcl");
                break;
            case "DM":
                jdbcUrlField.setText("jdbc:dm://localhost:5236?schema=SCHEMA_NAME&clobAsString=true");
                break;
        }
    }

    private void testConnection() {
        try {
            String driverName = getDriverName();
            Class.forName(driverName);
            try (Connection conn = DriverManager.getConnection(
                    jdbcUrlField.getText(),
                    usernameField.getText(),
                    new String(passwordField.getPassword()))) {
                Messages.showInfoMessage(project, "数据库连接成功!", "连接测试");
            }
        } catch (Exception e) {
            Messages.showErrorDialog(project, "连接失败: " + e.getMessage(), "连接测试");
        }
    }

    private void loadTables() {
        tableModel.setRowCount(0);
        try {
            String driverName = getDriverName();
            Class.forName(driverName);
            try (Connection conn = DriverManager.getConnection(
                    jdbcUrlField.getText(),
                    usernameField.getText(),
                    new String(passwordField.getPassword()))) {

                DatabaseMetaData metaData = conn.getMetaData();
                String schema = schemaNameField.getText();
                if (schema.isEmpty()) {
                    schema = null;
                }

                ResultSet tables = metaData.getTables(null, schema, "%", new String[]{"TABLE"});
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    String remarks = tables.getString("REMARKS");
                    tableModel.addRow(new Object[]{false, tableName, remarks != null ? remarks : ""});
                }

                Messages.showInfoMessage(project, "成功加载 " + tableModel.getRowCount() + " 张表", "加载表");
            }
        } catch (Exception e) {
            Messages.showErrorDialog(project, "加载表失败: " + e.getMessage(), "加载表");
        }
    }

    private void filterTables() {
        // TODO: Implement filter
    }

    private void selectAllTables(boolean selected) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(selected, i, 0);
        }
    }

    private String getDriverName() {
        String dbType = (String) dbTypeCombo.getSelectedItem();
        switch (dbType) {
            case "MySQL": return "com.mysql.cj.jdbc.Driver";
            case "PostgreSQL": return "org.postgresql.Driver";
            case "Oracle": return "oracle.jdbc.driver.OracleDriver";
            case "DM": return "dm.jdbc.driver.DmDriver";
            default: return "com.mysql.cj.jdbc.Driver";
        }
    }

    private List<String> getSelectedTables() {
        List<String> tables = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((Boolean) tableModel.getValueAt(i, 0)) {
                tables.add((String) tableModel.getValueAt(i, 1));
            }
        }
        return tables;
    }

    @Override
    protected void doOKAction() {
        List<String> selectedTables = getSelectedTables();
        if (selectedTables.isEmpty()) {
            Messages.showWarningDialog(project, "请至少选择一张表!", "提示");
            return;
        }

        saveSettings();

        GeneratorConfig config = new GeneratorConfig();
        config.jdbcUrl = jdbcUrlField.getText();
        config.username = usernameField.getText();
        config.password = new String(passwordField.getPassword());
        config.schemaName = schemaNameField.getText();
        config.driverName = getDriverName();
        config.parentPackage = parentPackageField.getText();
        config.moduleName = moduleNameField.getText();
        config.entityPath = entityPathField.getText();
        config.mapperPath = mapperPathField.getText();
        config.mapperXmlPath = mapperXmlPathField.getText();
        config.servicePath = servicePathField.getText();
        config.serviceImplPath = serviceImplPathField.getText();
        config.controllerPath = controllerPathField.getText();
        config.author = authorField.getText();
        config.tables = selectedTables;
        config.tablePrefix = tablePrefixField.getText();
        config.fieldPrefix = fieldPrefixField.getText();
        config.idType = (String) idTypeCombo.getSelectedItem();
        config.swagger2 = swagger2Check.isSelected();
        config.lombok = lombokCheck.isSelected();
        config.restController = restControllerCheck.isSelected();
        config.fileOverride = fileOverrideCheck.isSelected();
        config.jsonFormat = jsonFormatCheck.isSelected();
        config.jsonProperty = jsonPropertyCheck.isSelected();

        GeneratorService service = project.getService(GeneratorService.class);
        try {
            service.generate(config);
            Messages.showInfoMessage(project, "代码生成成功!", "成功");
            super.doOKAction();
        } catch (Exception e) {
            // 打印完整堆栈到控制台便于调试
            e.printStackTrace();
            // 获取根本原因
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            String errorMsg = e.getMessage() + "\n\n根本原因: " + cause.getClass().getSimpleName() + ": " + cause.getMessage();
            Messages.showErrorDialog(project, "代码生成失败: " + errorMsg, "错误");
        }
    }

    private void saveSettings() {
        GeneratorSettings settings = GeneratorSettings.getInstance();
        settings.dbType = (String) dbTypeCombo.getSelectedItem();
        settings.jdbcUrl = jdbcUrlField.getText();
        settings.username = usernameField.getText();
        settings.password = new String(passwordField.getPassword());
        settings.schemaName = schemaNameField.getText();
        settings.parentPackage = parentPackageField.getText();
        settings.moduleName = moduleNameField.getText();
        settings.entityPath = entityPathField.getText();
        settings.mapperPath = mapperPathField.getText();
        settings.mapperXmlPath = mapperXmlPathField.getText();
        settings.servicePath = servicePathField.getText();
        settings.serviceImplPath = serviceImplPathField.getText();
        settings.controllerPath = controllerPathField.getText();
        settings.author = authorField.getText();
        settings.tablePrefix = tablePrefixField.getText();
        settings.fieldPrefix = fieldPrefixField.getText();
        settings.idType = (String) idTypeCombo.getSelectedItem();
        settings.swagger2 = swagger2Check.isSelected();
        settings.lombok = lombokCheck.isSelected();
        settings.restController = restControllerCheck.isSelected();
        settings.fileOverride = fileOverrideCheck.isSelected();
        settings.jsonFormat = jsonFormatCheck.isSelected();
        settings.jsonProperty = jsonPropertyCheck.isSelected();
        settings.driverName = getDriverName();
    }
}
