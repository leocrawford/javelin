/**
 * An abstraction and simple in memory implementation of a key value store which can optionally operate as Content Addressable Storage.
 * The registered Adapters of the AddressableStorage determine what method is used to calculate digest. KeyFactory is current default. 
 * 
 * @author leo
 *
 */
package com.crypticbit.javelin.store;