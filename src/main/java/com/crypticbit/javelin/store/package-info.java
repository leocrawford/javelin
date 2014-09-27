/**
 * An abstraction and simple in memory implementation of a key value store which
 * can optionally operate as Content Addressable Storage. The registered
 * Adapters of the AddressableStorage determine what method is used to calculate
 * digest. KeyFactory is current default.
 * <p>
 * This may seem like a complicated way of creating a key value store, but the
 * the fact that the types that need to be stored aren't all Serializable means
 * we have to handle those differently.
 * 
 * @author leo
 *
 */
package com.crypticbit.javelin.store;