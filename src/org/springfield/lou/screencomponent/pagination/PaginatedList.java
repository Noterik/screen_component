package org.springfield.lou.screencomponent.pagination;

import java.util.Comparator;
import java.util.List;

public interface PaginatedList<E> extends List<E> {
	public void setPageSize(int size);
	public int getPageSize();
	public List<E> getPage(int page) throws PageOutOfBoundsException;
	public List<E> nextPage() throws PageOutOfBoundsException;
	public int countPages();
	public void reset();
	public boolean endReached();
	public void sortWith(Comparator<E> comparator);
}
