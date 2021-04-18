package com.github.gcorporationcare.data.convention;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class SqlNamingConvention {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public final class ColumnUtils {
		public static final String UTC_ZONE = "UTC";

		public static final String API_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'.'SSSSZ";
		public static final String API_DATE_FORMAT = "yyyy-MM-dd";
		public static final String API_TIME_FORMAT = "HH:mm:ss'.'SSSS";

		public static final String BYTEA_COLUMN_DEFINITION = "bytea";
		public static final String BINARY_COLUMN_TYPE = "org.hibernate.type.BinaryType";
		public static final String LONG_TEXT_COLUMN_TYPE = "org.hibernate.type.TextType";
	}

	/**
	 * <p>
	 * 1- In SQL, table names must be lower case.
	 * </p>
	 * <p>
	 * 2- Different words must be separated by underscore: class MyTable: Table
	 * my_table.
	 * </p>
	 * <p>
	 * 3- Use singular instead of plural, some words have no differences in plural
	 * and make it complicated to respect this principle.
	 * </p>
	 * <p>
	 * 4- When naming ManyToMany tables, use the 2 tables names, in alphabetical
	 * order table1: person, table2: job: ManyToMany table: job_person.
	 * </p>
	 * <p>
	 * 5- Avoid key words like user, they are reserved in some SGBD.
	 * </p>
	 * <p>
	 * 6- More on
	 * https://launchbylunch.com/posts/2014/Feb/16/sql-naming-conventions/
	 * </p>
	 * 
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public final class Table {
		// Common tables
		public static final String TEST_TABLE = "test_table";
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public final class Graph {
		public static final String GENERAL_GRAPH = "general-graph";
		public static final String PROPERTY_GRAPH = "javax.persistence.fetchgraph";
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public final class Property {
		public static final String TRANSLATIONS = "translations";
	}

	/**
	 * <p>
	 * 1- Primary keys are simply named ID. However, it is acceptable to prefix it
	 * with the table name, it make the reading more natural when joining table with
	 * foreign keys.
	 * </p>
	 * <p>
	 * 2- Column names must be snake case: FullName: full_name.
	 * </p>
	 * <p>
	 * 3- Use functional names rather than generic ones: (Person first_name and
	 * last_name rather than single column name)
	 * </p>
	 * 
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public final class Column {
		// Primary keys and foreign keys: Common tables
		public static final String ID = "id";

		public static final String TEST_TABLE_ID = "test_table_id";

		// Other columns
		public static final String CREATED = "created";
		public static final String CREATED_BY = "created_by";
		public static final String UPDATED = "updated";
		public static final String UPDATED_BY = "updated_by";
		public static final String LANGUAGE = "language";
		public static final String SOURCE = "source";
	}
}
