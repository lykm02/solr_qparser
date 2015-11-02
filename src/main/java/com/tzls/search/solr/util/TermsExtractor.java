package com.tzls.search.solr.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import com.tzls.search.solr.model.TokenizedTerm;

public class TermsExtractor {
	
	public static List<TokenizedTerm> extractTerms(TokenStream stream)
			throws IOException {
		CharTermAttribute termAtt = stream
				.getAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAttr = stream.hasAttribute(OffsetAttribute.class) ? stream
				.getAttribute(OffsetAttribute.class) : null;
		TypeAttribute termTypeAttr = stream
				.hasAttribute(TypeAttribute.class) ? stream
				.getAttribute(TypeAttribute.class) : null;
		FlagsAttribute flagsAttr = stream.hasAttribute(FlagsAttribute.class)?stream.getAttribute(FlagsAttribute.class):null;
		List<TokenizedTerm> terms = new ArrayList<TokenizedTerm>();
		while (stream.incrementToken()) {
			TokenizedTerm term = new TokenizedTerm();
			term.termAtt = termAtt.toString();
			
			if (offsetAttr != null) {
				term.offsetAtt = copyFrom(offsetAttr);
			}
			
			if (termTypeAttr != null) {
				term.type = termTypeAttr.type();
			}
			
			if(flagsAttr!=null){
				term.flag = flagsAttr.getFlags();
			}
			terms.add(term);
		}
		return terms;
	}

	private static OffsetAttribute copyFrom(OffsetAttribute offsetAttr) {
		OffsetAttribute copiedOffsetAttr = new OffsetAttributeImpl();
		copiedOffsetAttr.setOffset(offsetAttr.startOffset(),
				offsetAttr.endOffset());
		return copiedOffsetAttr;
	}
	
}
