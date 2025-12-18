package com.mybatisplus.generator.service;

import com.mybatisplus.generator.config.GeneratorConfig;

/**
 * 代码生成器服务接口
 *
 * @author generator
 */
public interface GeneratorService {

    /**
     * 生成代码
     *
     * @param config 生成器配置
     */
    void generate(GeneratorConfig config);
}
