// FileUtil.java
package com.spiders.news.util;

import com.spiders.news.model.News;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;

public class FileUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void exportToCsv(List<News> list, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ID,Title,Contributor,Source,ReadCount,PublishTime,Reviewer,Content");
            for (News n : list) {
                pw.printf("%d,%s,%s,%s,%d,%s,%s,%s\n",
                    n.getId(), escape(n.getTitle()), escape(n.getContributor()), escape(n.getSource()),
                    n.getReadCount(), sdf.format(n.getPublishTime()), escape(n.getReviewer()), escape(n.getContent()));
            }
        }
    }

    public static List<News> importFromCsv(File file) throws Exception {
        List<News> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",", 8);
                News n = new News();
                n.setTitle(arr[1]);
                n.setContributor(arr[2]);
                n.setSource(arr[3]);
                n.setReadCount(Integer.parseInt(arr[4]));
                n.setPublishTime(sdf.parse(arr[5]));
                n.setReviewer(arr[6]);
                n.setContent(arr[7]);
                list.add(n);
            }
        }
        return list;
    }

    private static String escape(String s) {
        return s.replace("\n", " ").replace(",", ";");
    }
}