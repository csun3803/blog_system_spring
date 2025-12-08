package com.example.blog_system_spring.controller;

import com.example.blog_system_spring.entity.Blog;
import com.example.blog_system_spring.entity.User;
import com.example.blog_system_spring.service.BlogService;
import com.example.blog_system_spring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    // ========== 对应原BlogServlet.doGet() - 博客列表API ==========

    /**
     * REST API: 获取所有博客列表（JSON格式）
     * 对应原GET /blog（不带参数）
     */
    @GetMapping("/api/list")
    @ResponseBody
    public String getAllBlogsJson() throws Exception {
        List<Blog> blogs = blogService.getAllBlogsForList();
        return objectMapper.writeValueAsString(blogs);
    }

    /**
     * REST API: 获取单个博客详情（JSON格式）
     * 对应原GET /blog?blogId=xxx
     */
    @GetMapping("/api/detail")
    @ResponseBody
    public String getBlogDetailJson(@RequestParam(value = "blogId", required = false) Integer blogId)
            throws Exception {
        if (blogId == null) {
            // 返回错误信息（与原Servlet一致）
            Map<String, String> error = new HashMap<>();
            error.put("error", "缺少blogId参数");
            return objectMapper.writeValueAsString(error);
        }

        Blog blog = blogService.getBlogById(blogId);
        if (blog == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "博客不存在");
            return objectMapper.writeValueAsString(error);
        }

        return objectMapper.writeValueAsString(blog);
    }

    // ========== 对应原BlogServlet.doPost() - 创建博客 ==========

    /**
     * 处理博客创建（表单提交）
     * 对应原POST /blog
     */
    @PostMapping("/create")
    public String createBlogPost(
            @RequestParam String title,
            @RequestParam String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 检查登录状态（与原Servlet一致）
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "当前用户未登录，不能提交博客!");
            return "redirect:/login";
        }

        try {
            // 创建博客
            blogService.createBlog(title, content, user.getUserId());

            // 重定向到博客列表页（与原Servlet一致）
            return "redirect:/blog/list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("content", content);
            return "redirect:/blog/edit";
        }
    }

    // ========== 对应原BlogDeleteServlet.doGet() - 删除博客 ==========

    /**
     * 删除博客
     * 对应原GET /blogDelete?blogId=xxx
     */
    @GetMapping("/delete")
    public String deleteBlog(
            @RequestParam(value = "blogId", required = false) Integer blogId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 检查登录状态（与原Servlet一致）
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "当前尚未登录，不能删除!");
            return "redirect:/login";
        }

        try {
            // 执行删除
            boolean success = blogService.deleteBlog(blogId, user.getUserId());

            if (success) {
                redirectAttributes.addFlashAttribute("message", "博客删除成功!");
            } else {
                redirectAttributes.addFlashAttribute("error", "博客删除失败!");
            }
        } catch (IllegalArgumentException | SecurityException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // 重定向到博客列表页（与原Servlet一致）
        return "redirect:/blog/list";
    }

    // ========== 对应原BlogUpdateServlet.doGet() - 获取编辑内容 ==========

    /**
     * REST API: 获取要编辑的博客内容（JSON格式）
     * 对应原GET /update?blogId=xxx
     */
    @GetMapping("/api/edit")
    @ResponseBody
    public String getBlogForEditJson(
            @RequestParam(value = "blogId", required = false) Integer blogId,
            HttpSession session) throws Exception {

        // 检查登录状态
        User user = (User) session.getAttribute("user");
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "当前尚未登录，不能修改!");
            return objectMapper.writeValueAsString(error);
        }

        try {
            Blog blog = blogService.getBlogForEdit(blogId, user.getUserId());
            return objectMapper.writeValueAsString(blog);
        } catch (IllegalArgumentException | SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return objectMapper.writeValueAsString(error);
        }
    }

    // ========== 对应原BlogUpdateServlet.doPost() - 更新博客 ==========

    /**
     * 更新博客
     * 对应原POST /update
     */
    @PostMapping("/update")
    public String updateBlogPost(
            @RequestParam(value = "blogId", required = false) Integer blogId,
            @RequestParam String title,
            @RequestParam String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 检查登录状态
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "当前尚未登录，不能修改!");
            return "redirect:/login";
        }

        try {
            // 执行更新
            blogService.updateBlog(blogId, title, content, user.getUserId());

            // 重定向到博客列表页（与原Servlet一致）
            return "redirect:/blog/list";
        } catch (IllegalArgumentException | SecurityException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("content", content);
            return "redirect:/blog/edit?blogId=" + blogId;
        }
    }

    // ========== 页面跳转（根据实际文件名调整） ==========

    /**
     * 博客列表页（HTML）
     * 对应 blog_list.html
     */
    @GetMapping("/list")
    public String blogListPage(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // 检查登录状态
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "请先登录查看博客!");
            return "redirect:/auth/blog_login";
        }

        // 获取当前用户的所有博客
        List<Blog> blogs = blogService.getBlogsByUser(user.getUserId());

        // 添加调试信息
        System.out.println("=== 当前用户博客列表调试信息 ===");
        System.out.println("当前用户ID: " + user.getUserId());
        System.out.println("博客数量: " + blogs.size());
        for (int i = 0; i < blogs.size(); i++) {
            Blog blog = blogs.get(i);
            System.out.println("博客 " + i + ": ID=" + blog.getBlogId() +
                    ", 标题=" + blog.getTitle() +
                    ", userId=" + blog.getUserId());
        }

        model.addAttribute("blogs", blogs);

        // 获取当前用户的博客数量（用于左侧统计）
        Integer blogCount = blogService.getUserBlogCount(user.getUserId());
        model.addAttribute("blogCount", blogCount);

        // 添加用户信息到model，方便模板使用
        model.addAttribute("currentUser", user);

        return "blog/blog_list";
    }

    /**
     * 博客详情页（HTML）
     */
    @GetMapping("/view/{id}")
    public String viewBlog(@PathVariable Integer id, Model model, HttpSession session) {
        // 获取博客详情
        System.out.println("DEBUG - 查看博客详情，接收的ID: " + id);
        Blog blog = blogService.getBlogById(id);
        if (blog == null) {
            // 博客不存在，重定向到列表页
            return "redirect:/blog/list";
        }

        // 获取作者信息
        User author = userService.selectById(blog.getUserId());

        // 检查当前用户是否为作者
        boolean isAuthor = false;
        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null && author != null) {
            isAuthor = currentUser.getUserId().equals(author.getUserId());
        }

        // 获取作者的文章数量
        Integer authorBlogCount = blogService.getUserBlogCount(blog.getUserId());

        // 添加到Model
        model.addAttribute("blog", blog);
        model.addAttribute("author", author);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("authorBlogCount", authorBlogCount);

        return "blog/blog_detail";  // 修改这里：改为 "blog/blog_detail"
    }

    // 替代原有的多个静态页面的URL映射
    @GetMapping("/detail/{id}")
    public String blogDetailPage(@PathVariable Integer id, Model model) {
        return viewBlog(id, model, null);
    }

    /**
     * 创建博客页面
     */
    // 修改创建博客页面
    @GetMapping("/edit")
    public String createBlogPage(Model model) {
        model.addAttribute("blog", new Blog());
        model.addAttribute("isEdit", false);
        return "blog/blog_edit";
    }

    // 修改编辑博客页面
    @GetMapping("/edit/{id}")
    public String editBlogPage(@PathVariable Integer id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Blog blog = blogService.getBlogForEdit(id, user.getUserId());
            model.addAttribute("blog", blog);
            model.addAttribute("isEdit", true);
            return "blog/blog_edit";
        } catch (Exception e) {
            return "redirect:/blog/list";
        }
    }

    // 在 BlogController 中添加这个方法
    /**
     * 统一处理博客的创建和更新
     */
    @PostMapping("/save")
    public String saveBlog(
            @RequestParam(required = false) Integer blogId,
            @RequestParam String title,
            @RequestParam String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 检查登录状态
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "当前用户未登录!");
            return "redirect:/login";
        }

        try {
            if (blogId == null) {
                // 创建新博客
                blogService.createBlog(title, content, user.getUserId());
                redirectAttributes.addFlashAttribute("message", "博客创建成功!");
            } else {
                // 更新现有博客
                blogService.updateBlog(blogId, title, content, user.getUserId());
                redirectAttributes.addFlashAttribute("message", "博客更新成功!");
            }

            return "redirect:/blog/list";
        } catch (IllegalArgumentException | SecurityException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("content", content);

            // 重定向回编辑页面
            if (blogId == null) {
                return "redirect:/blog/edit";
            } else {
                return "redirect:/blog/edit/" + blogId;
            }
        }
    }

    // ========== 额外的便捷方法 ==========

    /**
     * 首页重定向
     */
    @GetMapping("")
    public String index() {
        return "redirect:/blog/list";
    }

    /**
     * REST API：获取用户博客总数
     */
    @GetMapping("/api/user/{userId}/count")
    @ResponseBody
    public String getUserBlogCount(@PathVariable Integer userId) throws Exception {
        Integer count = blogService.getUserBlogCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("count", count);
        return objectMapper.writeValueAsString(result);
    }
}