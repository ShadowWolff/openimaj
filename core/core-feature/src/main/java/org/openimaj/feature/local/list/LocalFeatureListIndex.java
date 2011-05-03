/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.feature.local.list;

import gnu.trove.TIntObjectHashMap;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.openimaj.feature.local.LocalFeature;
import org.openimaj.feature.local.quantised.QuantisedLocalFeature;
import org.openimaj.io.ReadWriteable;


/**
 * LocalFeatureListIndex is a @{link ReadWriteable} map of keys to local feature lists.
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 * @param <K> the key type
 * @param <V> the value type
 *
 */
public class LocalFeatureListIndex<K extends ReadWriteable<K>,V extends LocalFeature> extends HashMap<K,LocalFeatureList<V>> implements ReadWriteable<LocalFeatureListIndex<K,V>> {
	private static final long serialVersionUID = 1L;

	/** The header used when writing LocalFeatureListIndex to streams and files */
	public static final byte[] BINARY_HEADER = "LFLI".getBytes();
	
	protected Class<K> keyClass;
	protected Class<V> valueClass;
	
	@SuppressWarnings("unchecked")
	@Override
	public LocalFeatureListIndex<K, V> readBinary(DataInput in) throws IOException {
		try {
			String kclzstr = in.readUTF();
			String vclzstr = in.readUTF();

			Class<?> kclz = Class.forName(kclzstr);
			Class<?> vclz = Class.forName(vclzstr);

			if (keyClass != null && !keyClass.equals(kclz)) {
				throw new IOException("type mismatch");
			}
			if (valueClass != null && !valueClass.equals(vclz)) {
				throw new IOException("type mismatch");
			}

			keyClass = (Class<K>) kclz;
			valueClass = (Class<V>) vclz;

			int size = in.readInt();

			for (int i=0; i<size; i++) {
				K key = keyClass.newInstance();  
				key.readBinary(in);

				MemoryLocalFeatureList<V> value = MemoryLocalFeatureList.readNoHeader(in, valueClass);
				
				this.put(key, value);
			}
		} catch (Exception e) {
			throw new IOException(e);
		}

		return this;
	}

	@Override
	public LocalFeatureListIndex<K, V> readASCII(Scanner in) throws IOException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public byte[] binaryHeader() {
		return BINARY_HEADER;
	}

	@Override
	public String asciiHeader() {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void writeBinary(DataOutput out) throws IOException {
		if (keyClass == null) {
			if (!this.keySet().iterator().hasNext()) {
				throw new IOException("unable to guess type");
			}
			keyClass = (Class<K>) this.keySet().iterator().next().getClass();
		}
		
		if (valueClass == null) {
			if (!this.values().iterator().hasNext()) {
				throw new IOException("unable to guess type");
			}
			if (!this.values().iterator().next().iterator().hasNext()) {
				throw new IOException("unable to guess type");
			}
			valueClass = (Class<V>) this.values().iterator().next().iterator().next().getClass();
		}
		
		out.writeUTF(keyClass.getCanonicalName());
		out.writeUTF(valueClass.getCanonicalName());
		out.writeInt(this.size());

		for (Entry<K,LocalFeatureList<V>> e : this.entrySet()) {
			e.getKey().writeBinary(out);
			e.getValue().writeBinary(out);
		}
	}

	@Override
	public void writeASCII(PrintWriter out) throws IOException {
		throw new UnsupportedOperationException("not implemented yet");
	}
	
	/**
	 * <p>
	 * Invert an index of quantised features. The inversion process swaps keys and 
	 * feature {@link QuantisedLocalFeature#id}s around so that the inverted index 
	 * is a hash of ids to {@link QuantisedLocalFeature}s with the 
	 * {@link Object#hashCode()} of the key stored in the {@link QuantisedLocalFeature#id}
	 * field.
	 * </p>
	 * <p>
	 * The original index is not affected by the inversion operation.
	 * </p>
	 *  
	 * @param <T> the type of local feature.
	 * @param <K> the type of key.
	 * @param index the index to invert.
	 * @return an inverted-index data structure.
	 */
	public static <K extends ReadWriteable<K>,T extends QuantisedLocalFeature<?>> TIntObjectHashMap<TIntObjectHashMap<List<T>>> invert(LocalFeatureListIndex<K, T> index) {
		TIntObjectHashMap<TIntObjectHashMap<List<T>>> invertedIndex = new TIntObjectHashMap<TIntObjectHashMap<List<T>>>();
		
		for (Entry<K, LocalFeatureList<T>> e : index.entrySet()) {
			K docid = e.getKey();
			
			for (T t : e.getValue()) {
				int termid = t.id;
				
				if (!invertedIndex.containsKey(termid)) invertedIndex.put(termid, new TIntObjectHashMap<List<T>>());
				TIntObjectHashMap<List<T>> postings = invertedIndex.get(termid);
				if (!postings.containsKey(docid.hashCode())) postings.put(docid.hashCode(), new ArrayList<T>());
				postings.get(docid.hashCode()).add(t);
			}
		}
		
		return invertedIndex;
	}
}
