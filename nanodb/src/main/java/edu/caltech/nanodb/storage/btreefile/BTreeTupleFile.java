package edu.caltech.nanodb.storage.btreefile;


import edu.caltech.nanodb.expressions.OrderByExpression;
import edu.caltech.nanodb.expressions.TupleComparator;
import edu.caltech.nanodb.expressions.TupleLiteral;
import edu.caltech.nanodb.queryeval.TableStats;
import edu.caltech.nanodb.relations.Schema;
import edu.caltech.nanodb.relations.Tuple;
import edu.caltech.nanodb.storage.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.caltech.nanodb.storage.btreefile.BTreePageTypes.BTREE_INNER_PAGE;
import static edu.caltech.nanodb.storage.btreefile.BTreePageTypes.BTREE_LEAF_PAGE;


/**
 * <p>
 * This class, along with a handful of helper classes in this package,
 * provides support for B<sup>+</sup> tree tuple files.  B<sup>+</sup> tree
 * tuple files can be used to provide a sequential storage format for ordered
 * tuples.  They are also used to implement indexes for enforcing primary,
 * candidate and foreign keys, and also for providing optimized access to
 * tuples with specific values.
 * </p>
 * <p>
 * Here is a brief overview of the NanoDB B<sup>+</sup> tree file format:
 * </p>
 * <ul>
 * <li>Page 0 is always a header page, and specifies the entry-points in the
 *     hierarchy:  the root page of the tree, and the leftmost leaf of the
 *     tree.  Page 0 also maintains a free-list of empty pages in the tree, so
 *     that adding new pages to the tree is fast.  (See the {@link HeaderPage}
 *     class for details.)</li>
 * <li>The remaining pages are either leaf pages, inner pages, or empty pages.
 *     The first byte of the page always indicates the kind of page.  For
 *     details about the internal structure of leaf and inner pages, see the
 *     {@link InnerPage} and {@link LeafPage} classes.</li>
 * <li>Empty pages are organized into a simple singly linked list.  Each empty
 *     page holds a page-pointer to the next empty page in the sequence, using
 *     an unsigned short stored at index 1 (after the page-type value in index
 *     0).  The final empty page stores 0 as its next-page pointer value.</li>
 * </ul>
 */
public class BTreeTupleFile implements SequentialTupleFile {
    /** A logging object for reporting anything interesting that happens. */
    private static Logger logger = LogManager.getLogger(BTreeTupleFile.class);


    /**
     * If this flag is set to true, all data in data-pages that is no longer
     * necessary is cleared.  This will increase the cost of write-ahead
     * logging, but it also exposes bugs more quickly because old data won't
     * still be present if someone erroneously accesses it.
     */
    public static final boolean CLEAR_OLD_DATA = true;


    /**
     * The storage manager to use for reading and writing file pages, pinning
     * and unpinning pages, write-ahead logging, and so forth.
     */
    private StorageManager storageManager;


    /**
     * The manager for B<sup>+</sup> tree tuple files provides some
     * higher-level operations such as saving the metadata of a tuple file,
     * so it's useful to have a reference to it.
     */
    private BTreeTupleFileManager btreeFileManager;


    /** The schema of tuples in this tuple file. */
    private Schema schema;


    /** Statistics for this tuple file. */
    private TableStats stats;


    /** The file that stores the tuples. */
    private DBFile dbFile;


    /**
     * A helper class that manages file-level operations on the B+ tree file.
     */
    private FileOperations fileOps;


    /**
     * A helper class that manages the larger-scale operations involving leaf
     * nodes of the B+ tree.
     */
    private LeafPageOperations leafPageOps;


    /**
     * A helper class that manages the larger-scale operations involving inner
     * nodes of the B+ tree.
     */
    private InnerPageOperations innerPageOps;


    // private IndexInfo idxFileInfo;


    public BTreeTupleFile(StorageManager storageManager,
                          BTreeTupleFileManager btreeFileManager, DBFile dbFile,
                          Schema schema, TableStats stats) {
        if (storageManager == null)
            throw new IllegalArgumentException("storageManager cannot be null");

        if (btreeFileManager == null)
            throw new IllegalArgumentException("btreeFileManager cannot be null");

        if (dbFile == null)
            throw new IllegalArgumentException("dbFile cannot be null");

        if (schema == null)
            throw new IllegalArgumentException("schema cannot be null");

        if (stats == null)
            throw new IllegalArgumentException("stats cannot be null");

        this.storageManager = storageManager;
        this.btreeFileManager = btreeFileManager;
        this.dbFile = dbFile;
        this.schema = schema;
        this.stats = stats;

        fileOps = new FileOperations(storageManager, dbFile);
        innerPageOps = new InnerPageOperations(storageManager, this, fileOps);
        leafPageOps = new LeafPageOperations(storageManager, this, fileOps,
                                             innerPageOps);
    }


    @Override
    public TupleFileManager getManager() {
        return btreeFileManager;
    }


    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public TableStats getStats() {
        return stats;
    }


    public DBFile getDBFile() {
        return dbFile;
    }


    @Override
    public List<OrderByExpression> getOrderSpec() {
        throw new UnsupportedOperationException("NYI");
    }


    @Override
    public Tuple getFirstTuple() {
        BTreeFilePageTuple tup = null;

        // By passing a completely empty Tuple (no columns), we can cause the
        // navigateToLeafPage() method to choose the leftmost leaf page.

        TupleLiteral noTup = new TupleLiteral();
        LeafPage leaf = navigateToLeafPage(noTup, false, null);

        if (leaf != null && leaf.getNumTuples() > 0)
            tup = leaf.getTuple(0);

        tup.pin();
        return tup;
    }


    @Override
    public Tuple getNextTuple(Tuple tup) {
        BTreeFilePageTuple tuple = (BTreeFilePageTuple) tup;

        DBPage dbPage;
        int nextIndex;
        LeafPage leaf;
        BTreeFilePageTuple nextTuple = null;

        if (tuple.isDeleted()) {
            // The tuple was deleted, so we need to find out the page number
            // and index of the next tuple.

            int nextPageNo = tuple.getNextTuplePageNo();
            if (nextPageNo != 0) {
                dbPage = storageManager.loadDBPage(dbFile, nextPageNo);
                nextIndex = tuple.getNextTupleIndex();

                leaf = new LeafPage(dbPage, schema);
                if (nextIndex >= leaf.getNumTuples()) {
                    throw new IllegalStateException(String.format(
                        "The \"next tuple\" field of deleted tuple is too " +
                        "large (must be less than %d; got %d)",
                        leaf.getNumTuples(), nextIndex));
                }

                nextTuple = leaf.getTuple(nextIndex);
            }
        }
        else {
            // Get the page that holds the current entry, and see where it
            // falls within the page.
            dbPage = tuple.getDBPage();
            leaf = new LeafPage(dbPage, schema);

            // Use the offset of the passed-in entry to find the next entry.

            // The next tuple follows the current tuple, unless the current
            // tuple was deleted!  In that case, the next tuple is actually
            // where the current tuple used to be.
            nextIndex = tuple.getTupleIndex() + 1;

            if (nextIndex < leaf.getNumTuples()) {
                // Still more entries in this leaf.
                nextTuple = leaf.getTuple(nextIndex);
            }
            else {
                // No more entries in this leaf.  Must go to the next leaf.
                int nextPageNo = leaf.getNextPageNo();
                if (nextPageNo != 0) {
                    dbPage = storageManager.loadDBPage(dbFile, nextPageNo);

                    leaf = new LeafPage(dbPage, schema);
                    if (leaf.getNumTuples() > 0) {
                        nextTuple = leaf.getTuple(0);
                    }
                    else {
                        // This would be *highly* unusual.  Leaves are
                        // supposed to be at least 1/2 full, always!
                        logger.error(String.format(
                            "Next leaf node %d has no entries?!", nextPageNo));
                    }

                    dbPage.unpin();
                }
            }
        }

        if (nextTuple != null) {
            nextTuple.pin();
        }

        return nextTuple;
    }


    @Override
    public Tuple getTuple(FilePointer fptr)
        throws InvalidFilePointerException {

        DBPage dbPage = storageManager.loadDBPage(dbFile, fptr.getPageNo());
        if (dbPage == null) {
            throw new InvalidFilePointerException("Specified page " +
                fptr.getPageNo() + " doesn't exist in file " + dbFile);
        }

        // In the B+ tree file format, the file-pointer points to the actual
        // tuple itself.

        int fpOffset = fptr.getOffset();
        LeafPage leaf = new LeafPage(dbPage, schema);
        for (int i = 0; i < leaf.getNumTuples(); i++) {
            BTreeFilePageTuple tup = leaf.getTuple(i);

            if (tup.getOffset() == fpOffset) {
                tup.pin();
                return tup;
            }

            // Tuple offsets within a page will be monotonically increasing.
            if (tup.getOffset() > fpOffset)
                break;
        }

        throw new InvalidFilePointerException("No tuple at offset " + fptr);
    }


    @Override
    public Tuple findFirstTupleEquals(Tuple searchKey) {
        logger.debug("Finding first tuple that equals " + searchKey +
            " in BTree file " + dbFile);

        LeafPage leaf = navigateToLeafPage(searchKey, false, null);
        if (leaf == null) {
            // This case is handled by the below loop, but it will make for
            // more understandable logging.
            logger.debug("BTree file is empty!");
            return null;
        }

        logger.debug("Navigated to leaf page " + leaf.getPageNo());
        while (leaf != null) {
            // We have at least one tuple to look at, so scan through to
            // find the first tuple that equals what we are looking for.
            for (int i = 0; i < leaf.getNumTuples(); i++) {
                BTreeFilePageTuple tup = leaf.getTuple(i);
                int cmp = TupleComparator.comparePartialTuples(tup, searchKey,
                    TupleComparator.CompareMode.IGNORE_LENGTH);
                // (donnie) This is just way too much logging!
                // logger.debug("Comparing search key to tuple " + tup +
                //     ", got cmp = " + cmp);

                if (cmp == 0) {
                    // Found it!
                    tup.pin();
                    return tup;
                }
                else if (cmp > 0) {
                    // Subsequent tuples will appear after the search key, so
                    // there's no point in going on.
                    leaf.getDBPage().unpin();
                    return null;
                }
            }

            int nextPageNo = leaf.getNextPageNo();
            logger.debug("Scanned through entire leaf page %d without " +
                "finding tuple.  Next page is %d.", leaf.getPageNo(),
                nextPageNo);

            // If we get here, we need to go to the next leaf-page.
            leaf.getDBPage().unpin();
            if (nextPageNo > 0) {
                DBPage dbpNextLeaf =
                    storageManager.loadDBPage(dbFile, nextPageNo);
                byte pageType = dbpNextLeaf.readByte(0);
                if (pageType != BTREE_LEAF_PAGE) {
                    throw new BTreeTupleFileException(String.format(
                        "Expected page %d to be a leaf; found %d instead",
                        nextPageNo, pageType));
                }

                leaf = new LeafPage(dbpNextLeaf, schema);
            }
            else {
                logger.debug("Reached end of leaf pages");
                leaf = null;
            }
        }

        // If we reach here, we reached the end of the leaf pages without
        // finding the tuple.
        return null;
    }


    @Override
    public PageTuple findFirstTupleGreaterThan(Tuple searchKey) {

        LeafPage leaf = navigateToLeafPage(searchKey, false, null);

        if (leaf != null && leaf.getNumTuples() > 0) {
            // We have at least one tuple to look at, so scan through to find
            // the first tuple that equals what we are looking for.
            for (int i = 0; i < leaf.getNumTuples(); i++) {
                BTreeFilePageTuple tup = leaf.getTuple(i);
                int cmp = TupleComparator.comparePartialTuples(tup, searchKey);
                if (cmp > 0) {
                    tup.pin();
                    return tup;  // Found it!
                }
            }

            leaf.getDBPage().unpin();
        }

        return null;
    }


    @Override
    public Tuple addTuple(Tuple tup) {
        logger.debug("Adding tuple " + tup + " to BTree file " + dbFile);

        // Navigate to the leaf-page, creating one if the BTree file is
        // currently empty.
        ArrayList<Integer> pagePath = new ArrayList<>();
        LeafPage leaf = navigateToLeafPage(tup, true, pagePath);

        // TODO:  This is definitely not ideal, but should get us going.
        TupleLiteral tupLit;
        if (tup instanceof TupleLiteral)
            tupLit = (TupleLiteral) tup;
        else
            tupLit = TupleLiteral.fromTuple(tup);
        tupLit.setStorageSize(PageTuple.getTupleStorageSize(schema, tupLit));

        BTreeFilePageTuple bTup = leafPageOps.addTuple(leaf, tupLit, pagePath);
        bTup.pin();
        return bTup;
    }


    @Override
    public void updateTuple(Tuple tup, Map<String, Object> newValues) {

        throw new UnsupportedOperationException("NYI");
    }


    @Override
    public void deleteTuple(Tuple tup) {
        BTreeFilePageTuple tuple = (BTreeFilePageTuple) tup;

        ArrayList<Integer> pagePath = new ArrayList<>();
        LeafPage leaf = navigateToLeafPage(tup, false, pagePath);

        logger.debug("Deleting tuple " + tuple + " from file " + dbFile);

        leafPageOps.deleteTuple(leaf, tuple, pagePath);
        tuple.setDeleted();
    }


    /**
     * This helper method performs the common task of navigating from the root
     * of the B<sup>+</sup> tree down to the appropriate leaf node, based on
     * the search-key provided by the caller.  Note that this method does not
     * determine whether the search-key actually exists; rather, it simply
     * navigates to the leaf in the file where the search-key would appear.
     *
     * @param searchKey the search-key being used to navigate the
     *        B<sup>+</sup> tree structure
     *
     * @param createIfNeeded If the B<sup>+</sup> tree is currently empty
     *        (i.e. not even containing leaf pages) then this argument can be
     *        used to create a new leaf page where the search-key can be
     *        stored.  This allows the method to be used for adding tuples to
     *        the file.
     *
     * @param pagePath If this optional argument is specified, then the method
     *        stores the sequence of page-numbers it visits as it navigates
     *        from root to leaf.  If {@code null} is passed then nothing is
     *        stored as the method traverses the B<sup>+</sup> tree structure.
     *
     * @return the leaf-page where the search-key would appear, or
     *         {@code null} if the B<sup>+</sup> tree file is currently empty
     *         and {@code createIfNeeded} is {@code false}.
     */
    private LeafPage navigateToLeafPage(Tuple searchKey,
        boolean createIfNeeded, List<Integer> pagePath) {

        // The header page tells us where the root page starts.
        DBPage dbpHeader = storageManager.loadDBPage(dbFile, 0);

        // Get the root page of the BTree file.
        int rootPageNo = HeaderPage.getRootPageNo(dbpHeader);
        DBPage dbpRoot;
        if (rootPageNo == 0) {
            // The file doesn't have any data-pages at all yet.  Create one if
            // the caller wants it.

            if (!createIfNeeded)
                return null;

            // We need to create a brand new leaf page and make it the root.

            logger.debug("BTree file currently has no data pages; " +
                         "finding/creating one to use as the root!");

            dbpRoot = fileOps.getNewDataPage();
            rootPageNo = dbpRoot.getPageNo();

            HeaderPage.setRootPageNo(dbpHeader, rootPageNo);
            HeaderPage.setFirstLeafPageNo(dbpHeader, rootPageNo);

            dbpRoot.writeByte(0, BTREE_LEAF_PAGE);
            LeafPage.init(dbpRoot, schema);

            logger.debug("New root pageNo is " + rootPageNo);
        }
        else {
            // The BTree file has a root page; load it.
            dbpRoot = storageManager.loadDBPage(dbFile, rootPageNo);

            logger.debug("BTree file root pageNo is " + rootPageNo);
        }

        // Next, descend down the file's structure until we find the proper
        // leaf-page based on the key value(s).

        DBPage dbPage = dbpRoot;
        while (true) {
            int pageType = dbPage.readByte(0);
            if (pageType != BTREE_INNER_PAGE && pageType != BTREE_LEAF_PAGE) {
                throw new BTreeTupleFileException(
                        "Invalid page type encountered:  " + pageType);
            }
            if (pagePath != null)
                pagePath.add(dbPage.getPageNo());
            if (pageType == BTREE_LEAF_PAGE) {
                return new LeafPage(dbPage, schema);
            }
            InnerPage inPage = new InnerPage(dbPage, schema);
            int keyNo = 0;
            while (keyNo < inPage.getNumKeys()) {
                int cmp = TupleComparator.comparePartialTuples(
                        searchKey,
                        inPage.getKey(keyNo),
                        TupleComparator.CompareMode.SHORTER_IS_LESS);
                if (cmp < 0) {
                    dbPage = storageManager.loadDBPage(dbFile,
                            inPage.getPointer(keyNo));
                    break;
                }
                keyNo++;
            }
            if (keyNo == inPage.getNumKeys()) {
                dbPage = storageManager.loadDBPage(dbFile,
                        inPage.getPointer(keyNo));
            }
        }
    }


    @Override
    public void analyze() {
        throw new UnsupportedOperationException("NYI");
    }


    @Override
    public List<String> verify() {
        BTreeFileVerifier verifier =
            new BTreeFileVerifier(storageManager, this);

        return verifier.verify();
    }


    @Override
    public void optimize() {
        throw new UnsupportedOperationException("NYI");
    }
}
