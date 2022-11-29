package com.shyg866.a3toa4.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.shyg866.a3toa4.controller.A3ToA4Controller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfUtils {
    static Log log = LogFactory.getLog(PdfUtils.class);
    public static void main(String[] args) throws DocumentException, IOException {

      String[] files= null;
        int num=0;
        for (int i = 0; i < 10; i++) {
            num ++;
            files = new String[num];
            files[num-1]="00_"+i;
        }

        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);
        }
    }

    /**
     * 将图片转换为PDF文件
     *
     * @param file SpringMVC获取的图片文件
     * @return PDF文件
     * @throws IOException       IO异常
     * @throws DocumentException PDF文档异常
     */
    private static void generatePdfFile(File file) throws IOException, DocumentException {

        //获取到文件名称
        String pdfFileName = file.getParent() + "\\" + file.getName().substring(0, file.getName().lastIndexOf("_")) + ".pdf";
        Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
        PdfWriter.getInstance(doc, new FileOutputStream(pdfFileName));
        doc.open();


        //循环添加图片
        doc.newPage();

        //读取图片文件流信息
        Image image = Image.getInstance(readBytesFromFile(file.getAbsolutePath()));
        image.setAlignment(Image.MIDDLE);
        float height = image.getHeight();
        float width = image.getWidth();
        int percent = getPercent(height, width);

        image.scalePercent(percent); //压缩比
        doc.add(image);

        doc.close();
        File pdfFile = new File(pdfFileName);
        log.info("生成的pdf文件名：" + pdfFileName);

    }


    /**
     * 等比压缩，获取压缩百分比
     *
     * @param height 图片的高度
     * @param weight 图片的宽度
     * @return 压缩百分比
     */
    private static int getPercent(float height, float weight) {
        float percent = 0.0F;
        if (height > weight) {
            percent = PageSize.A4.getHeight() / height * 100;
        } else {
            percent = PageSize.A4.getWidth() / weight * 100;
        }
        return Math.round(percent);
    }

    private static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
           log.error(e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }

        }

        return bytesArray;

    }
}


