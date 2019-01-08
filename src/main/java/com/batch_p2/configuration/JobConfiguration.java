package com.batch_p2.configuration;

import com.batch_p2.model.Customer;
import com.batch_p2.processor.PotentialCustomerItemProcessor;
import com.batch_p2.processor.UpperCaseItemProcessor;
import com.batch_p2.utils.CustomerFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SyncTaskExecutor;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

//    @Bean
//    public FlatFileItemReader<Customer> customerItemReader() {
//        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
//
//        reader.setLinesToSkip(1);
//        reader.setResource(new ClassPathResource("/data/customer.csv"));
//
//        DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
//
//        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
//        tokenizer.setNames(new String[] {"id", "firstName", "lastName", "birthdate"});
//
//        customerLineMapper.setLineTokenizer(tokenizer);
//        customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
//        customerLineMapper.afterPropertiesSet();
//
//        reader.setLineMapper(customerLineMapper);
//
//        return reader;
//    }
//
//    @Bean
//    public JdbcBatchItemWriter<Customer> customerItemWriter() {
//        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
//
//        itemWriter.setDataSource(this.dataSource);
//        itemWriter.setSql("INSERT INTO CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
//        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
//        itemWriter.afterPropertiesSet();
//
//        return itemWriter;
//    }
//
//    @Bean
//    public CompositeItemProcessor<Customer, Customer> compositeItemProcessor() throws Exception{
//        List<ItemProcessor<Customer, Customer>> processorList = new ArrayList<>(2);
//        processorList.add(new PotentialCustomerItemProcessor());
//        processorList.add(new UpperCaseItemProcessor());
//
//        CompositeItemProcessor<Customer, Customer> processor = new CompositeItemProcessor<>();
//        processor.setDelegates(processorList);
//        processor.afterPropertiesSet();
//
//        return processor;
//
//    }
//    @Bean
//    public Step step1() throws Exception{
//        return stepBuilderFactory.get("step1")
//                .<Customer, Customer>chunk(10)
//                .reader(customerItemReader())
//                .processor(compositeItemProcessor())
//                .writer(customerItemWriter())
//                .build();
//    }
//
//    @Bean
//    public Job job() throws Exception{
//        return jobBuilderFactory.get("job")
//                .start(step1())
//                .build();
//    }

    @Bean
    public Job job(@Qualifier("flow1") Flow flow1, @Qualifier("flow2") Flow flow2){
//        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("split");
//
//        Flow flow = flowBuilder.split(new SyncTaskExecutor())
//                .add(flow1, flow2)
//                .end();

        return jobBuilderFactory.get("job")
                .start(flow1)
                .next(flow2)
                .end()
                .build();

    }
}
