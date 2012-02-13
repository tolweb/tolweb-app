import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.jar.*;
import org.tolweb.treegrow.main.*;

/**
 * Main class that starts the application up.  Shows a password panel if no
 * user information is present, or goes directly to the ToLManager window
 */
public class ToLEditor {
    public static void main(String[] args) {
        final Controller controller = Controller.getController();
        String beta="beta";
        String dev="dev";
        String live="live";
        String play = "play";
        String newsystem = "newsystem";
        String db = "";
        if (args.length > 0) {
            db = args[0];
        }
        if (db.equals(beta)) {
            controller.setDatabase(beta);
        } else if (db.equals(dev)) {
            controller.setDatabase(dev);
        } else if (db.equals(play)) {
            controller.setDatabase(play);
        } else if(db.equals(newsystem)) {
            controller.setDatabase(newsystem);
        } else {
            controller.setDatabase("live");
        }
        if (controller.isMac()) {
            try {
                Class mac_class = Class.forName("Main.SpecialMacHandler");
                Constructor new_one = mac_class.getConstructor(new Class[] {});
                new_one.newInstance(new Object[] {});
            } catch (Exception e) {
            }
        }
        boolean result = controller.getUserDetails();
        controller.openFileManager();
        if (!result) {
            while (controller.getManagerFrame() == null) {}
        }
        CheckNetConnection checker = new CheckNetConnection();
        if (checker.isConnected() > 0) {
            SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    controller.getFileManager().fetchNewUserMessages(); 
                    return null;
                }
            };
            worker.start();
        }
    }
    
    private static void uncompressJarFile() {
        try {
            URL url = new URL("http://editor.tolweb.org/ToLSupportFiles.jar");
            //Create Zip Input Stream for the Zip File
            JarInputStream zis = new JarInputStream(new BufferedInputStream(url.openStream()));

            // Initially false. If no files are found needing to be decompressed, then this
            // value remains false. If any file at all is decompressed, this flag is set
            // to true.
            boolean decompressed = false;
            JarEntry entry;
            byte[] buf = new byte[2048];
            //Loop through the entries
            while ((entry = zis.getNextJarEntry()) != null) {
                String fileName = entry.getName();
                File f = new File(fileName);

                if (entry.isDirectory()) {
                    // If the entry is a directory, make sure its parent dirs
                    // are created before decompressing any files into it.
                    f.mkdirs();
                } else {
                    // Lets compare the one in the archive to the one on disk. If the
                    // size and timestamp are the same, no reason to uncompress it.
                    if ( (f.exists() && f.lastModified() != entry.getTime()) || !f.exists()) {
                        FileOutputStream fos = new FileOutputStream(fileName);
                        BufferedOutputStream dest = new BufferedOutputStream(fos, 2048);
                        int count;
                        while ((count = zis.read(buf)) > 0) {
                            dest.write(buf, 0, count);
                        }

                        dest.flush();
                        dest.close();
                        f.setLastModified(entry.getTime());
                        decompressed = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}