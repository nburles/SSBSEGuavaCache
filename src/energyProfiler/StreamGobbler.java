package energyProfiler;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/*
 * From http://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html
 */
class StreamGobbler extends Thread {
    InputStream is;
    String type;
    OutputStream os;
    
    StreamGobbler(InputStream is) {
    	this(is, null, null);
    }
    
    StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }
    
    StreamGobbler(InputStream is, String type, OutputStream redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;
    }
    
    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null) {
                pw = new PrintWriter(os);
            }
                
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                }
                if (type != null) {
                	System.out.println(type + "> " + line);
                }
            }
            if (pw != null) {
                pw.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }
}