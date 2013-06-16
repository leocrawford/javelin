package com.crypticbit.javelin.js;

import java.util.*;
import java.util.Map.Entry;

/**
 * Allows a set of add and remove operations to a list, all based on the original indexes. Thus if we have the list
 * <code>a,b,c,d</code> and then delete index 1 we have <code>a,c,d</code>. A subsequent operation that deletes index 2
 * would then end up deleting </code>d</code> instead of the <code>c</code> which may have been intended.
 * <p>
 * This class isn't semantically strong (I'm sure it's easy to break the intended behaviour). What it will do is for any
 * <code>add</code> or <code>remove</code> operation that has an index, it will remember the change such that subsequent
 * <code>add</code> and <code>remove</code> operations with an index apply to the old index pattern. All otehr
 * operations are unaffected.
 * 
 * @author Leo
 * @param <T>
 */
public class UnorderedIndexedWritesListDecorator<T> implements List<T> {

    private List<T> backingList;
    private Map<Object, Map<Integer, Integer>> indexShifts = new HashMap<>();
    private Map<Integer, Integer> indexShift;

    public UnorderedIndexedWritesListDecorator(List<T> backingList, Set<Object> modes) {
	this.backingList = backingList;
	for (Object mode : modes) {
	    indexShifts.put(mode, new TreeMap<Integer, Integer>());
	}
    }

    public void chooseMode(Object mode) {
	if (indexShifts.containsKey(mode))
	    indexShift = indexShifts.get(mode);
	else
	    throw new IllegalArgumentException("Not a valid mode. Choose from: " + indexShifts);
    }

    private int transformIndex(final int index) {
	int result = index;
	for (Entry<Integer, Integer> x : indexShift.entrySet()) {
	    if (x.getKey() <= index)
		result += x.getValue();
	    else
		break;
	}
	return result;
    }

    private void addIndexTransformation(int atIndex, int amount) {
	for (Map<Integer, Integer> t : indexShifts.values())
	    if (t != indexShift)
		if (t.containsKey(atIndex)) {
		    t.put(atIndex, t.get(atIndex) + amount);
		}
		else
		    t.put(atIndex, amount);
    }

    @Override
    public int size() {
	return backingList.size();
    }

    @Override
    public boolean isEmpty() {
	return backingList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
	return backingList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
	return backingList.iterator();
    }

    @Override
    public Object[] toArray() {
	return backingList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
	return backingList.toArray(a);
    }

    @Override
    public boolean add(T e) {
	return backingList.add(e);
    }

    @Override
    public boolean remove(Object o) {
	return backingList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
	return backingList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
	return backingList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
	int revisedIndex = transformIndex(index);
	addIndexTransformation(index, c.size());
	return backingList.addAll(revisedIndex, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
	return backingList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
	return backingList.retainAll(c);
    }

    @Override
    public void clear() {
	backingList.clear();
    }

    @Override
    public T get(int index) {
	return backingList.get(index);
    }

    @Override
    public T set(int index, T element) {
	return backingList.set(index, element);
    }

    @Override
    public void add(int index, T element) {
	int revisedIndex = transformIndex(index);
	backingList.add(revisedIndex, element);
	addIndexTransformation(index, 1);
    }

    @Override
    public T remove(int index) {
	int revisedIndex = transformIndex(index);
	addIndexTransformation(index, -1);
	return backingList.remove(revisedIndex);
    }

    @Override
    public int indexOf(Object o) {
	return backingList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
	return backingList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
	return backingList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
	return backingList.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
	return backingList.subList(fromIndex, toIndex);
    }

    public String toString() {
	return backingList.toString();
    }

}
