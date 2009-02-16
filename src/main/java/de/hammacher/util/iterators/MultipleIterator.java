package de.hammacher.util.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MultipleIterator<T> implements Iterator<T> {

	private final Iterator<? extends T>[] iterators;
	private int nextIteratorPos = 1;
	private Iterator<? extends T> currentIterator;

	public MultipleIterator(final Iterator<? extends T>... iterators) {
		this.iterators = iterators;
		this.currentIterator = iterators.length == 0 ? null : iterators[0];
	}

	public boolean hasNext() {
		while (this.currentIterator == null || !this.currentIterator.hasNext()) {
			if (this.nextIteratorPos >= this.iterators.length)
				return false;
			this.currentIterator = this.iterators[this.nextIteratorPos++];
		}
		return true;
	}

	public T next() {
		if (this.currentIterator == null)
			throw new NoSuchElementException();
		return this.currentIterator.next();
	}

	public void remove() {
		if (this.currentIterator == null)
			throw new NoSuchElementException();
		this.currentIterator.remove();
	}

}