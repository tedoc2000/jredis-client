/**
 * Copyright (C) 2006-2009 Dustin Sallings
 * Copyright (C) 2009-2011 Couchbase, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */

package com.zibobo.yedis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * NodeLocator implementation for dealing with simple array lookups using a
 * modulus of the hash code and node list length.
 */
public final class ArrayModNodeLocator implements NodeLocator {

    private final HashAlgorithm hashAlg;

    private RedisNode[] nodes;

    private RedisNode writeNode;

    /**
     * Construct an ArraymodNodeLocator over the given array of nodes and using
     * the given hash algorithm.
     *
     * @param n
     *            the array of nodes
     * @param alg
     *            the hash algorithm
     */
    public ArrayModNodeLocator(RedisNode writeNode, List<RedisNode> n, HashAlgorithm alg) {
        super();
        this.writeNode = writeNode;
        nodes = n.toArray(new RedisNode[n.size()]);
        hashAlg = alg;
    }

    private ArrayModNodeLocator(RedisNode writeNode, RedisNode[] n, HashAlgorithm alg) {
        super();
        this.writeNode = writeNode;
        nodes = n;
        hashAlg = alg;
    }

    @Override
    public Collection<RedisNode> getAll() {
        return Arrays.asList(nodes);
    }

    @Override
    public RedisNode getWrite() {
        return writeNode;
    }

    @Override
    public RedisNode getPrimary(String k) {
        return nodes[getServerForKey(k)];
    }

    @Override
    public Iterator<RedisNode> getSequence(String k) {
        return new NodeIterator(getServerForKey(k));
    }

    @Override
    public NodeLocator getReadonlyCopy() {
        RedisNode[] n = new RedisNode[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            n[i] = new RedisNodeROImpl(nodes[i]);
        }
        return new ArrayModNodeLocator(writeNode, n, hashAlg);
    }

    @Override
    public void updateLocator(RedisNode writeNode, List<RedisNode> newNodes) {
        this.writeNode = writeNode;
        this.nodes = newNodes.toArray(new RedisNode[newNodes.size()]);
    }

    private int getServerForKey(String key) {
        int rv = (int) (hashAlg.hash(key) % nodes.length);
        assert rv >= 0 : "Returned negative key for key " + key;
        assert rv < nodes.length : "Invalid server number " + rv + " for key "
                + key;
        return rv;
    }

    class NodeIterator implements Iterator<RedisNode> {

        private final int start;
        private int next = 0;

        public NodeIterator(int keyStart) {
            start = keyStart;
            next = start;
            computeNext();
            assert next >= 0 || nodes.length == 1 : "Starting sequence at "
                    + start + " of " + nodes.length + " next is " + next;
        }

        @Override
        public boolean hasNext() {
            return next >= 0;
        }

        private void computeNext() {
            if (++next >= nodes.length) {
                next = 0;
            }
            if (next == start) {
                next = -1;
            }
        }

        @Override
        public RedisNode next() {
            try {
                return nodes[next];
            } finally {
                computeNext();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove a node");
        }
    }
}
