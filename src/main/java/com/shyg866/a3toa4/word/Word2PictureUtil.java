package com.shyg866.a3toa4.word;

import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.SaveFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Word转成高质量的图片
 */
public class Word2PictureUtil {
    static Log log = LogFactory.getLog(Word2PictureUtil.class);

    public static void main(String[] args) {

        //word2pdf("C:/Users/Administrator/Desktop/1.doc","C:/Users/Administrator/Desktop/xxxx.pdf");//word转pdf
        //word转图片格式
        try {
            File file = new File("F:\\picture\\11.docx");

            InputStream inStream = new FileInputStream(file);
//            List<String> wordToImg = wordToImg(inStream, file);//

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @Description: 验证aspose.word组件是否授权：无授权的文件有水印标记
     */
    public static boolean isWordLicense() {
        boolean result = false;
        try {
            String s = "<License><Data><Products><Product>Aspose.Total for Java</Product><Product>Aspose.Words for Java</Product></Products><EditionType>Enterprise</EditionType><SubscriptionExpiry>20991231</SubscriptionExpiry><LicenseExpiry>20991231</LicenseExpiry><SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber></Data><Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature></License>";
            ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
            com.aspose.words.License license = new com.aspose.words.License();
            license.setLicense(inputStream);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param filePath 文件的绝对路劲
     * @Description: word和txt文件转换图片
     * 说明：将A3 格式中word的每一页都转换成单独的图片，并返回图片名称列表
     */
    public static String wordToImg(String filePath) throws Exception {
        if (!isWordLicense()) {
            return null;
        }

        //图片名称列表
        List<String> jpgList = new ArrayList<>();
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            long old = System.currentTimeMillis();
            Document doc = new Document(inputStream);
            ImageSaveOptions options = new ImageSaveOptions(SaveFormat.PNG);
            options.setPrettyFormat(true);
            options.setUseAntiAliasing(true);
            options.setUseHighQualityRendering(true);
            options.setResolution(512); //图片清晰度，数值越大，越清晰。
            int pageCount = doc.getPageCount();
            //doc 文件名称
            String fileName = file.getName();
            log.info("[" + fileName + "]总共有[" + pageCount + "]页需要转换成图片。");
            for (int i = 0; i < pageCount; i++) {
                List<BufferedImage> imageList = new ArrayList<BufferedImage>();
                OutputStream output = new ByteArrayOutputStream();
                options.setPageIndex(i);
                doc.save(output, options);
                ImageInputStream imageInputStream = javax.imageio.ImageIO.createImageInputStream(parse(output));
                imageList.add(javax.imageio.ImageIO.read(imageInputStream));

                BufferedImage mergeImage = mergeImage(false, imageList);
                //将word的每一页生成的图片命名规则为：doc名称-序号.jpg
                String jpgFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "-" + (i + 1) + ".jpg";
                String jpgFilePathName = file.getParent() + "\\" + jpgFileName;
                jpgList.add(jpgFileName);//返回每个图片的名称。
                ImageIO.write(mergeImage, "jpg", new File(jpgFilePathName));
                log.info("第" + (i + 1) + "页生成的图片名称为：" + jpgFilePathName);
//                imageInputStream.flush();
//                imageInputStream.close();
            }
            log.info("转换文档耗时："+(System.currentTimeMillis()-old)/1000L +" 秒。");
            return file.getParent();
        } catch (Exception e) {
            log.error("word转图片时发生异常，", e);
            throw e;
        }
    }

    /**
     * outputStream转inputStream
     */

    public static ByteArrayInputStream parse(OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }


    /**
     * 合并任数量的图片成一张图片
     *
     * @param isHorizontal true代表水平合并，false代表垂直合并
     * @param imgs         待合并的图片数组
     * @return
     * @throws IOException
     */
    public static BufferedImage mergeImage(boolean isHorizontal, List<BufferedImage> imgs) throws IOException {
        // 生成新图片
        BufferedImage destImage = null;
        // 计算新图片的长和高
        int allw = 0, allh = 0, allwMax = 0, allhMax = 0;
        // 获取总长、总宽、最长、最宽
        for (int i = 0; i < imgs.size(); i++) {
            BufferedImage img = imgs.get(i);
            allw += img.getWidth();

            if (imgs.size() != i + 1) {
                allh += img.getHeight() + 5;
            } else {
                allh += img.getHeight();
            }


            if (img.getWidth() > allwMax) {
                allwMax = img.getWidth();
            }
            if (img.getHeight() > allhMax) {
                allhMax = img.getHeight();
            }
        }
        // 创建新图片
        if (isHorizontal) {
            destImage = new BufferedImage(allw, allhMax, BufferedImage.TYPE_INT_RGB);
        } else {
            destImage = new BufferedImage(allwMax, allh, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g2 = (Graphics2D) destImage.getGraphics();
        g2.setBackground(Color.LIGHT_GRAY);
        g2.clearRect(0, 0, allw, allh);
        g2.setPaint(Color.RED);

        // 合并所有子图片到新图片
        int wx = 0, wy = 0;
        for (int i = 0; i < imgs.size(); i++) {
            BufferedImage img = imgs.get(i);
            int w1 = img.getWidth();
            int h1 = img.getHeight();
            // 从图片中读取RGB
            int[] ImageArrayOne = new int[w1 * h1];
            ImageArrayOne = img.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
            if (isHorizontal) { // 水平方向合并
                destImage.setRGB(wx, 0, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            } else { // 垂直方向合并
                destImage.setRGB(0, wy, w1, h1, ImageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            }
            wx += w1;
            wy += h1 + 5;
        }


        return destImage;
    }
}
