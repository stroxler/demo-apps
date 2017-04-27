package com.stroxler;

import com.stroxler.entity.TreeNode;
import org.springframework.web.bind.annotation.*;


@RestController
public class TreeController {

    public TreeController() {
        System.out.println("ExampleControllerInstantiated");
    }

    @RequestMapping(value="/hello", method=RequestMethod.GET)
    public @ResponseBody String hello(@RequestParam(value="name", defaultValue="World") String name) {
        return "Hello " + name + "!";
    }

    @RequestMapping(value="/tree/echo", method=RequestMethod.PUT)
    public @ResponseBody TreeNode echo(@RequestBody TreeNode treeNode) {
        return treeNode;
    }

    @RequestMapping(value="/tree/reverse", method=RequestMethod.PUT)
    public @ResponseBody TreeNode reverse(@RequestBody TreeNode treeNode) {
        return TreeNode.reverse(treeNode);
    }
}
