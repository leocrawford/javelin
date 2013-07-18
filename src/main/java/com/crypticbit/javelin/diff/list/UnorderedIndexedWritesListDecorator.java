package com.crypticbit.javelin.diff.list;

import java.util.*;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * A List which allows views onto it, whereby the views are independent of each other (they can not see each others
 * changes) but the original List maintains a merged view of them all.
 * 
 * @author Leo
 * @param <T>
 */
public class UnorderedIndexedWritesListDecorator<T> implements List<T> {

    private static final Object ROOT_MODE = new Object() {
	public String toString() {
	    return "ROOT";
	}
    };

    public static final class ValidInModePredicate<T> implements Predicate<AddRemoveRecord<T>> {
	private Object mode;

	ValidInModePredicate(Object mode) {
	    this.mode = mode;
	}

	@Override
	public boolean apply(AddRemoveRecord<T> input) {
	    return input.appliesToMode(mode);
	}
    }

    private static class AddRemoveRecord<T> {
	// we don't use String as it could be intern'd

	private Object addedByMode;
	private Set<Object> deletedByModes = new HashSet<Object>();
	private T value;

	public AddRemoveRecord(T value) {
	    this(ROOT_MODE, value);
	}

	public AddRemoveRecord(Object mode, T value) {
	    this.value = value;
	    this.addedByMode = mode;
	}

	public String toString() {
	    return value + " +{" + addedByMode + "} -{" + deletedByModes + "}";
	}

	public boolean appliesToMode(Object currentMode) {
	    // if added by root or current mode, and not removed by current mode (unless it is current is root, in which
	    // case removed by any mode)
	    return (addedByMode == ROOT_MODE || addedByMode == currentMode || currentMode == ROOT_MODE)
		    && !((deletedByModes.size() > 0 && currentMode == ROOT_MODE) || deletedByModes
			    .contains(currentMode));
	}

	public void setAddedByMode(Object mode) {
	    this.addedByMode = mode;

	}

	public void addDeletedByMode(Object mode) {
	    this.deletedByModes.add(mode);

	}

    }

    private final List<AddRemoveRecord<T>> backingList;
    private final Object currentMode;

    public UnorderedIndexedWritesListDecorator(List<T> backingList) {
	this.backingList = new ArrayList<>();
	for (T t : backingList) {
	    this.backingList.add(new AddRemoveRecord<T>(t));
	}
	currentMode = ROOT_MODE;
    }

    private UnorderedIndexedWritesListDecorator(List<AddRemoveRecord<T>> backingList, Object mode) {
	this.backingList = backingList;
	this.currentMode = mode;
    }

    public UnorderedIndexedWritesListDecorator<T> chooseMode(Object mode) {
	if (this.currentMode == mode)
	    return this;
	else
	    return new UnorderedIndexedWritesListDecorator<>(backingList, mode);
    }

    public int transformIndexForInsert(final int index) {
	int i = 0;
	for (int loop = 0; loop < index; loop++) {
	    // System.out.println(">>"+i+","+loop);
	    for (; !backingList.get(loop + i).appliesToMode(currentMode); i++)
		System.out.println("Skipping " + backingList.get(loop + i) + " as does not apply to " + currentMode);
	}
	System.out.println("Returning " + (index + i));
	return index + i;
    }

    public int transformIndexForAccess(final int index) {
	int i = 0;
	for (int loop = 0; loop <= index; loop++) {
	    // System.out.println(">>"+i+","+loop);
	    for (; !backingList.get(loop + i).appliesToMode(currentMode); i++)
		System.out.println("Skipping " + backingList.get(loop + i) + " as does not apply to " + currentMode);
	}
	System.out.println("Returning " + (index + i));
	return index + i;
    }

    private Iterable<T> transform(Iterable<AddRemoveRecord<T>> addRemoveList) {
	return Iterables.transform(addRemoveList, new Function<AddRemoveRecord<T>, T>() {
	    @Override
	    public T apply(AddRemoveRecord<T> input) {
		return input.value;
	    }
	});
    }

    public List<T> transform() {
	return ImmutableList.copyOf(transform(backingList));
    }

    private List<T> filterAndTransform() {
	return ImmutableList.copyOf(transform(Iterables.filter(backingList, new ValidInModePredicate<T>(currentMode))));
    }

    private List<AddRemoveRecord<T>> filter() {
	return ImmutableList.copyOf(Iterables.filter(backingList, new ValidInModePredicate<T>(currentMode)));
    }

    @Override
    public int size() {
	return filterAndTransform().size();
    }

    @Override
    public boolean isEmpty() {
	return filterAndTransform().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
	return filterAndTransform().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
	return filterAndTransform().iterator();
    }

    @Override
    public Object[] toArray() {
	return filterAndTransform().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
	return filterAndTransform().toArray(a);
    }

    @Override
    public boolean add(T e) {
	return backingList.add(new AddRemoveRecord<T>(currentMode, e));
    }

    @Override
    public boolean remove(Object o) {
	for (AddRemoveRecord<T> t : backingList)
	    if (t.value == o && t.appliesToMode(currentMode)) {
		t.deletedByModes.add(currentMode);
		return true;
	    }
	return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
	return filterAndTransform().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
	for (T t : c)
	    add(t);
	return c.size() > 0;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
	List<? extends T> cc = ImmutableList.copyOf(c);
	for (int i = 0; i < c.size(); i++)
	    add(i + index, cc.get(i));
	return c.size() > 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
	boolean result = false;
	for (Object t : c)
	    result = remove(t) | result;
	return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
	throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
	throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
	return filterAndTransform().get(index);
    }

    @Override
    public T set(int index, T element) {
	return filter().get(index).value = element;
    }

    @Override
    public void add(int index, T element) {
	int revisedIndex = transformIndexForInsert(index);
	backingList.add(revisedIndex, new AddRemoveRecord<T>(currentMode, element));
    }

    @Override
    public T remove(int index) {
	int revisedIndex = transformIndexForAccess(index);
	AddRemoveRecord<T> addRemoveRecord = backingList.get(revisedIndex);
	addRemoveRecord.addDeletedByMode(currentMode);
	return addRemoveRecord.value;

    }

    @Override
    public int indexOf(Object o) {
	return filterAndTransform().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
	return filterAndTransform().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
	return filterAndTransform().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
	return filterAndTransform().listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
	return filterAndTransform().subList(fromIndex, toIndex);
    }

    public String toString() {
	return filterAndTransform().toString();
    }

}
