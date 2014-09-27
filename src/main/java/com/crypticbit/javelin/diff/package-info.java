/**
 * This package allows you to perform a diff between two different branches and
 * their last common ancestor, and then re-apply those changes in the sequence
 * in which they occurred to the lca result in a merged set of changes.
 * <p>
 * Currently this supports a Maps and Lists with some decent heuristics for
 * merging, and for other types it simply takes the last change and applies
 * that.
 * <p>
 * In time this should also support:
 * <ul>
 * <li>pluggable merge resolvers</li>
 * <li>better generics</li>
 * <li>More flexible typing (currently Maps need to have String keys</li>
 * </ul>
 */
package com.crypticbit.javelin.diff;