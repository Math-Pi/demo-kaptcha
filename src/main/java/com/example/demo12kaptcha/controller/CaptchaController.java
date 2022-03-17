package com.example.demo12kaptcha.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
 
/**
 * 登录相关
 * @author CJM
 * @date 2022/3/15 14:23
 */
@Controller
public class CaptchaController {
 
    @Resource(name = "captchaProducerMath")
    private Producer kaptchaProducer;

    @Autowired
    private HttpServletRequest request;
    /**
     * 获取图形验证码
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping("captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        //设置页面不缓存
        response.setDateHeader("Expires", 0);
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setContentType("image/jpeg");
        //获得前端发送来的type="math"或"char"
        String type = request.getParameter("type");
        //码
        String capStr = null;
        //值
        String code = null;
        BufferedImage bi = null;
        if ("math".equals(type)) {
            //创建验证码中的内容
            String capText = kaptchaProducer.createText();
            //substring:截取字符串
            //lastIndexOf:返回参数从字符串右边开始的索引
            //capStr中是capText从0位到@位之前的数据
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            //输出码
            System.out.println(capStr);
            //获得上面剩余的字符
            //code中是@之后的数，一个随机数（就是验证码）
            code = capText.substring(capText.lastIndexOf("@") + 1);
            //输出值
            System.out.println(code);
            //创建验证码
            bi = kaptchaProducer.createImage(capStr);
        }else if ("char".equals(type)) {    //如果类型为字符型
            capStr = code = kaptchaProducer.createText();
            bi = kaptchaProducer.createImage(capStr);
        }
        //获取session，并将验证码编码存放到session中
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, code);
        //显示图片到客户端
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
    }
    @RequestMapping("/login")
    @ResponseBody
    public String login(String code){
        if (checkVerifyCode(request,code))
            return "验证码正确";
        return "验证码错误";
    }
    @RequestMapping("/")
    public String index(){
        return "index";
    }

    /**
     * 验证码校验
     * @param request
     * @param code
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest request,String code) {
        //获取生成的验证码
        String verifyCodeExpected = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if(code == null ||!code.equals(verifyCodeExpected)) {
            return false;
        }
        return true;
    }
}