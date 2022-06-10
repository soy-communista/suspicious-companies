package uz.sicnt.app.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "dbLegalEntitiesEntityManagerFactory",
    transactionManagerRef = "dbLegalEntitiesTransactionManager",
    basePackages = {
        "uz.sicnt.app.repository.legalentities"
    }
)
public class JpaLegalEntitiesDbConfig {

    private final Environment environment;

    public JpaLegalEntitiesDbConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "dbLegalEntitiesDataSource")
    public DataSource dbLegalEntitiesDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("spring.legal-entities.datasource.url"));
        config.setUsername(environment.getProperty("spring.legal-entities.datasource.username"));
        config.setPassword(environment.getProperty("spring.legal-entities.datasource.password"));
        config.setDriverClassName(environment.getProperty("spring.legal-entities.datasource.driver-class-name"));
        config.setConnectionTimeout(environment.getProperty("spring.legal-entities.datasource.hikari.connectionTimeout", Long.class, 30000L));
        config.setIdleTimeout(environment.getProperty("spring.legal-entities.datasource.hikari.idleTimeout", Long.class, 600000L));
        config.setMaxLifetime(environment.getProperty("spring.legal-entities.datasource.hikari.maxLifetime", Long.class, 1800000L));
        config.setMinimumIdle(environment.getProperty("spring.legal-entities.datasource.hikari.minimumIdle", Integer.class, 1));
        config.setMaximumPoolSize(environment.getProperty("spring.legal-entities.datasource.hikari.maximumPoolSize", Integer.class, 10));

        return new HikariDataSource(config);
    }

    @Bean(name = "dbLegalEntitiesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dbLegalEntitiesDataSource") DataSource dbInteractiveDataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dbInteractiveDataSource);
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setPackagesToScan("uz.sicnt.app.domain.legalentities");
        em.setPersistenceUnitName("db");   // <- giving 'default' as name

        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        em.setJpaPropertyMap(properties);

        return em;
    }

    public JpaVendorAdapter jpaVendorAdapter(){
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform(environment.getProperty("spring.jpa.database-platform", String.class));
        adapter.setGenerateDdl(false);
        adapter.setShowSql(environment.getProperty("spring.jpa.show-sql", Boolean.class, false));

        return adapter;
    }

    @Bean(name = "dbLegalEntitiesTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("dbLegalEntitiesEntityManagerFactory") EntityManagerFactory db1EntityManagerFactory) {
        return new JpaTransactionManager(db1EntityManagerFactory);
    }

}
