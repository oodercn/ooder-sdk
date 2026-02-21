package net.ooder.vfs.bigfile;

import org.apache.http.util.TextUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BigFileUtil {

    public final static int minSplitSize = 10 * 1024 * 1024;

    public final static int bigfileSize = 50 * 1024 * 1024;


    public static List<String> splitFile(String filePath) throws IOException {
        return splitFile(filePath, -1, 1024, false);
    }
    public static List<String> splitFile(String filePath, Integer fileCount) throws IOException {
        return splitFile(filePath, fileCount, 1024, false);
    }

    public static List<String> splitFile(String filePath, Integer fileCount, Integer bufferSize, boolean fullLine) throws IOException {

        List<String> paths = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filePath);
        FileChannel inputChannel = fis.getChannel();
        final long fileSize = inputChannel.size();

        if (fileSize < minSplitSize) {
            paths.add(filePath);
        } else {
            if (bufferSize == null) {
                bufferSize = 1024;
            }

            if (fileCount == null || fileCount < 1) {
                fileCount = Math.round(fileSize / (minSplitSize)) + 1;
            }

            long average = fileSize / fileCount;
            ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.valueOf(bufferSize + ""));
            long startPosition = 0;
            long endPosition = average < bufferSize ? 0 : average - bufferSize;
            for (int i = 0; i < fileCount; i++) {
                if (i + 1 != fileCount) {
                    int read = inputChannel.read(byteBuffer, endPosition);
                    if (fullLine) {
                        readW:
                        while (read != -1) {
                            byteBuffer.flip();
                            byte[] array = byteBuffer.array();
                            for (int j = 0; j < array.length; j++) {
                                byte b = array[j];
                                if (b == 10 || b == 13) {
                                    endPosition += j;
                                    break readW;
                                }
                            }
                            endPosition += bufferSize;
                            byteBuffer.clear();
                            read = inputChannel.read(byteBuffer, endPosition);
                        }
                    }
                } else {
                    endPosition = fileSize;
                }

                FileOutputStream fos = new FileOutputStream(filePath + (i + 1));
                paths.add(filePath + (i + 1));
                FileChannel outputChannel = fos.getChannel();
                inputChannel.transferTo(startPosition, endPosition - startPosition, outputChannel);
                outputChannel.close();
                fos.close();
                startPosition = endPosition;
                endPosition += average;
            }
            inputChannel.close();
            fis.close();
        }
        return paths;
    }


    public static boolean mergeFiles(List<String> fpaths, String resultPath) {
        File resultfile = new File(resultPath);
        if (resultfile != null && resultfile.exists()) {
            resultfile.delete();
        }

        if (fpaths == null || fpaths.size() < 1 || TextUtils.isEmpty(resultPath)) {
            return false;
        }
        if (fpaths.size() == 1) {
            return new File(fpaths.get(0)).renameTo(new File(resultPath));
        }

        File[] files = new File[fpaths.size()];
        for (int i = 0; i < fpaths.size(); i++) {
            files[i] = new File(fpaths.get(i));
            if (TextUtils.isEmpty(fpaths.get(i)) || !files[i].exists() || !files[i].isFile()) {
                return false;
            }
        }

        File resultFile = new File(resultPath);

        try {
            FileChannel resultFileChannel = new FileOutputStream(resultFile, true).getChannel();
            for (int i = 0; i < fpaths.size(); i++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
            resultFileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        for (int i = 0; i < fpaths.size(); i++) {
            files[i].delete();
        }

        return true;
    }

    public static void main(String[] args) throws Exception {


        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        long startTime = System.currentTimeMillis();
        List<String> paths = splitFile("d:\\mq.rar");

        mergeFiles(paths, "d:\\mq2.rar");

        long endTime = System.currentTimeMillis();
        System.out.println("耗费时间： " + (endTime - startTime) + " ms");
        scanner.nextLine();
    }


}
