import java.io.*;
import java.util.Arrays;

public class HidHandler extends Thread{

    private String logfile;
    private Process pythonHidHandler;

    public static void main(String[] args) throws IOException, InterruptedException {
        new HidHandler("hidevents.log");
    }

    public HidHandler(String logfile) throws IOException, InterruptedException {
        this.logfile = logfile;
        pythonHidHandler= Runtime.getRuntime().exec("py src/hidlib.py "+logfile);
        System.out.println(pythonHidHandler);

        watch();
    }

    public void watch() throws IOException, InterruptedException {
        File file = new File(logfile);
        Reader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);
        String line;
        while (true) {
            line = br.readLine();
            System.out.println(line);
            if (line == null) {
                Thread.sleep(1000);
                for (int c : pythonHidHandler.getErrorStream().readAllBytes()) {
                    System.out.print((char)c);
                };
                System.out.println();
                for (int c : pythonHidHandler.getInputStream().readAllBytes()) {
                    System.out.print((char)c);
                };
                System.out.println(pythonHidHandler);
            } else {
                System.out.println(line);
            }
        }
    }
}
