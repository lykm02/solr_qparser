package com.tzls.search.solr.func;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.common.SolrException;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.StrField;

public class TzlsTermFreqFunction extends ValueSource {

	private ValueSource dateText;
	private String field;
	private FieldType ft;
	public TzlsTermFreqFunction(String field,ValueSource dateText,FieldType ft){
		if(dateText == null){
			throw new SolrException(
					SolrException.ErrorCode.BAD_REQUEST,
					"One or more inputs missing for tzls term freq function"
					);
		}
		this.field = field;
		this.dateText = dateText;
		this.ft = ft;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public FunctionValues getValues(Map context,
			AtomicReaderContext readerContext) throws IOException {
		final FunctionValues dateTextField = dateText.getValues(context, readerContext);
		Fields fields = readerContext.reader().fields();
	    final Terms terms = fields.terms(field);

	    return new TzlsIntDocValues(this,dateTextField,field, ft,terms) ;
	}

	@Override
	public boolean equals(Object o) {
		if (this.getClass() != o.getClass()) return false;
		TzlsTermFreqFunction other = (TzlsTermFreqFunction) o;
		return this.dateText.equals(other.dateText)
				&& this.field.equals(other.field);
	}

	@Override
	public int hashCode() {
		long combinedHashes;
		combinedHashes = this.dateText.hashCode()
		+ this.field.hashCode();
		return (int) (combinedHashes ^ (combinedHashes >>> 32));
	}

	@Override
	public String description() {
		return "tzls term freq";
	}

	public static class TzlsIntDocValues extends IntDocValues{
		private WeakHashMap<BytesRef, TzlsDocsEnum> map;
		private FunctionValues fv;
		private String field ;
		private FieldType ft;
		private Terms terms;
		
		public TzlsIntDocValues(ValueSource vs, FunctionValues fv,String field, FieldType ft,Terms terms) {
			super(vs);
			map = new WeakHashMap<BytesRef, TzlsDocsEnum>();
			this.fv = fv;
			this.ft = ft;
			this.terms = terms;
		}

		@Override
		public int intVal(int doc) {
			String val = fv.strVal(doc);
			BytesRef ref = generateFromVal(val,field,ft);
			TzlsDocsEnum docs;
			try{
				if(map.containsKey(ref)){
				docs = map.get(ref);
				}else{
				//init
					docs = new TzlsDocsEnum(terms,ref);
					map.put(ref, docs);
				}
				
				if (doc < docs.lastRequestDocId) {
		            // out-of-order access.... reset
					docs.reset(terms,ref);
		          }
		          docs.lastRequestDocId = doc;

		          if (docs.atDocId < doc) {
		        	  docs.atDocId = docs.docs.advance(doc);
		          }

		          if (docs.atDocId > doc) {
		            // term doesn't match this document... either because we hit the
		            // end, or because the next doc is after this doc.
		            return 0;
		          }

		          // a match!
		          return docs.docs.freq();
			}catch(IOException e){
				throw new RuntimeException("caught exception in function "+super.vs.description()+" : doc="+doc, e);
			}
			
		}
		
	}
	
	public static BytesRef generateFromVal(String val, String field,FieldType ft){
		BytesRef ref  = new BytesRef();
		
	    if (ft == null) ft = new StrField();

	    ft.readableToIndexed(val, ref);
	    return ref;
	    
	}
	
	public static class TzlsDocsEnum {
		// doc iterator
		public DocsEnum docs;
		// from function
		public int lastRequestDocId;
		// docId from doc iterator
		public int atDocId;
		
		public TzlsDocsEnum(Terms terms, BytesRef ref) throws IOException{
			 if (terms != null) {
		          final TermsEnum termsEnum = terms.iterator(null);
		          if (termsEnum.seekExact(ref)) {
		            docs = termsEnum.docs(null, null);
		          } else {
		            docs = null;
		          }
		        } else {
		          docs = null;
		        }
			 if(docs == null){
				 docs = TzlsDocsEnum.emptyDoceEnum;
			 }
			 atDocId =-1;
		}
		
		public static DocsEnum emptyDoceEnum = new DocsEnum(){
			 @Override
	            public int freq() {
	              return 0;
	            }

	            @Override
	            public int docID() {
	              return DocIdSetIterator.NO_MORE_DOCS;
	            }

	            @Override
	            public int nextDoc() {
	              return DocIdSetIterator.NO_MORE_DOCS;
	            }

	            @Override
	            public int advance(int target) {
	              return DocIdSetIterator.NO_MORE_DOCS;
	            }

	            @Override
	            public long cost() {
	              return 0;
	            }
		};
		
		
		public void reset(Terms terms, BytesRef ref) throws IOException{
			 if (terms != null) {
		          final TermsEnum termsEnum = terms.iterator(null);
		          if (termsEnum.seekExact(ref)) {
		            docs = termsEnum.docs(null, null);
		          } else {
		            docs = null;
		          }
		        } else {
		          docs = null;
		        }
			 if(docs == null){
				 docs = TzlsDocsEnum.emptyDoceEnum;
			 }
			 atDocId =-1;
		}
	}

}
