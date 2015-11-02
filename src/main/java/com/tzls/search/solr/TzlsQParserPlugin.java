package com.tzls.search.solr;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

public class TzlsQParserPlugin extends QParserPlugin{
	public static final String NAME = "tzls";
	
	@Override
	public void init(@SuppressWarnings("rawtypes") NamedList args) {
		System.out.println("================Init "+NAME+"============");
		if(args==null){
			System.out.println(NAME + "  " + null);
		}else{
			System.out.println(args);
		}
		System.out.println("================Finish "+NAME+"============");

	}

	@Override
	public QParser createParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {
		return new TzlsQParser(qstr, localParams, params, req);
	}

}
