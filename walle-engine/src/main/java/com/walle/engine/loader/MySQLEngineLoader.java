package com.walle.engine.loader;

import com.alibaba.fastjson2.JSONObject;
import com.walle.engine.domain.entity.FlowEngineDO;
import com.walle.engine.domain.model.FlowDSL;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.List;

/**
 * @author harley.shi
 * @date 2025/1/23
 */
public class MySQLEngineLoader implements EngineLoader {

    private final SqlSessionFactory sqlSessionFactory;

    public MySQLEngineLoader(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("default", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMappers("com.walle.engine.loader.mapper");
        xmlMapperParser(configuration, "classpath*:mapper/*.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    private void xmlMapperParser(Configuration configuration, String xmlPath) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(xmlPath);
            for (Resource resource : resources) {
                try (InputStream inputStream = resource.getInputStream()) {
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource.toString(), configuration.getSqlFragments());
                    mapperParser.parse();
                }
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    /**
     * load all engine list from database
     */
    @Override
    public List<FlowDSL> loadAllEngineList() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            List<FlowEngineDO> engineList = sqlSession.selectList("com.walle.engine.loader.mapper.FlowEngineMapper.listAll");
            return engineList.stream()
                    .map(this::engineDoConvertToFlowDSL)
                    .toList();
        }
    }

    /**
     * load published engine list from database
     */
    @Override
    public List<FlowDSL> loadPublishedEngines() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            List<FlowEngineDO> engineList = sqlSession.selectList("com.walle.engine.loader.mapper.FlowEngineMapper.getPublishedEngines");
            return engineList.stream()
                    .map(this::engineDoConvertToFlowDSL)
                    .toList();
        }
    }

    /**
     * load engine by name from database
     * @param engineName engine name
     */
    @Override
    public FlowDSL getEngineByName(String engineName) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            FlowEngineDO engineDO = sqlSession.selectOne("com.walle.engine.loader.mapper.FlowEngineMapper.getEngineByName", engineName);
            return engineDoConvertToFlowDSL(engineDO);
        }
    }

    /**
     * convert engineDO to flowDSL
     */
    private FlowDSL engineDoConvertToFlowDSL(FlowEngineDO flowEngineDO) {
        try {
            FlowDSL flowDSL = new FlowDSL();
            flowDSL.setId(flowEngineDO.getId());
            flowDSL.setName(flowEngineDO.getName());
            flowDSL.setDescription(flowEngineDO.getDescription());
            flowDSL.setContent(JSONObject.parseObject(flowEngineDO.getContent(), FlowDSL.Graph.class));
            flowDSL.setVersion(flowEngineDO.getVersion());
            flowDSL.setExecutionMode(flowEngineDO.getExecutionMode());
            return flowDSL;
        } catch (Exception e) {
            throw new RuntimeException(String.format("[%s] parse error", flowEngineDO.getName()), e);
        }
    }
}
