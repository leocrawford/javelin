package com.crypticbit.javelin.js;

import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JList;

import com.crypticbit.javelin.store.Digest;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

public class LazyJsonArray extends AbstractList<Object> {

	private List<Digest> backingList;
	private DereferencedCasAccessInterface dereferencedCasAccessInterface;

	public LazyJsonArray(
			DereferencedCasAccessInterface dereferencedCasAccessInterface,
			List<Digest> backingList) {
		this.dereferencedCasAccessInterface = dereferencedCasAccessInterface;
		this.backingList = backingList;
	}

	@Override
	public Object get(int index) {
		try {
			return dereferencedCasAccessInterface.readAsObjects(backingList
					.get(index));
		} catch (Exception e) {
			// FIXME
			throw new Error(e);
		}
	}

	@Override
	public int size() {
		return backingList.size();
	}

	public Patch diff(LazyJsonArray them) {
		return DiffUtils.diff(backingList, them.backingList);
	}

	public void patch(Patch patch) throws PatchFailedException {
		new JList();
		List<Object> temp = new LinkedList<Object>(backingList);
		for (Delta d : patch.getDeltas()) {
			d.applyTo(temp);
			boolean start = false;
			for (Delta dd : patch.getDeltas()) {
				if (start) {
					if(dd.getOriginal().getPosition() >= d.getOriginal().getPosition())
						; // patch.
				} else {
					if (d == dd)
						start = true;
				}
			}
		}

	}

}
