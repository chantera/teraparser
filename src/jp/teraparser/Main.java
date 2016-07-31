package jp.teraparser;

import jp.teraparser.app.App;

/**
 * jp.teraparser
 *
 * @author Hiroki Teranishi
 */
public class Main {
    public static final String PRODUCT_NAME = Main.class.getPackage().getImplementationTitle();
    public static final String VERSION      = Main.class.getPackage().getImplementationVersion();
    public static final String AUTHOR       = Main.class.getPackage().getImplementationVendor();

    // entry point
    public static void main(String[] args) {
        App.execute(args);
        System.exit(0);
    }
}
