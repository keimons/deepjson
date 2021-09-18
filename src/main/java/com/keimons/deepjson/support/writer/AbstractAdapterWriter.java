package com.keimons.deepjson.support.writer;

import com.keimons.deepjson.AbstractWriter;
import com.keimons.deepjson.Charsets;
import com.keimons.deepjson.IAdapter;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * buffer
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractAdapterWriter extends AbstractWriter<Void> implements IAdapter {

	@Override
	public final Void write(char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException {
		int size = Charsets.UTF_8.length(buffers, bufferIndex, writeIndex);
		before(size);
		Charsets.UTF_8.encode(buffers, bufferIndex, writeIndex, this);
		return null;
	}
}