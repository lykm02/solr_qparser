package com.tzls.search.solr.func;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

/**
 * Range value source parser.
 * 
 * */
public class RangeValueSourceParser extends ValueSourceParser{

	@Override
	public ValueSource parse(FunctionQParser fp) throws SyntaxError {
		ValueSource variableVs = fp.parseValueSource();
		ValueSource left = fp.parseValueSource();
		ValueSource right = fp.parseValueSource();
		if(fp.hasMoreArguments()){
			Integer leftInc = fp.parseInt();
			Integer rightInc = 1;
			if(fp.hasMoreArguments()){
				rightInc = fp.parseInt();
			}
			return new RangeValueSource(variableVs,left,right,leftInc,rightInc);
		}else{
			return new RangeValueSource(variableVs,left,right);
		}
	}

}
