package org.springfield.lou.screencomponent.pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PaginatedArrayList<E> extends ArrayList<E> implements
		PaginatedList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int pageSize;
	private int currentPage;
	private boolean endReached = false;
	
	public PaginatedArrayList() {
		super();
		reset();
		// TODO Auto-generated constructor stub
	}

	public PaginatedArrayList(Collection<? extends E> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	public PaginatedArrayList(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public void setPageSize(int size) {
		this.pageSize = size;
	}
	
	public int getPageSize(){
		return this.pageSize;
	}

	@Override
	public List<E> getPage(int page) throws PageOutOfBoundsException {
		// TODO Auto-generated method stub
		this.currentPage = page;
		int actualPage = page - 1;
		int start = actualPage * pageSize;
		int end = start + pageSize;
		
		if(end > this.size()){
			end = this.size();
		}
		
		if(end == this.size()){
			this.endReached = true;
		}
		
		if(start > this.size()){
			throw new PageOutOfBoundsException("Page " + page  + " doesn't exist!");
		}
		return this.subList(start, end);
	}
	
	@Override
	public List<E> nextPage() throws PageOutOfBoundsException{
		return this.getPage(currentPage + 1);
	}
	
	@Override
	public int countPages() {
		double size = (double) this.size();
		double pageSize = (double) this.pageSize;
		return (int) Math.ceil(size / pageSize);
	}

	@Override
	public void reset() {
		this.currentPage = 0;
		this.endReached = false;
	}
	
	public void sortWith(Comparator<E> comparator){
		if(comparator != null){
			Collections.sort(this, comparator);
		}
	}

	@Override
	public boolean endReached() {
		// TODO Auto-generated method stub
		return this.endReached;
	}	
}
