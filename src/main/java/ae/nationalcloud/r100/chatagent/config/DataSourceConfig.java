package ae.nationalcloud.r100.chatagent.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    // Default datasource (used by JPA repositories)
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties defaultDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource defaultDataSource(@Qualifier("defaultDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    @Bean(name = "pgvectorJdbcTemplate")
    public JdbcTemplate pgvectorJdbcTemplate(@Qualifier("pgVectorDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }


    @Bean
    @ConfigurationProperties(prefix = "spring.pgvectordatasource")
    public DataSourceProperties pgVectorDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "pgVectorDataSource")
    public DataSource pgVectorDataSource(@Qualifier("pgVectorDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }
}
