package ie.ucd.forlang.neo4j;

import org.neo4j.graphdb.Lock;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

/** Dummy implementation class */
public final class TransactionImpl implements Transaction {

	@Override
	public final Lock acquireReadLock(PropertyContainer entity) {
		return null;
	}

	@Override
	public final Lock acquireWriteLock(PropertyContainer entity) {
		return null;
	}

	@Override
	public final void close() {
	}

	@Override
	public final void failure() {
	}

	@Override
	public final void finish() {
	}

	@Override
	public final void success() {
	}

	@Override
	public final void terminate() {
	}
}