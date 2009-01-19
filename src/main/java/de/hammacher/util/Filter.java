package de.hammacher.util;

/**
 * A generic class acting as a filter on arbitrary objects.
 *
 * @author Clemens Hammacher
 * @param <T> the class whose instances should be filtered
 */
public interface Filter<T> {

	/**
	 * A {@link Filter} that accepts all objects.
	 *
	 * @author Clemens Hammacher
	 */
	public static class FilterAll<T> implements Filter<T> {

		private static final FilterAll<Object> instance = new FilterAll<Object>();

		/**
		 * Returns a singleton instance of FilterAll.
		 *
		 * @param <T>
		 * @param obj this object is never used; it just determines the
		 *            generic type to return.
		 * @return a singleton instance of FilterAll
		 */
		@SuppressWarnings("unchecked")
		public static <T> FilterAll<T> get(T obj) {
			return (FilterAll<T>) instance;
		}

		public boolean filter(T obj) {
			return true;
		}
	}

	/**
	 * A {@link Filter} that rejects all objects.
	 *
	 * @author Clemens Hammacher
	 */
	public static class FilterNone<T> implements Filter<T> {

		private static final FilterNone<Object> instance = new FilterNone<Object>();

		/**
		 * Returns a singleton instance of FilterNone.
		 *
		 * @param <T>
		 * @param obj this object is never used; it just determines the
		 *            generic type to return.
		 * @return a singleton instance of FilterNone
		 */
		@SuppressWarnings("unchecked")
		public static <T> FilterNone<T> get(T obj) {
			return (FilterNone<T>) instance;
		}

		public boolean filter(T obj) {
			return true;
		}
	}

	/**
	 * Determines whether the given object should be filtered or not.
	 *
	 * In general, returning true means accepting the object, false means
	 * rejecting it.
	 *
	 * @param obj the object under consideration
	 * @return true to accept the object, false to reject it
	 */
	boolean filter(T obj);

}
