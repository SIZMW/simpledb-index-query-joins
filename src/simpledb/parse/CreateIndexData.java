package simpledb.parse;

import simpledb.index.IndexType;

/**
 * CS 4432 Project 2
 *
 * We added the index type information to this class.
 *
 * The parser for the <i>create index</i> statement.
 *
 * @author Edward Sciore
 */
public class CreateIndexData {
    protected String idxname, tblname, fldname;

    // The index type
    protected IndexType type;

    /**
     * CS 4432 Project 2
     *
     * We added a parameter to pass in the index type.
     *
     * Saves the table and field names of the specified index.
     *
     * @param type
     *            The index type to create.
     */
    public CreateIndexData(String idxname, String tblname, String fldname, IndexType type) {
        this.idxname = idxname;
        this.tblname = tblname;
        this.fldname = fldname;
        this.type = type;
    }

    /**
     * Returns the name of the indexed field.
     *
     * @return the name of the indexed field
     */
    public String fieldName() {
        return fldname;
    }

    /**
     * Returns the name of the index.
     *
     * @return the name of the index
     */
    public String indexName() {
        return idxname;
    }

    /**
     * Returns the name of the indexed table.
     *
     * @return the name of the indexed table
     */
    public String tableName() {
        return tblname;
    }

    /**
     * CS 4432 Project 2
     *
     * We added this method to get access to the index type.
     *
     * Returns the type of the indexed table.
     *
     * @return the type of the indexed table.
     */
    public IndexType indexType() {
        return type;
    }
}
