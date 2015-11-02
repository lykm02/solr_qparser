package com.tzls.search.solr;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.apache.solr.search.SyntaxError;

import com.tzls.search.solr.generator.TermsQueryGenerator;
import com.tzls.search.solr.model.TokenizedTerm;
import com.tzls.search.solr.util.TermsExtractor;

public class TzlsQueryParser extends SolrQueryParser {

	public TzlsQueryParser(QParser parser, String defaultField) {
		super(parser, defaultField);
	}

	@Override
	protected Query getFieldQuery(String field, String queryText, boolean quoted)
			throws SyntaxError {
		BooleanQuery query = new BooleanQuery();
		query.add(super.getFieldQuery(field, queryText, quoted), Occur.SHOULD);
		System.out.println("TzlsQueryParser.getFieldQuery(String field, String queryText, boolean quoted)== handler "
				+ field+"  " + queryText + " query:["+ query);

		SchemaField sf = schema.getFieldOrNull(field);
		if (sf != null) {
			FieldType ft = sf.getType();
			// delegate to type for everything except tokenized fields
			if (ft.isTokenized() && sf.indexed()) {
				return generateFieldQuery(analyzer, field, queryText, quoted);
			} else {
				return sf.getType().getFieldQuery(parser, sf, queryText);
			}
		}

		// default to a normal field query
		return super.newFieldQuery(analyzer, field, queryText, quoted);
	}

	@Override
	protected Query getFieldQuery(String field, String val, int slop)
			throws SyntaxError {
		System.out.println("TzlsQueryParser.getFieldQuery(String field, String val, int slop)" + val);
		return super.getFieldQuery(field, val, slop);
	}

	// Only need consider flag
	private Query generateFieldQuery(Analyzer analyzer, String field,
			String queryText, boolean quoted) throws SyntaxError {
		System.out.println("dive into generateFieldQuery() " + queryText);
		TokenStream source;
		try {
			source = analyzer.tokenStream(field, queryText);
			source.reset();
		} catch (IOException e) {
			throw new SyntaxError(
					"Unable to initialize TokenStream to analyze query text", e);
		}
		List<TokenizedTerm> terms = null;
		try {
			terms = TermsExtractor.extractTerms(source);
		} catch (IOException e) {
			throw new SyntaxError(
					"IO exception when TokenStream to analyze query text", e);
		} finally {
			try {
				source.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			// close original stream - all tokens buffered
			source.close();
		} catch (IOException e) {
			throw new SyntaxError(
					"Cannot close TokenStream analyzing query text", e);
		}

		if (terms.isEmpty()){
			return new MatchAllDocsQuery();
		}
		
		BooleanQuery query = new BooleanQuery();
		query.add(TermsQueryGenerator.generate(field, terms,
				this.getDefaultOperator()),Occur.SHOULD);
		query.add(generateFullTermQuery(field,queryText) , Occur.SHOULD);
		return query;
	}
	
	private static Query generateFullTermQuery(String fieldName, String queryWords){
		TermQuery regularTermQuery = new TermQuery(new Term(fieldName, queryWords));
		regularTermQuery.setBoost(0.8f);
		return regularTermQuery;
	}
}
