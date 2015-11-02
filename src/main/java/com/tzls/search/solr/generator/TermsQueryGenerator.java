package com.tzls.search.solr.generator;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.parser.QueryParser.Operator;

import com.tzls.search.solr.model.TokenizedTerm;
/**
 * 
 * */
public class TermsQueryGenerator {
	
	public static Query generate(String fieldName, List<TokenizedTerm> terms, Operator operator){
		if(terms.isEmpty()){
			return new MatchAllDocsQuery();
		}
		BooleanQuery query = new BooleanQuery();
		Query strictQuery = strictQuery(fieldName,terms);
		query.add(strictQuery, Occur.SHOULD);

		if(!containsWeakTypeWords(terms) || operator == Operator.AND){
			return query;
		}
		strictQuery.setBoost(2.0f);
		Query customizedQuery = custormizedQuery(fieldName,terms);
		customizedQuery.setBoost(1.1f);
		Query relaxQuery = relaxQuery(fieldName,terms);
		relaxQuery.setBoost(0.5f);
		
		query.add(customizedQuery, Occur.SHOULD);
		query.add(relaxQuery, Occur.SHOULD);
		return query;
	}
	
	
	private static boolean containsWeakTypeWords(List<TokenizedTerm> terms){
		for(TokenizedTerm term : terms){
			if(term.flag!=null && term.flag > 0){
				return true;
			}
		}
		return false;
	}
	
	private static Query strictQuery(String fieldName, List<TokenizedTerm> terms){
		Query query = generateFirstTokenQuery(fieldName,terms.get(0));
		if(terms.size() == 1){
			return query;
		}
		BooleanQuery bq = new BooleanQuery();
		bq.add(query, Occur.MUST);
		for(int i=1;i<terms.size();i++){
//			if(terms.get(i).termAtt.length() < 2){
//				bq.add(generateTermQuery(fieldName, terms.get(i)), Occur.SHOULD);
//			}else{
				bq.add(generateTermQuery(fieldName,terms.get(i)), Occur.MUST);
//			}
		}
		return bq;
	}
	
	private static Query custormizedQuery(String fieldName, List<TokenizedTerm> terms){
		Query query = generateFirstTokenQuery(fieldName,terms.get(0));
		if(terms.size() == 1){
			return query;
		}
		BooleanQuery bq = new BooleanQuery();
		bq.add(query, Occur.MUST);
		for(int i=1;i<terms.size();i++){
			TokenizedTerm term= terms.get(i);
			if(term.flag!=null && term.flag > 0){
				bq.add(generateTermQuery(fieldName, term), Occur.SHOULD);
			}if(term.termAtt.length() < 2){
				bq.add(generateTermQuery(fieldName, term), Occur.SHOULD);
			}
			else{
				bq.add(generateTermQuery(fieldName, term), Occur.MUST);
			}
		}
		return bq;
	}
	
	private static Query relaxQuery(String fieldName, List<TokenizedTerm> terms){
		Query query = generateFirstTokenQuery(fieldName,terms.get(0));
		if(terms.size() == 1){
			return query;
		}
		BooleanQuery bq = new BooleanQuery();
		bq.add(query, Occur.SHOULD);
		for(int i=1;i<terms.size();i++){
			bq.add(generateTermQuery(fieldName,terms.get(i)), Occur.SHOULD);
		}
		return bq;
	}
	
	
	private static Query generateFirstTokenQuery(String fieldName, TokenizedTerm term){
		BooleanQuery bq = new BooleanQuery();
		if("en".equals(term.type)){
			TermQuery fullTermQuery = new TermQuery(new Term(fieldName, "^"+term.termAtt+"$"));
			fullTermQuery.setBoost(1.1f);
			TermQuery prefixTermQuery = new TermQuery(new Term(fieldName, "^"+term.termAtt));
			prefixTermQuery.setBoost(0.9f);
			TermQuery preTermQuery = new TermQuery(new Term(fieldName, term.termAtt+"$"));
			preTermQuery.setBoost(0.75f);
			TermQuery regularTermQuery = new TermQuery(new Term(fieldName, term.termAtt));
			regularTermQuery.setBoost(0.7f);
			
			bq.add(fullTermQuery, Occur.SHOULD);
			bq.add(prefixTermQuery, Occur.SHOULD);
			bq.add(regularTermQuery, Occur.SHOULD);
			bq.add(preTermQuery, Occur.SHOULD);
		}else{
			TermQuery prefixTermQuery = new TermQuery(new Term(fieldName, "^"+term.termAtt));
			prefixTermQuery.setBoost(1.2f);
			
			TermQuery regularTermQuery = new TermQuery(new Term(fieldName, term.termAtt));
			regularTermQuery.setBoost(0.8f);
			
			bq.add(prefixTermQuery, Occur.SHOULD);
			bq.add(regularTermQuery, Occur.SHOULD);
		}
		return bq;
	}
	
	private static Query generateTermQuery(String fieldName, TokenizedTerm term){
		BooleanQuery bq = new BooleanQuery();
		if("en".equals(term.type)){
			TermQuery fullTermQuery = new TermQuery(new Term(fieldName, term.termAtt+"$"));
			fullTermQuery.setBoost(1.1f);
			TermQuery prefixTermQuery = new TermQuery(new Term(fieldName, term.termAtt));
			prefixTermQuery.setBoost(0.9f);
			
			bq.add(fullTermQuery, Occur.SHOULD);
			bq.add(prefixTermQuery, Occur.SHOULD);
		}else{
			TermQuery regularTermQuery = new TermQuery(new Term(fieldName, term.termAtt));
			regularTermQuery.setBoost(0.8f);
			return regularTermQuery;
		}
		return bq;
	}
	
	

}
