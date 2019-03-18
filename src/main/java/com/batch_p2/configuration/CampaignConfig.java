package com.batch_p2.configuration;

import com.batch_p2.model.Campaign;
import com.batch_p2.processor.BudgetCheckProcessor;
import com.batch_p2.utils.CampaignRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class CampaignConfig extends DefaultBatchConfigurer implements ApplicationContextAware {
//    @Autowired
//    public JobBuilderFactory jobBuilderFactory;

//    @Autowired
//    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Autowired
    public JobRepository jobRepository;

    @Autowired
    public JobRegistry jobRegistry;

//    @Autowired
//    public JobLauncher jobLauncher;

    @Autowired
    public JobExplorer jobExplorer;

    private ApplicationContext applicationContext;

    @Bean
    @StepScope
    public JdbcPagingItemReader<Campaign> pagingItemReader() {
        JdbcPagingItemReader<Campaign> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CampaignRowMapper());

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, name, statusID, start_date, end_date, budget, bid");
        queryProvider.setFromClause("from campaign");
        queryProvider.setWhereClause("where statusID = 1");

        Map<String, Order> sortKeys = new HashMap<>(1);

        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Campaign> jdbcBatchItemWriter(){
        JdbcBatchItemWriter<Campaign> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(this.dataSource);
//        itemWriter.setSql("INSERT INTO CAMPAIGN (name, statusID, startDate, endDate, budget, bid) VALUES (:name, :statusID, :startDate, :endDate, :budget, :bid)");
        itemWriter.setSql("UPDATE CAMPAIGN SET statusID='2' WHERE id=:id");

        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public BudgetCheckProcessor processor(){
        return new BudgetCheckProcessor();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory){
        return stepBuilderFactory.get("step1")
                .<Campaign, Campaign>chunk(10)
                .reader(pagingItemReader())
                .processor(processor())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory){
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(step1(null))
                .build();
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistrar() throws Exception {
        JobRegistryBeanPostProcessor registrar = new JobRegistryBeanPostProcessor();

        registrar.setJobRegistry(this.jobRegistry);
        registrar.setBeanFactory(this.applicationContext.getAutowireCapableBeanFactory());
        registrar.afterPropertiesSet();

        return registrar;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public JobLauncher getJobLauncher() {
        SimpleJobLauncher jobLauncher = null;
        try {
            jobLauncher = new SimpleJobLauncher();
            jobLauncher.setJobRepository(jobRepository);
            jobLauncher.setTaskExecutor(new SyncTaskExecutor());
            jobLauncher.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobLauncher;
    }

    @Bean
    public JobOperator jobOperator(JobLauncher jobLauncher, JobExplorer jobExplorer) throws Exception {
        SimpleJobOperator simpleJobOperator = new SimpleJobOperator();

        simpleJobOperator.setJobLauncher(jobLauncher);
        simpleJobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
        simpleJobOperator.setJobRepository(this.jobRepository);
        simpleJobOperator.setJobExplorer(jobExplorer);
        simpleJobOperator.setJobRegistry(this.jobRegistry);
        simpleJobOperator.afterPropertiesSet();

        return simpleJobOperator;
    }
}
