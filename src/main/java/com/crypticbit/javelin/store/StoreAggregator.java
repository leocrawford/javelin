package com.crypticbit.javelin.store;

import java.util.List;

public class StoreAggregator implements AddressableStorage {

	private AddressableStorage primary, secondary;

	public StoreAggregator(AddressableStorage primary,
			AddressableStorage secondary) {
		this.primary = primary;
		this.secondary = secondary;
	}

	@Override
	public boolean checkCas(Key key) {
		return primary.checkCas(key) || secondary.checkCas(key);
	}

	@Override
	public boolean checkKas(Key key) {
		return primary.checkKas(key);
	}

	@Override
	public <S> S getCas(Key digest, Class<S> clazz) throws StoreException {
		if (primary.checkCas(digest))
			return primary.getCas(digest, clazz);
		else {
			S result = secondary.getCas(digest, clazz);
			primary.store(result, clazz);
			return result;
		}
	}

	@Override
	public <S> S getKas(Key digest, Class<S> clazz) throws StoreException {
		return primary.getKas(digest, clazz);
	}

	@Override
	public String getName() {
		return "Aggreagting " + primary.getName() + " and "
				+ secondary.getName();
	}



	@Override
	public <T> void registerAdapter(Adapter<T> adapter, Class<T> clazz) {
		primary.registerAdapter(adapter, clazz);

	}

	@Override
	public <S> void store(Key key, S oldValue, S newValue, Class<S> clazz)
			throws StoreException {
		primary.store(key, oldValue, newValue, clazz);

	}

	@Override
	public <S> Key store(S po, Class<S> clazz) {
		return primary.store(po, clazz);
	}

	@Override
	public List<Key> listCas() {
		// TODO Auto-generated method stub
		return null;
	}

}
