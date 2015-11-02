package com.tzls.search.solr;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SolrQueryParser;
import org.apache.solr.search.SyntaxError;
/**
 * Refer to LuceneQParser
 * 
 * */
public class TzlsQParser extends QParser{
	private SolrQueryParser lparser;

	public TzlsQParser(String qstr, SolrParams localParams, SolrParams params,
			SolrQueryRequest req) {
		super(qstr, localParams, params, req);
	}


	  @Override
	  public Query parse() throws SyntaxError {
	    String qstr = getString();
	    if (qstr == null || qstr.length()==0) return null;

	    String defaultField = getParam(CommonParams.DF);
	    if (defaultField==null) {
	      defaultField = getReq().getSchema().getDefaultSearchFieldName();
	    }
	    lparser = new TzlsQueryParser(this, defaultField);

	    lparser.setDefaultOperator
	      (QueryParsing.getQueryParserDefaultOperator(getReq().getSchema(),
	                                                  getParam(QueryParsing.OP)));

	    return lparser.parse(qstr);
	  }


	  @Override
	  public String[] getDefaultHighlightFields() {
	    return lparser == null ? new String[]{} : new String[]{lparser.getDefaultField()};
	  }


}
