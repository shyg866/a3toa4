package com.shyg866.a3toa4.controller;

import com.shyg866.a3toa4.picutre.PictureCutUtil;
import com.shyg866.a3toa4.word.Word2PictureUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class A3ToA4Controller {
    static Log log = LogFactory.getLog(A3ToA4Controller.class);

    @RequestMapping("/a3ImgFilePath")
    public String index(@RequestParam String filePath, Model model) {

        System.out.println("您上送的文件目录或者文件为：" + filePath);
        try {
            if (filePath != null) {
                //如果输入的是word文档，先讲word转换成图片。
                filePath =  getWordFileList(filePath);

                String name = PictureCutUtil.imgToPdf(filePath);
                model.addAttribute("pdfFileName", name);
            }
            return "success";
        } catch (Exception e) {
            log.error("转换的时候出错了，[{}]", e);
            return "error";
        }
    }

    //获取当前目录下所有的word文件名称。
    public String  getWordFileList(String filePath) throws Exception {
        //如果输入的是word文档，先讲word转换成图片。
        if (filePath.endsWith("docx") || filePath.endsWith("doc")) {
            log.info("您输入的是word文档。此时需要先将word转换成图片，再生成pdf文件。");
            //返回所有图片的父目录。
            filePath = Word2PictureUtil.wordToImg(filePath);
            //此时需要单独处理某张图片。
        } else if (filePath.endsWith("jpg") || filePath.endsWith("JPG") || filePath.endsWith("png") || filePath.endsWith("PNG")) {

        } else {
            File file = new File(filePath);

            //获取所有文件
            String[] fileNames = file.list();
            //获取需要操作的图片名称数量

            List listName = new ArrayList();
            for (int i = 0; i < fileNames.length; i++) {
                String name = fileNames[i];
                if (name != null && name != "") {
                    //文件格式（后缀）
                    String formatName = name.substring(name.lastIndexOf(".") + 1);
                    if ("docx".equalsIgnoreCase(formatName) || "doc".equalsIgnoreCase(formatName)) {
                        //返回所有图片的父目录。
                        filePath = Word2PictureUtil.wordToImg(filePath +"\\"+name);
                    }
                }
            }

        }

        return filePath;
    }

}



