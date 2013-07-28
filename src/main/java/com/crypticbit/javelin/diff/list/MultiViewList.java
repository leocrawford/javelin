package com.crypticbit.javelin.diff.list;

import java.util.*;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * A List which allows views onto it, whereby the views are independent of each other (they can not see each others
 * changes) but the original List maintains a merged view of them all. We call these views, modes. This means that we
 * wan re-order read and writes in different modes safe in the knowledge they won't affect each other - and the merged
 * view is consistent.
 * <p>
 * This is not current threadsafe.
 * 
 * @author Leo
 * @param <T>
 */
public class MultiViewList<T> implements List<T> {

    private static final Object MERGED_MODE = new Object() {

	@Override
	public String toString() {
	    return "MERGED";
	}
    };

    private final List<AddRemoveRecord<T>> backingList;

    private final Object currentMode;

    /**
     * The only public constructor. Given a starting list (which can be empty) we create a wrapper, which allows new
     * modes to be entered. By default we are in the merged mode where we see the ongoing impact of merges from other
     * modes.
     */
    public MultiViewList(List<T> backingList) {
	this.backingList = new ArrayList<>();
	for (T t : backingList) {
	    this.backingList.add(new AddRemoveRecord<T>(t));
	}
	currentMode = MERGED_MODE;
    }

    /** Create a view on an existing MultiViewList */
    private MultiViewList(List<AddRemoveRecord<T>> backingList, Object mode) {
	this.backingList = backingList;
	this.currentMode = mode;
    }

    @Override
    public void add(int index, T element) {
	int revisedIndex = transformIndexForInsert(index);
	backingList.add(revisedIndex, new AddRemoveRecord<T>(currentMode, element));
    }

    @Override
    public boolean add(T e) {
	return backingList.add(new AddRemoveRecord<T>(currentMode, e));
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
	for (T t : c) {
	    add(t);
	}
	return c.size() > 0;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
	List<? extends T> cc = ImmutableList.copyOf(c);
	for (int i = 0; i < c.size(); i++) {
	    add(i + index, cc.get(i));
	}
	return c.size() > 0;
    }

    @Override
    public void clear() {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
	return filterAndTransform().contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
	return filterAndTransform().containsAll(c);
    }

    @Override
    public T get(int index) {
	return filterAndTransform().get(index);
    }

    /**
     * Create a new view on this List. It is legitimate (but possibly not resource wise) to create multiple views with
     * the same name. It is fine to create a view off a view - this is semantically equivalent to producing it off the
     * root
     */
    public MultiViewList<T> getMode(Object mode) {
	if (this.currentMode == mode) {
	    return this;
	}
	else {
	    return new MultiViewList<>(backingList, mode);
	}
    }

    @Override
    public int indexOf(Object o) {
	return filterAndTransform().indexOf(o);
    }

    @Override
    public boolean isEmpty() {
	return filterAndTransform().isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
	return filterAndTransform().iterator();
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
    public T remove(int index) {
	int revisedIndex = transformIndexForAccess(index);
	AddRemoveRecord<T> addRemoveRecord = backingList.get(revisedIndex);
	addRemoveRecord.addDeletedByMode(currentMode);
	return addRemoveRecord.value;

    }

    @Override
    public boolean remove(Object o) {
	for (AddRemoveRecord<T> t : backingList) {
	    if (t.value == o && t.appliesToMode(currentMode)) {
		t.addDeletedByMode(currentMode);
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
	boolean result = false;
	for (Object t : c) {
	    result = remove(t) | result;
	}
	return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
	throw new UnsupportedOperationException();
    }

    @Override
    public T set(int index, T element) {
	return filter().get(index).value = element;
    }

    @Override
    public int size() {
	return filterAndTransform().size();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
	return filterAndTransform().subList(fromIndex, toIndex);
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
    public String toString() {
	return filterAndTransform().toString() + " in view " + currentMode + " = " + filterAndTransform();
    }

    /**
     * Works out which list elements between 0 and index aren't visible, and should thus be ignored - giving us a new
     * index.
     */
    public int transformIndexForAccess(final int index) {
	int i = 0;
	for (int loop = 0; loop <= index; loop++) {
	    for (; !backingList.get(loop + i).appliesToMode(currentMode); i++) {
		;
	    }
	}
	return index + i;
    }

    /**
     * Works out which list elements between 0 and index aren't visible, and should thus be ignored - giving us a new
     * index. This version assumed that a insert before element n, is actually a write after element n-1. This makes a
     * difference when there are elements between n-1 and n that are not visible in this view.
     */
    public int transformIndexForInsert(final int index) {
	int i = 0;
	for (int loop = 0; loop < index; loop++) {
	    for (; !backingList.get(loop + i).appliesToMode(currentMode); i++) {
		;
	    }
	}
	return index + i;
    }

    /**
     * Return a list with elements not applying to this mode removed.
     */
    private List<AddRemoveRecord<T>> filter() {
	return ImmutableList.copyOf(Iterables.filter(backingList, new ValidInModePredicate<T>(currentMode)));
    }

    /**
     * Return a list with elements not applying to this mode removed, and only the value returned instead of teh full
     * AddRemoveRecord
     */
    private List<T> filterAndTransform() {
	return ImmutableList.copyOf(transform(Iterables.filter(backingList, new ValidInModePredicate<T>(currentMode))));
    }

    /** Return only the value from an Iterable of AddRemoveRecords. */
    private Iterable<T> transform(Iterable<AddRemoveRecord<T>> addRemoveList) {
	return Iterables.transform(addRemoveList, new Function<AddRemoveRecord<T>, T>() {
	    @Override
	    public T apply(AddRemoveRecord<T> input) {
		return input.value;
	    }
	});
    }

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

	private final Object addedByMode;
	private final Set<Object> deletedByModes = new HashSet<Object>();
	private T value;

	public AddRemoveRecord(Object mode, T value) {
	    this.value = value;
	    this.addedByMode = mode;
	}

	public AddRemoveRecord(T value) {
	    this(MERGED_MODE, value);
	}

	public void addDeletedByMode(Object mode) {
	    this.deletedByModes.add(mode);

	}

	public boolean appliesToMode(Object currentMode) {
	    // if added by root or current mode, and not removed by current mode (unless it is current is root, in which
	    // case removed by any mode)
	    return (addedByMode == MERGED_MODE || addedByMode == currentMode || currentMode == MERGED_MODE)
		    && !((deletedByModes.size() > 0 && currentMode == MERGED_MODE) || deletedByModes
			    .contains(currentMode));
	}

	@Override
	public String toString() {
	    return value + " +{" + addedByMode + "} -{" + deletedByModes + "}";
	}

    }

}
