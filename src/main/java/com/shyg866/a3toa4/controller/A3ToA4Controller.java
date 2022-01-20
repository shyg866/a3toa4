package com.shyg866.a3toa4.controller;

import com.shyg866.a3toa4.picutre.PictureCutUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class A3ToA4Controller {
    static Log log = LogFactory.getLog(A3ToA4Controller.class);
    @RequestMapping("/a3ImgFilePath")
    public String index(@RequestParam String filePath, Model model) {

        System.out.println("您上送的文件目录为：" + filePath);
        try{
            if (filePath != null) {
              String name =  PictureCutUtil.imgToPdf(filePath);
              model.addAttribute("pdfFileName", name);
            }
            return "success";
        }catch (Exception e){
            return "error";
        }
    }


}
