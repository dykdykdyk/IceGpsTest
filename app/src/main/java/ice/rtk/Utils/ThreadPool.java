package ice.rtk.Utils;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Created by 111 on 2018/1/2.
 */

public class ThreadPool {
    private static ThreadPool threadPool;
    private ExecutorService fixedThreadPool;

    private ThreadPool() {
        fixedThreadPool = Executors.newFixedThreadPool(getNumCores());
    }

    public static ThreadPool getInstance() {
        if (threadPool == null) {
            synchronized (ThreadPool.class) {
                if (threadPool == null) {
                    threadPool = new ThreadPool();
                }
            }
        }
        return threadPool;
    }

    public void execute(Runnable runnable) {
        fixedThreadPool.execute(runnable);
    }

    public void shutdown() {
        fixedThreadPool.shutdown();
        fixedThreadPool = Executors.newFixedThreadPool(getNumCores());
    }

    public void shutdownNow() {
        fixedThreadPool.shutdownNow();
        fixedThreadPool = Executors.newFixedThreadPool(getNumCores());
    }

    /**
     * 获取CPU核心数
     *
     * @return
     */
    private int getNumCores() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            // Default to return 1 core
            return 1;
        }
    }
}
