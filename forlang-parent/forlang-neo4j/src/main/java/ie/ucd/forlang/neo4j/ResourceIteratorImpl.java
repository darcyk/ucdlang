package ie.ucd.forlang.neo4j;

import java.util.Iterator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

public final class ResourceIteratorImpl implements ResourceIterator<Node> {

	private Iterator<Node> iterator = null;
	
	public ResourceIteratorImpl(Iterator<Node> iterator) {
		super();
		this.iterator = iterator;
	}
	
	@Override
	public final void close() {
	}

	@Override
	public final boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public final Node next() {
		return iterator.next();
	}

	@Override
	public final void remove() {
		iterator.remove();
	}
}