package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * ${table.comment!} 服务实现类（独立模式，无接口）
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName}(
    private val ${table.mapperName?uncap_first}: ${table.mapperName}
) {

}
<#else>
public class ${table.serviceImplName} {

    @Autowired
    private ${table.mapperName} ${table.mapperName?uncap_first};

}
</#if>
