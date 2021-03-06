package com.example.shopping_cart.controller;

import com.example.shopping_cart.model.Item;
import com.example.shopping_cart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("cart")
public class CartController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public ModelAndView index(HttpSession session){
        ModelAndView modelAndView = new ModelAndView("cart/index");
        modelAndView.addObject("total", sum(session));
        return modelAndView;
    }

    @RequestMapping(value = "buy/{id}", method = RequestMethod.GET)
    public String index(@PathVariable("id") int id, HttpSession session){
        if (session.getAttribute("cart") == null){
            List<Item> cart = new ArrayList<Item>();
            cart.add(new Item(productService.find(id), 1));
            session.setAttribute("cart", cart);
        } else {
            List<Item> cart = (List<Item>) session.getAttribute("cart");
            int index = isExist(id, cart);
            if (index == -1){
                cart.add(new Item(productService.find(id),1));
            } else {
                int quantity = cart.get(index).getQuantity() +1;
                cart.get(index).setQuantity(quantity);
            }
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart/";
    }

    @RequestMapping(value = "remove/{id}", method = RequestMethod.GET)
    public String remove(@PathVariable("id") int id, HttpSession session) {
        List<Item> cart = (List<Item>) session.getAttribute("cart");
        int index = isExist(id, cart);
        cart.remove(index);
        session.setAttribute("cart", cart);
        return "redirect:/cart/";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update( HttpServletRequest request, HttpSession session) {
        String[] quantities = request.getParameterValues("quantity");
        List<Item> cart = (List<Item>) session.getAttribute("cart");
        for (int i = 0 ; i < cart.size();i++){
            cart.get(i).setQuantity(Integer.parseInt(quantities[i]));
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart/";
    }


    private int isExist(int id, List<Item> cart){
        for (int i=0; i <cart.size();i++){
            if (cart.get(i).getProduct().getId() == id){
                return i;
            }
        }
        return -1;
    }

    private double sum(HttpSession session){
        List<Item> cart = (List<Item>) session.getAttribute("cart");
        double s = 0;
        for (Item item: cart){
            s += item.getQuantity()
                    * item.getProduct().getPrice().doubleValue();
        }
        return s;
    }
}
