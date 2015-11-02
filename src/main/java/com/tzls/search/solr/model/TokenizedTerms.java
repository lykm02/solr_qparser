package com.tzls.search.solr.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TokenizedTerms implements List<TokenizedTerm>{
	private List<TokenizedTerm> termList;
	public float weight;
	public TokenizedTerms(List<TokenizedTerm> source){
		termList = source;
		weight = 1.0f;
	}
	
	public TokenizedTerms(){
		termList = new ArrayList<TokenizedTerm>();
		weight = 1.0f;
	}

	@Override
	public int size() {
		return termList.size();
	}

	@Override
	public boolean isEmpty() {
		return termList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return termList.contains(o);
	}

	@Override
	public Iterator<TokenizedTerm> iterator() {
		
		return termList.iterator();
	}

	@Override
	public Object[] toArray() {
		return termList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return termList.toArray(a);
	}

	@Override
	public boolean add(TokenizedTerm e) {
		return termList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return termList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return termList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends TokenizedTerm> c) {
		return termList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends TokenizedTerm> c) {
		return termList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return termList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return termList.retainAll(c);
	}

	@Override
	public void clear() {
		termList.clear();
	}

	@Override
	public TokenizedTerm get(int index) {
		return termList.get(index);
	}

	@Override
	public TokenizedTerm set(int index, TokenizedTerm element) {
		return termList.set(index, element);
	}

	@Override
	public void add(int index, TokenizedTerm element) {
		termList.add(index, element);
	}

	@Override
	public TokenizedTerm remove(int index) {
		return termList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return termList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return termList.lastIndexOf(o);
	}

	@Override
	public ListIterator<TokenizedTerm> listIterator() {
		return termList.listIterator();
	}

	@Override
	public ListIterator<TokenizedTerm> listIterator(int index) {
		return termList.listIterator(index);
	}

	@Override
	public List<TokenizedTerm> subList(int fromIndex, int toIndex) {
		return termList.subList(fromIndex, toIndex);
	}
	
	@Override
	public String toString(){
		return "Terms: "+ termList.toString();
	}
	
}
