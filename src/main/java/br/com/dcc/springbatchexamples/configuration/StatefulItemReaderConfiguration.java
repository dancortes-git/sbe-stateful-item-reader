package br.com.dcc.springbatchexamples.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.dcc.springbatchexamples.reader.StatefulItemReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class StatefulItemReaderConfiguration {

	@Bean
	public StatefulItemReader statefulItemReader() {
		List<String> items = new ArrayList<>(100);

		for (int i = 1; i <= 100; i++) {
			items.add(String.valueOf(i));
		}

		return new StatefulItemReader(items);
	}

	@Bean
	public ItemWriter<String> statefulItemWriter() {
		return new ItemWriter<String>() {
			@Override
			public void write(List<? extends String> items) throws Exception {
				for (String item : items) {
					log.info(">> {}", item);
				}
			}
		};
	}
	@Bean
	public Step statefulItemReaderStep1(StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("StatefulItemReaderStep1")
				.<String, String>chunk(10)
				.reader(statefulItemReader())
				.writer(statefulItemWriter())
				.stream(statefulItemReader())
				.build();
	}

	@Bean
	public Job statefulItemReaderJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		return jobBuilderFactory.get("StatefulItemReaderJob")
				.start(statefulItemReaderStep1(stepBuilderFactory))
				.build();

	}

}
