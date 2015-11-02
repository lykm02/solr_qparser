package com.tzls.search.solr.model;

import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/**
 * Model the TokenizedTerm when query.
 * */
public class TokenizedTerm {
	public OffsetAttribute offsetAtt;

	public String termAtt;
	public String type;
	// light
	public Integer flag;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("term:[" + termAtt + "]");
		if (offsetAtt == null) {
			builder.append(" offset:[ NULL ]");
		} else {
			builder.append(" offset:[" + offsetAtt.toString() + "]");
		}
		builder.append(" type:[" + type + "]");
		builder.append("flag:[" + flag + "]");
		return builder.toString();
	}
}
