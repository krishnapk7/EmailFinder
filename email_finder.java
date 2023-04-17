import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class main_finder{
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the path for the corrupted files: ");
        String path = scan.nextLine();
        scan.close();
        File test2 = new File(path);
        File[] listOfFiles = test2.listFiles();
        ExecutorService executor = Executors.newCachedThreadPool();
        LinkedBlockingQueue<String> buffer = new LinkedBlockingQueue<>(listOfFiles.length);
        EmailReader test = new EmailReader(null);
        FileWorker test1 = new FileWorker(null, buffer);
        for(File file : listOfFiles){
            test1.setFile(file);
            executor.execute(test1);
            test.setString(buffer.take());
            executor.execute(test);
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        System.out.println(test.getCounter());
    }
}

class FileWorker implements Runnable{
    File file;
    LinkedBlockingQueue<String> buffer;
    public FileWorker(File file1, LinkedBlockingQueue<String> buffer1){
        file = file1;
        buffer = buffer1;
    }
    public synchronized void run(){
        try {
            Scanner scanner = new Scanner(file);
            String str = scanner.useDelimiter("\\A").next();
            scanner.close();
            buffer.put(str);
        } catch (Exception e) {
        }
    }

    public synchronized void setFile(File file1){
        file = file1;
    }
}

class EmailReader implements Runnable{
    private String str;
    private volatile int counter = 0;

    public EmailReader(String str1){
        str = str1;
    }

    public synchronized void run(){
        Pattern pattern = Pattern.compile("[_a-zA-Z]+[_a-zA-Z0-9]?[\\._]?[_a-zA-Z0-9]*@([a-zA-Z]+\\.)?([a-zA-Z]+\\.)?[a-zA-Z]+\\.(com|net|de|uk|ro|jp)");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            incCounter();
        }
    }

    private synchronized void incCounter(){
        counter++;
    }

    public synchronized void setString(String str1){
        str = str1;
    }

    public synchronized int getCounter(){
        return counter;
    }
}