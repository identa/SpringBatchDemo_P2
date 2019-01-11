package com.batch_p2.configuration;

import com.batch_p2.model.Customer;
import com.batch_p2.partitioner.ColumnRangePartitioner;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;

import javax.sql.DataSource;

@Configuration
public class Flow2Configuration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    private Resource outputResource = new FileSystemResource("output/outputData.csv");

    @Bean
    @StepScope
    public JdbcCursorItemReader<Customer> jdbcCursorItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) throws Exception, JobExecutionException {
        System.out.println("reading " + minValue + " to " + maxValue +" in " + Thread.currentThread());

        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(100000);
        reader.setRowMapper((resultSet, i) ->
                new Customer(
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getDate("birthdate")));
        reader.setSql("select firstName, lastName, birthdate from customer");
        return reader;
    }

    @Bean
    public FlatFileItemWriter<Customer> flatFileItemWriter() throws Exception {
        FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();

        itemWriter.setResource(outputResource);
        itemWriter.setAppendAllowed(true);
        DelimitedLineAggregator<Customer> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);
        BeanWrapperFieldExtractor<Customer> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"firstName", "lastName", "birthdate"});
        aggregator.setFieldExtractor(extractor);
        itemWriter.setLineAggregator(aggregator);
        itemWriter.setHeaderCallback(writer -> writer.write("firstName,lastName,birthdate"));

        return itemWriter;
    }

    @Bean
    public ColumnRangePartitioner partitioner() {
        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();

        columnRangePartitioner.setColumn("id");
        columnRangePartitioner.setDataSource(this.dataSource);
        columnRangePartitioner.setTable("customer");

       return columnRangePartitioner;
    }

    @Bean
    public Step step2() throws Exception {
        return stepBuilderFactory.get("step2")
                .<Customer, Customer>chunk(100000)
                .reader(jdbcCursorItemReader(null, null))
                .writer(flatFileItemWriter())
                .build();
    }

    @Bean
    public Step step3() throws Exception {
        return stepBuilderFactory.get("step3")
                .partitioner(step2().getName(), partitioner())
                .step(step2())
                .gridSize(20)
                .taskExecutor(new SyncTaskExecutor())
                .build();
    }

    @Bean
    public Flow flow2() throws Exception{
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow2");

        flowBuilder
                .start(step3())
                .end();

        return flowBuilder.build();
    }
}
