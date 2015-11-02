package com.tzls.search.solr.func;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

/**
 * 
 * 
 * */
public class SalePriceValueSourceParser extends ValueSourceParser{

	@Override
	public ValueSource parse(FunctionQParser fp) throws SyntaxError {
		ValueSource salePrice = fp.parseValueSource();
		List<ValueSource> vsList = fp.parseValueSourceList();
		return null;
	}

	static class SalePriceValueSource extends ValueSource{
		
		public SalePriceValueSource(ValueSource salePriceVs, List<ValueSource> vsList){
			
		}
		
		@Override
		public FunctionValues getValues(Map context,
				AtomicReaderContext readerContext) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean equals(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String description() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}

