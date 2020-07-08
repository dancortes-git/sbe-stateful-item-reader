package br.com.dcc.springbatchexamples.reader;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class StatefulItemReader implements ItemStreamReader<String> {

	private final List<String> items;
	private int curIndex = -1;
	private boolean restart = false;

	public StatefulItemReader(List<String> items) {
		this.items = items;
		this.curIndex = 0;
	}

	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		log.info(">> Running read method");
		String item = null;

		if (this.curIndex < this.items.size()) {
			item = this.items.get(this.curIndex);
			this.curIndex++;
		}

		if (this.curIndex == 42 && !restart) {
			throw new RuntimeException("The Answer to the ultimate question of life, the universe, and everything...");
		}

		return item;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		log.info(">> Running open method");
		if (executionContext.containsKey("curIndex")) {
			this.curIndex = executionContext.getInt("curIndex");
			this.restart = true;
		} else {
			this.curIndex = 0;
			executionContext.put("curIndex", this.curIndex);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		log.info(">> Running update method");
		executionContext.put("curIndex", this.curIndex);
	}

	@Override
	public void close() throws ItemStreamException {
		log.info(">> Running close method");
	}

}
