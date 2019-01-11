package com.batch_p2.configuration;

import com.batch_p2.model.Customer;
import com.batch_p2.processor.PotentialCustomerItemProcessor;
import com.batch_p2.processor.UpperCaseItemProcessor;
import com.batch_p2.utils.CustomerFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Flow1Configuration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> flatFileItemReader() throws Exception {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();

        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("/data/customer.csv"));

        DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(new String[]{"firstName", "lastName", "birthdate"});

        customerLineMapper.setLineTokenizer(tokenizer);
        customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
        customerLineMapper.afterPropertiesSet();

        reader.setLineMapper(customerLineMapper);

        return reader;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> jdbcBatchItemWriter() throws Exception {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(this.dataSource);
        itemWriter.setSql("INSERT INTO CUSTOMER (firstName, lastName, birthdate) VALUES (:firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public CompositeItemProcessor<Customer, Customer> compositeItemProcessor() throws Exception {
        List<ItemProcessor<Customer, Customer>> processorList = new ArrayList<>(2);
        processorList.add(new PotentialCustomerItemProcessor());
        processorList.add(new UpperCaseItemProcessor());

        CompositeItemProcessor<Customer, Customer> processor = new CompositeItemProcessor<>();
        processor.setDelegates(processorList);
        processor.afterPropertiesSet();

        return processor;

    }

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get("step1")
                .<Customer, Customer>chunk(100000)
                .reader(flatFileItemReader())
                .processor(compositeItemProcessor())
                .writer(jdbcBatchItemWriter())
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Flow flow1() throws Exception {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow1");

        flowBuilder.start(step1())
                .end();

        return flowBuilder.build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new ScheduledDecider();
    }

    @Bean
    @StepScope
    public Tasklet tasklet(@Value("#{jobParameters['name']}") String name) {
        return (stepContribution, chunkContext) -> {
            System.out.println(String.format("The job ran in %s", name));
            return RepeatStatus.FINISHED;
        };
    }

    public static class ScheduledDecider implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            if (jobExecution.getJobId() != 2) return new FlowExecutionStatus("LAUNCHED");
            else return new FlowExecutionStatus("UNLAUNCHED");
        }
    }

    @Bean
    @StepScope
    public Step step4() {
        return stepBuilderFactory.get("step4")
                .tasklet(tasklet(null))
                .build();
    }

    @Bean
    @StepScope
    public Job launchingJob() throws Exception{
        return jobBuilderFactory.get("launchingJob")
                .incrementer(new RunIdIncrementer())
                .start(step4()).preventRestart()
                .next(decider())
                .on("UNLAUNCHED")
                .to(step1())
                .next(decider())
                .on("LAUNCHED")
                .end()
                .end()
                .build();
    }
}
