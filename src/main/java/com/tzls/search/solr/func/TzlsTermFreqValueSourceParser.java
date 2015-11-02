package com.tzls.search.solr.func;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.StrField;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

public class TzlsTermFreqValueSourceParser extends ValueSourceParser{

	@Override
	public ValueSource parse(FunctionQParser fqp) throws SyntaxError {
		String fieldName = fqp.parseArg();
		ValueSource value2 = fqp.parseValueSource();
		FieldType ft = fqp.getReq().getSchema().getFieldTypeNoEx(fieldName);
		if (ft == null) ft = new StrField();
		return new TzlsTermFreqFunction(fieldName,value2,ft);
	}

}
