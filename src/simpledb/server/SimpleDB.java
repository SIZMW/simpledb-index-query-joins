package simpledb.server;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import simpledb.buffer.BufferMgr;
import simpledb.file.FileMgr;
import simpledb.index.planner.IndexUpdatePlanner;
import simpledb.log.LogMgr;
import simpledb.metadata.MetadataMgr;
import simpledb.opt.HeuristicQueryPlanner;
import simpledb.planner.BasicQueryPlanner;
import simpledb.planner.ExploitSortQueryPlanner;
import simpledb.planner.Planner;
import simpledb.planner.QueryPlanner;
import simpledb.planner.SortQueryPlanner;
import simpledb.planner.UpdatePlanner;
import simpledb.tx.Transaction;

/**
 * The class that provides system-wide static global values. These values must
 * be initialized by the method {@link #init(String) init} before use. The
 * methods {@link #initFileMgr(String) initFileMgr},
 * {@link #initFileAndLogMgr(String) initFileAndLogMgr},
 * {@link #initFileLogAndBufferMgr(String) initFileLogAndBufferMgr}, and
 * {@link #initMetadataMgr(boolean, Transaction) initMetadataMgr} provide
 * limited initialization, and are useful for debugging purposes.
 *
 * @author Edward Sciore
 */
public class SimpleDB {
    public static int BUFFER_SIZE = 32;
    public static String LOG_FILE = "simpledb.log";

    public static String LOG_CS4432 = "cs4432.log";

    private static FileMgr fm;
    private static BufferMgr bm;
    private static LogMgr logm;
    private static MetadataMgr mdm;

    private static String queryPlanner;

    // CS 4432 Project 2
    // Logger for log file output
    private static Logger logger;

    /**
     * Passthrough
     *
     * @param dirname
     *            the name of the database directory
     */
    public static void init(String dirname) {
        init(dirname, "");
    }

    /**
     * Initializes the system. This method is called during system startup.
     *
     * @param dirname
     *            the name of the database directory
     * @param initQueryPlanner
     *            The query planner to use
     */
    public static void init(String dirname, String initQueryPlanner) {
        queryPlanner = initQueryPlanner;

        initFileLogAndBufferMgr(dirname);
        Transaction tx = new Transaction();
        boolean isnew = fm.isNew();
        if (isnew)
            System.out.println("creating new database");
        else {
            System.out.println("recovering existing database");
            tx.recover();
        }
        initMetadataMgr(isnew, tx);
        tx.commit();
    }

    /**
     * CS 4432 Project 1
     *
     * Returns the global SimpleDB file logger.
     *
     * @return a Logger
     */
    public static Logger getLogger() {
        return logger;
    }

    // The following initialization methods are useful for
    // testing the lower-level components of the system
    // without having to initialize everything.

    /**
     * Initializes only the file manager.
     *
     * @param dirname
     *            the name of the database directory
     */
    public static void initFileMgr(String dirname) {
        fm = new FileMgr(dirname);
    }

    /**
     * Initializes the file and log managers.
     *
     * @param dirname
     *            the name of the database directory
     */
    public static void initFileAndLogMgr(String dirname) {
        initFileMgr(dirname);
        logm = new LogMgr(LOG_FILE);

        // CS 4432 Project 2
        // Added our own log file logging handlers
        try {
            FileHandler logFileHandler = new FileHandler(LOG_CS4432);
            logFileHandler.setLevel(Level.ALL);

            SimpleFormatter simpleFormatter = new SimpleFormatter();
            logFileHandler.setFormatter(simpleFormatter);

            logger = Logger.getGlobal();
            logger.setUseParentHandlers(false);

            logger.addHandler(logFileHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Initializes the file, log, and buffer managers.
     *
     * @param dirname
     *            the name of the database directory
     */
    public static void initFileLogAndBufferMgr(String dirname) {
        initFileAndLogMgr(dirname);
        bm = new BufferMgr(BUFFER_SIZE);
    }

    /**
     * Initializes metadata manager.
     *
     * @param isnew
     *            an indication of whether a new database needs to be created.
     * @param tx
     *            the transaction performing the initialization
     */
    public static void initMetadataMgr(boolean isnew, Transaction tx) {
        mdm = new MetadataMgr(isnew, tx);
    }

    public static FileMgr fileMgr() {
        return fm;
    }

    public static BufferMgr bufferMgr() {
        return bm;
    }

    public static LogMgr logMgr() {
        return logm;
    }

    public static MetadataMgr mdMgr() {
        return mdm;
    }

    /**
     * CS 4432 Project 2
     *
     * We modified the planner to return a HeuristicQueryPlanner and
     * IndexUpdatePlanner instead of the basic ones.
     *
     * Creates a planner for SQL commands. To change how the planner works,
     * modify this method.
     *
     * @return the system's planner for SQL commands
     */
    public static Planner planner() {
        QueryPlanner qplanner;
        if (queryPlanner.equals("smart")) {
            qplanner = new ExploitSortQueryPlanner();
        } else if (queryPlanner.equals("heuristic")) {
            qplanner = new HeuristicQueryPlanner();
        } else if (queryPlanner.equals("sort")) {
            qplanner = new SortQueryPlanner();
        } else {
            qplanner = new BasicQueryPlanner();
        }
        UpdatePlanner uplanner = new IndexUpdatePlanner();
        return new Planner(qplanner, uplanner);
    }
}
