package com.hwer.admin.contrller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwer.admin.bean.HwerConfig;
import com.hwer.admin.entity.R;
import com.hwer.admin.entity.User;
import com.hwer.admin.entity.vo.OrderVO;
import com.hwer.admin.entity.vo.UserVO;
import com.hwer.admin.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    @Resource
    private UserService userService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private HwerConfig hwerConfig;

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("user/login");
    }

    @PostMapping("/logon")
    @ResponseBody
    public R logon(User user, HttpServletRequest request, HttpServletResponse response) {
        User one = userService.getOne(new QueryWrapper<User>().eq("username", user.getUsername()).eq("role", 1));
        if (null == one) {
            return R.no("用户名或密码错误");
        }
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                user.getUsername(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        HttpSession session = request.getSession(true);
        session.setAttribute("USERINFO", user);
        securityContextRepository.saveContext(context, request, response);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return R.ok();
    }

    @GetMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("user/index");
    }

    @GetMapping("/getUserList")
    @ResponseBody
    public R getUserList(UserVO vo) {
        return R.ok(userService.getUserList(vo));
    }

    @PostMapping("/updateAllow")
    @ResponseBody
    public R updateAllow(UserVO vo) {
        return R.ok(userService.updateAllow(vo));
    }

    @PostMapping("/connect")
    @ResponseBody
    public R connect(UserVO vo) {
        userService.connect(vo);
        return R.ok();
    }

    @PostMapping("/close")
    @ResponseBody
    public R close(UserVO vo) {
        userService.close(vo);
        return R.ok();
    }

    @PostMapping("/generateUserInfo")
    @ResponseBody
    public R generateUserInfo(UserVO vo) {
        userService.generateUserInfo(userService.getByUsername(vo));
        return R.ok();
    }

    @PostMapping("/createOrder")
    @ResponseBody
    public R createOrder(@RequestBody OrderVO vo) {
        return R.ok(userService.createOrder(vo));
    }

    @GetMapping("/exportUserInfo")
    public ResponseEntity<ByteArrayResource> exportUserInfo() {
        try {
            List<String> paths = FileUtil.listFileNames(hwerConfig.getFileDir()).stream().filter(e -> e.endsWith("xlsx")).toList();
            String filename = "accounts_" + DateUtil.formatDate(new Date()) + ".zip";
            File zipFile = FileUtil.file(hwerConfig.getFileDir(), filename);
            if (zipFile.exists()) {
                FileUtil.del(zipFile);
            }
            ZipUtil.zip(zipFile, false, paths.stream().map(e -> FileUtil.file(hwerConfig.getFileDir(), e)).toArray(File[]::new));
            ByteArrayResource resource = new ByteArrayResource(IoUtil.readBytes(FileUtil.getInputStream(zipFile)));
            FileUtil.del(zipFile);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(filename, "UTF-8"))
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("导出文件失败:" + e);
        }
    }
}
