package com.tzls.search.solr.func;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

public class LocaleDateValueSourceParser extends ValueSourceParser{

	@Override
	public ValueSource parse(FunctionQParser fqp) throws SyntaxError {
		ValueSource value1 = fqp.parseValueSource();
		ValueSource value2 = fqp.parseValueSource();
		return new LocaleDateValueSource(value1,value2);
	}

}
