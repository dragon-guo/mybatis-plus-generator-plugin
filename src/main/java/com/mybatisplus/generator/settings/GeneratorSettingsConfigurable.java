package com.mybatisplus.generator.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 代码生成器设置界面
 *
 * @author generator
 */
public class GeneratorSettingsConfigurable implements Configurable {

    private JPanel mainPanel;

    // 数据库配置组件
    private JComboBox<String> dbTypeCombo;
    private JBTextField jdbcUrlField;
    private JBTextField usernameField;
    private JBPasswordField passwordField;
    private JBTextField schemaNameField;

    // 包配置组件
    private JBTextField parentPackageField;
    private JBTextField authorField;

    // 选项配置
    private JBCheckBox swagger2Check;
    private JBCheckBox lombokCheck;
    private JBCheckBox restControllerCheck;
    private JBCheckBox jsonFormatCheck;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "MyBatis Plus Generator";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        // 数据库类型下拉框
        dbTypeCombo = new JComboBox<>(new String[]{"MySQL", "PostgreSQL", "Oracle", "DM"});

        // 文本输入框
        jdbcUrlField = new JBTextField();
        usernameField = new JBTextField();
        passwordField = new JBPasswordField();
        schemaNameField = new JBTextField();
        parentPackageField = new JBTextField();
        authorField = new JBTextField();

        // 复选框
        swagger2Check = new JBCheckBox("启用 Swagger2 注解");
        lombokCheck = new JBCheckBox("启用 Lombok");
        restControllerCheck = new JBCheckBox("使用 RestController");
        jsonFormatCheck = new JBCheckBox("启用 JsonFormat 日期格式化");

        // 构建表单
        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("数据库类型:"), dbTypeCombo, 1, false)
                .addLabeledComponent(new JBLabel("JDBC URL:"), jdbcUrlField, 1, false)
                .addLabeledComponent(new JBLabel("用户名:"), usernameField, 1, false)
                .addLabeledComponent(new JBLabel("密码:"), passwordField, 1, false)
                .addLabeledComponent(new JBLabel("Schema:"), schemaNameField, 1, false)
                .addSeparator()
                .addLabeledComponent(new JBLabel("父包名:"), parentPackageField, 1, false)
                .addLabeledComponent(new JBLabel("作者:"), authorField, 1, false)
                .addSeparator()
                .addComponent(swagger2Check)
                .addComponent(lombokCheck)
                .addComponent(restControllerCheck)
                .addComponent(jsonFormatCheck)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        GeneratorSettings settings = GeneratorSettings.getInstance();
        return !settings.dbType.equals(dbTypeCombo.getSelectedItem())
                || !settings.jdbcUrl.equals(jdbcUrlField.getText())
                || !settings.username.equals(usernameField.getText())
                || !settings.password.equals(new String(passwordField.getPassword()))
                || !settings.schemaName.equals(schemaNameField.getText())
                || !settings.parentPackage.equals(parentPackageField.getText())
                || !settings.author.equals(authorField.getText())
                || settings.swagger2 != swagger2Check.isSelected()
                || settings.lombok != lombokCheck.isSelected()
                || settings.restController != restControllerCheck.isSelected()
                || settings.jsonFormat != jsonFormatCheck.isSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        GeneratorSettings settings = GeneratorSettings.getInstance();
        settings.dbType = (String) dbTypeCombo.getSelectedItem();
        settings.jdbcUrl = jdbcUrlField.getText();
        settings.username = usernameField.getText();
        settings.password = new String(passwordField.getPassword());
        settings.schemaName = schemaNameField.getText();
        settings.parentPackage = parentPackageField.getText();
        settings.author = authorField.getText();
        settings.swagger2 = swagger2Check.isSelected();
        settings.lombok = lombokCheck.isSelected();
        settings.restController = restControllerCheck.isSelected();
        settings.jsonFormat = jsonFormatCheck.isSelected();

        // 根据数据库类型设置驱动
        switch (settings.dbType) {
            case "MySQL":
                settings.driverName = "com.mysql.cj.jdbc.Driver";
                break;
            case "PostgreSQL":
                settings.driverName = "org.postgresql.Driver";
                break;
            case "Oracle":
                settings.driverName = "oracle.jdbc.driver.OracleDriver";
                break;
            case "DM":
                settings.driverName = "dm.jdbc.driver.DmDriver";
                break;
        }
    }

    @Override
    public void reset() {
        GeneratorSettings settings = GeneratorSettings.getInstance();
        dbTypeCombo.setSelectedItem(settings.dbType);
        jdbcUrlField.setText(settings.jdbcUrl);
        usernameField.setText(settings.username);
        passwordField.setText(settings.password);
        schemaNameField.setText(settings.schemaName);
        parentPackageField.setText(settings.parentPackage);
        authorField.setText(settings.author);
        swagger2Check.setSelected(settings.swagger2);
        lombokCheck.setSelected(settings.lombok);
        restControllerCheck.setSelected(settings.restController);
        jsonFormatCheck.setSelected(settings.jsonFormat);
    }
}
