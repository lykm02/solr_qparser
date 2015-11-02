package com.tzls.search.solr.func;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public class FalsePriceValueSource extends ValueSource{

	
	@SuppressWarnings("rawtypes")
	@Override
	public FunctionValues getValues(Map context,
			AtomicReaderContext readerContext) throws IOException {
		return new FunctionValues(){
			@Override
			public boolean boolVal(int doc){
				return false;
			}
			
			@Override
			public int intVal(int doc){
				return -1;
			}
			
			@Override
			public long longVal(int doc){
				return -1l;
			}
			
			@Override
			public double doubleVal(int doc){
				return -1d;
			}
			
			@Override
			public float floatVal(int doc){
				return -1f;
			}

			@Override
			public String toString(int doc) {
				return "false";
			}
			
		};
	}

	@Override
	public boolean equals(Object o) {
		return true;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "false price";
	}

}
