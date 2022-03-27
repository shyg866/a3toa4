package com.shyg866.a3toa4.picutre;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.shyg866.a3toa4.controller.A3ToA4Controller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片裁剪工具类
 */
public final class PictureCutUtil {
    static Log log = LogFactory.getLog(PictureCutUtil.class);
    
    public static void main(String[] args) throws Exception {
        //图片路径
        String imagPath = "F:\\picture";
        imagPath = getSrcImgPath(imagPath);
        //获取文件列表
        List jpgList = getJPGList(imagPath);
        if (jpgList == null || jpgList.size() == 0) {
            log.info("未找到要操作的图片");
            return;
        } else {
            log.info("需要操作的A3图片数量为：" + jpgList.size());
        }
        //裁剪 所有A3图片
        cut(imagPath, jpgList);
        //拼接所有的图片，生成最终A4的pdf文件，用于打印
        generatePdfFile(imagPath, jpgList);
    }

    /**
     * 提供封装方法，以便其他类调用。
     * @param filePath
     */
    public static String imgToPdf(String filePath) throws Exception {
        //图片路径
//        String imagPath = "F:\\picture";
        String     imagPath = getSrcImgPath(filePath);
        //获取文件列表
        List jpgList = getJPGList(filePath);
        if (jpgList == null || jpgList.size() == 0) {
            log.info("未找到要操作的图片");
            return "未找到需要处理的图片";
        } else {
            log.info("需要操作的A3图片数量为：" + jpgList.size());
            //裁剪 所有A3图片
            cut(imagPath, jpgList);
            //拼接所有的图片，生成最终A4的pdf文件，用于打印
             return    generatePdfFile(imagPath, jpgList);
        }

    }


    /**
     * 如果文件目录不正确的话，进行修正。
     * @param imagPath
     * @return
     */
    public static String getSrcImgPath(String imagPath){

        File file = new File(imagPath);
        if (file.isDirectory()){
            return imagPath;
        }else{
            return file.getParent() + "\\";
        }
    }

    /**
     * 将单个A3图片裁剪图片成两个A4图片
     * 生成的裁剪图片放到各个目录下以文件名命名的文件夹下。后续生成PDF的时候，需要注意对应的目录
     *
     * @param imageFilePath 需要裁剪的图片路径
     * @return
     * @
     */
    public static void cut(String imageFilePath, List jpgNameList) throws IOException {

        int width = 0, height = 0;

        for (int i = 0; i < jpgNameList.size(); i++) {
            log.info("开始处理A3图片的名称为：" + jpgNameList.get(i));
            String fileParentPath = imageFilePath.endsWith("/")||imageFilePath.endsWith("\\")?imageFilePath:imageFilePath+"\\"; // 父级目录

            File srcImageFile = new File(fileParentPath + jpgNameList.get(i));

            try {
                //使用ImageIO的read方法读取图片
                BufferedImage read = ImageIO.read(srcImageFile);
                width = read.getWidth() / 2; //取一半
                height = read.getHeight(); //高不变
                //调用裁剪方法(先生成左侧图片)
                BufferedImage image4Left = read.getSubimage(0, 0, width, height);
                //获取到文件名称
                String fileName = srcImageFile.getName().substring(0, srcImageFile.getName().lastIndexOf("."));


                //文件格式（后缀）
                String formatName = srcImageFile.getName().substring(srcImageFile.getName().lastIndexOf(".") + 1);

                String pictureCutFilePath = fileParentPath + "\\" + fileName + "\\";
                File destImageFilePath = new File(pictureCutFilePath);

                if (!destImageFilePath.exists()) {
                    destImageFilePath.mkdir();

                }

                //左侧图片名称
                String leftImageName = fileName + "_001." + formatName;

                //图片最终路径
                String filePath1 = pictureCutFilePath + leftImageName;
                //图片最终路径

                File destImageFile = new File(filePath1);


                //使用ImageIO的write方法进行输出
                ImageIO.write(image4Left, formatName, destImageFile);
                log.info("左侧图片：" + leftImageName);
                //右侧图片名称
                String rightImageName = fileName + "_002." + formatName;

                //调用裁剪方法(先生成左侧图片)
                BufferedImage image4Right = read.getSubimage(width, 0, width, height);
                //图片最终路径
                String filePath2 = pictureCutFilePath + rightImageName;
                //右侧文件
                destImageFile = new File(filePath2);
                //使用ImageIO的write方法进行输出
                ImageIO.write(image4Right, formatName, destImageFile);
                log.info("右侧图片：" + rightImageName);
            } catch (Exception e) {
                log.info("图片裁剪时,出错。日志为：[{}]",e);
                throw e;            }
            log.info("A3图片的名称为：" + jpgNameList.get(i) + "处理完毕");
        }
    }


    /**
     * -------------------------------------------------分隔符-------------------------------------------------------------
     * 将图片转换为PDF文件
     *
     * @param imageFilePath A3图片路径，也就是所有图片的父目录
     * @return PDF文件
     * @throws IOException       IO异常
     * @throws DocumentException PDF文档异常
     */
    private static String generatePdfFile(String imageFilePath, List jpgNameList) throws DocumentException, IOException {
        try{
            imageFilePath = imageFilePath + "\\";
            //获取到文件名称
            String pdfFileName =   imageFilePath +((String) jpgNameList.get(0)).substring(0, ((String) jpgNameList.get(0)).lastIndexOf(".")) + ".pdf";
            Document doc = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter.getInstance(doc, new FileOutputStream(pdfFileName));
            doc.open();

            for (int i = 0; i < jpgNameList.size(); i++) {
                //对生成的A4图片进行循环遍历
                String subImgPath = ((String) jpgNameList.get(i)).substring(0, ((String) jpgNameList.get(i)).lastIndexOf("."));
                String a4ImgPath = imageFilePath + subImgPath + "\\";
                File a4ImgFile = new File(a4ImgPath);
                //获取所有A4图片
                String[] a4Name = a4ImgFile.list();
                //遍历子目录
                for (int i1 = 0; i1 < a4Name.length; i1++) {
                    //循环添加图片
                    doc.newPage();
                    //读取图片文件流信息
                    String subImgFile = a4ImgPath + a4Name[i1];
                    Image image = Image.getInstance(readBytesFromFile(subImgFile));
                    image.setAlignment(Image.MIDDLE);

                    float height = image.getHeight();
                    float width = image.getWidth();
                    int percent = getPercent(height, width);
                    image.scalePercent(percent); //压缩比
                    doc.add(image);
                }

            }

            doc.close();
            File pdfFile = new File(pdfFileName);
            log.info("生成的pdf文件名：" + pdfFileName);
            return pdfFileName;
        }catch (Exception e){
            log.info("生成Pdf时,出错。日志为：",e);
            throw e;
        }


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
            e.printStackTrace();
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

    //读取文件列表，返回到List中
    public static List getJPGList(String imagPath) {
        //获取需要操作的图片名称数量
        //读取目录文件
        File file = new File(imagPath);
        List listName = new ArrayList();
        //如果就单独处理某张图片的话，此处需要单独处理下，防止处理所有的图片信息
        if (imagPath.endsWith("jpg")||imagPath.endsWith("JPG")||imagPath.endsWith("png")||imagPath.endsWith("PNG")){
            listName.add(file.getName());
            return listName;
        }



        //获取所有文件
        String[] fileNames = file.list();
        for (int i = 0; i < fileNames.length; i++) {
            String name = fileNames[i];
            if (name != null && name != "") {
                //文件格式（后缀）
                String formatName = name.substring(name.lastIndexOf(".") + 1);
                if ("jpg".equalsIgnoreCase(formatName)||"png".equalsIgnoreCase(formatName)) {
                    listName.add(name);
                }
            }
        }
        return listName;

    }

}
