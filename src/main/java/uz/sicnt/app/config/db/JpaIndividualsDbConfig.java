package uz.sicnt.app.config.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    entityManagerFactoryRef = "dbIndividualsEntityManagerFactory",
    transactionManagerRef = "dbIndividualsTransactionManager",
    basePackages = {
            "uz.sicnt.app.repository.individuals"
    }
)
public class JpaIndividualsDbConfig {

    private final Environment environment;

    public JpaIndividualsDbConfig(Environment environment) {
        this.environment = environment;
    }

    @Primary
    @Bean(name = "dbIndividualsDataSource")
    public DataSource dbIndividualsDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("spring.individuals.datasource.url"));
        config.setUsername(environment.getProperty("spring.individuals.datasource.username"));
        config.setPassword(environment.getProperty("spring.individuals.datasource.password"));
        config.setDriverClassName(environment.getProperty("spring.individuals.datasource.driver-class-name"));
        config.setConnectionTimeout(environment.getProperty("spring.individuals.datasource.hikari.connectionTimeout", Long.class, 30000L));
        config.setIdleTimeout(environment.getProperty("spring.individuals.datasource.hikari.idleTimeout", Long.class, 600000L));
        config.setMaxLifetime(environment.getProperty("spring.individuals.datasource.hikari.maxLifetime", Long.class, 1800000L));
        config.setMinimumIdle(environment.getProperty("spring.individuals.datasource.hikari.minimumIdle", Integer.class, 1));
        config.setMaximumPoolSize(environment.getProperty("spring.individuals.datasource.hikari.maximumPoolSize", Integer.class, 10));

        return new HikariDataSource(config);
    }

    @Primary
    @Bean(name = "dbIndividualsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dbIndividualsDataSource") DataSource dbInteractiveDataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dbInteractiveDataSource);
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setPackagesToScan("uz.sicnt.app.domain.individuals");
        em.setPersistenceUnitName("db");   // <- giving 'default' as name

        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean
    public JpaVendorAdapter jpaVendorAdapter(){
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform(environment.getProperty("spring.jpa.database-platform", String.class));
        adapter.setGenerateDdl(false);
        adapter.setShowSql(environment.getProperty("spring.jpa.show-sql", Boolean.class, false));

        return adapter;
    }

    @Primary
    @Bean(name = "dbIndividualsTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("dbIndividualsEntityManagerFactory") EntityManagerFactory db1EntityManagerFactory) {
        return new JpaTransactionManager(db1EntityManagerFactory);
    }

}
